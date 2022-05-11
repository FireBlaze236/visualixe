package com.example.visualixe;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


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


        String line = reader.readLine();
        while(line != null)
        {
            if(line.startsWith("v "))
            {
                ParseAttributes("v", line, data.positions);
            }
            else if(line.startsWith("f "))
            {
                ParseIndices("f", line, data.indices, data.normalIndices, data.uvIndices);
                data.faceCount++;
            }
            else if(line.startsWith("vt "))
            {
                ParseAttributes("vt", line, data.uv);
            }
            else if(line.startsWith("vn "))
            {
                ParseAttributes("vn", line, data.normals);
            }
            line = reader.readLine();
        }


        return data;
    }

    private static void ParseAttributes(String attrib, String line, ArrayList<Float[]> list)
    {

        if(line.startsWith(attrib))
        {
            Float[] vert = new Float[3];
            int count = 0;
            String[] content = line.split(" ");
            for(int i = 0; i < content.length; i++)
            {
                if(!content[i].isEmpty() && !content[i].trim().equals(attrib))
                {
                    vert[count] = Float.parseFloat(content[i].trim());
                    count++;
                }
            }

            list.add(vert);
        }
    }

    private static void ParseIndices(String face, String line, ArrayList<Integer> vList, ArrayList<Integer> nList, ArrayList<Integer> tList)
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
                    if(indices.length != 0)
                    {
                        //Remember to sub 1 as indexing is 1 based
                        vList.add( Integer.parseInt(indices[0].trim())-1); // add the first to vindex
                        if(indices.length > 1) tList.add(Integer.parseInt(indices[1].trim()) - 1);
                        if(indices.length > 2) nList.add(Integer.parseInt(indices[2].trim()) - 1);
                    }

                }

            }
        }
    }

    public static float[] ArrayToFloat(ArrayList<Float> f)
    {
        float[] floats = new float[f.size()];
        int i = 0;
        for(Float fv : f)
        {
            floats[i] = fv;
            i++;
        }

        return  floats;
    }

    public static int[] ArrayToInt(ArrayList<Integer> i)
    {
        int[] ints = new int[i.size()];
        int c = 0;
        for(Integer iv : i)
        {
            ints[c] = iv;
            c++;
        }

        return ints;
    }

    public static float Clamp(float val, float max, float min)
    {
        return Math.max(min , Math.min(max, val));
    }

}
