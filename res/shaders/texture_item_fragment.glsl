
in vec2 fragment_tex_mapping;

out vec4 out_color;

uniform sampler2D TEXTURE_SAMPLER;

void main() {
    out_color = texture(TEXTURE_SAMPLER, fragment_tex_mapping);
}
