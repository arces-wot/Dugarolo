package com.example.dugarolo;

import android.content.Context;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

public class RequestLab {
    private static RequestLab requestLab;
    private AssetLoader assetLoader;

    private List<Request> requestList;

    public static RequestLab get(Context context) {
        if(requestLab == null) {
            requestLab = new RequestLab(context);
        }
        return requestLab;
    }

    private RequestLab(Context context) {
        requestList = new ArrayList<>();
        /*
        requestList.add(new Request("Bertacchini\'s farm", new DateTime(2019, 11, 24, 13, 47), "interrupted", "18"));
        requestList.add(new Request( "Ferrari\'s farm", new DateTime(2019, 10, 25, 12, 48), "unknown", "7"));
         */
    }

    public List<Request> getRequestList() {
        return requestList;
    }

}
