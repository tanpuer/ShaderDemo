precision mediump float;
uniform sampler2D uWaterMarkTextureSampler;
varying vec2 vTextureCoord;
void main()
{
    gl_FragColor = texture2D(uWaterMarkTextureSampler, vTextureCoord);
}
