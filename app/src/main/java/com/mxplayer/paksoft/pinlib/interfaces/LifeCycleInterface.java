package com.mxplayer.paksoft.pinlib.interfaces;

import android.app.Activity;


public interface LifeCycleInterface {


    public void onActivityResumed(Activity activity);


    public void onActivityUserInteraction(Activity activity);


    public void onActivityPaused(Activity activity);
}
