package com.example.control.gles2sample11;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by tommy on 2015/06/18.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GestureDetector.OnGestureListener {
    private final Context mContext;
    private GLRenderer renderer;

    // サーフェースビューのコンストラクタ
    public MyGLSurfaceView(Context context) {
        super(context);
        mContext=context;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        renderer = new GLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distx, float disty) {
        renderer.setScrollValue(distx, disty);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }
    @Override

    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        renderer.toggleViewmode();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }
}
