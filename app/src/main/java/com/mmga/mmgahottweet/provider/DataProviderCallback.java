package com.mmga.mmgahottweet.provider;


import com.mmga.mmgahottweet.data.model.Status;

import java.util.List;

public interface DataProviderCallback {

    void OnDataSuccess(List<Status> status, int loadType);

    void OnDataComplete();

    void OnDataError(Throwable e);

    void OnAuthComplete();

    void OnAuthError(Throwable e);
}
