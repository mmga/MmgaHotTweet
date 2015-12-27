package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;


public class ReTweet {

    @SerializedName("text")
    private String rtText;

    @SerializedName("user")
    private User rtUser;






    public class User {

        @SerializedName("name")
        private String userName;

        @SerializedName("screen_name")
        private String screenName;

        @SerializedName("profile_image_url")
        private String profileImageUrl;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }
    }


    public String getRtText() {
        return rtText;
    }

    public void setRtText(String rtText) {
        this.rtText = rtText;
    }

    public User getRtUser() {
        return rtUser;
    }

    public void setRtUser(User rtUser) {
        this.rtUser = rtUser;
    }

}
