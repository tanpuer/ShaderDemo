attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
varying vec4 vCoord;
void main()
{
  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
  gl_Position = aPosition;
  vCoord = aPosition;
}