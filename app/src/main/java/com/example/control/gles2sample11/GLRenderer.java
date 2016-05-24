package com.example.control.gles2sample11;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.text.format.Time;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tommy on 2015/06/18.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    //システム
    private final Context mContext;
    private boolean validProgram=false; //シェーダプログラムが有効
    private float aspect;//アスペクト比
    private float viewlength = 5.0f; //視点距離
    private float   angle=0f; //回転角度
    private int   viewmode=0; //視点モード　0：俯瞰　1：一緒に回る

    //視点変更テスト変数
    private float alph=0f,beta=0f;

    //光源
    //光源の座標　x,y,z
    private  float[] LightPos={0f,1.5f,3f,1f};//x,y,z,1
    //光源の環境光
    private float[] LightAmb={.15f,.15f,.15f,1f}; //r,g,b,a
    //光源の乱反射光
    private float[] LightDif={.5f,.5f,.5f,1f}; //r,g,b,a
    //光源の鏡面反射反射光
    private float[] LightSpc={.9f,.9f,.9f,1f}; //r,g,b,a

    //変換マトリックス
    private  float[] pMatrix=new float[16]; //プロジェクション変換マトリックス
    private  float[] mMatrix=new float[16]; //モデル変換マトリックス
    private  float[] cMatrix=new float[16]; //カメラビュー変換マトリックス

    //モデル座標系の原点
    private  float[] origin= {0f,0f,0f,1f};

    private Axes MyAxes= new Axes();  //原点周囲の軸表示とためのオブジェクトを作成
    private TexCube MyTexCube = new TexCube(); //原点に，外接球半径１の立方体オブジェクトを作成
    private Circle MyCircle =new Circle(64); //zx平面の原点に，半径１の円オブジェクト(64分割)を作成
    private TexSphere MyTexSphere=new TexSphere(40,20); //原点に，半径１の球体オブジェクト（40スライス，20スタック）を作成
    private Line_PtoP line1= new Line_PtoP(); //2点を結ぶ直線オブジェクトを作成
    private TexRectangular MyTexRectangular = new TexRectangular();//xy平面の原点に一辺１の正方形を作成
    private Texture Cherry;
    private Texture Cloud;
    private Texture Goldfish;
    private Texture LightSource;
    private Texture Rock;
    private Texture EarthPicture;
    private Texture WoodenBox;
    private Texture SampleDroid;
    private StringTexture Hello;
    private StringTexture CurrentTime;

    GLRenderer(final Context context) {
        mContext = context;
    }

    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //プログラムの生成
        validProgram = GLES.makeProgram();

        //デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // カリングの有効化
        GLES20.glEnable(GLES20.GL_CULL_FACE); //裏面を表示しないチェックを行う

        // 裏面を描画しない
        GLES20.glFrontFace(GLES20.GL_CCW); //表面のvertexのindex番号はCCWで登録
        GLES20.glCullFace(GLES20.GL_BACK); //裏面は表示しない

        //背景色の設定
        GLES20.glClearColor(0f, 0f, 0.2f, 1.0f);

        //光源の設定
        GLES.putLightAttribute(LightAmb, LightDif, LightSpc);

        //テクスチャの有効化
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        // 背景とのブレンド方法を設定します。
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);    // 単純なアルファブレンド

        Cherry = new Texture(mContext,R.drawable.cherry); //テクスチャを作成
        Cloud = new Texture(mContext,R.drawable.cloud); //テクスチャを作成
        Goldfish = new Texture(mContext,R.drawable.goldfish); //テクスチャを作成
        LightSource = new Texture(mContext,R.drawable.lightsource); //テクスチャを作成
        Rock = new Texture(mContext,R.drawable.rock); //テクスチャを作成
        EarthPicture = new Texture(mContext,R.drawable.earthpicture); //テクスチャを作成
        WoodenBox = new Texture(mContext,R.drawable.woodenbox); //テクスチャを作成
        SampleDroid = new Texture(mContext,R.drawable.sample); //テクスチャを作成
        //                                                    res -> drawable -> sample.png (256×256)が入っている
        Hello = new StringTexture("Hello",20, Color.WHITE, Color.parseColor("#000F00C0")); //文字列テクスチャを作成
        CurrentTime = new StringTexture("CurrentTime",20, Color.WHITE, Color.parseColor("#000F00C0")); //文字列テクスチャを作成

    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (!validProgram) return;

        float[] tmpPos1= new float[4];
        float[] tmpPos2 = new float[4];
        Time time = new Time("Asia/Tokyo");
        String currenttime;

        //画面のクリア
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
                GLES20.GL_DEPTH_BUFFER_BIT);

        //プロジェクション変換（射影変換）--------------------------------------
        //透視変換（遠近感を作る）
        //カメラは原点に有り，z軸の負の方向を向いていて，上方向はy軸＋方向である。
        GLES.gluPerspective(pMatrix,
                45.0f,  //Y方向の画角
                aspect, //アスペクト比
                1.0f,   //ニアクリップ　　　z=-1から
                100.0f);//ファークリップ　　Z=-100までの範囲を表示することになる
        GLES.setPMatrix(pMatrix);

        //カメラビュー変換（視野変換）-----------------------------------
        //カメラ視点が原点になるような変換
        Matrix.setLookAtM(cMatrix, 0,
                (float) (viewlength * Math.sin(beta) * Math.cos(alph)),  //カメラの視点 x
                (float) (viewlength * Math.sin(alph)),                    //カメラの視点 y
                (float) (viewlength * Math.cos(beta) * Math.cos(alph)),  //カメラの視点 z
                0.0f, 0.0f, 0.0f, //カメラの視線方向の代表点
                0.0f, 1.0f, 0.0f);//カメラの上方向
        if (viewmode!=0) {
            if (viewmode==2) Matrix.rotateM(cMatrix, 0, -angle * 2f, 0, 1, 0);
            Matrix.translateM(cMatrix, 0, 0f, 0f, -1.2f);
            Matrix.rotateM(cMatrix, 0, -angle * 1.5f, 0, 1, 0);

        }

        //カメラビュー変換はこれで終わり。
        GLES.setCMatrix(cMatrix);

        //cMatrixをセットしてから光源位置をセット
        GLES.setLightPosition(LightPos);

        GLES.selectProgram(GLES.SP_SimpleObject);

        //座標軸の描画
        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        //座標軸の描画本体
        //引数 r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる), linewidth
        //shininessは使用していない
        MyAxes.draw(1f, 1f, 1f, 1f, 10.f, 2f);//座標軸の描画本体

        //円の描画
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.scaleM(mMatrix, 0, 1.2f, 1.2f, 1.2f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        //円の描画本体
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる), linewidth
        //shininessは使用していない
        MyCircle.draw(1f, 1f, 0.1f, 1f, 10.f, 1f);

        GLES.deselectProgram();
        GLES.selectProgram(GLES.SP_TextureWithLight);

        Matrix.setIdentityM(mMatrix, 0);  //ここではすでに設定されているので省略可
        Matrix.translateM(mMatrix, 0, 0f, 1.2f, 0f);
        Matrix.rotateM(mMatrix, 0, angle, 0, 0, 1);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.rotateM(mMatrix, 0, angle * 3f, 1, 0, 0);
        Matrix.scaleM(mMatrix, 0, 0.6f, 0.6f, 0.6f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        WoodenBox.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexCube.draw(1f, 1f, 1f, 1f, 20.f);

        Matrix.setIdentityM(mMatrix, 0);  //ここではすでに設定されているので省略可
        Matrix.translateM(mMatrix, 0, 0f, -1.2f, 0f);
        Matrix.rotateM(mMatrix, 0, angle * 3f, 1, 0, 0);
        Matrix.rotateM(mMatrix, 0, angle, 0, 0, 1);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.6f, 0.6f, 0.6f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        Rock.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexCube.draw(1f, 1f, 1f, 1f, 20.f);

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.rotateM(mMatrix, 0, 1.5f * angle, 0, 1, 0);
        Matrix.translateM(mMatrix, 0, 1.2f, 0f, 0f);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        Cherry.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexSphere.draw(1f, 1f, 1f, 1f, 5.f);

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.rotateM(mMatrix, 0, 1.5f * angle, 0, 1, 0);
        Matrix.translateM(mMatrix, 0, -1.2f, 0f, 0f);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        Cloud.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexSphere.draw(1f, 1f, 1f, 1f, 5.f);

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.rotateM(mMatrix, 0, 1.5f * angle, 0, 1, 0);
        Matrix.translateM(mMatrix, 0, 0f, 0f, -1.2f);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        Goldfish.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexSphere.draw(1f, 1f, 1f, 1f, 5.f);
        Matrix.multiplyMV(tmpPos1, 0, mMatrix, 0, origin, 0); //中心の取得

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.rotateM(mMatrix, 0, 1.5f * angle, 0, 1, 0);
        Matrix.translateM(mMatrix, 0, 0f, 0f, 1.2f);
        Matrix.rotateM(mMatrix, 0, angle * 2, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
         GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        EarthPicture.setTexture();
        // r, g, b, a, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexSphere.draw(1f, 1f, 1f, 1f, 5.f);
        Matrix.multiplyMV(tmpPos2, 0, mMatrix, 0, origin, 0); //中心の取得

        GLES.deselectProgram();
        GLES.selectProgram(GLES.SP_SimpleTexture);

        //光源を表す白い球の描画
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, LightPos[0], LightPos[1], LightPos[2]);
        Matrix.rotateM(mMatrix, 0, angle * 5, 0, 1, 0);
        Matrix.scaleM(mMatrix, 0, 0.1f, 0.1f, 0.1f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        LightSource.setTexture();
        // r, g, b, shininess(1以上の値　大きな値ほど鋭くなる)
        MyTexSphere.draw(1f, 1f, 1f, 1f, 5.f);

        GLES.deselectProgram();
        GLES.selectProgram(GLES.SP_SimpleObject);

        //物体の中心点を線で結ぶ
        Matrix.setIdentityM(mMatrix, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        line1.setVertexs(tmpPos1, LightPos);
        line1.draw(1f, 1f, 1f, 1f, 0f, 2f);
        line1.setVertexs(tmpPos2, LightPos);
        line1.draw(1f, 1f, 1f, 1f, 0f, 2f);
        line1.setVertexs(tmpPos1, tmpPos2);
        line1.draw(1f, 1f, 1f, 1f, 0f, 2f);

        GLES.deselectProgram();
        GLES.selectProgram(GLES.SP_TextureWithLight);

        //Sampleの絵を張り付ける
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, .25f, 0f, 0f);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        SampleDroid.setTexture();
        MyTexRectangular.draw(1f, 1f, 1f, 1f, 5f);
        Matrix.rotateM(mMatrix, 0, 180f, 0, 1, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        MyTexRectangular.draw(1f, 1f, 1f, 1f, 5f);

        //Helloを張り付ける
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, -.25f, 0f, 0f);
        Matrix.scaleM(mMatrix, 0, 0.5f, 0.5f, 0.5f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        Hello.setTexture();
        MyTexRectangular.draw(1f, 1f, 1f, 1f, 5f);
        Matrix.rotateM(mMatrix, 0, 180f, 0, 1, 0);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        MyTexRectangular.draw(1f, 1f, 1f, 1f, 5f);

        //CurrentTimeを張り付ける
        time.setToNow();
        currenttime = time.year + "/" + (time.month+1) + "/" + time.monthDay + " " +
                time.hour + ":" + time.minute + ":" + time.second;
        CurrentTime.makeStringTexture(currenttime, 20, Color.WHITE, Color.parseColor("#000F00C0"));
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, 0f, 1.8f, 0f);
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        CurrentTime.setTexture();
        MyTexRectangular.draw(1f, 1f, 1f, 1f, 5f);

        GLES.deselectProgram();

        angle+=0.5;

    }

    private float Scroll[] = {0f, 0f}; //１本指のドラッグ[rad]
    public void setScrollValue(float DeltaX, float DeltaY) {
        Scroll[0] += DeltaX * 0.01;
        if (3.14f<Scroll[0]) Scroll[0]=3.14f;
        if (Scroll[0]<-3.14) Scroll[0]=-3.14f;
        Scroll[1] -= DeltaY * 0.01;
        if (1.57f<Scroll[1]) Scroll[1]=1.57f;
        if (Scroll[1]<-1.57) Scroll[1]=-1.57f;
        alph=Scroll[1];
        beta=Scroll[0];
    }

    public void toggleViewmode() {
        viewmode += 1;
        if (viewmode==3) viewmode=0;
    }
}
