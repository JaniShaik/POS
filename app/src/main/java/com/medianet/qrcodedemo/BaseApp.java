package com.medianet.qrcodedemo;

import android.app.Application;
import android.widget.Toast;

import com.medianet.qrcodedemo.AidlUtil;

/**
 * Created by JANI SHAIK on 14/11/2018.
 */

public class BaseApp extends Application {
    private boolean isAidl;

    public boolean isAidl() {
        return isAidl;
    }

    public void setAidl(boolean aidl) {
        isAidl = aidl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isAidl = true;
        AidlUtil.getInstance().connectPrinterService(this);
    }
}
