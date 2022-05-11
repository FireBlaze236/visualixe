package com.example.visualixe;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.IOException;

enum TRANSFORM_MODE {MOVE, ROTATE, SCALE, SET_POS};

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    Context parentContext;

    private TRANSFORM_MODE currentMode = TRANSFORM_MODE.ROTATE;
    private String selectedMesh = "Object";
    private float[] selectedAxis = new float[]{0f, 1f, 0f};

    private void init(Context context)
    {
        parentContext = context;
        setEGLContextClientVersion(3);
        setEGLConfigChooser(true);
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);

        String vertexShader = new String(), fragmentShader = new String(),
                vertexLight = new String(), fragmentLight = new String();
        try {
            vertexShader = Util.LoadShaderCodeFromFile(context, "shader.vert");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fragmentShader = Util.LoadShaderCodeFromFile(context, "shader.frag");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MeshData data = new MeshData();
        try {
            data = Util.LoadObj(context, "box.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }

        data.LoadShaders(vertexShader, fragmentShader);
        renderer.AddMesh(data, "Object");
    }
    public MyGLSurfaceView(Context context)
    {
        super(context);
        init(context);

    }

    public MyGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);

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

                float amt = ((dx + dy) * TOUCH_SCALE_FACTOR);
                switch (currentMode)
                {
                    case ROTATE:
                        renderer.SetMeshRotation(selectedMesh, amt, selectedAxis[0], selectedAxis[1], selectedAxis[2]);
                        break;
                    case MOVE:
                        renderer.SetMeshTranslation(selectedMesh, amt * 0.1f, selectedAxis[0], selectedAxis[1], selectedAxis[2]);
                        break;
                    case SCALE:
                        renderer.SetMeshScale(selectedMesh, amt * 0.01f, selectedAxis[0], selectedAxis[1], selectedAxis[2]);
                        break;
                }

                requestRender();
        }

        previousX = x;
        previousY = y;


        return true;
    }

    public void setSelectedAxis(float x, float y, float z)
    {
        selectedAxis[0] = x;
        selectedAxis[1] = y;
        selectedAxis[2] = z;
    }

    public float[] getPrevAxisValues()
    {
        float[] f = new float[3];

        switch (currentMode)
        {
            case MOVE:
                f = renderer.GetMeshPosition(selectedMesh);
                break;
            case ROTATE:
                f = renderer.GetMeshRotationAxis(selectedMesh);
                break;
            case SCALE:
                f = renderer.GetMeshScaleAxis(selectedMesh);
                break;
        }

        return f;
    }

    public void setTransformMode(TRANSFORM_MODE mode)
    {
        currentMode = mode;
    }

    public void setSelectedMesh(String name)
    {
        selectedMesh = name;
    }

    public String getSelectedMesh()
    {
        return selectedMesh;
    }

    public float[] getSelectedAxis() {
        return selectedAxis.clone();
    }
}
