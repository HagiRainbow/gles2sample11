package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by tommy on 2015/06/19.
 */
public class Cube {
    //bufferの定義
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;
    private FloatBuffer normalBuffer;

    //頂点座標
    private float[] vertexs={
            1f,1f,1f,//P0　第0面
            1f,1f,-1f,//P1
            -1f,1f,1f,//P2
            -1f,1f,-1f,//P3
            -1f,1f,1f,//P4　 第1面
            -1f,1f,-1f,//P5
            -1f,-1f,1f,//P6
            -1f,-1f,-1f,//P7
            -1f,-1f,1f,//P8　第2面
            -1f,-1f,-1f,//P9
            1f,-1f,1f,//P10
            1f,-1f,-1f,//P11
            1f,-1f,1f,//P12　第3面
            1f,-1f,-1f,//P13
            1f,1f,1f,//P14
            1f,1f,-1f,//P15
            -1f,-1f,1f,//P16　第4面
            1f,-1f,1f,//P17
            -1f,1f,1f,//P18
            1f,1f,1f,//P19
            1f,-1f,-1f,//P20　第5面
            -1f,-1f,-1f,//P21
            1f,1f,-1f,//P22
            -1f,1f,-1f,//P23
    };
    //頂点座標番号列
    private byte[] indexs= {
            0,1,2,3, //第0面
            4,5,6,7, //第1面
            8,9,10,11, //第2面
            12,13,14,15, //第3面
            16,17,18,19, //第4面
            20,21,22,23 //第5面
    };
    //拡頂点の法線ベクトル
    private float[] normals={
            0,1,0,  //P0 第0面
            0,1,0,  //P1
            0,1,0,  //P2
            0,1,0,  //P3
            -1,0,0,  //P4 第1面
            -1,0,0,  //P5
            -1,0,0,  //P6
            -1,0,0,  //P7
            0,-1,0,  //P8 第2面
            0,-1,0,  //P9
            0,-1,0,  //P10
            0,-1,0,  //P11
            1,0,0,  //P12 第3面
            1,0,0,  //P13
            1,0,0,  //P14
            1,0,0,  //P15
            0,0,1,  //P16 第4面
            0,0,1,  //P17
            0,0,1,  //P18
            0,0,1,  //P19
            0,0,-1,  //P20 第5面
            0,0,-1,  //P21
            0,0,-1,  //P22
            0,0,-1,  //P23
    };

    //constructor
    Cube() {
        makeCube(1f);
    }
    Cube(float r) { //r rudius of circumsphere
        makeCube(r);
    }

    public void makeCube(float r){ //r rudius of circumsphere
        int i;
        float m,mm;
        m= (float) (r/Math.sqrt(vertexs[0]*vertexs[0]+vertexs[1]*vertexs[1]+vertexs[2]*vertexs[2]));
        mm= (float) (1/Math.sqrt(normals[0]*normals[0]+normals[1]*normals[1]+normals[2]*normals[2]));
        for (i=0; i<24*3; i++) {
            vertexs[i] *= m;
            normals[i] *= mm;
        }
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

        //描画をシェーダに指示
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                24, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }
}
