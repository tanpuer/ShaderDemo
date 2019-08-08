#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
    vec4 texture = texture2D(uTextureSampler, vTextureCoord);
    vec3 rgb = 1.0 - texture.rgb;
    gl_FragColor  = vec4(rgb, texture.w);
}
