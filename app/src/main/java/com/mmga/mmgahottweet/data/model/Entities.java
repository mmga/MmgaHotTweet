package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entities {

    @SerializedName("media")
    private List<Media> medias;

    public class Media {
        @SerializedName("media_url")
        private String mediaUrl;

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }
}
