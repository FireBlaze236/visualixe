package com.example.visualixe;

import android.app.Application;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Debug;
import android.os.SystemClock;
import android.util.AndroidException;
import android.util.Log;

import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Timer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Mesh mMesh;
    private Context parentContext;

    MyGLRenderer(Context context)
    {
        super();
        parentContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(0.7f, 0.1f, 0.1f, 1.0f);
        Matrix.setIdentityM(modelMatrix, 0);

        String vertexShader = new String(), fragmentShader = new String();
        try {
            vertexShader = Util.LoadShaderCodeFromFile(parentContext, "shader.vert");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            fragmentShader = Util.LoadShaderCodeFromFile(parentContext, "shader.frag");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MeshData data = new MeshData();
        try {
             data = Util.LoadObj(parentContext, "torus.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMesh = new Mesh(data, vertexShader, fragmentShader);

    }
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] modelMatrix = new float[16];

    private float[] pos = new float[] {0.0f, 0.0f, -3.0f};

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        Matrix.setIdentityM(modelMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        float now = SystemClock.uptimeMillis() % 40000L;
        float angle = 0.090f * ((int) now);
        //Log.d("DEUBG",Float.toString(now));

        Matrix.setRotateM(modelMatrix, 0, mAngle, 0.0f, 0.0f, -1.0f);
        Matrix.translateM(modelMatrix, 0, 0, 0, -3f);

        mMesh.draw(modelMatrix, viewMatrix, projectionMatrix);
    }



    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}
