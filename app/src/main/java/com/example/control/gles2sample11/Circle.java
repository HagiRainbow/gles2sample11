package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by tommy on 2015/06/22.
 */
public class Circle {
    //buffer
    private FloatBuffer vertexBuffer;
    private ByteBuffer indexBuffer;

    private int NumberParts;

    Circle(){makeCircle(1f,16);}
    Circle(int NumParts){makeCircle(1f,NumParts);} //Max 255
    public void makeCircle(float Radius, int NumParts) {
        float[] vertexs=new float[NumParts*3];
        byte[] indexs=new byte[NumParts];
        short i;
        NumberParts=NumParts;
        float theta;
        for (i=0; i<NumParts; i++) {
            theta=(float)(i * 2 * Math.PI / (float)NumParts);
            vertexs[3*i]=(float)(Radius*Math.sin(theta));
            vertexs[3*i+1]=0f;
            vertexs[3*i+2]=(float)(Radius*Math.cos(theta));
            indexs[i]=(byte)i;
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

        //描画をシェーダに指示
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_LINE_LOOP,
                NumberParts, GLES20.GL_UNSIGNED_BYTE, indexBuffer);
    }
}
