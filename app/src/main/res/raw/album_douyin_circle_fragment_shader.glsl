precision mediump float;
uniform sampler2D uTextureSampler;
varying vec2 vTextureCoord;
uniform float uRatio;
uniform float uRadius;
varying vec4 vPosition;
uniform float uCenterX;
uniform float uCenterY;
void main()
{
    if (pow((vPosition.x - uCenterX), 2.0) + pow((vPosition.y - uCenterY) / uRatio, 2.0) <= uRadius) {
        gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
    } else {
        discard;
    }
}
