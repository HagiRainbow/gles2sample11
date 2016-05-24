package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by tommy on 2015/06/29.
 */
public class Rectangular {
    //bufferの定義
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;
    private FloatBuffer normalBuffer;

    //頂点座標
    private float[] vertexs= new float[4*3];

    //頂点座標番号列
    private byte[] indexs= {
            0,2,1,3
    };
    //拡頂点の法線ベクトル
    private float[] normals= {
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f
    };

    Rectangular() {
        setRectangular(1f, 1f);
    }

    public void setRectangular(float width, float height) {
        float top=height*.5f;
        float bottom=-top;
        float right=width*.5f;
        float left=-right;

        //頂点座標
        float[] vertexs= {
                left, top, 0f,     //左上 0
                right, top, 0f,    //右上 1
                left, bottom, 0f,  //左下 2
                right, bottom, 0f      //右下 3
        };
        vertexBuffer=BufferUtil.makeFloatBuffer(vertexs);
        indexBuffer=BufferUtil.makeByteBuffer(indexs);
        normalBuffer=BufferUtil.makeFloatBuffer(normals);
    }

    public void draw(float r,float g,float b,float a, float shininess) {
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //頂点での法線ベクトル
        GLES20.glVertexAttribPointer(GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, normalBuffer);

        //周辺光反射
        GLES20.glUniform4f(GLES.materialAmbientHandle, r, g, b, a);

        //拡散反射
        GLES20.glUniform4f(GLES.materialDiffuseHandle, r, g, b, a);

        //鏡面反射
        GLES20.glUniform4f(GLES.materialSpecularHandle, 1f, 1f, 1f, a);
        GLES20.glUniform1f(GLES.materialShininessHandle, shininess);

        //shadingを使わない時に使う単色の設定 (r, g, b,a)
        GLES20.glUniform4f(GLES.objectColorHandle, r, g, b, a);

        //描画
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }
}
