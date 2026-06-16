#version 330

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#moj_import <minecraft:fog.glsl>
#endif

#moj_import <minecraft:dynamictransforms.glsl>

#ifdef AARON_MOD_CHROMA
#moj_import <minecraft:globals.glsl>
#moj_import <aaron-mod:chroma.glsl>
#endif

uniform sampler2D Sampler0;

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
in float sphericalVertexDistance;
in float cylindricalVertexDistance;
#endif

in vec4 vertexColor;
in vec2 texCoord0;

#ifdef AARON_MOD_CHROMA
in vec4 rawColour;
#endif

out vec4 fragColor;

void main() {
#ifdef IS_GRAYSCALE
	vec4 texColor = texture(Sampler0, texCoord0).rrrr;
#else
	vec4 texColor = texture(Sampler0, texCoord0);
#endif

#ifdef IS_SEE_THROUGH
	vec4 color = texColor * vertexColor;
#else
	vec4 color = texColor * vertexColor * ColorModulator;
#endif
	if (color.a < 0.1) {
		discard;
	}

#ifdef AARON_MOD_CHROMA
	color = applyChroma(rawColour, color);
#endif

#ifdef IS_SEE_THROUGH
	fragColor = color * ColorModulator;
#elif defined(IS_GUI)
	fragColor = color;
#else
	fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
#endif
}
