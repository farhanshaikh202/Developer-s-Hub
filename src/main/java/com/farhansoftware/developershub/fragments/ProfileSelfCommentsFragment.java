package com.farhansoftware.developershub.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.activities.CategoryPosts;
import com.farhansoftware.developershub.activities.MyProfile;
import com.farhansoftware.developershub.activities.Startup;
import com.farhansoftware.developershub.activities.ViewPost;
import com.farhansoftware.developershub.adapters.MainFeedAdapter;
import com.farhansoftware.developershub.adapters.MyProfileCommentAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.interfaces.OnPostItemViewClickListener;
import com.farhansoftware.developershub.models.PostModel;
import com.farhansoftware.developershub.utils.Internet;
import com.farhansoftware.developershub.utils.MySounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Farhan on 10-03-2017.
 */


public class ProfileSelfCommentsFragment extends Fragment {

    ListView lv;
    ArrayList<JSONObject> arrayList;
    MyProfileCommentAdapter adapter;
    SharedPreferences pref;
    int limit = -1;
    String categories = "";
    private boolean flag_loading = false, no_more_result = false;
    private Animation fadein;
    private Animation fadeout;
    private ProgressBar prog;
    private SwipeRefreshLayout mySwipeRefresh;
    private String userid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_feed, container, false);
        MyProfile profile=(MyProfile)getActivity();
        userid=profile.getUserid();
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        lv = (ListView) view.findViewById(R.id.mainscreen_listview);
        prog = (ProgressBar) view.findViewById(R.id.progressBar2);

        mySwipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        mySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeRefresh.setRefreshing(true);
                limit=-1;
                flag_loading=false;
                adapter=null;
                loadPosts();
            }
        });
        prog = (ProgressBar) view.findViewById(R.id.progressBar2);
        ViewCompat.setNestedScrollingEnabled(lv, true);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    String pid=adapter.getPostId(position);
                    Log.e("pid",pid);
                    Intent i = new Intent(getActivity(), ViewPost.class);
                    i.putExtra("post_id", pid);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        fadein = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        fadeout = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);

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
        arrayList = new ArrayList<>();
        loadPosts();


        return view;
    }




    private void loadPosts() {
        if (Internet.isAvail(getContext())) {
            final ProgressBar bar = new ProgressBar(getContext());
            lv.addFooterView(bar);

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        limit++;
                         {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody formBody = new FormBody.Builder()
                                    .add(Server.PARAM_USER_ID,userid)
                                    .build();


                            Request request = new Request.Builder()
                                    .url(Server.SERVER_URL + Server.USER_COMMENTED_POSTS)
                                    .post(formBody)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, final IOException e) {
                                    Runnable runnable1 = new Runnable() {
                                        public void run() {
                                            Alert.show(getActivity(), "Server Error :\n" + e.getMessage());
                                            lv.removeFooterView(bar);
                                        }
                                    };
                                    getActivity().runOnUiThread(runnable1);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String res = response.body().string();
                                    Runnable runnable1 = new Runnable() {
                                        public void run() {
                                            lv.removeFooterView(bar);
                                            setAdapter(res);
                                        }
                                    };
                                    getActivity().runOnUiThread(runnable1);
                                }
                            });
                        }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_posts,menu);
    }

    String sortby="",asc="";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sort_likes:
                sortby="bylike";
                reload();
                return true;
            case R.id.menu_sort_rating:
                sortby="byrating";
                reload();
                return true;
            case R.id.menu_sort_time:
                sortby="bytime";
                reload();
                return true;
            case R.id.menu_sort_asc:
                if(!item.isChecked()){
                    asc="asc";
                    item.setChecked(true);
                }
                else{
                    asc="";
                    item.setChecked(false);
                }
                reload();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reload(){
        mySwipeRefresh.setRefreshing(true);
        limit=-1;
        flag_loading=false;
        adapter=null;
        loadPosts();
    }
    private void setAdapter(String res) {
        try {
            Log.e("json str",res);
            JSONArray array = new JSONArray(res);
            if (array.length() == 0) no_more_result = true;
            arrayList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                arrayList.add(obj);
            }

            if (adapter == null) {
                adapter = new MyProfileCommentAdapter(getContext(), arrayList);
                lv.setAdapter(adapter);
                lv.startAnimation(fadein);
            } else {
                adapter.getArrayList().addAll(arrayList);
                adapter.notifyDataSetChanged();
            }

            mySwipeRefresh.setRefreshing(false);
            limit += 9;
            flag_loading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
