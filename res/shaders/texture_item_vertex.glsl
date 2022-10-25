
layout(location=0) in vec2 in_position;
layout(location=1) in vec2 in_tex_mapping;

out vec2 fragment_tex_mapping;

uniform mat4 PROJECTION_VIEW_MATRIX;

void main() {
    gl_Position = PROJECTION_VIEW_MATRIX * vec4(in_position, 0.0, 1.0);
    fragment_tex_mapping = in_tex_mapping;
}
