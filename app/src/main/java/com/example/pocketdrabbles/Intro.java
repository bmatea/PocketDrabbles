package com.example.pocketdrabbles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Intro extends AppCompatActivity {

    public static final String quote_url = "http://api.quotable.io/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(quote_url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200)
                {
                    try {
                        final TextView quote = findViewById(R.id.quote);
                        final TextView author = findViewById(R.id.author);

                        String api_quote = response.getString("content");
                        String api_author = response.getString("author");

                        quote.setText(api_quote);
                        author.setText(api_author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), StoryGrid.class);
                startActivity(intent);
            }
        }, 100); //4500
    }
}
