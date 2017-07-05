package com.farhansoftware.developershub.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.custom.TAG;
import com.farhansoftware.developershub.interfaces.OnPostItemViewClickListener;
import com.farhansoftware.developershub.models.PostModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Farhan on 03-02-2017.
 */

public class MainFeedAdapter extends BaseAdapter  {
    private final SpringSystem springSystem;
    private final Spring spring;
    Context context;
    ArrayList<PostModel> arrayList;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    DateFormat formatter2 = new SimpleDateFormat("dd MMM yyyy");
    OnPostItemViewClickListener listener = null;

    public MainFeedAdapter(Context context, ArrayList<PostModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        // Create a system to run the physics loop for a set of springs.
        springSystem = SpringSystem.create();

        // Add a spring to the system.
        spring = springSystem.createSpring();
        SpringConfig config = new SpringConfig(800, 10);
        spring.setSpringConfig(config);

    }

    public void setClickListener(OnPostItemViewClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<PostModel> getArrayList() {
        return arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.mainscreen_listview_item, null);
            holder.userpic = (CircleImageView) convertView.findViewById(R.id.mainscreen_listview_item__usrimg_imageview);
            holder.username = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_usernm_textview);
            holder.postImage = (ImageView) convertView.findViewById(R.id.mainscreen_lv_item_postimg_imgview);
            holder.posttitle = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_posttitle_tv);
            holder.likes = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_like_text);
            holder.comment = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_comments_txt);
            holder.rating = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_rating_text);
            holder.menu = (ImageButton) convertView.findViewById(R.id.mainscreen_listview_item_menu_imgbtn);
            holder.subtext = (TextView) convertView.findViewById(R.id.mainscreen_listview_item_subtext);
            holder.likeRoot=(View) convertView.findViewById(R.id.mainscreen_listview_item_like_root);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();


        final PostModel model = arrayList.get(position);
        Picasso.with(context).load(model.getUserPhoto()).into(holder.userpic);
        //Log.d(TAG.APP_NAME,"img url : "+model.getPostImage());
        if (model.getPostImage().toLowerCase().endsWith("null")) {
            holder.postImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Picasso.with(context).load(model.getCategory_icon()).placeholder(R.drawable.item_bg).into(holder.postImage);
        } else {
            Picasso.with(context).load(model.getPostImage()).into(holder.postImage);
        }
        holder.username.setText(Html.fromHtml("<b>" + model.getUsername() + "</b>  |  " + "<font color='red'>❤ " + model.getProfileLikes() + "</font>"));

        Date date = null;   //string to date convert
        try {
            date = (Date) formatter.parse(model.getTime());
            holder.subtext.setText(Html.fromHtml("<font color='" + model.getCategory_color() + "'>" + model.getCategory_name() + "</font> | Posted on " + formatter2.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.subtext.setText(model.getCategory_name());
        }

        holder.posttitle.setText(model.getPosttitle());

        holder.likes.setText(" "+model.getPostLikes());
        holder.comment.setText(" "+model.getPostComments());
        if (model.getPostRate().equals("null")) holder.rating.setText(" 0.0");
        else holder.rating.setText(" "+model.getPostRate());

        convertView.setBackgroundColor(Color.parseColor(model.getCategory_color()));


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, model);
            }
        });
        holder.userpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, model);
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, model);
            }
        });
        holder.subtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, model);
            }
        });


        final View view = holder.likeRoot;
        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f + (value * 0.7f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            }
        });

        holder.likeRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        spring.setEndValue(1f);
                        return true;
                    case MotionEvent.ACTION_UP:
                        listener.onClick(holder.likes, model);
                        holder.likeRoot.postDelayed(new Runnable() {
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
        return convertView;
    }


    class ViewHolder {
        CircleImageView userpic;
        ImageView postImage;
        ImageButton menu;
        TextView username, subtext, posttitle, likes, comment, rating;
        View likeRoot;
    }
}
