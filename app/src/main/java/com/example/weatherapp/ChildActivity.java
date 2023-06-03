package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Weather display screen
 */
public class ChildActivity extends AppCompatActivity {

    // Declare view fields
    private RelativeLayout homeRL;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView iconIV, searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private String cityName;

    // Reference for the shared preference
    final String MYPREFS = "MyPreferences_002";

    // create a reference to the shared preferences object
    SharedPreferences mySharedPreferences;

    // obtain an editor to add data to my SharedPreferences object
    SharedPreferences.Editor myEditor;

    public ChildActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        // Used to make application fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Store views
        homeRL = findViewById(R.id.idRLHome);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        // create a reference & editor for the shared preferences object
        mySharedPreferences = getSharedPreferences(MYPREFS, 0);
        myEditor = mySharedPreferences.edit();

        // has a Preferences file been already created?
        if (mySharedPreferences != null
                && mySharedPreferences.contains("relativeLayout_")) {
            // object and key found, show all saved values
            applySavedPreferences();
        }

        // Use the getIntent method to store the Intent that started this Activity in a variable
        Intent intentThatStartedThisActivity = getIntent();

        // Create an if statement to check if this Intent has the extra we passed from MainActivity
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            // If the Intent contains the correct extra, retrieve the text
            String textEntered = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            // If the Intent contains the correct extra, use it to set the TextView text
            cityNameTV.setText(textEntered);
        }

        // Get weather info based on the chosen city
        getWeatherDetails((String) cityNameTV.getText());

        // Get weather info based on city entered in the search icon
        searchIV.setOnClickListener(view -> {
            // Retrieve the text from the cityEdt and store it in a variable
            String city = cityEdt.getText().toString();

            // Checks if the user has entered a city in the text box
            if (city.isEmpty()) {
                // If no city is entered, a message is displayed
                Toast.makeText(ChildActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
            } else {
                // Set city text view
                cityNameTV.setText(cityName);
                // Get weather info based on the chosen city
                getWeatherDetails(city);
            }
        });
    }


    /**
     * Fetches weather details for the chosen city and displays the data on the screen.
     *
     * @param city The city to get the weather details for.
     */
    public void getWeatherDetails(String city) {
        // Set city text view
        cityNameTV.setText(city);

        // Create the url to fetch weather data from
        String tempUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + getString(R.string.appid);

        // A request dispatch queue with a thread pool of dispatchers
        RequestQueue requestQueue = Volley.newRequestQueue(ChildActivity.this);

        // A canned request for retrieving the response body at a given URL as a String.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {
            try {
                // Pull the weather information from the received JSON
                JSONObject jsonResponse = new JSONObject(response);

                // Get city's weather description
                JSONArray jsonArrayWeather = jsonResponse.getJSONArray("weather");
                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                String description = jsonObjectWeather.getString("description");
                conditionTV.setText(description);

                // Get weather icon
                String icon = jsonObjectWeather.getString("icon");
                Picasso.get().load("https://openweathermap.org/img/wn/".concat(icon).concat("@2x.png")).into(iconIV);

                // Get city's temperature
                JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                double temp = jsonObjectMain.getDouble("temp") - 273.15;
                temperatureTV.setText((int) temp + "°C");

                // Get city's longitude and latitude
                JSONObject jsonObjectCoord = jsonResponse.getJSONObject("coord");
                String lon = jsonObjectCoord.getString("lon");
                String lat = jsonObjectCoord.getString("lat");

                // Fill the recycler view
                getRecyclerView(lon, lat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(ChildActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show());
        // Add request to queue
        requestQueue.add(stringRequest);
    }

    /**
     * Fill the recycler view with weather details on 3 hour time periods
     *
     * @param lon City's longitude
     * @param lat City's latitude
     */
    public void getRecyclerView(String lon, String lat) {
        // A request dispatch queue with a thread pool of dispatchers
        RequestQueue requestQueue = Volley.newRequestQueue(ChildActivity.this);

        // Create the url to fetch weather data from
        String tempUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + getString(R.string.appid);

        // A canned request for retrieving the response body at a given URL as a String.
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, tempUrl, response -> {
            // Clear the current data in the recycler view
            weatherRVModelArrayList.clear();
            try {
                // Pull the weather information from the received JSON
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray jsonArrayList = jsonResponse.getJSONArray("list");

                // For the length of data items in the list (amount of 3 hour periods)
                for (int i = 0; i < jsonArrayList.length(); i++) {
                    // Get the data for the specified hour
                    JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                    // Get time of the weather details
                    String time = jsonObjectList.getString("dt_txt");

                    // Get temperature
                    String temp = String.valueOf(Math.round(jsonObjectList.getJSONObject("main").getDouble("temp") - 273.15));

                    // Get basic weather description
                    JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                    String description = jsonObjectWeather.getString("main");

                    // Pass the data to the WeatherRVModel and add the data to the RV array list
                    weatherRVModelArrayList.add(new WeatherRVModel(time, temp, description));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // notify the adapter that the array list has changed
            weatherRVAdapter.notifyDataSetChanged();
        }, error -> Toast.makeText(ChildActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show());
        // Add request to queue
        requestQueue.add(stringRequest1);

    }

    /**
     * Inflate the menu resource (menu.xml).
     *
     * @param menu The menu resource.
     * @return Return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Response to menu click events by overriding.
     *
     * @param item The item clicked on the menu.
     * @return Return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Mark in the editor to remove all values from the preferences.
        myEditor.clear();

        // Run code based on clicked menu item
        switch (item.getItemId()) {
            case R.id.home:
                // Close ChildActivity and return to MainActivity
                finish();
                return true;

            case R.id.dark:
                // Stores preferences
                myEditor.putInt("relativeLayout_", R.color.black_shade_1);
                myEditor.putInt("textView_", R.color.light_shade_1);
                myEditor.putInt("textInputLayout_", R.color.light_shade_1);
                myEditor.putInt("textInputEditText_", R.color.light_shade_1);
                myEditor.putInt("imageView_", R.color.light_shade_1);
                myEditor.putInt("textView1_", R.color.light_shade_1);
                myEditor.putInt("textView2_", R.color.light_shade_1);
                myEditor.putInt("textView3_", R.color.light_shade_1);

                // Commit your preferences changes back from this Editor to the SharedPreferences object it is editing
                myEditor.commit();
                // Applies preference changes
                applySavedPreferences();

                return true;

            case R.id.light:
                // Stores preferences
                myEditor.putInt("relativeLayout_", R.color.light_shade_1);
                myEditor.putInt("textView_", R.color.black_shade_1);
                myEditor.putInt("textInputLayout_", R.color.black_shade_1);
                myEditor.putInt("textInputEditText_", R.color.black_shade_1);
                myEditor.putInt("imageView_", R.color.black_shade_1);
                myEditor.putInt("textView1_", R.color.black_shade_1);
                myEditor.putInt("textView2_", R.color.black_shade_1);
                myEditor.putInt("textView3_", R.color.black_shade_1);

                // Commit your preferences changes back from this Editor to the SharedPreferences object it is editing
                myEditor.commit();
                // Applies preference changes
                applySavedPreferences();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * This method runs when the activity is no longer in the foreground
     */
    @Override
    protected void onPause() {
        // warning: activity is on its last state of visibility!
        // It's on the edge of being killed! Better save all current
        // state data into Preference object (be quick!)
        myEditor.putString("DateLastExecution", new Date().toLocaleString());
        myEditor.commit();
        super.onPause();
    }

    /**
     * Extracts preferences and applies the changes to the appropriate views
     */
    public void applySavedPreferences() {
        // extract the <key-value> pairs, use default param for missing data
        int relativeLayout_ = mySharedPreferences.getInt("relativeLayout_", R.color.black_shade_1);
        int textView_ = mySharedPreferences.getInt("textView_", R.color.light_shade_1);
        int textInputLayout_ = mySharedPreferences.getInt("textInputLayout_", R.color.light_shade_1);
        int textInputEditText_ = mySharedPreferences.getInt("textInputEditText_", R.color.light_shade_1);
        int imageView_ = mySharedPreferences.getInt("imageView_", R.color.light_shade_1);
        int textView1_ = mySharedPreferences.getInt("textView1_", R.color.light_shade_1);
        int textView2_ = mySharedPreferences.getInt("textView2_", R.color.light_shade_1);
        int textView3_ = mySharedPreferences.getInt("textView3_", R.color.light_shade_1);

        // Declare views
        RelativeLayout relativeLayout = findViewById(R.id.idIVB);
        TextView textView = findViewById(R.id.idTVCityName);
        TextInputLayout textInputLayout = findViewById(R.id.idTILCity);
        TextInputEditText textInputEditText = findViewById(R.id.idEdtCity);
        ImageView imageView = findViewById(R.id.idIVSearch);
        TextView textView1 = findViewById(R.id.idTVTemperature);
        TextView textView2 = findViewById(R.id.idTVCondition);
        TextView textView3 = findViewById(R.id.idTVWeather);

        // Apply the preferences
        relativeLayout.setBackgroundResource(relativeLayout_);
        textView.setTextColor(getResources().getColor(textView_));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(textInputLayout_)));
        textInputEditText.setTextColor(getResources().getColor(textInputEditText_));
        imageView.setColorFilter(getResources().getColor(imageView_));
        textView1.setTextColor(getResources().getColor(textView1_));
        textView2.setTextColor(getResources().getColor(textView2_));
        textView3.setTextColor(getResources().getColor(textView3_));
    }

    /**
     * Allows you to add key/value pairs to the outState of the app.
     * Data retained under configuration changes.
     *
     * @param savedInstanceState A reference to a Bundle object.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("city", String.valueOf(cityNameTV.getText()));
    }

    /**
     * This method gets triggered when something was saved in onSaveInstanceState method.
     *
     * @param savedInstanceState A reference to a Bundle object.
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        String city = savedInstanceState.getString("city");
        // Restore weather details
        getWeatherDetails(city);
    }
}


