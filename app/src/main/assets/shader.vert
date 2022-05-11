#version 300 es
precision mediump float;
layout(location = 0) in vec3 vPosition;
layout(location = 1) in vec3 vNormal;
layout(location = 2) in vec2 vTex;

uniform mat4 umodel;
uniform mat4 uview;
uniform mat4 uprojection;

out vec3 norm;
out vec2 tex;
out vec3 fragPos;
out vec3 vertColor;


void main() {
    vec4 pos = vec4(vPosition.x, vPosition.y, vPosition.z, 1.0);
    gl_Position = uprojection * uview * umodel * pos;

    fragPos = vec3(umodel * pos);
    norm = mat3(transpose(inverse(umodel))) * vNormal;
    tex = vTex;
    vertColor = vec3(sin(pos.x), cos(pos.y), 1.0);
}