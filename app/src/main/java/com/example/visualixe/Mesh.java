package com.example.visualixe;

import android.app.Activity;
import android.app.Application;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL;

public class Mesh {
    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    private int VAO, VBO, EBO;

    static float[] positions = {
            // first triangle
            0.5f,  0.5f, 0.0f,  // top right
            0.5f, -0.5f, 0.0f,  // bottom right
            -0.5f, -0.5f, 0.0f,  // bottom left
            -0.5f,  0.5f, 0.0f   // top left
    };

    int[] indices = {  // note that we start from 0!
            0, 1, 3,   // first triangle
            1, 2, 3    // second triangle
    };

    Shader shader;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private  int vertexCount = 0;
    private int vertexStride = 0;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Mesh(MeshData data, String vertexShader, String fragmentShader){
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                data.positions.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());


        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(data.positions);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        ByteBuffer bi = ByteBuffer.allocateDirect( data.indices.length * 4);
        bi.order(ByteOrder.nativeOrder());
        indexBuffer = bi.asIntBuffer();
        indexBuffer.put(data.indices);
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

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,EBO);
        GLES30.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES30.GL_STATIC_DRAW);

        Util.CheckOpenglErrors();

        shader = new Shader(vertexShader, fragmentShader);

        vertexCount = data.positions.length;
        vertexStride = COORDS_PER_VERTEX * 4;
    }



    public void draw(float[] model, float[] view, float[] projection) {

        GLES30.glBindVertexArray(VAO);

        // Add program to OpenGL ES environment
        shader.UseShader();

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GLES30.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO);
        GLES30.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(0, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                0, 0);



        // Prepare the triangle coordinate data

        GLES30.glEnableVertexAttribArray(0);

        // get handle to fragment shader's vColor member
        shader.SetUniformVec4fv("vColor", color);
        // get handle to shape's transformation matrix
        shader.SetUniformMat4f("umodel", model);
        shader.SetUniformMat4f("uview", view);
        shader.SetUniformMat4f("uprojection", projection);



        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.capacity(), GLES30.GL_UNSIGNED_INT, 0);
    }
}
