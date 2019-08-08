#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform float uImageWidthFactor;
uniform float uImageHeightFactor;
uniform float uPixel;
void main()
{
  vec2 uv  = vTextureCoord.xy;
  float dx = uPixel * uImageWidthFactor;
  float dy = uPixel * uImageHeightFactor;
  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
  vec3 tc = texture2D(uTextureSampler, coord).xyz;
  gl_FragColor = vec4(tc, 1.0);
}