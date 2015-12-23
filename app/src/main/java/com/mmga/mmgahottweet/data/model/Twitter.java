package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Twitter {

    @SerializedName("statuses")
    private List<Status> statuses = new ArrayList<Status>();


    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
}
