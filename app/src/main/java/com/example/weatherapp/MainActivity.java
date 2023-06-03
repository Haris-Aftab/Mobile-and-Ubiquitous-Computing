package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

/**
 * Home screen
 */
public class MainActivity extends AppCompatActivity {

    // Declare view fields
    private ImageView searchIV;
    private TextInputEditText cityEdt;

    // Reference for the shared preference
    final String MYPREFS = "MyPreferences_001";

    // create a reference to the shared preferences object
    SharedPreferences mySharedPreferences;

    // obtain an editor to add data to my SharedPreferences object
    SharedPreferences.Editor myEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Used to make application fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Store views
        searchIV = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);


        // create a reference & editor for the shared preferences object
        mySharedPreferences = getSharedPreferences(MYPREFS, 0);
        myEditor = mySharedPreferences.edit();

        // has a Preferences file been already created?
        if (mySharedPreferences != null
                && mySharedPreferences.contains("relativeLayout_")) {
            // object and key found, show all saved values
            applySavedPreferences();
        }


        // Runs when the searchIV image is clicked
        searchIV.setOnClickListener(new View.OnClickListener() {
            /**
             * The onClick method is triggered when this image (searchIV) is clicked.
             * @param v The view that is clicked. In this case, it's searchIV.
             */
            @Override
            public void onClick(View v) {
                // Retrieve the text from the cityEdt and store it in a variable
                String city = cityEdt.getText().toString();

                // Checks if the user has entered a city in the text box
                if (city.isEmpty()) {
                    // If no city is entered, a message is displayed
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                } else {
                    // Create the Explicit Intent that will start the ChildActivity.
                    Intent startChildActivityIntent = new Intent(MainActivity.this, ChildActivity.class);

                    // Use the putExtra method to put the String from the cityEdt in the Intent
                    startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, city);

                    // Start ChildActivity
                    startActivity(startChildActivityIntent);
                }
            }
        });


    }

    /**
     * This method is called when the Open Website button is clicked. It will open the website
     * specified by the URL represented by the variable urlAsString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    public void onClickOpenWebpageButton(View v) {
        // The url link
        String urlAsString = "https://www.bbc.co.uk/weather";
        // Create implicit intent
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAsString));
        // Start intent
        startActivity(intent);
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

        // preferences set based on which menu item was clicked
        switch (item.getItemId()) {
            case R.id.dark:
                // Stores preferences
                myEditor.putInt("relativeLayout_", R.color.black_shade_1);
                myEditor.putInt("textView_", R.color.light_shade_1);
                myEditor.putInt("textInputLayout_", R.color.light_shade_1);
                myEditor.putInt("textInputEditText_", R.color.light_shade_1);
                myEditor.putInt("imageView_", R.color.light_shade_1);

                // Commit your preferences changes back from this Editor to the SharedPreferences object it is editing.
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

                // Commit your preferences changes back from this Editor to the SharedPreferences object it is editing.
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

        // Declare views
        RelativeLayout relativeLayout = findViewById(R.id.idIVB);
        TextView textView = findViewById(R.id.idTVCityName);
        TextInputLayout textInputLayout = findViewById(R.id.idTILCity);
        TextInputEditText textInputEditText = findViewById(R.id.idEdtCity);
        ImageView imageView = findViewById(R.id.idIVSearch);

        // Apply the preferences
        relativeLayout.setBackgroundResource(relativeLayout_);
        textView.setTextColor(getResources().getColor(textView_));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(textInputLayout_)));
        textInputEditText.setTextColor(getResources().getColor(textInputEditText_));
        imageView.setColorFilter(getResources().getColor(imageView_));
    }
}