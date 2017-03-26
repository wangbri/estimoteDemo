package com.example.airport;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.estimote.sdk.EstimoteSDK.getApplicationContext;

/**
 * Created by Brian on 3/25/2017.
 */

public class JsonPostRequest extends AsyncTask<Void, Void, String> {

    @Override
    public String doInBackground(Void... voids) {
        try {
            String address = "http://server/postvalue";
            JSONObject json = new JSONObject();
            json.put("Doctor Alan", MyApplication.currentDateTimeString);
            String requestBody = json.toString();
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            writer.write(requestBody);
            writer.flush();
            writer.close();
            outputStream.close();

            InputStream inputStream;
            // get stream
            if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }
            // parse stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String temp, response = "";
            while ((temp = bufferedReader.readLine()) != null) {
                response += temp;
            }
            // put into JSONObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Content", response);
            jsonObject.put("Message", urlConnection.getResponseMessage());
            jsonObject.put("Length", urlConnection.getContentLength());
            jsonObject.put("Type", urlConnection.getContentType());
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        } catch (IOException | JSONException e) {
            System.out.println(e.toString());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Context context = getApplicationContext();
        CharSequence text = MyApplication.currentDateTimeString + ": Request sent!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
