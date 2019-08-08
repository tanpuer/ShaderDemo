attribute vec4 aWaterMarkPosition;
uniform mat4 uWaterMarkMatrix;
attribute vec4 aWaterMarkTextureCoord;
varying vec2 vTextureCoord;
void main()
{
  vTextureCoord = (uWaterMarkMatrix * aWaterMarkTextureCoord).xy;
  gl_Position = aWaterMarkPosition;
}