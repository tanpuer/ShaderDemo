attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
uniform mat4 uCoordMatrix;
void main()
{
  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
  gl_Position = uCoordMatrix * aPosition;
}