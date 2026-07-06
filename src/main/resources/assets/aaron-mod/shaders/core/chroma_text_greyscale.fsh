#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

#ifdef AARON_MOD_CHROMA
#moj_import <minecraft:globals.glsl>
#moj_import <aaron-mod:chroma.glsl>
#endif

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0).rrrr * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }

#ifdef AARON_MOD_CHROMA
	color = applyChromaTextColour(color);
#endif

    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
