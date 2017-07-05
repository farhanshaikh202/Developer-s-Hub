package com.farhansoftware.developershub.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.activities.CategoryPosts;
import com.farhansoftware.developershub.activities.CategorySelection;
import com.farhansoftware.developershub.activities.Login;
import com.farhansoftware.developershub.activities.MainActivity;
import com.farhansoftware.developershub.adapters.CategoryAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.TAG;
import com.farhansoftware.developershub.utils.Internet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Farhan on 03-02-2017.
 *
 */

public class MainCategoryFragment extends Fragment {
    private GridView grid;
    private Animation fadein;
    private Animation fadeout;
    private ProgressBar prog;
    private CategoryAdapter adapt;
    private SwipeRefreshLayout mySwipeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fregment_main_category,container,false);
        grid=(GridView)view.findViewById(R.id.mainscreen_category_gridview);
        ViewCompat.setNestedScrollingEnabled(grid,true);

        mySwipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeRefresh.setRefreshing(true);
                adapt=null;
                setup();
            }
        });
        prog=(ProgressBar)view.findViewById(R.id.progressBar2);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent in=new Intent(getActivity(), CategoryPosts.class);
                    in.putExtra(Server.RETURN_CATEGORY_NAME,adapt.getName(position));
                    in.putExtra(Server.RETURN_CATEGORY_COLOR,adapt.getColor(position));
                    in.putExtra(Server.RETURN_CATEGORY_ID,adapt.getId(position));
                    in.putExtra(Server.RETURN_CATEGORY_ICON,adapt.getIcon(position));
                    startActivity(in);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        fadein= AnimationUtils.loadAnimation(getContext(),android.R.anim.fade_in);
        fadeout=AnimationUtils.loadAnimation(getContext(),android.R.anim.fade_out);

        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                prog.startAnimation(fadeout);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                prog.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setup();

        return view;
    }

    private void setup() {
        if (Internet.isAvail(getContext())) {

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_CATEGORY, "")
                                .build();


                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.CATEGORY_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(getActivity(), "Server Error :\n" + e.getMessage());
                                    }
                                };
                                getActivity().runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {

                                        setAdapter(res);
                                    }
                                };
                                getActivity().runOnUiThread(runnable1);
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
            Alert.show(getActivity(), "Please connect to Internet");
        }
    }

    private void setAdapter(String res) {
        try {
            JSONArray array=new JSONArray(res);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref.edit().putString("categoryjson",res).commit();

            adapt = new CategoryAdapter(getContext(), array);
            grid.setAdapter(adapt);
            grid.startAnimation(fadein);

            mySwipeRefresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,0,0,"Select Categories")
                .setIcon(R.drawable.ic_manage_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                startActivity(new Intent(getContext(),CategorySelection.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
