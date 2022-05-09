package com.example.visualixe;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import java.io.IOException;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;


    public MyGLSurfaceView(Context context)
    {
        super(context);

        setEGLContextClientVersion(3);
        setEGLConfigChooser(true);
        renderer = new MyGLRenderer(context);

        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;

    }
}
