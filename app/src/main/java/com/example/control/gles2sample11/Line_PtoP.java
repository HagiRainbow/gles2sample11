package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Tommy on 2015/06/27.
 */
public class Line_PtoP {
    //buffer
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;
    private FloatBuffer normalBuffer;
    private byte[] indexs= {
            0,1
    };
    float[] vertexs=new float[2*3];

    //コンストラクタ
    Line_PtoP(){
        indexBuffer = BufferUtil.makeByteBuffer(indexs);
        vertexBuffer = BufferUtil.makeFloatBuffer(vertexs);
    }

    public void setVertexs(float p0[],float p1[]){
        System.arraycopy(p0, 0, vertexs, 0, 3);
        System.arraycopy(p1, 0, vertexs, 3, 3);
        BufferUtil.setFloatBuffer(vertexs,vertexBuffer);
    }

    public void draw(float r,float g,float b,float a, float shininess, float linewidth){
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //shadingを使わない時に使う単色の設定 (r, g, b,a)
        GLES20.glUniform4f(GLES.objectColorHandle, r, g, b, a);

        //線の太さ
        GLES20.glLineWidth(linewidth);

        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_LINES,
                2, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }
}
