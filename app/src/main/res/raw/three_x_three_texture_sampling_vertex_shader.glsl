attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
uniform float uTexelWidth;
uniform float uTexelHeight;
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
    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
    gl_Position = aPosition;
    vec2 widthStep = vec2(uTexelWidth, 0.0);
    vec2 heightStep = vec2(0.0, uTexelHeight);
    vec2 widthHeightStep = vec2(uTexelWidth, uTexelHeight);
    vec2 widthNegativeHeightStep = vec2(uTexelWidth, -uTexelHeight);
    textureCoordinate = vTextureCoord;
    leftTextureCoordinate = vTextureCoord - widthStep;
    rightTextureCoordinate = vTextureCoord + widthStep;
    topTextureCoordinate = vTextureCoord - heightStep;
    topLeftTextureCoordinate = vTextureCoord - widthHeightStep;
    topRightTextureCoordinate = vTextureCoord + widthNegativeHeightStep;
    bottomTextureCoordinate = vTextureCoord + heightStep;
    bottomLeftTextureCoordinate = vTextureCoord - widthNegativeHeightStep;
    bottomRightTextureCoordinate = vTextureCoord + widthHeightStep;
}
