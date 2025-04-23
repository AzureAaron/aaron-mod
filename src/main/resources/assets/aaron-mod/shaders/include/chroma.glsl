#version 150

const vec3 BROWN = vec3(170.0, 85.0, 0.0) / 255.0;
const vec3 SHADOWED_BROWN = vec3(42.0, 21.0, 0.0) / 255.0; // The colour of the text shadow is the text's colour divided by 4
const vec3 EPSILON = vec3(0.001); // The epsilon used for checking the text's colour
const float NORMAL_VALUE = 1.0; // Full brightness used for normal text
const float SHADOW_VALUE = 0.25; // Quarter brightness used for text shadows

// In the event of a conflict, you can prevent the uniforms from being included in your shader automatically (but they all need to be declared before the import!)
#ifndef AARON_MOD_DONT_DECLARE_UNIFORMS
uniform vec2 ScreenSize;
uniform float Ticks;
uniform float ChromaSize;
uniform float ChromaSpeed;
uniform float ChromaSaturation;
#endif

/// Internal Methods - may change at any time!

bool matchesColour(vec4 colour, vec3 targetColour) {
	return all(lessThan(abs(colour.rgb - targetColour), EPSILON));
}

vec3 hsv2rgb_smooth(vec3 c) {
	vec3 rgb = abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0;
	rgb = smoothstep(0.0, 1.0, rgb); // Cubic smoothing - smooths out colour transitions

	return c.z * mix(vec3(1.0), rgb, c.y);
}

vec4 applyChromaColourInternal(vec4 textColour, float v) {
	vec2 screenSize = clamp(ChromaSize, 1.0, 200.0) * (ScreenSize / 100.0); // Scale the screen size to increase/decrease the size of colours in the gradient
	vec2 uv = gl_FragCoord.xy / screenSize; // Normalize the coordinates to a range of [0, 1]
	float offset = Ticks * (clamp(ChromaSpeed, 1.0, 64.0) / 360.0); // Adjust the speed of the animation

	// Move the gradient horizontally from the top left to the bottom right
	uv.x = uv.y - uv.x;
	uv.y = 0.0;

	float h = mod(offset + uv.x * 1.75, 1.0); // Vary the hue based on uv.x and time
	float s = clamp(ChromaSaturation, 0.0, 1.0); // Default saturation is at 0.75 for 3/4 saturation

	vec3 hsv = vec3(h, s, v);
	vec3 rgb = hsv2rgb_smooth(hsv);

	// Return a new vector containing the chroma colour with the original alpha value
	return vec4(rgb, textColour.a);
}

/// Public Methods

// Applies the chroma text effect to eligible text.
// Returns either the textColour vector or a new vector containing the chroma colour if it was applied which preserves the original alpha value.
//
// originalColour is the vector containing the original colour of the text as passed from the vertex shader
// textColour     is the vector that holds the colour for the fragment after being computed
vec4 applyChroma(vec4 originalColour, vec4 textColour) {
	if (matchesColour(originalColour, BROWN)) {
		return applyChromaColourInternal(textColour, NORMAL_VALUE);
	}

	if (matchesColour(originalColour, SHADOWED_BROWN)) {
		return applyChromaColourInternal(textColour, SHADOW_VALUE);
	}

	return textColour;
}
