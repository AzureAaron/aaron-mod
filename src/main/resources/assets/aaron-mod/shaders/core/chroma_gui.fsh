#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <aaron-mod:chroma.glsl>

in vec4 vertexColor;

out vec4 fragColor;

void main() {
	vec4 colour = vertexColor;
	colour = applyChroma(vertexColor, colour);

	if (colour.a == 0.0) {
		discard;
	}

	fragColor = colour * ColorModulator;
}
