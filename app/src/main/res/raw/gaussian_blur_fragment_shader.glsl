#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
const int GAUSSIAN_SAMPLES = 9;
varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];
void main()
{
    vec3 sum = vec3(0.0);
    vec4 fragColor=texture2D(uTextureSampler,vTextureCoord);
    sum += texture2D(uTextureSampler, blurCoordinates[0]).rgb * 0.05;
    sum += texture2D(uTextureSampler, blurCoordinates[1]).rgb * 0.09;
    sum += texture2D(uTextureSampler, blurCoordinates[2]).rgb * 0.12;
    sum += texture2D(uTextureSampler, blurCoordinates[3]).rgb * 0.15;
    sum += texture2D(uTextureSampler, blurCoordinates[4]).rgb * 0.18;
    sum += texture2D(uTextureSampler, blurCoordinates[5]).rgb * 0.15;
    sum += texture2D(uTextureSampler, blurCoordinates[6]).rgb * 0.12;
    sum += texture2D(uTextureSampler, blurCoordinates[7]).rgb * 0.09;
    sum += texture2D(uTextureSampler, blurCoordinates[8]).rgb * 0.05;
    gl_FragColor = vec4(sum,fragColor.a);
}