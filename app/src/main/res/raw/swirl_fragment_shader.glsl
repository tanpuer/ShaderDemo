#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform vec2 uCenter;
uniform float uRadius;
uniform float uAngle;
void main()
{
    vec2 vTextureCoordToUse = vTextureCoord;
    float dist = distance(uCenter, vTextureCoord);
    if (dist < uRadius)
    {
        vTextureCoordToUse -= uCenter;
        float percent = (uRadius - dist) / uRadius;
        float theta = percent * percent * uAngle * 8.0;
        float s = sin(theta);
        float c = cos(theta);
        vTextureCoordToUse = vec2(dot(vTextureCoordToUse, vec2(c, -s)), dot(vTextureCoordToUse, vec2(s, c)));
        vTextureCoordToUse += uCenter;
    }
    gl_FragColor = texture2D(uTextureSampler, vTextureCoordToUse );
}