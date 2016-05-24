package com.example.control.gles2sample11;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by tommy on 2015/06/26.
 */
public class Sphere {
    //bufferの定義
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    //一般的には必要だが，球では頂点座標ベクトルと法線ベクトルが一致するため使わない
    //private FloatBuffer normalBuffer;

    private int nIndexs;

    Sphere(){makeSphere(1f, 20, 10);}
    Sphere(int nSlices, int nStacks){makeSphere(1f, nSlices, nStacks);}
    public void makeSphere(float Radius, int nSlices, int nStacks) {
        //頂点座標
        int sizeArray=((nStacks-1)*nSlices+2)*3;
        float[] vertexs= new float[sizeArray];
        vertexs[0]=0f; vertexs[1]=Radius; vertexs[2]=0f;
        int i,j,px;
        float theta,phi;
        for (i=0;i<nStacks-1;i++) {
            for (j=1;j<=nSlices;j++) {
                px=(i*nSlices+j)*3;
                theta=(float)(nStacks-i-1)/(float)nStacks*3.14159265f-3.14159265f*0.5f;
                phi=(float)j/(float)nSlices*2.f*3.14159265f;
                vertexs[px]=(float)(Radius*Math.cos(theta)*Math.sin(phi));
                vertexs[px+1]=(float)(Radius*Math.sin(theta));
                vertexs[px+2]=(float)(Radius*Math.cos(theta)*Math.cos(phi));
            }
        }
        px=((nStacks-1)*nSlices+1)*3;
        vertexs[px]=0f; vertexs[px+1]=-Radius; vertexs[px+2]=0f;

        //拡頂点の法線ベクトル
        //一般的には必要だが，球では頂点座標ベクトルと法線ベクトルが一致するため使わない
        //float[] normals= new float[sizeArray];
        //for (i=0;i<sizeArray;i++) normals[i]=vertexs[i]/Radius;

        //頂点座標番号列
        nIndexs=(((nStacks-1)*nSlices+2)-2)*2+4*nSlices-2;
        short [] indexs= new short[nIndexs];
        int p=0;
        for (i=0;i<nSlices;i++) {
            if (p!=0) indexs[p++]=0;
            indexs[p++]=0;
            for (j=0;j<nStacks-1;j++) {
                indexs[p++]=(short)(j*nSlices+i+1);
                indexs[p++]=(short)(j*nSlices+1+(i+1)%nSlices);
            }
            indexs[p++]=(short)((nStacks-1)*nSlices+1);
            if (p!=nIndexs) indexs[p++]=(short)((nStacks-1)*nSlices+1);
        }

        vertexBuffer=BufferUtil.makeFloatBuffer(vertexs);
        indexBuffer=BufferUtil.makeShortBuffer(indexs);
        //一般的には必要だが，球では頂点座標ベクトルと法線ベクトルが一致するため使わない
        //normalBuffer=BufferUtil.makeFloatBuffer(normals);
    }

    public void draw(float r,float g,float b,float a, float shininess){
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //頂点での法線ベクトル
        //球では頂点座標ベクトルと法線ベクトルが一致するため，法線ベクトルとして座標ベクトルを共用する
        GLES20.glVertexAttribPointer(GLES.normalHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //周辺光反射
        GLES20.glUniform4f(GLES.materialAmbientHandle, r, g, b, a);

        //拡散反射
        GLES20.glUniform4f(GLES.materialDiffuseHandle, r, g, b, a);

        //鏡面反射
        GLES20.glUniform4f(GLES.materialSpecularHandle, 1f, 1f, 1f, a);
        GLES20.glUniform1f(GLES.materialShininessHandle, shininess);

        //shadingを使わない時に使う単色の設定 (r, g, b,a)
        GLES20.glUniform4f(GLES.objectColorHandle, 1f, 1f, 1f, a);

        //描画をシェーダに指示
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                nIndexs, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
}
