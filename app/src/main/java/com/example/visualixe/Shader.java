package com.example.visualixe;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    private int mProgram;
    private HashMap<String, Integer> uniformMap = new HashMap<>();

    public Shader(String vertexShaderCode, String fragmentShaderCode){
        LinkShaders(vertexShaderCode, fragmentShaderCode);
    }
    public void UseShader()
    {
        GLES30.glUseProgram(mProgram);
    }

    private void LinkShaders(String vertexShaderCode, String fragmentShaderCode)
    {
        mProgram = GLES30.glCreateProgram();
        int vertexShader = CompileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CompileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);

        GLES30.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES30.GL_TRUE) {
            GLES30.glDeleteProgram(mProgram);
            throw new RuntimeException("Could not link program: "
                    + GLES30.glGetProgramInfoLog(mProgram));
        }
    }

    private int CompileShader(int type, String shaderCode)
    {
        int shader = GLES30.glCreateShader(type);

        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }



    private int GetUniformLocation(String name)
    {
        if(!uniformMap.isEmpty() && uniformMap.containsKey(name))
        {
            int loc = uniformMap.get(name);
            return loc;
        }
        else
        {
            int loc = GLES30.glGetUniformLocation(mProgram, name);
            Util.CheckOpenglErrors();
            uniformMap.put(name, loc);
            return loc;
        }

    }

    // Uniform methods
    public void SetUniformVec3f(String name, float x, float y, float z)
    {
        int id = GetUniformLocation(name);
        GLES30.glUniform3f(id, x, y, z);
    }

    public void SetUniformVec4f(String name, float x, float y, float z, float w)
    {
        int id = GetUniformLocation(name);
        GLES30.glUniform4f(id, x, y, z, w);
    }

    public void SetUniformVec4fv(String name, float[] a)
    {
        int id = GetUniformLocation(name);
        GLES30.glUniform4f(id, a[0], a[1], a[2], a[3]);
    }

    public void SetUniformMat4f(String name, float[] mat)
    {
        int id = GetUniformLocation(name);
        GLES30.glUniformMatrix4fv(id, 1, false, mat, 0);
    }

    public void SetUniform1f(String name, float val)
    {
        int id = GetUniformLocation(name);
        GLES30.glUniform1f(id, val);
    }


}
