
layout(location=0) in vec3 in_position;
layout(location=1) in vec3 in_normal;
layout(location=2) in vec2 in_tex_mapping;

out vec3 fragment_position;
flat out vec3 fragment_normal;
out vec2 fragment_tex_mapping;

uniform mat4 PROJECTION_VIEW_MATRIX;
uniform mat4 MODEL_MATRIX;
uniform mat4 MODEL_ROTATION_MATRIX;

void main() {
    gl_Position = PROJECTION_VIEW_MATRIX * MODEL_MATRIX * vec4(in_position, 1.0);
    fragment_position = (MODEL_MATRIX * vec4(in_position, 1)).xyz;
    fragment_normal = (MODEL_ROTATION_MATRIX * vec4(in_normal, 1)).xyz;
    fragment_tex_mapping = in_tex_mapping;
}
