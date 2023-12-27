#version 150
precision highp float;

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 xyzPos;

out vec4 fragColor;

vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

void main() {
    if (color.a < 0.1) {
        discard;
    }

    if(color.r <= 126.50/255.0 && color.r > 126.49/255.0 && color.g == 252/255.0 && color.b <= 31.63/255.0 && color.b > 31.62/255.0){
        color = vec4(0.7176, 0.8549, 0.4471, color.a);
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
