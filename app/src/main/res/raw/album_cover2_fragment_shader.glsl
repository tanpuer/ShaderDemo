precision mediump float;
uniform sampler2D uTextureSampler;
varying vec2 vTextureCoord;
varying vec2 vPosition;
uniform float uWidth;
uniform float uHeight;
uniform float uWidthSection;
uniform float uHeightSection;
uniform float uVideoRatio;
uniform float uAlphaPos;
bool ignoreColor(vec2 position, float xNum, float yNum);
float getAlpha(vec2 position);
void main()
{
    float xNum = 4.0;
    float yNum = 5.0;
    float width = 0.02;
    if (ignoreColor(vPosition, uWidthSection, uHeightSection)) {
        gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
    } else {
        float alpha = getAlpha(vPosition);
        if (alpha < 0.01) {
            gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        } else if (alpha > 0.99) {
            gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
        } else {
            gl_FragColor = vec4(texture2D(uTextureSampler, vTextureCoord).xyz, alpha);
        }
    }
}
bool ignoreColor(vec2 position, float xNum, float yNum) {
    for (float i =0.0; i<= xNum; i++) {
        float top = 2.0 / xNum * i -1.0;
        if (position.x > (top - uWidth) && position.x < (top + uWidth)) {
            return true;
        }
    }
    for (float i = 0.0; i<= yNum; i++) {
        float left = 2.0 / yNum *i -1.0;
        if (position.y > (left - uHeight) && position.y < (left + uHeight)) {
            return true;
        }
    }
    return false;
}
float getAlpha(vec2 position) {
    if (position.y > uAlphaPos) {
        return 1.0;
    } else if (position.y < uAlphaPos - 0.2){
        return 0.0;
    } else {
        return 1.0 - (uAlphaPos - position.y) / 0.2;
    }
}
