#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <aaron-mod:chroma.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

#ifdef AARON_MOD_CHROMA
in vec4 rawColour;
#endif

out vec4 fragColor;

void main() {
    vec4 colour = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    if (colour.a < 0.1) {
        discard;
    }

#ifdef AARON_MOD_CHROMA
    colour = applyChroma(rawColour, colour);
#endif

    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}
