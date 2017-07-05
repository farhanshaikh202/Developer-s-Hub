package com.farhansoftware.developershub.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.config.Server;
import com.farhansoftware.developershub.utils.MyUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Farhan on 03-04-2017.
 */

public class MyProfileCommentAdapter extends BaseAdapter {
    Context context;
    ArrayList<JSONObject> array;
    private CircleImageView im;
    private TextView uname;
    private RatingBar urate;
    private TextView udate;
    private TextView ucmt;
    private ArraySet arrayList;

    public MyProfileCommentAdapter(Context context, ArrayList<JSONObject> array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);

    }

    public String getPostId(int i) throws JSONException {
        JSONObject object=array.get(i);
        return object.getString(Server.RETURN_POST_ID);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView==null){
            v= LayoutInflater.from(context).inflate(R.layout.comment_item,null);

        }else v=convertView;
        im=(CircleImageView)v.findViewById(R.id.comment_item__usrimg_imageview);
        uname=(TextView)v.findViewById(R.id.comment_item_usernm_textview);
        urate=(RatingBar)v.findViewById(R.id.comment_item_rate);
        udate=(TextView)v.findViewById(R.id.comment_item_subtext);
        ucmt=(TextView)v.findViewById(R.id.comment_item_comment_tv);

        try {
            JSONObject object=array.get(position);
            uname.setText(object.getString(Server.RETURN_USER_NAME));
            ucmt.setText(object.getString("comment_text"));
            udate.setText(MyUtils.dateFormat(object.getString(Server.RETURN_TIME)));
            urate.setRating(Integer.parseInt(object.getString("rating_no")));
            Picasso.with(context).load(object.getString(Server.RETURN_USER_PIC)).into(im);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    public ArraySet getArrayList() {
        return arrayList;
    }
}
