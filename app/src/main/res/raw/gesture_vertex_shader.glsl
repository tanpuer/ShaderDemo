attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
uniform mat4 uCoordMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
void main()
{
    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
    gl_Position = uCoordMatrix * aPosition;
}