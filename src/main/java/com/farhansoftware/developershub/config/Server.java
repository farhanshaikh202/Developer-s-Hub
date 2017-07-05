package com.farhansoftware.developershub.config;

/**
 * Created by Farhan on 31-01-2017.
 */

public class Server {
    public static final String SERVER_URL="http://developers-hub.pe.hu/api/";
    public static final String SHARE_POST_URL="http://developers-hub.pe.hu/post?id=";

    public static final String REGISTER_URL="register.php";
    public static final String LOGIN_URL="login.php";
    public static final String FORGOT_PWD_URL="forgotPassword.php";
    public static final String CHANGE_PWD_URL="changePassword.php";
    public static final String VERIFY_OTP_URL="verifyOTP.php";

    public static final String TRENDING_POST_URL="trending.php";
    public static final String CATEGORY_URL="categories.php";

    public static final String VIEW_POST_URL="viewPost.php";
    public static final String GET_IMAGES_URL="getImages.php";
    public static final String GET_COMMENTS_URL="getComments.php";
    //OPERATIONS
    public static final String LIKE_POST_URL="likePost.php";
    public static final String COMMENT_POST_URL="postComment.php";
    public static final String LIKE_PROFILE_URL="likeProfile.php";

    public static final String UPLOAD_POST_URL="uploadPost.php";//with description file
    public static final String UPLOAD_POST_IMAGES_URL="uploadPostImage.php";

    //USER SPECIFIC
    public static final String USER_PROFILE_URL="profile.php";
    public static final String USER_POSTS_URL="userPosts.php";
    public static final String USER_LIKED_POSTS_URL="likedPosts.php";
    public static final String USER_COMMENTED_POSTS="commentedPosts.php";
    public static final String FOLLOWING_URL="following.php";
    public static final String FOLLOWERS_URL="followers.php";

    //SELF PROFILE
    public static final String UPDATE_MY_PROFILE_URL="updateProfile.php";
    public static final String UPLOAD_MY_PROFILE_IMAGE="updateProfileImage.php";



    //post parameters
    public static final String PARAM_EMAIL="email";
    public static final String PARAM_PASSWORD="pwd";
    public static final String PARAM_LOGIN="loginfromapp";
    public static final String PARAM_REGISTER="registerfromapp";
    public static final String PARAM_VERIFY="verifyemail";
    public static final String PARAM_RETURN_ID="returnId";
    public static final String PARAM_USERNAME="username";
    public static final String PARAM_SUCCESS="success";
    public static final String PARAM_USER_ID="user_id";
    public static final String PARAM_USER_PHOTO="photo_link";
    public static final String PARAM_POST_ID="postid";
    public static final String PARAM_LIKE_POST="likepost";
    public static final String PARAM_OTP="otp";
    public static final String PARAM_CATEGORY="categories";



    //return params
    public static final String RETURN_CATEGORY_ID="category_id";
    public static final String RETURN_CATEGORY_NAME="category_title";
    public static final String RETURN_CATEGORY_ICON="category_icon";
    public static final String RETURN_CATEGORY_COLOR="category_color";

    public static final String RETURN_POST_ID="post_id";
    public static final String RETURN_POST_TYPE="post_type";
    public static final String RETURN_POST_TITLE="post_title";
    public static final String RETURN_POST_DECRIPTION="post_description";
    public static final String RETURN_POST_DESCRIPTION_URL="post_description_url";
    public static final String RETURN_POST_DOWNLOAD_URL="download_url";
    public static final String RETURN_POST_DONLOADS="no_of_downloads";
    public static final String RETURN_POST_LIKES="LIKE_COUNT";
    public static final String RETURN_POST_COMMENTS="COMMENT_COUNT";
    public static final String RETURN_POST_RATING="RATING";
    public static final String RETURN_POST_IMAGE="image_url";

    public static final String RETURN_USER_ID="user_id";
    public static final String RETURN_USER_NAME="user_name";
    public static final String RETURN_USER_PIC="photo_link";
    public static final String RETURN_USER_PROFILE_LIKES="profile_likes";

    public static final String RETURN_TIME="timestamp";


    public static final String POFILE_IMAGE_UPLOAD_URL = "uploadUserImage.php";
    public static final String RENAME_PROFILE_URL = "renameProfile.php";
    public static final String CREATE_POST_URL="createPost.php";
    public static final String POST_IMAGE_UPLOAD_URL = "uploadPostImage.php";
    public static final String DELETE_POST_URL="deletePost.php";
}
