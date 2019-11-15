package com.example.dugarolo;

import android.content.Context;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class RequestLab {
    private static RequestLab requestLab;

    private List<Request> requestList;

    public static RequestLab get(Context context) {
        if(requestLab == null) {
            requestLab = new RequestLab(context);
        }
        return requestLab;
    }

    private RequestLab(Context context) {
        requestList = new ArrayList<>();
        requestList.add(new Request("Bertacchini\'s farm", new LocalDate(2019, 9, 18, null), "interrupted"));
        requestList.add(new Request( "Ferrari\'s farm", new LocalDate(2019, 9, 19, null), "unknown"));
    }

    public List<Request> getRequestList() {
        return requestList;
    }
}
