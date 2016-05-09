package com.coffeinum.jarvis.model;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public abstract class JsonTask extends AsyncTask<Void, Void, String> {

    private final String url;
    private final String method;
    private JSONObject json;

    public JsonTask(String url, String method, JSONObject json) {
        this.url = url;
        this.method = method;
        this.json = json;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(Void... params) {
        try {
            HttpURLConnection connection = createHttpURLConnection(method, url);
            String jsonObject = convertToString(json);
            if (!jsonObject.isEmpty()) {
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream ());
                wr.writeBytes (jsonObject);
                wr.flush ();
                wr.close ();
            }
            connection.connect();

            final int statusCode = connection.getResponseCode();
            if (statusCode == (HttpURLConnection.HTTP_OK) || statusCode == (HttpURLConnection.HTTP_CREATED)) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                catch (IOException ignored) {}
                return parseJson(sb.toString());
            }
        } catch (IOException e) {
        }
        return "";
    }

    protected String convertToString(JSONObject json) {
        if (json != null) {
            return json.toString();
        }
        return "";
    };

    private HttpURLConnection createHttpURLConnection(String method, String url) throws IOException {
        URL requestURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
        connection.addRequestProperty("X-Parse-REST-API-Key", "HU7HusntdCaKWT1MN0gQArhNhKGswDoaY4XpuQbO");
        connection.addRequestProperty("X-Parse-Application-Id", "Aq4KBWY3OM21ILq2BSITvUOSgRyioYOSrQge04AO");
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        return connection;
    }

    protected abstract String parseJson(String s);

    @Override
    protected abstract void onPostExecute(final String result);

    @Override
    protected abstract void onCancelled();
}