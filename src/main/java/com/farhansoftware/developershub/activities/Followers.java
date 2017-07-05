package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.FollowAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.utils.Internet;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Followers extends AppCompatActivity {

    private ListView followinglist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        followinglist = (ListView) findViewById(R.id.following_lv);
        setup();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);

    }

    private void setup() {
        if (Internet.isAvail(getApplicationContext())) {
            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("userid", getIntent().getStringExtra(Server.PARAM_USER_ID))
                                .build();


                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.FOLLOWERS_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(getApplicationContext(), "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        setAdapter(res);
                                    }
                                };
                                runOnUiThread(runnable1);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread th = new Thread(runnable1);
            th.start();
        } else {
            Alert.show(getApplicationContext(), "Please connect to Internet");
        }
    }

    private void setAdapter(String res) {
        Log.e("json", res);
        try {
            JSONArray array = new JSONArray(res);
            final FollowAdapter followAdapter = new FollowAdapter(getApplicationContext(), array);
            followinglist.setAdapter(followAdapter);
            followinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Intent in = new Intent(Followers.this, MyProfile.class);
                        in.putExtra(Server.PARAM_USER_ID, followAdapter.getUserId(position));
                        in.putExtra(Server.PARAM_USERNAME, followAdapter.getName(position));
                        in.putExtra(Server.PARAM_USER_PHOTO, followAdapter.getImg(position));
                        startActivity(in);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
