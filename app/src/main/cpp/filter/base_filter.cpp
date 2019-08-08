//
// Created by templechen on 2019/3/15.
//

#include "base_filter.h"
#include "../common/matrix_util.h"
#include <android/surface_texture.h>
#include <android/surface_texture_jni.h>
#include <android/asset_manager_jni.h>

base_filter::base_filter(int textureId) {
    oesTextureId = static_cast<GLuint>(textureId);
    initMatrix();
}

base_filter::~base_filter() {
    auto textures = new GLuint[1];
    textures[0] = oesTextureId;
    glDeleteTextures(1, textures);

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    glDeleteProgram(program);

    delete vertex_shader_string;
    delete fragment_shader_string;

    delete textureMatrix;
    delete coordMatrix;
}

void base_filter::initProgram() {
    readShaderString();
    vertexShader = loadShader(GL_VERTEX_SHADER, vertex_shader_string);
    fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragment_shader_string);
    program = createShaderProgram(vertexShader, fragmentShader);
}

void base_filter::doFrame() {
    glUseProgram(program);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, oesTextureId);

    GLint vertexCount = sizeof(myVertex) / (sizeof(myVertex[0]) * 2);
    aPositionLocation = glGetAttribLocation(program, aPosition);
    glEnableVertexAttribArray(aPositionLocation);
    glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, GL_FALSE, 8, myVertex);

    aTextureCoordinateLocation = glGetAttribLocation(program, aTextureCoordinate);
    glEnableVertexAttribArray(aTextureCoordinateLocation);
    glVertexAttribPointer(aTextureCoordinateLocation, 2, GL_FLOAT, GL_FALSE, 8, texture);

    uTextureMatrixLocation = glGetUniformLocation(program, uTextureMatrix);
//    glUniformMatrix4fv(uTextureMatrixLocation, 1, GL_FALSE, this->transformMatrix);
    glUniformMatrix4fv(uTextureMatrixLocation, 1, GL_FALSE, this->textureMatrix->m);

    uCoordMatrixLocation = glGetUniformLocation(program, uCoordMatrix);
    glUniformMatrix4fv(uCoordMatrixLocation, 1, GL_FALSE, this->coordMatrix->m);

    glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    glDisableVertexAttribArray(aPositionLocation);
    glDisableVertexAttribArray(aTextureCoordinateLocation);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);

}

void base_filter::readShaderString() {
    vertex_shader_string = {
            "uniform mat4 uTextureMatrix;\n"
            "uniform mat4 uCoordMatrix;\n"
            "attribute vec4 aPosition;\n"
            "attribute vec4 aTextureCoordinate;\n"
            "varying vec2 vTextureCoord;\n"
            "void main()\n"
            "{\n"
            "    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n"
            "    gl_Position = uCoordMatrix * aPosition;\n"
            "}\n"
    };
    fragment_shader_string = {
            "#extension GL_OES_EGL_image_external : require\n"
            "precision mediump float;\n"
            "uniform samplerExternalOES uTextureSampler;\n"
            "varying vec2 vTextureCoord;\n"
            "void main()\n"
            "{\n"
            "    gl_FragColor = texture2D(uTextureSampler, vTextureCoord);\n"
            "}\n"
    };
}

void base_filter::initMatrix() {
    textureMatrix = new ESMatrix();
    textureMatrix->m[0] = 1.0f;
    textureMatrix->m[1] = 0.0f;
    textureMatrix->m[2] = 0.0f;
    textureMatrix->m[3] = 0.0f;
    textureMatrix->m[4] = 0.0f;
    textureMatrix->m[5] = -1.0f;
    textureMatrix->m[6] = 0.0f;
    textureMatrix->m[7] = 0.0f;
    textureMatrix->m[8] = 0.0f;
    textureMatrix->m[9] = 0.0f;
    textureMatrix->m[10] = 1.0f;
    textureMatrix->m[11] = 0.0f;
    textureMatrix->m[12] = 0.0f;
    textureMatrix->m[13] = 1.0f;
    textureMatrix->m[14] = 0.0f;
    textureMatrix->m[15] = 1.0f;

    coordMatrix = new ESMatrix();
    setIdentityM(coordMatrix);
}

void
base_filter::setScaleAndTransform(float scaleX, float scaleY, float transformX, float transformY,
                                  int rotateDegrees) {
    setIdentityM(coordMatrix);
    scaleM(coordMatrix, 0, scaleX, scaleY, 1.0f);
    translateM(coordMatrix, 0, transformX / scaleX, transformY / scaleY, 0.0f);
//    rotateM(coordMatrix, rotateDegrees, 0.0f, 0.0f, 1.0f);
    updateVertexCoord(rotateDegrees);
}

void base_filter::setVideoSize(int width, int height) {
    this->videoWidth = width;
    this->videoHeight = height;
}

void base_filter::setViewSize(int width, int height) {
    this->viewWidth = width;
    this->viewHeight = height;
}

void base_filter::updateVertexCoord(float degrees) {
    if (viewWidth > 0 && viewHeight > 0 && videoWidth > 0 && videoHeight > 0) {
        float ratio = videoWidth * 1.0f / videoHeight;
        float radian = -degrees * PI / 180;

        PointF *pointF1 = new PointF();
        pointF1->x = 1.0;
        pointF1->y = 1.0;
        PointF *pointF2 = new PointF();
        pointF2->x = -1.0;
        pointF2->y = 1.0;
        PointF *pointF3 = new PointF();
        pointF3->x = -1.0;
        pointF3->y = -1.0;
        PointF *pointF4 = new PointF();
        pointF4->x = 1.0;
        pointF4->y = -1.0;
        PointF *vertexCoord[4] = {
                pointF1, pointF2, pointF3, pointF4
        };

        for (int i = 0; i < 4; ++i) {
            PointF *pointF = vertexCoord[i];
            PointF *newPointF = new PointF();
            newPointF->x = (pointF->x) * cos(radian) - (pointF->y / ratio) * sin(radian);
            newPointF->y = ((pointF->x) * sin(radian) + (pointF->y / ratio) * cos(radian)) * ratio;
            vertexCoord[i] = newPointF;
        }
        myVertex[0] = vertexCoord[0]->x;
        myVertex[1] = vertexCoord[0]->y;
        myVertex[2] = vertexCoord[1]->x;
        myVertex[3] = vertexCoord[1]->y;
        myVertex[4] = vertexCoord[2]->x;
        myVertex[5] = vertexCoord[2]->y;
        myVertex[6] = vertexCoord[0]->x;
        myVertex[7] = vertexCoord[0]->y;
        myVertex[8] = vertexCoord[2]->x;
        myVertex[9] = vertexCoord[2]->y;
        myVertex[10] = vertexCoord[3]->x;
        myVertex[11] = vertexCoord[3]->y;
    };

}

