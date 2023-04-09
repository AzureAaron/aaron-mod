#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform float Time;
uniform vec4 FogColor;
uniform vec2 ScreenSize;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

bool isBrown(vec4 v) {
    // Checks if the colour is the "brown" colour (#AA5500)
    return v.r == 168.0/255.0 && v.g == 84.0/255.0 && v.b == 0.0/255.0 && v.a == 1.0;
}

void main() {
    vec4 colour = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (colour.a < 0.1) {
	    discard;
    }
	
    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);

    if (isBrown(fragColor)) {
        // Calculate a diagonal gradient by adding sine waves at different frequencies and phases
        vec2 uv = gl_FragCoord.xy / ScreenSize.xy;
        float freq = 6.0;
        float phase = (Time * 20) * 2.0;
        colour.r = 0.5 + 0.5 * sin(freq * (-uv.x + uv.y) + phase);
        colour.g = 0.5 + 0.5 * sin(freq * (-uv.x + uv.y + 1.0/3.0) + phase);
        colour.b = 0.5 + 0.5 * sin(freq * (-uv.x + uv.y + 2.0/3.0) + phase);
    }

    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}