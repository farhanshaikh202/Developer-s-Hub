package com.farhansoftware.developershub.models;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by Farhan on 03-02-2017.
 */

public class PostModel implements Serializable {
    private String postid;
    private String userid;
    private String username;
    private String posttype;
    private String categoryid;
    private String posttitle;
    private String postdescription;
    private String postDescriptionUrl;
    private String download_url;
    private String downloads;
    private String userPhoto;
    private String profileLikes;
    private String postLikes;
    private String postComments;
    private String postRate;
    private String postImage;
    private String time;
    private String category_name;
    private String category_icon;
    private String category_color;


    public PostModel(String postid, String userid, String username, String posttype, String categoryid, String posttitle, String postdescription, String postDescriptionUrl, String download_url, String downloads, String userPhoto, String profileLikes, String postLikes, String postComments, String postRate, String postImage, String time, String category_name, String category_icon, String category_color) {
        this.postid = postid;
        this.userid = userid;
        this.username = username;
        this.posttype = posttype;
        this.categoryid = categoryid;
        this.posttitle = posttitle;
        this.postdescription = postdescription;
        this.postDescriptionUrl = postDescriptionUrl;
        this.download_url = download_url;
        this.downloads = downloads;
        this.userPhoto = userPhoto;
        this.profileLikes = profileLikes;
        this.postLikes = postLikes;
        this.postComments = postComments;
        this.postRate = postRate;
        this.postImage = postImage;
        this.time = time;
        this.category_name = category_name;
        this.category_icon = category_icon;
        this.category_color = category_color;
    }

    public PostModel() {
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_icon() {
        return category_icon;
    }

    public String getCategory_color() {
        return category_color;
    }

    public void increamentLike(){
        this.postLikes=""+(Integer.parseInt(this.postLikes)+1);
    }
    public void decreamentLike(){
        this.postLikes=""+(Integer.parseInt(this.postLikes)-1);
    }
    public String getPostid() {
        return postid;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }


    public String getPosttype() {
        return posttype;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public String getPostDescriptionUrl() {
        return postDescriptionUrl;
    }

    public String getDownload_url() {
        return download_url;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getProfileLikes() {
        return profileLikes;
    }

    public String getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(String postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostComments() {
        return postComments;
    }

    public String getPostRate() {

        float rr = 0;
        try {
            rr = Float.parseFloat(postRate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return String.format("%.1f",rr);
    }

    public String getPostImage() {
        return postImage;
    }

    public String getTime() {
        return time;
    }

    public void setPostId(String postId) {
        this.postid = postId;
    }
}
