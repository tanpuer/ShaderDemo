#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform vec2 uCenter;
uniform float uRadius;
uniform float uAspectRatio;
uniform float uRefractiveIndex;
const vec3 lightPosition = vec3(-0.5, 0.5, 1.0);
const vec3 ambientLightPosition = vec3(0.0, 0.0, 1.0);
void main()
{
    vec2 vTextureCoordToUse = vec2(vTextureCoord.x, (vTextureCoord.y * uAspectRatio + 0.5 - 0.5 * uAspectRatio));
    float distanceFromCenter = distance(uCenter, vTextureCoordToUse);
    float checkForPresenceWithinSphere = step(distanceFromCenter, uRadius);
    distanceFromCenter = distanceFromCenter / uRadius;
    float normalizedDepth = uRadius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);
    vec3 sphereNormal = normalize(vec3(vTextureCoordToUse - uCenter, normalizedDepth));
    vec3 refractedVector = 2.0 * refract(vec3(0.0, 0.0, -1.0), sphereNormal, uRefractiveIndex);
    refractedVector.xy = -refractedVector.xy;
    vec3 finalSphereColor = texture2D(uTextureSampler, (refractedVector.xy + 1.0) * 0.5).rgb;
    // Grazing angle lighting
    float lightingIntensity = 2.5 * (1.0 - pow(clamp(dot(ambientLightPosition, sphereNormal), 0.0, 1.0), 0.25));
    finalSphereColor += lightingIntensity;
    // Specular lighting
    lightingIntensity  = clamp(dot(normalize(lightPosition), sphereNormal), 0.0, 1.0);
    lightingIntensity  = pow(lightingIntensity, 15.0);
    finalSphereColor += vec3(0.8, 0.8, 0.8) * lightingIntensity;
    gl_FragColor = vec4(finalSphereColor, 1.0) * checkForPresenceWithinSphere;
}
