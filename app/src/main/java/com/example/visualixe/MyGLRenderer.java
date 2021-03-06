package com.example.visualixe;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private ArrayList<MeshData> allMeshData = new ArrayList<>();
    private HashMap<String, Integer> meshNameToIndex = new HashMap<>();

    private ArrayList<Mesh> meshes = new ArrayList<>();

    private Context parentContext;

    MyGLRenderer(Context context)
    {
        super();
        parentContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d("D", "onCreate");
        GLES30.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        meshes.clear();
        for(MeshData m :allMeshData)
        {
            Mesh mesh = new Mesh(m);
            meshes.add(mesh);
        }
    }
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];



    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);

        float ratio = (float) width / height;

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 100);

        meshes.clear();
        for(MeshData m :allMeshData)
        {
            Mesh mesh = new Mesh(m);
            meshes.add(mesh);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 10, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Render mesh
        for (Mesh m : meshes) {
            m.draw(viewMatrix, projectionMatrix);
        }

    }

    public void AddMesh(MeshData m, String name)
    {
        allMeshData.add(m);
        meshNameToIndex.put(name, allMeshData.size() - 1);
    }

    private Mesh GetMeshInstanceByName(String name)
    {
        if(meshNameToIndex.containsKey(name))
        {
            int idx = meshNameToIndex.get(name);
            if(meshes.size() > idx)
            {
                return  meshes.get(idx);
            }
        }
        return null;
    }

    public void SetMeshRotation(String name, float angle, float x, float y, float z)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
            m.RotateMesh(angle, x, y, z);
    }

    public void SetMeshTranslation(String name, float amount, float x, float y, float z)
    {
        if(name == "Light")
        {
            for(Mesh m : meshes)
                m.MoveLight(amount, x, y, z);
            return;
        }
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
            m.MoveMesh(amount, x, y, z);
    }

    public void SetMeshScale(String name, float amount, float x, float y, float z)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
            m.ScaleMesh(amount, x, y, z);
    }

    public float[] GetMeshRotationAxis(String name)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
           return m.GetRotationAxis();
        else
            return new float[] {0.0f, 1.0f, 0.0f};
    }

    public float[] GetMeshPosition(String name)
    {
        if(name == "Light"){
            if(!meshes.isEmpty()) return meshes.get(0).GetLightPos();
        }
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
           return m.GetPosition();
        else
            return new float[] {0.0f, 0.0f, 0.0f};
    }

    public float[] GetMeshScaleAxis(String name)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
           return m.GetScale();
        else
            return new float[] {1.0f, 1.0f, 1.0f};
    }

    public void SetHideMesh(String name, boolean value)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
        {
            m.hidden = value;
        }
    }

    public boolean GetHideMesh(String name)
    {
        Mesh m = GetMeshInstanceByName(name);
        if(m != null)
        {
            return m.hidden;
        }
        return false;
    }

}
