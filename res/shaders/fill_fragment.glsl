
uniform float ALPHA;
uniform int TIMESTEP;

in vec2 fragment_position;

out vec4 out_color;

void main() {
    float distance_to_center = min(pow(length(fragment_position), 3.0) / 3.0, 1.0);
    out_color = vec4(random(gl_FragCoord.xy, float(TIMESTEP) + 0.1), random(gl_FragCoord.xy, float(TIMESTEP) + 0.2), random(gl_FragCoord.xy, float(TIMESTEP) + 0.3), ALPHA * distance_to_center);
}
