package com.bryonnicoson.surfaceviewexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by bryon on 3/23/18.
 */

public class GameView extends SurfaceView implements Runnable {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private int mBitmapX;
    private int mBitmapY;
    private RectF mWinnerRect;
    private int mViewWidth;
    private int mViewHeight;
    private boolean mRunning;
    private Thread mGameThread;
    private FlashlightCone mFlashLightCone;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        mPath = new Path();
    }

    private void setUpBitmap(){
        mBitmapX = (int) Math.floor(Math.random() * (mViewWidth - mBitmap.getWidth()));
        mBitmapY = (int) Math.floor(Math.random() * (mViewHeight - mBitmap.getHeight()));
        mWinnerRect = new RectF(mBitmapX, mBitmapY,
                mBitmapX + mBitmap.getWidth(), mBitmapY + mBitmap.getHeight());
    }

    @Override
    public void run(){
        Canvas canvas;
        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                int x = mFlashLightCone.getX();
                int y = mFlashLightCone.getY();
                int radius = mFlashLightCone.getRadius();

                // set the canvas
                canvas = mSurfaceHolder.lockCanvas();  // for multi-thread need to try/catch this
                canvas.save();
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);

                // add flashlight circle, blackout canvas
                mPath.addCircle(x, y, radius, Path.Direction.CCW);
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);  // set circle as clipping path
                canvas.drawColor(Color.BLACK);

                // check whether center of flashlight circle is within winning rectangle
                if (x > mWinnerRect.left && x < mWinnerRect.right &&
                        y > mWinnerRect.top && y < mWinnerRect.bottom) {
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);
                    canvas.drawText("WIN!", mViewWidth / 3, mViewHeight / 2, mPaint);
                }

                // finished drawing - rewind path, restore canvas, and release canvas lock
                mPath.rewind();
                canvas.restore();
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void pause(){
        mRunning = false;
        try {
            // stop the thread (rejoin the main thread)
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewWidth = w;
        mViewHeight = h;
        mFlashLightCone = new FlashlightCone(mViewWidth, mViewHeight);
        mPaint.setTextSize(mViewHeight / 5);
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skatedroid);
        setUpBitmap();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // invalidate only inside case statement because there are many motion event types
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setUpBitmap();
                updateFrame((int) x, (int) y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                updateFrame((int) x, (int) y);
                invalidate();
                break;
            default:
                // nada
        }
        return true;
    }

    private void updateFrame(int newX, int newY) {
        mFlashLightCone.update(newX, newY);
    }
}
