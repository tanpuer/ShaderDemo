#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform float uAlpha;
void main()
{
    gl_FragColor = vec4(texture2D(uTextureSampler, vTextureCoord).rgb, uAlpha);
}
