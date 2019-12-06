package com.example.intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        ArrayList<String> url = new ArrayList<String>();

        OkHttpClient client = new OkHttpClient();
        String Url;
        Url = "https://www.reddit.com/.json";
        Request req = new Request.Builder().url(Url).build();


        // We can't use blocking network calls in the UI thread,
        // so we need to create a new thread to handle the
        // network execution.

        // In production, you want to read
        // https://developer.android.com/guide/components/processes-and-threads
        // and use AsyncTask intead of Thread
        Thread t = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    Response response = client.newCall(req).execute();
                    String text = response.body().string();
                    Log.d("response", text);

                    JSONObject object = (JSONObject) new JSONTokener(text).nextValue();

                    JSONArray listings = object.getJSONObject("data").getJSONArray("children");

                    ArrayList<String> titles = new ArrayList<>(listings.length());
                    ArrayList<String> comments = new ArrayList<>(listings.length());

                    for (int i = 0; i < listings.length(); i++) {
                        JSONObject item = listings.getJSONObject(i);
                        titles.add(item.getJSONObject("data").getString("title"));
                        url.add(item.getJSONObject("data").getString("permalink"));
                    }

                    // We can't update UI on a different thread, so we need to send
                    // the processing back to the UI thread via runOnUiThread method
                    runOnUiThread(() -> {
                        String result = titles.stream().reduce("", (a, b) -> a += "\n" + b);
                        titles.add(result);

                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, titles);
                        listView.setAdapter(arrayAdapter);
                    });
                } catch (IOException | JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    });

                }
            }
        };


        t.start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object i = listView.getItemAtPosition(position);

                String Url = url.get(position);

                Intent intent = new Intent(MainActivity.this, FullNews.class);
                intent.putExtra("url", Url);
                startActivity(intent);
            }
        });

    };

}

