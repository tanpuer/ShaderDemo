precision mediump float;
uniform sampler2D uTextureSampler;
varying vec2 vTextureCoord;
uniform float uRatio;
uniform float uRadius;
varying vec4 vPosition;
void main()
{
    float value = pow(vPosition.x, 2.0) + pow(vPosition.y / uRatio, 2.0);
    if (pow(vPosition.x, 2.0) + pow(vPosition.y / uRatio, 2.0) <= uRadius) {
        vec4 color = texture2D(uTextureSampler, vTextureCoord);
        if (value >= uRadius - 0.02) {
            gl_FragColor = vec4(mix(color.rgb, vec3(1.0, 1.0, 0.0), 0.5), 1.0);
        } else {
            gl_FragColor = color;
        }
    } else {
        discard;
    }
}
