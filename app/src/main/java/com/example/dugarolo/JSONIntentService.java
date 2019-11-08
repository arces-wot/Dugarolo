package com.example.dugarolo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class JSONIntentService extends IntentService {

    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public JSONIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        JSONReceiver jsonReceiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();
        try {
            //prendi il json
            //ArrayList<Canal> results = new ArrayList<>();
            //bundle.putParcelableArrayList("results", (ArrayList<? extends Parcelable>) results);
        } catch (Exception e) {
            //gestisci l'errore
            jsonReceiver.send(STATUS_ERROR, bundle);
        }
        jsonReceiver.send(STATUS_FINISHED, bundle);
    }
}
