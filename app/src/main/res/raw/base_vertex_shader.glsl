attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
uniform mat4 uMVPMatrix;
void main()
{
  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
  gl_Position = uMVPMatrix * aPosition;
}