package com.example.control.gles2sample11;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Tommy on 2015/07/15.
 */
public class StringTexture {
    private int TextureId=-1;
    private int TextureUnitNumber=0;
    StringTexture(String text, float textSize, int txtcolor, int bkcolor, int textureidnumber) {
        TextureUnitNumber = textureidnumber;
        makeStringTexture(text, textSize, txtcolor, bkcolor);
    }
    StringTexture(String text, float textSize, int txtcolor, int bkcolor) {
        makeStringTexture(text, textSize, txtcolor, bkcolor);
    }
    public void makeStringTexture(String text, float textSize, int txtcolor, int bkcolor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        paint.getTextBounds(text, 0, text.length(), new Rect(0, 0, (int) textSize * text.length(), (int) textSize));

        int textWidth = (int) paint.measureText(text);
        int textHeight = (int) (Math.abs(fontMetrics.top) + fontMetrics.bottom);

        if (textWidth == 0) textWidth = 10;
        if (textHeight == 0) textHeight = 10;

        int bitmapsize = 2; //現時点でNexus7ではビットマップは正方形で一辺の長さは2のべき乗でなければならない
        while (bitmapsize < textWidth) bitmapsize *= 2;
        while (bitmapsize < textHeight) bitmapsize *= 2;

        Bitmap bitmap = Bitmap.createBitmap(bitmapsize, bitmapsize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        paint.setColor(bkcolor);
        canvas.drawRect(new Rect(0, 0, bitmapsize, bitmapsize), paint);
        paint.setColor(txtcolor);
        canvas.drawText(text, bitmapsize / 2 - textWidth / 2, bitmapsize / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2, paint);

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
