#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
varying vec2 textureCoordinate;
varying vec2 leftTextureCoordinate;
varying vec2 rightTextureCoordinate;
varying vec2 topTextureCoordinate;
varying vec2 topLeftTextureCoordinate;
varying vec2 topRightTextureCoordinate;
varying vec2 bottomTextureCoordinate;
varying vec2 bottomLeftTextureCoordinate;
varying vec2 bottomRightTextureCoordinate;
void main()
{
    float bottomLeftIntensity = texture2D(uTextureSampler, bottomLeftTextureCoordinate).r;
    float topRightIntensity = texture2D(uTextureSampler, topRightTextureCoordinate).r;
    float topLeftIntensity = texture2D(uTextureSampler, topLeftTextureCoordinate).r;
    float bottomRightIntensity = texture2D(uTextureSampler, bottomRightTextureCoordinate).r;
    float leftIntensity = texture2D(uTextureSampler, leftTextureCoordinate).r;
    float rightIntensity = texture2D(uTextureSampler, rightTextureCoordinate).r;
    float bottomIntensity = texture2D(uTextureSampler, bottomTextureCoordinate).r;
    float topIntensity = texture2D(uTextureSampler, topTextureCoordinate).r;
    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;
    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;
    float mag = 1.0 - length(vec2(h, v));
    gl_FragColor = vec4(vec3(mag), 1.0);
}