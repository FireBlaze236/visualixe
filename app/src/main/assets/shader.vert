#version 300 es

precision mediump float;
layout(location = 0) in vec3 vPosition;

uniform mat4 umodel;
uniform mat4 uview;
uniform mat4 uprojection;

void main() {

    vec4 pos = vec4(vPosition.x, vPosition.y, vPosition.z , 1.0);
    gl_Position = uprojection * uview * umodel * pos;
}