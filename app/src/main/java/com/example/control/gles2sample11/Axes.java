package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Tommy on 2015/06/21.
 */
public class Axes {
    //buffer
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;

    private float[] vertexs = {
    //  x, y, z
        -1f,0f,0f,  //x axis start     P0
        1f,0f,0f,                    //P1
        0.9f,0.05f,0f,               //P2
        0.9f,0f,0f,                  //P3
        0f,0f,0f,  //x axis end      //P4
        0f,-1f,0f,  //y axis start   //P5
        0f,1f,0f,                    //P6
        -0.05f,0.9f,0f,               //P7
        0f,0.9f,0f,                  //P8
        0f,0f,0f,   //y axis end       P9
        0f,0f,-1f,  //z axis start     P10
        0f,0f,1f,                    //P11
        -0.05f,0f,0.9f,               //P12
        0f,0f,0.9f,   //z axis end     P13

        1.05f,0f,0f,  //char X         P14
        1.15f,0.12f,0f,              //P15
        1.1f,0.06f,0f,               //P16
        1.05f,0.12f,0f,              //P17
        1.15f,0f,0f,                 //P18

        0.05f,1.05f,0f, //char Y     //P19
        0.05f,1.12f,0f,              //P20
        0f,1.17f,0f,                 //P21
        0.05f,1.12f,0f,              //P22
        0.1f,1.17f,0f,               //P23

        0.05f,0.12f,1.05f,   //char Z   P24
        0.1f,0.12f,1.05f,             //P25
        0.05f,0f,1.05f,               //P26
        0.1f,0f,1.05f                 //P27
    };

    //点の番号
    private byte[] indexs= {
            0,1,2,3,4,5,6,7,8,9,10,11,12,13,
            14,15,16,17,18,
            19,20,21,22,23,
            24,25,26,27
    };

    Axes(){makeAxes(1f);}
    Axes(float size){makeAxes(size);}
    public void makeAxes(float size) {
        int i;
        for (i = 0; i < 28 * 3; i++) {
            vertexs[i] *= size;
        }
        vertexBuffer = BufferUtil.makeFloatBuffer(vertexs);
        indexBuffer = BufferUtil.makeByteBuffer(indexs);
    }

    public void draw(float r,float g,float b,float a, float shininess, float linewidth){
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);


        //shadingを使わない時に使う単色の設定 (r, g, b,a)
        GLES20.glUniform4f(GLES.objectColorHandle, r, g, b, a);

        //線の太さ
        GLES20.glLineWidth(linewidth);

        //ここから描画
        //P0から14個の連続点で，座標軸を一気に描く（都合上同じ線を2度引くところもある）
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                14, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        //P14から5個の連続点で，文字Xを描く
        indexBuffer.position(14);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                5, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        //P19から5個の連続点で，文字Yを描く
        indexBuffer.position(19);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                5, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        //P24から4個の連続点で，文字Zを描く
        indexBuffer.position(24);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                4, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }
}