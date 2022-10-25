
layout(location=0) in vec2 in_position;

out vec2 fragment_position;

void main() {
    gl_Position = vec4(in_position, 0.0, 1.0);
    fragment_position = in_position;
}
