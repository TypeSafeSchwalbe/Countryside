
in vec3 fragment_position;
flat in vec3 fragment_normal;
in vec2 fragment_tex_mapping;

out vec4 out_color;

const int LIGHT_COUNT = 16;

uniform vec3[LIGHT_COUNT] LIGHT_POSITIONS;
uniform float[LIGHT_COUNT] LIGHT_STRENGTHS;
uniform float AMBIENT_STRENGTH;

uniform sampler2D NORMAL_TEXTURE_SAMPLER;
uniform sampler2D BRIGHT_TEXTURE_SAMPLER;

void main() {
    float max_light_diffuse = 0.0;
    for(int light_index = 0; light_index < LIGHT_COUNT; light_index++) {
        float light_diffuse = max(getLightDiffuse(LIGHT_POSITIONS[light_index], LIGHT_STRENGTHS[light_index], fragment_position, fragment_normal), getLightDiffuse(LIGHT_POSITIONS[light_index], LIGHT_STRENGTHS[light_index], fragment_position, fragment_normal * -1.0));
        max_light_diffuse = max(light_diffuse, max_light_diffuse);
    }
    if(max_light_diffuse < 0.5) {
        vec4 tex_value = texture(NORMAL_TEXTURE_SAMPLER, fragment_tex_mapping);
        out_color = vec4(tex_value.rgb * AMBIENT_STRENGTH, tex_value.a);
    } else out_color = texture(BRIGHT_TEXTURE_SAMPLER, fragment_tex_mapping);
    if(out_color == vec4(0, 0, 0, 0)) gl_FragDepth = 1.0;
    else gl_FragDepth = gl_FragCoord.z;
}
