#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
varying vec4 vCoord;
uniform float uCornerRadiusX;
uniform float uCornerRadiusY;
uniform float uRatio;
void main()
{
    float diffX = abs(vCoord.x) - 1.0 + uCornerRadiusX;
    float diffY = abs(vCoord.y) - 1.0 + uCornerRadiusY;
    if (diffX > 0.0 && diffY > 0.0) {
        if (sqrt(diffX * diffX + diffY * diffY / uRatio / uRatio) >= uCornerRadiusX) {
//            gl_FragColor = mix(vec4(1.0, 1.0, 1.0, 1.0), texture2D(uTextureSampler, vTextureCoord), 0.1);
            gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        } else {
            gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
        }
    } else {
        gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
    }
}
