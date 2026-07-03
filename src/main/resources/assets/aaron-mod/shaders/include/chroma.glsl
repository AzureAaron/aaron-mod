#version 330

const vec3 CHROMA_TEXT_COLOUR = vec3(170.0, 85.0, 0.0) / 255.0;

layout(std140) uniform Chroma {
	float Ticks;
	float ChromaSize;
	float ChromaSpeed;
	float ChromaSaturation;
};

float getValue(vec3 colour) {
	return max(colour.r, max(colour.g, colour.b));
}

vec3 hsv2rgb_smooth(vec3 c) {
	vec3 rgb = abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0;
	rgb = smoothstep(0.0, 1.0, rgb); // Cubic smoothing - smooths out colour transitions

	return c.z * mix(vec3(1.0), rgb, c.y);
}

vec4 applyChromaColour(vec4 originalColour, float v) {
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
	return vec4(rgb, originalColour.a);
}

vec4 applyChromaTextColour(vec4 textColour) {
	// Infer the value/brightness from the text's colour
	float baseBrightness = getValue(CHROMA_TEXT_COLOUR);
	float textBrightness = getValue(textColour.rgb);
	// Since the default chroma colour isn't white it won't have a brightness of 1.0 which means the colour
	// is always darker than wanted, so this method normalizes the textColour's brightness against the baseBrightness.
	float normalizedValue = clamp(textBrightness / baseBrightness, 0.0, 1.0);

	return applyChromaColour(textColour, normalizedValue);
}
