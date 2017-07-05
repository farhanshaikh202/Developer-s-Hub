package com.farhansoftware.developershub.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.MainScreenPagerAdapter;
import com.farhansoftware.developershub.adapters.MyProfilePagerAdapter;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.RobotoTextView;
import com.farhansoftware.developershub.custom.TAG;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.utils.Internet;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyProfile extends AppCompatActivity {

    private TabLayout tabs;
    private AppBarLayout appBarLayout;
    private RelativeLayout layout;
    private Toolbar toolbar;
    private SharedPreferences pref;
    private ViewPager pager;
    private TextView post;
    private TextView followers;
    private TextView following;
    private String userid,username,userpic;
    private String useremail;
    private Button followbtn,followingbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.my_profile_collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));


        post=(TextView)findViewById(R.id.my_profile_user_posts);
        followers=(TextView)findViewById(R.id.my_profile_user_followers);
        following=(TextView)findViewById(R.id.my_profile_user_following);

        followbtn=(Button)findViewById(R.id.my_profile_follow_btn);
        followingbtn=(Button)findViewById(R.id.my_profile_following_btn);

        tabs=(TabLayout)findViewById(R.id.my_profile_tabs);
        pager=(ViewPager)findViewById(R.id.my_profile_viewpager);

        pager.setAdapter(new MyProfilePagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);
        tabs.setupWithViewPager(pager);

        tabs.getTabAt(0).setIcon(R.drawable.my_profile_posts_icon);
        tabs.getTabAt(1).setIcon(R.drawable.my_profile_likes_icon);
        tabs.getTabAt(2).setIcon(R.drawable.my_profile_comments_icon);

        layout=(RelativeLayout)findViewById(R.id.my_profile_top);
        appBarLayout=(AppBarLayout) findViewById(R.id.my_profile_appbar);
        /*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed

                if (scrollRange + verticalOffset == 0) {
                    toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
                }else{
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                }

            }
        });*/


        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(getIntent().getStringExtra(Server.PARAM_USER_ID).equals(pref.getString(Server.PARAM_USER_ID,"0"))){
            //self
            userpic=pref.getString(Server.PARAM_USER_PHOTO,"");
            username=pref.getString(Server.PARAM_USERNAME,"");
            userid=pref.getString(Server.PARAM_USER_ID,"");
            useremail = pref.getString(Server.PARAM_EMAIL, "");
        }else {
            //others
            Intent in = getIntent();
            userid=  in.getStringExtra(Server.PARAM_USER_ID);
            username=in.getStringExtra(Server.PARAM_USERNAME);
            userpic=in.getStringExtra(Server.PARAM_USER_PHOTO);
            useremail="";
        }
        init();
        loadMyProfile();

        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFollowbtn();
                followUnfollow();
            }
        });

        followingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFollowbtn();
                followUnfollow();
            }
        });

        findViewById(R.id.my_profile_followers_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFollowers();
            }
        });

        findViewById(R.id.my_profile_following_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFollowing();
            }
        });

    }

    private void openFollowing(){
        Intent intent=new Intent(MyProfile.this,Following.class);
        intent.putExtra(Server.PARAM_USER_ID,getUserid());
        startActivity(intent);
    }

    private void openFollowers(){
        Intent intent=new Intent(MyProfile.this,Followers.class);
        intent.putExtra(Server.PARAM_USER_ID,getUserid());
        startActivity(intent);
    }
    private void showFollowbtn(){
        followbtn.setVisibility(View.VISIBLE);
    }

    private void showFollowingBtn(){
        followingbtn.setVisibility(View.VISIBLE);
    }
    private void toggleFollowbtn() {
        if(followbtn.getVisibility()==View.VISIBLE){
            followbtn.setVisibility(View.GONE);
            followingbtn.setVisibility(View.VISIBLE);
        }else{
            followbtn.setVisibility(View.VISIBLE);
            followingbtn.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getStringExtra(Server.PARAM_USER_ID).equals(pref.getString(Server.PARAM_USER_ID,"0"))){
            //self
            userpic=pref.getString(Server.PARAM_USER_PHOTO,"");
            username=pref.getString(Server.PARAM_USERNAME,"");
            userid=pref.getString(Server.PARAM_USER_ID,"");
            useremail = pref.getString(Server.PARAM_EMAIL, "");
        }else {
            //others
            Intent in = getIntent();
            userid=  in.getStringExtra(Server.PARAM_USER_ID);
            username=in.getStringExtra(Server.PARAM_USERNAME);
            userpic=in.getStringExtra(Server.PARAM_USER_PHOTO);
            useremail="";
        }
        init();
    }

    public String getUserid(){
        return userid;
    }
    private void init() {
        TextView pName = (TextView) findViewById(R.id.my_profile_user_name);
        TextView pEmail = (TextView) findViewById(R.id.my_profile_user_email);
        CircleImageView pPic = (CircleImageView) findViewById(R.id.my_profile_user_pic);

        Picasso.with(this).load(userpic).into(pPic);
        pName.setText(username);
        pEmail.setText(useremail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().getStringExtra(Server.PARAM_USER_ID).equals(pref.getString(Server.PARAM_USER_ID,"0")))
        getMenuInflater().inflate(R.menu.myprofile,menu);//self

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        else if(id==R.id.profile_logout){

            pref.edit().remove("loginskipped").apply();
            pref.edit().remove("islogin").apply();
            pref.edit().remove(Server.PARAM_USER_PHOTO).commit();
            pref.edit().remove(Server.PARAM_USER_ID).commit();
            pref.edit().remove(Server.PARAM_EMAIL).commit();
            pref.edit().remove(Server.PARAM_USERNAME).commit();
            finish();
            Intent in=new Intent(getApplicationContext(), MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
        }else if(id==R.id.profile_edit){
            Intent in = new Intent(MyProfile.this, EditProfile.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }


    void loadMyProfile(){

        if (Internet.isAvail(getApplicationContext())) {

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {

                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_USER_ID, userid)
                                .build();

                        if(pref.getBoolean("islogin",false)){
                            if(!getIntent().getStringExtra(Server.PARAM_USER_ID).equals(pref.getString(Server.PARAM_USER_ID,"0"))){
                                formBody = new FormBody.Builder()
                                        .add("liker",pref.getString(Server.PARAM_USER_ID,"0"))
                                        .add(Server.PARAM_USER_ID, userid)
                                        .build();
                            }
                        }
                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.USER_PROFILE_URL)
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(MyProfile.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        jsonResponse(res);
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
            Alert.show(MyProfile.this, "Please connect to Internet");
        }
    }

    private void jsonResponse(String res) {
        try {
            JSONArray array=new JSONArray(res);
            JSONObject obj = array.getJSONObject(0);
            post.setText(obj.getString("posts"));
            followers.setText(obj.getString("followers"));
            following.setText(obj.getString("following"));

            obj=array.getJSONObject(1);
            if(obj.getString("following").equals("yes")){
                showFollowingBtn();
            }else showFollowbtn();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void followUnfollow(){

        try {
            MultipartUploadRequest uploadRequest=new MultipartUploadRequest(getApplicationContext(),Server.SERVER_URL+Server.LIKE_PROFILE_URL)
                    .addParameter("userid",getUserid())
                    .addParameter("likerid",pref.getString(Server.PARAM_USER_ID,""))
                    .addParameter("likeprofile","")
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            Log.e("respo",serverResponse.getBodyAsString());
                            try {
                                JSONObject object=new JSONObject(serverResponse.getBodyAsString());
                                if(object.getString("success").equals("yes")){
                                    if(object.getString("follow").equals("yes")) Toast.show(getApplicationContext(),"Following");
                                    else Toast.show(getApplicationContext(),"Un-Followed");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    });
                    uploadRequest.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
