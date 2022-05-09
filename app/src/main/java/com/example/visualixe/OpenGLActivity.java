package com.example.visualixe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLActivity extends Activity {
    private GLSurfaceView glView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new MyGLSurfaceView(this);
        setContentView(glView);


    }
}