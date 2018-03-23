package com.bryonnicoson.surfaceviewexample;

/**
 * Created by bryon on 3/23/18.
 */

public class FlashlightCone {

    // location and size
    private int mX;
    private int mY;
    private int mRadius;

    int getX() {
        return mX;
    }

    int getY() {
        return mY;
    }

    int getRadius() {
        return mRadius;
    }

    FlashlightCone(int viewWidth, int viewHeight){
        // center of screen
        mX = viewWidth / 2;
        mY = viewHeight / 2;
        // adjust radius for narrowest view dimension
        mRadius = ((viewWidth <= viewHeight) ? mX / 3 : mY / 3);
    }

    public void update(int newX, int newY) {
        mX = newX;
        mY = newY;
    }

}
