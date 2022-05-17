package com.example.visualixe;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    public boolean hidden = false;
    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    private int VAO, VBO, EBO;


    Shader shader;
    // number of coordinates per vertex in this array
    static int COORDS_PER_VERTEX = 8;
    static final int float_size = 4;

    private volatile float mAngle = 0;
    private volatile float[] rotationAxis = new float[]{ 0.0f, 1.0f, 0.0f };
    private volatile float[] position = new float[]{ 0.0f, -0.5f, 2.0f };
    private volatile float[] lightPos = new float[] {0.0f, 0.0f, 2.0f};
    private volatile float[] scale = new float[]{ 1.0f, 1.0f, 1.0f };

    private final float MAX_SCALE = 3f;
    private final float MIN_SCALE = 0.1f;

    float[] modelMat = new float[16];


    public Mesh(MeshData data){
        float[] vertices = data.GetCompiledData();
        COORDS_PER_VERTEX = data.GetCoordsPerVertex();
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                 vertices.length * float_size);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertices);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        ByteBuffer bi = ByteBuffer.allocateDirect( data.indices.size() * float_size);
        bi.order(ByteOrder.nativeOrder());
        indexBuffer = bi.asIntBuffer();
        indexBuffer.put(data.earr);
        indexBuffer.position(0);

        int[] vaos = new int[1];

        GLES30.glGenVertexArrays(1, vaos, 0);
        VAO = vaos[0];
        GLES30.glBindVertexArray(VAO);

        int[] vbos = new int[2];

        GLES30.glGenBuffers(2, vbos, 0);
        VBO = vbos[0];
        EBO = vbos[1];

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBO);
        GLES30.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);
        int size = float_size * COORDS_PER_VERTEX;
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, size, (int) 0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, size, (int) 3 * 4);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, size, (int) 6 * 4);
        GLES30.glEnableVertexAttribArray(2);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,EBO);
        GLES30.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES30.GL_STATIC_DRAW);

        Util.CheckOpenglErrors();

        shader = new Shader(data.vert, data.frag);

        Matrix.setIdentityM(modelMat, 0);
    }



    public void draw(float[] view, float[] projection) {
        if(hidden) return;
        GLES30.glBindVertexArray(VAO);

        // Add program to OpenGL ES environment
        shader.UseShader();



        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO);
        vertexBuffer.position(0);
        //GLES30.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GLES30.GL_STATIC_DRAW);
        int size = 4 * COORDS_PER_VERTEX;
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, size, (int) 0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, size, (int) 3 * float_size);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, size, (int) 6 * float_size);
        GLES30.glEnableVertexAttribArray(2);

        applyTransformation();

        // Set vertex shader props
        shader.SetUniformMat4f("umodel", modelMat);

        //TODO: CAMERA CLASS WILL ALWAYS HAVE VIEW MATRIX SO NEED PARAM
        shader.SetUniformMat4f("uview", view);
        shader.SetUniformMat4f("uprojection", projection);

        //TODO: CAMERA CLASS
        float[] cam = new float[16];
        Matrix.invertM(view, 0, cam, 0);
        shader.SetUniformVec3f("viewPos", cam[12], cam[13],cam[14]);

        //Set materials
        shader.SetUniformVec3f("mat.diffuse", 0.3f, 0.5f, 0.8f);
        shader.SetUniformVec3f("mat.ambient", 0.1f, 0.1f, 0.1f);
        shader.SetUniformVec3f("mat.specular", 1f, 1f, 1f);
        shader.SetUniform1f("mat.shininess", 32.0f);

        //Set lights
        shader.SetUniformVec3f("l.ambient", 0.1f, 0.1f, 0.1f);
        shader.SetUniformVec3f("l.diffuse", 0.4f, 0.4f, 0.4f);
        shader.SetUniformVec3f("l.specular", 0.8f, 0.8f, 0.8f);
        shader.SetUniform1f("l.intensity", 10.0f);
        shader.SetUniformVec3f("l.pos", lightPos[0], lightPos[1], lightPos[2]);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        //GLES30.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Float.BYTES, indexBuffer, GLES30.GL_STATIC_DRAW);

        //GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.capacity(), GLES30.GL_UNSIGNED_INT, 0);

        GLES30.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBuffer.capacity() / COORDS_PER_VERTEX);
    }

    private void applyTransformation()
    {
        Matrix.setIdentityM(modelMat, 0);
        Matrix.translateM(modelMat, 0, position[0], position[1],  position[2]);
        Matrix.scaleM(modelMat, 0, scale[0], scale[1],  scale[2]);
        Matrix.rotateM(modelMat, 0, mAngle, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
    }

    public void SetRotation(float angle, float x, float y, float z)
    {
        mAngle = angle;
        if( x== 0 && y == 0 && z == 0) return;
        rotationAxis[0] = x;
        rotationAxis[1] = y;
        rotationAxis[2] = z;
    }

    public void RotateMesh(float angle, float x, float y, float z)
    {
        SetRotation(mAngle + angle, x, y, z);
    }

    public void SetPosition(float x, float y, float z)
    {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public void MoveMesh(float amount, float x, float y, float z)
    {
        position[0] = position[0] + x * amount;
        position[1] = position[1] + y * amount;
        position[2] = position[2] + z * amount;
    }

    public void ScaleMesh(float amount, float x, float y, float z)
    {
        if( x == 0 && y == 0 && z == 0) return;
        scale[0] = Util.Clamp(scale[0] + x * amount, MAX_SCALE, MIN_SCALE);
        scale[1] = Util.Clamp(scale[1] + y * amount, MAX_SCALE, MIN_SCALE);
        scale[2] = Util.Clamp(scale[2] + z * amount, MAX_SCALE, MIN_SCALE);
    }

    public float[] GetRotationAxis()
    {
        return new float[]{rotationAxis[0], rotationAxis[1], rotationAxis[2]};
    }

    public float[] GetScale()
    {
        return new float[]{scale[0], scale[1], scale[2]};
    }

    public float[] GetPosition()
    {
        return new float[]{position[0], position[1], position[2]};
    }


    public void MoveLight(float amount, float x, float y, float z)
    {
        lightPos[0] = lightPos[0] + x * amount;
        lightPos[1] = lightPos[1] + y * amount;
        lightPos[2] = lightPos[2] + z * amount;
    }

    public float[] GetLightPos()
    {
        return lightPos;
    }

}
