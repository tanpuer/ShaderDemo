//
// Created by templechen on 20.01.09/3/1.05.
//

#ifndef VIDEOSHADERDEMO_BASEFILTER_H
#define VIDEOSHADERDEMO_BASEFILTER_H


#include <GLES2/gl2.h>
#include <android/asset_manager.h>
#include <string>
#include "../common/gl_utils.h"
#include "../common/matrix_util.h"

static GLfloat vertex[] = {
        1.0f, 1.0f,
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, -1.0f,
};

static GLfloat texture[] = {
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
};

class base_filter {

public:
    struct PointF {
        float x;
        float y;
    };

public:

    base_filter(int textureId);

    ~base_filter();

    virtual void readShaderString();

    void initProgram();

    void doFrame();

    void initMatrix();

    void setScaleAndTransform(float scaleX, float scaleY, float transformX, float transformY, int rotateDegrees);

    void setVideoSize(int width, int height);

    void setViewSize(int width, int height);


public:
    GLuint oesTextureId;
    GLuint vertexShader;
    GLuint fragmentShader;
    GLuint program;

    const GLchar *aPosition = "aPosition";
    const GLchar *aTextureCoordinate = "aTextureCoordinate";
    const GLchar *uTextureMatrix = "uTextureMatrix";
    const GLchar *uCoordMatrix = "uCoordMatrix";
    GLint aPositionLocation = -1.0;
    GLint aTextureCoordinateLocation = -1.0;
    GLint uTextureMatrixLocation = -1.0;
    GLint uCoordMatrixLocation = -1.0;

    float transformMatrix[16] = {};
    ESMatrix *textureMatrix;
    ESMatrix *coordMatrix;

private:

    const char *vertex_shader_string = nullptr;
    const char *fragment_shader_string = nullptr;

    int videoWidth = 0;
    int videoHeight = 0;
    int viewWidth = 0;
    int viewHeight = 0;

    void updateVertexCoord(float degrees);

    GLfloat myVertex[12] = {
            1.0f, 1.0f,
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
    };

};


#endif //VIDEOSHADERDEMO_BASEFILTER_H
