package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Tommy on 2015/07/11.
 */
public class TexSphere {
    //bufferの定義
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer texcoordBuffer;

    private int nIndexs;

    TexSphere() {
        makeTexSphere(20, 10);
    }
    TexSphere(int nSlices, int nStacks) {
        makeTexSphere(nSlices, nStacks);
    }
    public void makeTexSphere(int nSlices, int nStacks) {
        float Radius=1f;
        //頂点座標
        int nSlices1=nSlices+1;
        int nPoints=(nStacks+1)*nSlices1;
        int sizeArray=nPoints*3;
        float[] vertexs= new float[sizeArray];
        int i,j,px;
        float theta,phi;
        for (i=0;i<=nStacks;i++) {
            for (j=0;j<nSlices1;j++) {
                px=(i*nSlices1+j)*3;
                theta=(float)(nStacks-i)/(float)nStacks*3.14159265f-3.14159265f*0.5f;
                if (j==nSlices) {
                    phi = 0f;
                } else {
                    phi=(float)j/(float)nSlices*2.f*3.14159265f;
                }
                vertexs[px]=(float)(Radius* Math.cos(theta)* Math.sin(phi));
                vertexs[px+1]=(float)(Radius* Math.sin(theta));
                vertexs[px+2]=(float)(Radius* Math.cos(theta)* Math.cos(phi));
            }
        }

        //頂点座標番号列
        nIndexs=(nStacks+1)*nSlices1*2-(2*nStacks+4);
        short [] indexs= new short[nIndexs];
        int p=0;
        for (i=0;i<nSlices;i++) {
            if (p!=0) indexs[p++]=(short)i;
            indexs[p++]=(short)i;
            for (j=1;j<nStacks;j++) {
                indexs[p++]=(short)(j*nSlices1+i);
                indexs[p++]=(short)(j*nSlices1+i+1);
            }
            indexs[p++]=(short)(nStacks*nSlices1+i);
            if (p!=nIndexs) indexs[p++]=(short)(nStacks*nSlices1+i);
        }

        sizeArray=nPoints*2;
        float textcoords[] = new float[sizeArray];
        float dx = 1f/(float)nSlices;
        float dy = 1f/(float)nStacks;
        for (i=0;i<=nStacks;i++) {
            for (j = 0; j < nSlices1; j++) {
                px = (i * nSlices1 + j) * 2;
                textcoords[px++] = j * dx;
                textcoords[px++] = i * dy;
            }
        }

        vertexBuffer=BufferUtil.makeFloatBuffer(vertexs);
        indexBuffer=BufferUtil.makeShortBuffer(indexs);
        texcoordBuffer = BufferUtil.makeFloatBuffer(textcoords);

    }

    public void draw(float r,float g,float b,float a, float shininess){

        //頂点点列のテクスチャ座標
        GLES20.glVertexAttribPointer(GLES.texcoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, texcoordBuffer);

        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        if (GLES.checkLiting()) {

            //頂点での法線ベクトル （これは頂点座標に等しい）
            GLES20.glVertexAttribPointer(GLES.normalHandle, 3,
                    GLES20.GL_FLOAT, false, 0, vertexBuffer);

            //周辺光反射
            GLES20.glUniform4f(GLES.materialAmbientHandle, r, g, b, a);

            //拡散反射
            GLES20.glUniform4f(GLES.materialDiffuseHandle, r, g, b, a);

            //鏡面反射
            GLES20.glUniform4f(GLES.materialSpecularHandle, 1f, 1f, 1f, a);
            GLES20.glUniform1f(GLES.materialShininessHandle, shininess);

        }

        //描画
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                nIndexs, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
}

