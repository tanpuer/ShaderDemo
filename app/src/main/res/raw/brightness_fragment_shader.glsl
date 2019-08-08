#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform float uBrightness;
void main()
{
    vec4 textureColor = texture2D(uTextureSampler, vTextureCoord);
    gl_FragColor = vec4((textureColor.rgb + vec3(uBrightness)), textureColor.w);
}
