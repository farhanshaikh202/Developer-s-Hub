package com.farhansoftware.developershub.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.custom.Alert;
import com.farhansoftware.developershub.custom.Loading;
import com.farhansoftware.developershub.custom.Toast;
import com.farhansoftware.developershub.models.PostModel;
import com.farhansoftware.developershub.utils.BlurBuilder;
import com.farhansoftware.developershub.utils.Internet;
import com.farhansoftware.developershub.utils.MySounds;
import com.farhansoftware.developershub.utils.MyUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewPost extends AppCompatActivity {

    private Toolbar toobar;
    private PostModel model;
    private TextView ptitle;
    private TextView posterName;
    private TextView postTime;
    private WebView postDescription;
    private TextView postLikes, postLikes2;
    private TextView postComments;
    private TextView postRateSmall;
    private TextView postRateBig;
    private TextView postRaters;
    private TextView selfName;
    private TextView selfRateTime;
    private TextView rtext5,rtext4,rtext3,rtext2,rtext1;
    private CircleImageView posterImg, selfImg;
    private RatingBar selfRatingBar, postRateBarSmall;
    private View rate1, rate2, rate3, rate4, rate5;
    private EditText commentBox;
    private Button saveComnt;
    private ImageView topImage;
    private ImageButton likeBtn;
    private LinearLayout screenshotRoot, selfCommnetRoot, topCommentsRoot;
    private Animation fadein;
    private Animation fadeout;
    private ProgressBar prog;
    private Button morebtn,downloadbtn;
    private SharedPreferences pref;
    private CollapsingToolbarLayout colaps;
    private Uri data;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        toobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toobar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        colaps = (CollapsingToolbarLayout) findViewById(R.id.post_collapsing);
        colaps.setCollapsedTitleTextColor(Color.WHITE);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_material_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    // Collapsed
                    //toobar.setBackgroundColor(Color.parseColor("#50000000"));
                } else {
                    // Not collapsed
                }
            }
        });

        screenshotRoot = (LinearLayout) findViewById(R.id.post_content_screenshot_root);
        selfCommnetRoot = (LinearLayout) findViewById(R.id.post_content_self_comment_root);
        topCommentsRoot = (LinearLayout) findViewById(R.id.post_content_top_comments_root);

        ptitle = (TextView) findViewById(R.id.post_content_posttitle_tv);
        posterName = (TextView) findViewById(R.id.post_content_usernm_textview);
        postTime = (TextView) findViewById(R.id.post_content_subtext);
        postDescription = (WebView) findViewById(R.id.post_content_description_wv);
        postLikes = (TextView) findViewById(R.id.post_content_like_text);
        postLikes2 = (TextView) findViewById(R.id.post_content_like_text2);
        postComments = (TextView) findViewById(R.id.post_content_comments_txt);
        postRateSmall = (TextView) findViewById(R.id.post_content_rating_text);
        postRateBig = (TextView) findViewById(R.id.post_content_ratingbig);
        postRaters = (TextView) findViewById(R.id.post_content_total_raters);

        selfName = (TextView) findViewById(R.id.post_content_selfname_textview);
        selfRateTime = (TextView) findViewById(R.id.post_content_selft_subtext);

        selfRatingBar = (RatingBar) findViewById(R.id.post_content_rating_bar_big);
        postRateBarSmall = (RatingBar) findViewById(R.id.post_content_rating_bar_small);

        posterImg = (CircleImageView) findViewById(R.id.post_content__usrimg_imageview);
        selfImg = (CircleImageView) findViewById(R.id.post_content_self_imageview);

        commentBox = (EditText) findViewById(R.id.post_content_comment_box);
        saveComnt = (Button) findViewById(R.id.post_content_comment_submit);

        topImage = (ImageView) findViewById(R.id.post_content_top_image);
        likeBtn = (ImageButton) findViewById(R.id.post_content_Like_btn);

        // Create a system to run the physics loop for a set of springs.
        SpringSystem springSystem = SpringSystem.create();

        // Add a spring to the system.
        final Spring spring = springSystem.createSpring();
        SpringConfig config = new SpringConfig(800, 10);
        spring.setSpringConfig(config);
        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f + (value * 0.7f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    likeBtn.setScaleX(scale);
                    likeBtn.setScaleY(scale);
                }
            }
        });
        likeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        spring.setEndValue(1f);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        likePost();
                        likeBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                spring.setEndValue(0f);
                            }
                        },10);

                        return true;
                }
                return false;
            }
        });

        morebtn = (Button) findViewById(R.id.more_btn);
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams lay = postDescription.getLayoutParams();
                lay.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                postDescription.setLayoutParams(lay);
                v.setVisibility(View.GONE);
            }
        });

        downloadbtn=(Button)findViewById(R.id.download_btn);
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(model.getDownload_url()));
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        rate1 = (View) findViewById(R.id.post_content_rate_1);
        rate2 = (View) findViewById(R.id.post_content_rate_2);
        rate3 = (View) findViewById(R.id.post_content_rate_3);
        rate4 = (View) findViewById(R.id.post_content_rate_4);
        rate5 = (View) findViewById(R.id.post_content_rate_5);

        rtext1=(TextView)findViewById(R.id.post_content_rate_txt1);
        rtext2=(TextView)findViewById(R.id.post_content_rate_txt2);
        rtext3=(TextView)findViewById(R.id.post_content_rate_txt3);
        rtext4=(TextView)findViewById(R.id.post_content_rate_txt4);
        rtext5=(TextView)findViewById(R.id.post_content_rate_txt5);


        pref = PreferenceManager.getDefaultSharedPreferences(this);


        saveComnt.setEnabled(false);
        saveComnt.setVisibility(View.GONE);
        commentBox.setVisibility(View.GONE);
        if (!pref.getBoolean("islogin", false)) {
            selfImg.setVisibility(View.GONE);
            selfName.setVisibility(View.GONE);

        } else {
            Picasso.with(this).load(pref.getString(Server.PARAM_USER_PHOTO, "")).into(selfImg);
            selfName.setText(pref.getString(Server.PARAM_USERNAME, ""));
        }
        selfRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!pref.getBoolean("islogin", false)) {
                    startActivity(new Intent(ViewPost.this, Startup.class));
                } else {
                    if (rating != 0) {
                        saveComnt.setEnabled(true);
                        saveComnt.setVisibility(View.VISIBLE);
                        commentBox.setVisibility(View.VISIBLE);
                        commentBox.setEnabled(true);
                    }else {
                        saveComnt.setEnabled(false);
                        saveComnt.setVisibility(View.GONE);
                        commentBox.setVisibility(View.GONE);
                    }
                }
            }
        });

        saveComnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMyComment();

            }
        });


        Intent intent = getIntent();
        data = intent.getData();
        if(data!=null){
            model=new PostModel();
            model.setPostId(data.getQueryParameter("id"));
        }
        else if(getIntent().hasExtra("post")){
            model = (PostModel) getIntent().getSerializableExtra("post");//// TODO: 04-04-2017
            init();
        }else{
            model=new PostModel();
            model.setPostId(getIntent().getStringExtra("post_id"));
        }
        loadPost();
        loadImages();
        loadComments();

    }

    private void postMyComment() {
        if (Internet.isAvail(getApplicationContext())) {
            MySounds.click(getApplicationContext());
            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {
                        Log.e("rating",String.valueOf((int)selfRatingBar.getRating()));
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("postcomment", "")
                                .add("userid", pref.getString(Server.RETURN_USER_ID, "0"))
                                .add(Server.PARAM_POST_ID, model.getPostid())
                                .add("rating", String.valueOf((int)selfRatingBar.getRating()))
                                .add("comment", commentBox.getText().toString())
                                .build();


                        final Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.COMMENT_POST_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ViewPost.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Log.e("post comment",res);
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        try {
                                            final JSONObject object = new JSONObject(res);
                                            if (object.getString(Server.PARAM_SUCCESS).equals("yes")) {
                                                selfRateTime.setText(MyUtils.dateFormat(new Date()));
                                                commentBox.setEnabled(false);
                                                saveComnt.setEnabled(false);
                                                saveComnt.setVisibility(View.GONE);
                                                loadComments();
                                                loadPost();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
            Alert.show(ViewPost.this, "Please connect to Internet");
        }
    }


    private void likePost() {


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
                                            Alert.show(ViewPost.this, "Server Error :\n" + e.getMessage());
                                        }
                                    };
                                    runOnUiThread(runnable1);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String res = response.body().string();
                                    try {
                                        JSONObject object = new JSONObject(res);
                                        if (object.getString(Server.PARAM_SUCCESS).equals("yes")) {
                                            if (object.getString("like").equals("inc")) {
                                                Runnable runnable1 = new Runnable() {
                                                    public void run() {
                                                        //liked
                                                        model.increamentLike();
                                                        isLiked = true;
                                                        likePost2();
                                                    }
                                                };
                                                runOnUiThread(runnable1);
                                            } else {
                                                Runnable runnable1 = new Runnable() {
                                                    public void run() {
                                                        model.decreamentLike();
                                                        isLiked = false;
                                                        likePost2();
                                                    }
                                                };
                                                runOnUiThread(runnable1);
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
                Alert.show(ViewPost.this, "Please connect to Internet");
            }
        } else {
            startActivity(new Intent(ViewPost.this, Startup.class));
        }
    }

    @Override
    protected void onStart() {//works enter replace first
        overridePendingTransition(R.anim.slide_in_right, 0);
        super.onStart();
    }


    boolean isLiked = false;

    private void likePost2() {
        if (isLiked) {
            likeBtn.setImageDrawable(getDrawable(R.drawable.ic_like_50dp));
        } else {
            likeBtn.setImageDrawable(getDrawable(R.drawable.ic_like_border_50dp));
        }
        postLikes.setText(" " + model.getPostLikes());
        postLikes2.setText(" " + model.getPostLikes());
    }


    private void init() {
        colaps.setStatusBarScrimColor(Color.parseColor(model.getCategory_color()));
        ptitle.setText(model.getPosttitle());
        getSupportActionBar().setTitle(model.getPosttitle());
        colaps.setTitle(model.getPosttitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(model.getCategory_color()));
        }
        Picasso.with(this).load(model.getUserPhoto()).into(posterImg);
        if (model.getPostImage().toLowerCase().endsWith("null")) {

            Picasso.with(this).load(model.getCategory_icon()).placeholder(R.drawable.item_bg).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    Bitmap blur = BlurBuilder.blur(ViewPost.this, bitmap);//Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    topImage.setBackground(new BitmapDrawable(blur));
                    topImage.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Picasso.with(this).load(model.getPostImage()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Bitmap blur = BlurBuilder.blur(ViewPost.this, bitmap);//Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                    topImage.setBackground(new BitmapDrawable(blur));
                    topImage.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }


        //topImage.setBackgroundColor(Color.parseColor(model.getCategory_color()));
        ImageView im = new ImageView(this);
        im.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));


        if (model.getPostImage().toLowerCase().endsWith("null")) {
            Picasso.with(this).load(model.getCategory_icon()).placeholder(R.drawable.item_bg).into(im);
        } else {
            Picasso.with(this).load(model.getPostImage()).into(im);
        }
        screenshotRoot.addView(im);

        posterName.setText(Html.fromHtml("<b>" + model.getUsername() + "</b>  |  " + "<font color='red'>❤ " + model.getProfileLikes() + "</font>"));

        postTime.setText(Html.fromHtml("<font color='" + model.getCategory_color() + "'>" + model.getCategory_name() + "</font> | Posted on " + MyUtils.dateFormat(model.getTime())));

        if (!model.getPostdescription().equals("null"))
            postDescription.loadData(model.getPostdescription(), null, "UTF-8");
        else
            postDescription.loadUrl(Server.SERVER_URL + "post_description/" + model.getPostDescriptionUrl());

        postLikes.setText(" " + model.getPostLikes());
        postLikes2.setText(" " + model.getPostLikes());
        postComments.setText(" " + model.getPostComments());
        String rates = model.getPostRate();
        if (rates.equals("null")) rates = "0.0";

        postRateBig.setText(rates);
        postRateSmall.setText(" " + rates);
        postRateBarSmall.setRating(Float.parseFloat(rates));

        postRaters.setText(model.getPostComments() + " ");

        if (model.getDownload_url().isEmpty()){
            downloadbtn.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        menu.clear();
        menu.add(0, 0, 0, "Share");
        if (model!=null){
            if(model.getUserid()!=null) {
                if (pref.getBoolean("islogin", false)) {
                    if (model.getUserid().equals(pref.getString(Server.PARAM_USER_ID, "0"))) {
                        menu.add(0, 1, 1, "Delete");
                    }
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case 0:
                //share
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, model.getPosttitle());
                i.putExtra(Intent.EXTRA_TEXT   ,model.getPosttitle()+"\n"+Server.SHARE_POST_URL+model.getPostid());
                startActivity(Intent.createChooser(i, "Share"));
                break;
            case 1:
                //delete
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewPost.this)
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

        return super.onOptionsItemSelected(item);
    }

    private void deletePost(final PostModel model) {
        final Loading load = new Loading();
        try {
            MultipartUploadRequest request=new MultipartUploadRequest(getApplicationContext(),Server.SERVER_URL+Server.DELETE_POST_URL);
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
                                    Toast.show(getApplicationContext(),"Deleted");
                                    finish();
                                }else Toast.show(getApplicationContext(),"Error");
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
            load.show(ViewPost.this,"Deleteing post");
            request.startUpload();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loadPost() {
        if (Internet.isAvail(this)) {

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_POST_ID, model.getPostid())
                                .build();

                        if(pref.getBoolean("islogin",false)){
                            formBody = new FormBody.Builder()
                                    .add("myid",pref.getString(Server.RETURN_USER_ID,""))
                                    .add(Server.PARAM_POST_ID, model.getPostid())
                                    .build();
                        }

                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.VIEW_POST_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ViewPost.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        jsonDecode(res);
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
            Alert.show(ViewPost.this, "Please connect to Internet");
        }
    }

    private void jsonDecode(String res) {
        Log.d("json", res);
        try {
            JSONArray array = new JSONArray(res);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                model = new PostModel(
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
                if(getIntent().hasExtra("post_id")|| data!=null)init();
                postComments.setText(" " + model.getPostComments());
                String rates = model.getPostRate();
                if (rates.equals("null")) rates = "0.0";
                postRateBig.setText(rates);
                postRateSmall.setText(" " + rates);
                postRateBarSmall.setRating(Float.parseFloat(rates));
                postRaters.setText(model.getPostComments() + " ");

                JSONObject object = array.getJSONObject(1);
                String r1 = object.getString("ONE_STAR");
                String r2 = object.getString("TWO_STAR");
                String r3 = object.getString("THREE_STAR");
                String r4 = object.getString("FOUR_STAR");
                String r5 = object.getString("FIVE_STAR");

                setRatebar(r1, r2, r3, r4, r5);

                if(pref.getBoolean("islogin",false))
                {
                    //like
                    object=array.getJSONObject(2);
                    setSelfLike(object);
                    //comment
                    object=array.getJSONObject(3);
                    setSelfComment(object);
                }

                onCreateOptionsMenu(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setSelfLike(JSONObject object) {
        try {
            if(object.getString("liked").equals("yes")){
                isLiked=true;
            }else {
                isLiked=false;
            }
            likePost2();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSelfComment(JSONObject object) {
        try {
            selfRateTime.setText(MyUtils.dateFormat(object.getString(Server.RETURN_TIME)));
            selfRatingBar.setRating(Integer.parseInt(object.getString("rating")));
            commentBox.setText(object.getString("comment_text"));
            commentBox.setEnabled(false);
            saveComnt.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRatebar(String r1, String r2, String r3, String r4, String r5) {
        int a, b, c, d, e;
        a = Integer.parseInt(r1);
        b = Integer.parseInt(r2);
        c = Integer.parseInt(r3);
        d = Integer.parseInt(r4);
        e = Integer.parseInt(r5);
        int max = a;
        if (b > max) max = b;
        if (c > max) max = c;
        if (d > max) max = d;
        if (e > max) max = e;

        int rh=15;
        rate1.setLayoutParams(new LinearLayout.LayoutParams(countWidth(r1, max), rh));
        rate2.setLayoutParams(new LinearLayout.LayoutParams(countWidth(r2, max), rh));
        rate3.setLayoutParams(new LinearLayout.LayoutParams(countWidth(r3, max), rh));
        rate4.setLayoutParams(new LinearLayout.LayoutParams(countWidth(r4, max), rh));
        rate5.setLayoutParams(new LinearLayout.LayoutParams(countWidth(r5, max), rh));

        rtext5.setText(" "+r5);
        rtext4.setText(" "+r4);
        rtext3.setText(" "+r3);
        rtext2.setText(" "+r2);
        rtext1.setText(" "+r1);
    }

    private int countWidth(String r1, int b) {
        int a = Integer.parseInt(r1);
        int c = 1;
        try {
            c = (250 * a) / b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private void loadImages() {
        if (Internet.isAvail(this)) {

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_POST_ID, model.getPostid())
                                .build();


                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.GET_IMAGES_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ViewPost.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        jsonDecodeImages(res);
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
            Alert.show(ViewPost.this, "Please connect to Internet");
        }
    }

    private void jsonDecodeImages(String res) {
        Log.d("json", res);
        try {
            JSONArray array = new JSONArray(res);
            if (array.length() > 0) {
                screenshotRoot.removeAllViews();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    final String url = object.getString(Server.RETURN_POST_IMAGE);
                    ImageView im = new ImageView(this);
                    im.setBackgroundColor(Color.GRAY);
                    im.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    Picasso.with(this).load(Server.SERVER_URL + url).placeholder(R.drawable.item_bg).into(im);
                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(ViewPost.this,ViewImage.class);
                            intent.putExtra("url",Server.SERVER_URL + url);
                            startActivity(intent);
                        }
                    });
                    screenshotRoot.addView(im);
                    TextView vv = new TextView(this);
                    vv.setText(" ");
                    vv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    screenshotRoot.addView(vv);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadComments() {
        if (Internet.isAvail(this)) {

            Runnable runnable1 = new Runnable() {
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add(Server.PARAM_POST_ID, model.getPostid())
                                .build();


                        Request request = new Request.Builder()
                                .url(Server.SERVER_URL + Server.GET_COMMENTS_URL)
                                .post(formBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        Alert.show(ViewPost.this, "Server Error :\n" + e.getMessage());
                                    }
                                };
                                runOnUiThread(runnable1);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String res = response.body().string();
                                Runnable runnable1 = new Runnable() {
                                    public void run() {
                                        jsonDecodeCommets(res);
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
            Alert.show(ViewPost.this, "Please connect to Internet");
        }
    }

    private void jsonDecodeCommets(String res) {
        Log.d("json comments", res);
        try {
            topCommentsRoot.removeAllViews();
            JSONArray array = new JSONArray(res);

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.getString(Server.RETURN_USER_NAME);
                    String imgurl = object.getString(Server.RETURN_USER_PIC);
                    String rate = object.getString("rating");
                    String cmt = object.getString("comment_text");
                    String sub = object.getString(Server.RETURN_TIME);

                    View root = LayoutInflater.from(this).inflate(R.layout.comment_item, null);
                    CircleImageView img = (CircleImageView) root.findViewById(R.id.comment_item__usrimg_imageview);
                    TextView nametv = (TextView) root.findViewById(R.id.comment_item_usernm_textview);
                    RatingBar rr = (RatingBar) root.findViewById(R.id.comment_item_rate);
                    TextView comntv = (TextView) root.findViewById(R.id.comment_item_comment_tv);
                    TextView subtv = (TextView) root.findViewById(R.id.comment_item_subtext);
                    Picasso.with(this).load(imgurl).placeholder(R.drawable.item_bg).into(img);

                    rr.setRating(Float.parseFloat(rate));
                    nametv.setText(name);
                    comntv.setText(cmt);

                    subtv.setText(MyUtils.dateFormat(sub));
                    topCommentsRoot.addView(root);

                }
            } else {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("Be First to comment on this post");
                tv.setTextColor(Color.GRAY);
                tv.setPadding(5, 10, 5, 10);
                topCommentsRoot.addView(tv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
