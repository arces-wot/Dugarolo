package com.example.dugarolo;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class JSONReceiver extends ResultReceiver {

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */

    private Receiver mReceiver;

    public JSONReceiver(Handler handler) {
        super(handler);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public void setmReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface  Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
