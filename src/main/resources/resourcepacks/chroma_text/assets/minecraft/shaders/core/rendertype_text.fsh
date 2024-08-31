#version 150

#moj_import <fog.glsl>

// Set a small epsilon value
const float EPSILON = 0.001;

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform float Time;
uniform float ChromaSpeed;
uniform float ChromaSaturation;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 rawColour;

out vec4 fragColor;

// Checks if the colour is the "brown" colour (#AA5500)
bool isBrown(vec4 v) {
    // Compare the colour components with epsilon
    return abs(v.r - 170.0/255.0) < EPSILON &&
            abs(v.g - 85.0/255.0) < EPSILON &&
            abs(v.b - 0.0/255.0) < EPSILON;
}

// Checks if the colour is the shadowed "brown" colour (#2A1500)
// NB: The colour of shadowed text is the text's colour divided by 4
bool isShadowedBrown(vec4 v) {
    // Compare the colour components with epsilon
    return abs(v.r - 42.0/255.0) < EPSILON &&
            abs(v.g - 21.0/255.0) < EPSILON &&
            abs(v.b - 0.0/255.0) < EPSILON;
}

vec3 hsv2rgb_smooth(vec3 c) {
    vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    rgb = rgb * rgb * (3.0 - 2.0 * rgb); // Cubic smoothing - smooths out colour transitions

    return c.z * mix(vec3(1.0), rgb, c.y);
}

void main() {
    vec4 colour = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

    if (colour.a < 0.1) {
        discard;
    }

    if (isBrown(rawColour)) {
        vec2 uv = gl_FragCoord.xy / ScreenSize.xy; // Normalize coordinates to range [0, 1]
        float offset = Time * clamp(ChromaSpeed, 1.0, 64.0); // Adjust the speed of the animation
    
        // Move the gradient horizontally from the top left to the bottom right
        uv.x -= uv.y;
        uv.y = 0.0;

        float h = mod(offset + -uv.x * 1.75, 1.0); // Vary the hue based on UV.x and time
        float s = clamp(ChromaSaturation, 0.0, 1.0); // Default saturation is at 0.75 for 3/4 saturation
        float v = 1.0; // Keep value constant at 1.0 for full brightness

        vec3 hsv = vec3(h, s, v);
        vec3 rgb = hsv2rgb_smooth(hsv);

        colour.r = rgb.r;
        colour.g = rgb.g;
        colour.b = rgb.b;
    }

    if (isShadowedBrown(rawColour)) {
        vec2 uv = gl_FragCoord.xy / ScreenSize.xy; // Normalize coordinates to range [0, 1]
        float offset = Time * clamp(ChromaSpeed, 1.0, 64.0); // Adjust the speed of the animation
    
        // Move the gradient horizontally from the top left to the bottom right
        uv.x -= uv.y;
        uv.y = 0.0;

        float h = mod(offset + -uv.x * 1.75, 1.0); // Vary the hue based on UV.x and time
        float s = clamp(ChromaSaturation, 0.0, 1.0); // Default saturation is at 0.75 for 3/4 saturation
        float v = 0.25; // Keep value constant at 0.25 for quarter brightness

        vec3 hsv = vec3(h, s, v);
        vec3 rgb = hsv2rgb_smooth(hsv);

        colour.r = rgb.r;
        colour.g = rgb.g;
        colour.b = rgb.b;
    }

    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}
