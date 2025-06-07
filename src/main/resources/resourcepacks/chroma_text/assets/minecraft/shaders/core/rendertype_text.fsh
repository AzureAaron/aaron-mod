#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <aaron-mod:chroma.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
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

	fragColor = apply_fog(colour, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
