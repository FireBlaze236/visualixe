package com.example.visualixe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshData {
    public ArrayList<Float[]> positions;
    public ArrayList<Float[]> normals;
    public ArrayList<Float[]> uv;

    public ArrayList<Integer> indices;
    public ArrayList<Integer> normalIndices;
    public ArrayList<Integer> uvIndices;

    public String vert, frag;

    private float[] varr;
    public int[] earr;

    private boolean cached = false;

    int faceCount = 0;

    int coordsPerVertex = 3;

    public MeshData()
    {
        positions = new ArrayList<>();
        normals = new ArrayList<>();
        uv = new ArrayList<>();

        indices = new ArrayList<>();
        normalIndices = new ArrayList<>();
        uvIndices = new ArrayList<>();

        //TODO: DEFAULT SHADERS
        vert = "";
        frag = "";
    }

    public void LoadShaders(String vertex, String fragment)
    {
        vert = vertex;
        frag = fragment;
    }

    public float[] GetCompiledData()
    {
        if(cached) return varr;
        ArrayList<Float> v = new ArrayList<>();

        boolean hasNormal = normals.size() > 0;
        boolean hasTex = uv.size() > 0;


        if(hasNormal)
        {coordsPerVertex += 3;}
        if(hasTex) {coordsPerVertex += 2;}

        for(int i = 0; i < indices.size(); i++)
        {
            ProcessFaceIndex(i, hasNormal, hasTex, v);
        }

        varr = Util.ArrayToFloat(v);
        earr = Util.ArrayToInt(indices);

        cached = true;
        return varr;
    }

    public int GetCoordsPerVertex() {
        return coordsPerVertex;
    }

    private void ProcessFaceIndex(int i, boolean hasNormal, boolean hasTex, ArrayList<Float> v)
    {
        Float[] pos = new Float[] {0f, 0f, 0f};
        Float[] norm = new Float[] {0f, 0f, 0f};
        Float[] t = new Float[] {0f, 0f};


        pos = positions.get(indices.get(i));
        v.add(pos[0]);
        v.add(pos[1]);
        v.add(pos[2]);

        if(hasNormal){
            norm = normals.get(normalIndices.get(i));
            v.add(norm[0]);
            v.add(norm[1]);
            v.add(norm[2]);
        }

        if(hasTex)
        {
            t = uv.get(uvIndices.get(i));
            v.add(t[0]);
            v.add(t[1]);
        }
    }
}
