#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform vec2 uBlurCenter;
uniform float uBlurSize;
void main() {
    vec2 samplingOffset = 1.0/100.0 * (uBlurCenter - vTextureCoord) * uBlurSize;
    vec4 fragmentColor = texture2D(uTextureSampler, vTextureCoord) * 0.18;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord + samplingOffset) * 0.15;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord + (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord + (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord + (4.0 * samplingOffset)) * 0.05;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord - samplingOffset) * 0.15;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord - (2.0 * samplingOffset)) *  0.12;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord - (3.0 * samplingOffset)) * 0.09;
    fragmentColor += texture2D(uTextureSampler, vTextureCoord - (4.0 * samplingOffset)) * 0.05;
    gl_FragColor = fragmentColor;
}