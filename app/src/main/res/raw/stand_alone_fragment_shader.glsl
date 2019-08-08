#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform float uLeftBorder;
uniform float uRightBorder;
uniform float uAlpha;
void main()
{
    vec3 color = texture2D(uTextureSampler, vTextureCoord).xyz;
    if (vTextureCoord.x < uLeftBorder || vTextureCoord.x > uRightBorder) {
        gl_FragColor = vec4(color, uAlpha);
    } else {
        gl_FragColor = vec4(color, 1.0);
    }
}
