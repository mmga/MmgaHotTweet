package com.mmga.mmgahottweet.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Twitter {

    @SerializedName("statuses")
    private List<Status> statuses = new ArrayList<Status>();

    @SerializedName("search_metadata")
    private SearchMetadata searchMetadata;


    public class SearchMetadata{

        @SerializedName("max_id")
        private long maxId;

        public long getMaxId() {
            return maxId;
        }

        public void setMaxId(long maxId) {
            this.maxId = maxId;
        }
    }


    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public SearchMetadata getSearchMetadata() {
        return searchMetadata;
    }

    public void setSearchMetadata(SearchMetadata searchMetadata) {
        this.searchMetadata = searchMetadata;
    }
}
