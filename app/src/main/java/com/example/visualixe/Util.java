package com.example.visualixe;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Util {

    public static void CheckOpenglErrors()
    {
        int error;
        error = GLES30.glGetError();
        while(error != GLES30.GL_NO_ERROR)
        {
            error = GLES30.glGetError();
            if(error != GLES30.GL_NO_ERROR)
            {
                throw new RuntimeException("OPENGL ERROR: "
                        + error);
            }

        };
    }

    public static String LoadShaderCodeFromFile(Context context, String filepath) throws IOException {
        AssetManager am = context.getAssets();
        InputStream inputStream = am.open(filepath);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        while(line != null)
        {
            stringBuilder.append(line).append("\n");
            line = reader.readLine();
        }

        return stringBuilder.toString();
    }

    public static MeshData LoadObj(Context context, String filepath) throws IOException {
        MeshData data = new MeshData();


        AssetManager am = context.getAssets();
        InputStream inputStream = am.open(filepath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        ArrayList<Float> positions = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        String line = reader.readLine();
        while(line != null)
        {
            if(line.startsWith("v "))
            {
                ParseAttributes("v", line, positions);
            }
            if(line.startsWith("f "))
            {
                ParseIndices("f", line, indices);
            }
            line = reader.readLine();
        }

        float[] p = new float[positions.size()];
        for(int i =0; i < positions.size(); i++) p[i] = positions.get(i);
        int[] e = new int[indices.size()];
        for(int i =0; i < indices.size(); i++) e[i] = indices.get(i);
        data.positions = p;
        data.indices = e;

        return data;
    }

    private static void ParseAttributes(String attrib, String line, ArrayList<Float> list)
    {
        if(line.startsWith(attrib))
        {
            String[] content = line.split(" ");
            for(int i = 0; i < content.length; i++)
            {
                if(!content[i].isEmpty() && !content[i].trim().equals(attrib))
                    list.add( Float.parseFloat(content[i].trim()));
            }
        }
    }

    private static void ParseIndices(String face, String line, ArrayList<Integer> list)
    {
        if(line.startsWith(face))
        {
            String[] content = line.split(" ");
            for(int i = 1; i < content.length; i++)
            {
                if(!content[i].isEmpty() && !content[i].trim().equals(face))
                {
                    String[] indices = content[i].split("/");
                    //for(int j = 0; j < indices.length; j++)
                    list.add( Integer.parseInt(indices[0].trim())); // add the first to vindex
                }

            }
        }
    }

}
