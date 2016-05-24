package com.example.control.gles2sample11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tommy on 2015/07/14.
 */
public class Texture {
    private int TextureId=-1;
    private int TextureUnitNumber=0;
    Texture(Context mContext, int id, int textureidnumber) {
        TextureUnitNumber = textureidnumber;
        makeTexture(mContext, id);
    }
    Texture(Context mContext, int id) {
        makeTexture(mContext, id);
    }
    public void makeTexture(Context mContext, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //これをつけないとサイズが勝手に変更されてしまう
        //現時点でNexus7では正方形で一辺が2のべき乗サイズでなければならない
        //元のファイルの段階で大きさをそろえておく必要がある
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), id, options);
        int FIRST_INDEX = 0;
        final int DEFAULT_OFFSET = 0;
        final int[] textures = new int[1];
        if (TextureId!=-1) {
            textures[FIRST_INDEX]=TextureId;
            GLES20.glDeleteTextures(1, textures, DEFAULT_OFFSET);
        }
        GLES20.glGenTextures(1, textures, DEFAULT_OFFSET);
        TextureId = textures[FIRST_INDEX];
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+TextureUnitNumber);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureId);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
    }

    public void setTexture() {
        // テクスチャの指定
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+TextureUnitNumber);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureId);
        GLES20.glUniform1i(GLES.textureHandle, TextureUnitNumber); //テクスチャユニット番号を指定する
    }
}
