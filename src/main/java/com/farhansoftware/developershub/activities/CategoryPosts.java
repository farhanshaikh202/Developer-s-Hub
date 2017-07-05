package com.farhansoftware.developershub.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.MainFeedAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.interfaces.OnPostItemViewClickListener;
import com.farhansoftware.developershub.models.PostModel;
import com.farhansoftware.developershub.utils.Internet;
import com.farhansoftware.developershub.utils.MySounds;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryPosts extends AppCompatActivity {

    private Bitmap micon;
    ListView lv;
    ArrayList<PostModel> arrayList;
    MainFeedAdapter adapter;
    private boolean flag_loading = false, no_more_result = false;
    private String cid;
    private Animation fadein;
    private Animation fadeout;
    private ProgressBar prog;
    private SwipeRefreshLayout mySwipeRefresh;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_posts);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent in=getIntent();
        String cname=in.getStringExtra(Server.RETURN_CATEGORY_NAME);
        String ccolor=in.getStringExtra(Server.RETURN_CATEGORY_COLOR);
        cid=in.getStringExtra(Server.RETURN_CATEGORY_ID);
        String cicon=in.getStringExtra(Server.RETURN_CATEGORY_ICON);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(ccolor)));
        /*try {
            micon=Picasso.with(this).load(cicon).get();
            getSupportActionBar().setIcon(new BitmapDrawable(micon));
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        lv = (ListView) findViewById(R.id.category_post_listview);

        mySwipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
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
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (flag_loading == false && !no_more_result) {
                        flag_loading = true;
                        loadPosts();
                    }
                }
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("list", " called click");
                PostModel ob = adapter.getArrayList().get(position);
                Intent i = new Intent(CategoryPosts.this, ViewPost.class);
                i.putExtra("post", (Serializable) ob);
                startActivity(i);
            }
        });

        prog=(ProgressBar)findViewById(R.id.progressBar2);
        fadein= AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeout=AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_posts,menu);
        return super.onCreateOptionsMenu(menu);
    }

    String sortby="",asc="";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
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
    int limit = -1;

    private void loadPosts() {
        if (Internet.isAvail(getApplicationContext())) {


            final ProgressBar bar = new ProgressBar(CategoryPosts.this);
            lv.addFooterView(bar);

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        limit++;
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_CATEGORY, cid)
                                .add("lastpost", String.valueOf(limit))
                                .add(sortby,"")
                                .add(asc,"")
                                .build();


                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.TRENDING_POST_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(CategoryPosts.this, "Server Error :\n" + e.getMessage());
                                        lv.removeFooterView(bar);
                                    }
                                };
                                runOnUiThread(runnable1);
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
            Alert.show(CategoryPosts.this, "Please connect to Internet");
        }
    }

    private void setAdapter(String res) {
        try {
            lv.setAnimation(fadein);
            JSONArray array = new JSONArray(res);
            if (array.length() == 0) no_more_result = true;
            arrayList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PostModel model = new PostModel(
                        obj.getString(Server.RETURN_POST_ID),
                        obj.getString(Server.RETURN_USER_ID),
                        obj.getString(Server.RETURN_USER_NAME),
                        obj.getString(Server.RETURN_POST_TYPE),
                        obj.getString(Server.RETURN_CATEGORY_ID),
                        obj.getString(Server.RETURN_POST_TITLE),
                        obj.getString(Server.RETURN_POST_DECRIPTION),
                        obj.getString(Server.RETURN_POST_DESCRIPTION_URL),
                        obj.getString(Server.RETURN_POST_DOWNLOAD_URL),
                        obj.getString(Server.RETURN_POST_DONLOADS),
                        obj.getString(Server.RETURN_USER_PIC),
                        obj.getString(Server.RETURN_USER_PROFILE_LIKES),
                        obj.getString(Server.RETURN_POST_LIKES),
                        obj.getString(Server.RETURN_POST_COMMENTS),
                        obj.getString(Server.RETURN_POST_RATING),
                        Server.SERVER_URL + obj.getString(Server.RETURN_POST_IMAGE),
                        obj.getString(Server.RETURN_TIME),
                        obj.getString(Server.RETURN_CATEGORY_NAME),
                        obj.getString(Server.RETURN_CATEGORY_ICON),
                        obj.getString(Server.RETURN_CATEGORY_COLOR)
                );
                arrayList.add(model);
            }

            if (adapter == null) {
                adapter = new MainFeedAdapter(CategoryPosts.this, arrayList);
                adapter.setClickListener(new OnPostItemViewClickListener() {
                    @Override
                    public void onClick(View v, PostModel model) {
                        onPostItemClick(v, model);
                    }
                });
                lv.setAdapter(adapter);
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


    private void onPostItemClick(final View view, PostModel model) {


        switch (view.getId()) {
            case R.id.mainscreen_listview_item__usrimg_imageview:
                viewProfile(model);
                break;
            case R.id.mainscreen_listview_item_like_text:
                likePost(view, model);
                break;
            case R.id.mainscreen_listview_item_menu_imgbtn:
                postMenu(view, model);
                break;
            case R.id.mainscreen_listview_item_usernm_textview:
                viewProfile(model);
                break;
            case R.id.mainscreen_listview_item_subtext:
                categoryOpen(view, model);
                break;
        }
    }

    private void viewProfile(PostModel model) {

        Intent in = new Intent(CategoryPosts.this, MyProfile.class);
        in.putExtra(Server.PARAM_USER_ID,model.getUserid());
        in.putExtra(Server.PARAM_USERNAME,model.getUsername());
        in.putExtra(Server.PARAM_USER_PHOTO,model.getUserPhoto());
        startActivity(in);
    }
    private void postMenu(View view, final PostModel model) {

        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(0, 0, 0, "Share");
        if(pref.getBoolean("islogin",false)){
            if(model.getUserid().equals(pref.getString(Server.PARAM_USER_ID,"0"))){
                popup.getMenu().add(0, 1, 1, "Delete");
            }
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        //share
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, model.getPosttitle());
                        i.putExtra(Intent.EXTRA_TEXT   ,model.getPosttitle()+"\n"+Server.SHARE_POST_URL+model.getPostid());
                        startActivity(Intent.createChooser(i, "Share"));
                        break;
                    case 1:
                        AlertDialog.Builder builder=new AlertDialog.Builder(CategoryPosts.this)
                                .setMessage("Sure ?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePost(model);
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.create().show();
                        break;
                }
                return false;
            }
        });

        popup.show();


    }

    private void deletePost(final PostModel model) {
        final Loading load = new Loading();
        try {
            MultipartUploadRequest request=new MultipartUploadRequest(CategoryPosts.this,Server.SERVER_URL+Server.DELETE_POST_URL);
            request.addParameter("deletepost","")
                    .addParameter(Server.RETURN_POST_ID,model.getPostid())
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                            load.hide();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            try {
                                JSONObject object=new JSONObject(serverResponse.getBodyAsString());
                                if(object.getString("success").equals("yes")){
                                    Toast.show(CategoryPosts.this,"Deleted");
                                    adapter.getArrayList().remove(model);
                                    adapter.notifyDataSetChanged();
                                }else Toast.show(CategoryPosts.this,"Error");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            load.hide();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            load.hide();
                        }
                    });
            load.show(CategoryPosts.this,"Deleteing post");
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void categoryOpen(View view, PostModel model) {
        Intent in = new Intent(CategoryPosts.this, CategoryPosts.class);
        in.putExtra(Server.RETURN_CATEGORY_NAME, model.getCategory_name());
        in.putExtra(Server.RETURN_CATEGORY_COLOR, model.getCategory_color());
        in.putExtra(Server.RETURN_CATEGORY_ID, model.getCategoryid());
        in.putExtra(Server.RETURN_CATEGORY_ICON, model.getCategory_icon());
        startActivity(in);
    }

    private void likePost(final View view, final PostModel model) {


        if (pref.getBoolean("islogin", false)) {
            if (Internet.isAvail(getApplicationContext())) {
                MySounds.click(getApplicationContext());
                Runnable runnable1 = new Runnable() {
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody formBody = new FormBody.Builder()
                                    .add(Server.PARAM_LIKE_POST, "")
                                    .add(Server.PARAM_USER_ID, pref.getString(Server.RETURN_USER_ID, "0"))
                                    .add(Server.RETURN_POST_ID, model.getPostid())
                                    .build();


                            final Request request = new Request.Builder()
                                    .url(Server.SERVER_URL + Server.LIKE_POST_URL)
                                    .post(formBody)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, final IOException e) {
                                    Runnable runnable1 = new Runnable() {
                                        public void run() {
                                            Alert.show(CategoryPosts.this, "Server Error :\n" + e.getMessage());
                                        }
                                    };
                                    CategoryPosts.this.runOnUiThread(runnable1);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String res = response.body().string();
                                    try {
                                        JSONObject object = new JSONObject(res);
                                        if (object.getString(Server.PARAM_SUCCESS).equals("yes")) {
                                            if (object.getString("like").equals("inc")) {
                                                model.increamentLike();
                                                Runnable runnable1 = new Runnable() {
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                        Toast.show(getApplicationContext(), "Liked Post");
                                                    }
                                                };
                                                CategoryPosts.this.runOnUiThread(runnable1);
                                            } else {
                                                model.decreamentLike();
                                                Runnable runnable1 = new Runnable() {
                                                    public void run() {
                                                        adapter.notifyDataSetChanged();
                                                        //((TextView)view).setText(model.getPostLikes());
                                                        Toast.show(getApplicationContext(), "Un-Liked");
                                                    }
                                                };
                                                CategoryPosts.this.runOnUiThread(runnable1);
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

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
                Alert.show(CategoryPosts.this, "Please connect to Internet");
            }
        } else {
            startActivity(new Intent(CategoryPosts.this, Startup.class));
        }
    }
}
