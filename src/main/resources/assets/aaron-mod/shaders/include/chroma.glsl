#version 150

// The epsilon used for checking the text's colour
const float _EPSILON = 0.001;
// Full brightness used for normal text
const float _NORMAL_VALUE = 1.0;
// Quarter brightness used for text shadows
const float _SHADOW_VALUE = 0.25;

// In the event of a conflict, you can prevent the uniforms from being included in your shader automatically (but they all need to be declared before the import!)
#ifndef AARON_MOD_DONT_DECLARE_UNIFORMS
uniform vec2 ScreenSize;
uniform float Time;
uniform float ChromaSpeed;
uniform float ChromaSaturation;
#endif

/// Internal Methods - may change at any time!

// Checks if the colour is the "brown" colour (#AA5500)
bool _isBrown(vec4 colour) {
    // Compare the rgb colour components with the epsilon
    return abs(colour.r - 170.0/255.0) < _EPSILON &&
            abs(colour.g - 85.0/255.0) < _EPSILON &&
            abs(colour.b - 0.0/255.0) < _EPSILON;
}

// Checks if the colour is the shadowed "brown" colour (#2A1500)
// NB: The colour of shadowed text is the text's colour divided by 4
bool _isShadowedBrown(vec4 colour) {
    // Compare the rgb colour components with the epsilon
    return abs(colour.r - 42.0/255.0) < _EPSILON &&
            abs(colour.g - 21.0/255.0) < _EPSILON &&
            abs(colour.b - 0.0/255.0) < _EPSILON;
}

vec3 _hsv2rgb_smooth(vec3 c) {
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    rgb = rgb * rgb * (3.0 - 2.0 * rgb); // Cubic smoothing - smooths out colour transitions

    return c.z * mix(vec3(1.0), rgb, c.y);
}

vec4 _applyChromaColour(vec4 textColour, float v) {
    vec2 uv = gl_FragCoord.xy / ScreenSize.xy; // Normalize the coordinates to a range of [0, 1]
    float offset = Time * clamp(ChromaSpeed, 1.0, 64.0); // Adjust the speed of the animation

    // Move the gradient horizontally from the top left to the bottom right
    uv.x -= uv.y;
    uv.y = 0.0;

    float h = mod(offset + -uv.x * 1.75, 1.0); // Vary the hue based on uv.x and time
    float s = clamp(ChromaSaturation, 0.0, 1.0); // Default saturation is at 0.75 for 3/4 saturation

    vec3 hsv = vec3(h, s, v);
    vec3 rgb = _hsv2rgb_smooth(hsv);

    // Return a new vector containing the chroma colour with the original alpha value
    return vec4(rgb, textColour.a);
}

/// Public Methods

// Applies the chroma text effect to eligible text.
// Returns either the textColour vector or a new vector containing the chroma colour if it was applied (the original colour's alpha is preserved upon application).
//
// originalColour is the vector containing the original colour of the text as passed from the vertex shader
// textColour     is the vector that holds the colour for the fragment after being computed
vec4 applyChroma(vec4 originalColour, vec4 textColour) {
    if (_isBrown(originalColour)) {
        return _applyChromaColour(textColour, _NORMAL_VALUE);
    }

    if (_isShadowedBrown(originalColour)) {
        return _applyChromaColour(textColour, _SHADOW_VALUE);
    }

    return textColour;
}
