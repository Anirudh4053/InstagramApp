package com.app.instagramapp.Other;

import android.widget.TextView;

public class Slider {
    private TextView[] dots;

    public TextView[] initCode(int imageSize){
        dots = new TextView[imageSize];
        return dots;
    }
}
