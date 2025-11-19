#ifdef GL_ES
precision mediump float;
#endif

// same colors as your original
const vec3 mixColor1 = vec3(0.396, 0.137, 0.141);
const vec3 mixColor2 = vec3(0.396, 0.137, 0.141);

#define NUM_OCTAVES 5

uniform vec2  u_resolution;
uniform float u_time;

// Get random value
float random(in vec2 st)
{
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// Get noise
float noise(in vec2 st)
{
    vec2 i = floor(st);
    vec2 f = fract(st);

    float a = random(i + vec2(0.0, 0.0));
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x)
    + (c - a) * u.y * (1.0 - u.x)
    + (d - b) * u.x * u.y;
}

// fractional brownian motion
float fbm(in vec2 st)
{
    float v = 0.0;
    float a = 0.5;

    for (int i = 0; i < NUM_OCTAVES; i++)
    {
        v += a * noise(st);
        st = st * 2.0;
        a *= 0.5;
    }

    return v;
}

void main()
{
    // Shadertoy-style uv
    vec2 fragCoord = gl_FragCoord.xy;
    vec2 st = fragCoord / u_resolution;

    vec3 color = vec3(0.0);

    vec2 q = vec2(0.0);
    q.x = fbm(st + vec2(0.0));
    q.y = fbm(st + vec2(1.0));

    vec2 r = vec2(0.0);
    r.x = fbm(st + (4.0 * q) + vec2(1.7, 9.2) + (0.15 * u_time));
    r.y = fbm(st + (4.0 * q) + vec2(8.3, 2.8) + (0.12 * u_time));

    // Mixed color by 'q' and 'r'.
    color = mix(color, mixColor1, clamp(length(q), 0.0, 1.0));
    color = mix(color, mixColor2, clamp(length(r), 0.0, 1.0));

    // Domain warping
    float f = fbm(st + 4.0 * r);

    // f^3 + 0.6f^2 + 0.5f
    float coef = (f * f * f + (0.6 * f * f) + (0.5 * f));
    color *= coef;

    gl_FragColor = vec4(color, 1.0);
}
