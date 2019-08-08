#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
varying vec2 vPosition;
uniform float uRotate;
void main()
{
    if (uRotate > 0.0) {
        gl_FragColor = texture2D(uTextureSampler, vec2(vTextureCoord.y, vTextureCoord.x));
    } else {
        gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
    }
}
