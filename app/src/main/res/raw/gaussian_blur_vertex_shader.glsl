attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoord;
const int GAUSSIAN_SAMPLES = 9;
uniform vec2 uTexelOffset;
varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];
void main()
{
    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
    gl_Position = aPosition;
    // Calculate the positions for the blur +
    int multiplier = 0;
    vec2 blurStep;
    for (int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));
       // Blur in x (horizontal) +
        blurStep = float(multiplier) * uTexelOffset;
        blurCoordinates[i] = vTextureCoord.xy + blurStep;
    }
}