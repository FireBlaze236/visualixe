#version 300 es

precision mediump float;
out vec4 fragColor;

struct Material
{
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

struct PointLight{
    vec3 pos;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float intensity;
};

in vec3 norm;
in vec2 tex;
in vec3 fragPos;
in vec3 vertColor;

uniform PointLight l;
uniform Material mat;
uniform vec3 viewPos;

void main() {

//AMBIENT
    vec3 ambient = l.ambient * mat.ambient * l.intensity;
    vec3 lightDir = (l.pos - fragPos);
// ATTENUATION
    float d = length(lightDir);
    float attenuation = 1.0 / (1.0 + 0.9 * d + 0.032 * (d * d));   
// DIFFUSE
    lightDir = normalize(lightDir);
    vec3 Normal = normalize(norm);
    float dirDiff = max(dot(Normal, lightDir), 0.0);
    vec3 diffuse = l.diffuse * (dirDiff * mat.diffuse) * l.intensity;
//SPECULAR
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = max(dot(reflectDir, viewDir), 0.0);
    spec = pow(spec, mat.shininess);
    vec3 specular = l.specular * (spec * mat.specular);

//APPLY ATTENUATION
    diffuse *= attenuation;
    ambient *= attenuation;
    specular *= attenuation;

    vec3 result = (ambient + diffuse + specular) ;
    fragColor = vec4(result, 1.0);
}