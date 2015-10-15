package com.coffeinum.jarvis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Aleksandr Sh on 14.10.2015.
 */
//rest-api ap key Z0cOnEG8xd2OUE58En3NIFkALxRDuxFTBD374KtL
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        usernameEditText = (EditText) findViewById(R.id.input_username);
        passwordEditText = (EditText) findViewById(R.id.input_password);
        // Set up the submit button click handler
        Button actionButton = (Button) findViewById(R.id.btn_login);

        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });


    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (username.length() == 0) {
            Toast.makeText(LoginActivity.this, "Blank username", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        try {
            String result = new LogInTask(username, password).execute().get();
            if (result.equals("OK")) {
                saveInPref(username,password);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (result.equals("Not Found")) {
                Toast.makeText(LoginActivity.this, "Error: wrong username or password", Toast.LENGTH_LONG)
                        .show();
                return;
            } else {
                Toast.makeText(LoginActivity.this, "Something goes wrong", Toast.LENGTH_LONG)
                        .show();
                return;
            }
        } catch (Exception e) {
            Log.d("debuglog", e.getMessage());
        }

    }

    private void saveInPref(String username, String password) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("username", username);
        ed.putString("password", password);
        ed.commit();

    }

    private class LogInTask extends AsyncTask<Void, Void, String> {
        private final String urlLink = "https://api.parse.com/1/login";
        private String parameters;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result, resultJson;

        LogInTask(String username, String password) {
            try {
                this.parameters = "?username=" + URLEncoder.encode(username, "UTF-8") + "&password=" +
                        URLEncoder.encode(password, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(urlLink + parameters);
                Log.d("debuglog", urlLink + parameters);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("X-Parse-REST-API-Key", "Z0cOnEG8xd2OUE58En3NIFkALxRDuxFTBD374KtL");
                urlConnection.setRequestProperty("X-Parse-Application-Id", "DuatBOrkhMVLZNV3Xy83vIeHHGzC9jmCPVrH6hR7");
                urlConnection.connect();
                Log.d("debuglog", urlConnection.getResponseMessage());
                result = urlConnection.getResponseMessage();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
                Log.d("debuglog", resultJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
