package com.example.control.gles2sample11;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


//シェーダ操作
public class GLES {
    //照明なしのときの描画　（線画にも使える）
    //頂点シェーダのコード
    private final static String SimpleObject_VSCODE =
            //照明を必要としないときの色
            "uniform vec4 u_ObjectColor;" +
                    //行列
                    "uniform mat4 u_MMatrix;" +       //モデルビュー行列
                    "uniform mat4 u_PMatrix;" +       //射影行列
                    //頂点情報
                    "attribute vec4 a_Position;" +  //位置
                    //出力
                    "varying vec4 v_Color;" + "" +

                    "void main() {" +
                    //位置の指定
                    "gl_Position=u_PMatrix*u_MMatrix*a_Position;" +
                    //色の指定
                    "v_Color=u_ObjectColor;" +
                    "}";

    //フラグメントシェーダのコード
    private final static String SimpleObject_FSCODE =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "gl_FragColor=v_Color;" +
                    "}";

    //********************************************************************

    //照明ありのときの描画
    //頂点シェーダのコード
    private final static String ObjectWithLight_VSCODE =
            //光源
            "uniform vec4 u_LightAmbient;" + //光源の環境光色
                    "uniform vec4 u_LightDiffuse;" + //光源の拡散光色
                    "uniform vec4 u_LightSpecular;" +//光源の鏡面光色
                    "uniform vec4 u_LightPos;" +     //光源の位置（カメラビュー座標系）
                    //マテリアル
                    "uniform vec4 u_MaterialAmbient;" +   //マテリアルの環境光色
                    "uniform vec4 u_MaterialDiffuse;" +   //マテリアルの拡散光色
                    "uniform vec4 u_MaterialSpecular;" +  //マテリアルの鏡面光色
                    "uniform float u_MaterialShininess;" +//マテリアルの鏡面指数
                    //行列
                    "uniform mat4 u_MMatrix;" +       //モデルビュー行列
                    "uniform mat4 u_PMatrix;" +       //射影行列
                    //頂点情報
                    "attribute vec4 a_Position;" +  //位置
                    "attribute vec3 a_Normal;" +     //法線
                    //出力
                    "varying vec4 v_Color;" +

                    "void main(){" +
                    //環境光の計算
                    "vec4 ambient=u_LightAmbient*u_MaterialAmbient;" +
                    //拡散光の計算
                    "vec3 P=vec3(u_MMatrix*a_Position);" +
                    "vec3 L=normalize(vec3(u_LightPos)-P);" +  //光源方向単位ベクトル
                    "vec3 N=normalize(mat3(u_MMatrix)*a_Normal);" +  //法線単位ベクトル
                    "float dotLN=max(dot(L,N),0.0);" +
                    "vec4 diffuseP=vec4(dotLN);" +
                    "vec4 diffuse=diffuseP*u_LightDiffuse*u_MaterialDiffuse;" +
                    //鏡面光の計算
                    "vec3 V=normalize(-P);" +  //視点方向単位ベクトル
                    "float dotNLEffect=ceil(dotLN);" +
                    "vec3 R=2.*dotLN*N-L;" +
                    "float specularP=pow(max(dot(R,V),0.0),u_MaterialShininess)*dotNLEffect;" +
                    "vec4 specular=specularP*u_LightSpecular*u_MaterialSpecular;" +
                    //色の指定
                    "v_Color=ambient+diffuse+specular;" +
                    //位置の指定
                    "gl_Position=u_PMatrix*u_MMatrix*a_Position;" +
                    "}";

    //フラグメントシェーダのコード
    private final static String ObjectWithLight_FSCODE =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main(){" +
                    "gl_FragColor=v_Color;" +
                    "}";

    //********************************************************************

    //照明なしのときのテクスチャ描画
    //頂点シェーダのコード
    private final static String SimpleTexture_VSCODE =
            //行列
            "uniform mat4 u_MMatrix;" +       //モデルビュー行列
                    "uniform mat4 u_PMatrix;" +       //射影行列
                    //頂点情報
                    "attribute vec4 a_Position;" +  //位置
                    // テクスチャ情報
                    "attribute vec2 a_Texcoord;" + //テクスチャ
                    //出力
                    "varying vec2 v_Texcoord;" +

                    "void main() {" +
                    //位置の指定
                    "gl_Position=u_PMatrix*u_MMatrix*a_Position;" +
                    //テクスチャの指定
                    "v_Texcoord = a_Texcoord;" +
                    "}";

    //フラグメントシェーダのコード
    private final static String SimpleTexture_FSCODE =
            "precision mediump float;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_Texcoord;" +
                    "void main() {" +
                    "gl_FragColor = texture2D(u_Texture, v_Texcoord);" +
                    "}";

    //********************************************************************

    //照明下のテクスチャの描画
    private final static String TextureWithLight_VSCODE =
            //光源
            "uniform vec4 u_LightAmbient;" + //光源の環境光色
                    "uniform vec4 u_LightDiffuse;" + //光源の拡散光色
                    "uniform vec4 u_LightSpecular;" +//光源の鏡面光色
                    "uniform vec4 u_LightPos;" +     //光源の位置（カメラビュー座標系）
                    //マテリアル
                    "uniform vec4 u_MaterialAmbient;" +   //マテリアルの環境光色
                    "uniform vec4 u_MaterialDiffuse;" +   //マテリアルの拡散光色
                    "uniform vec4 u_MaterialSpecular;" +  //マテリアルの鏡面光色
                    "uniform float u_MaterialShininess;" +//マテリアルの鏡面指数
                    //行列
                    "uniform mat4 u_MMatrix;" +       //モデルビュー行列
                    "uniform mat4 u_PMatrix;" +       //射影行列
                    "uniform mat4 u_NormalMatrix;" + //法線ベクトル用モデルビュー行列
                    //頂点情報
                    "attribute vec4 a_Position;" +  //位置
                    "attribute vec3 a_Normal;" +     //法線
                    // テクスチャ情報
                    "attribute vec2 a_Texcoord;" + //テクスチャ
                    //出力
                    "varying vec4 v_Color;" +
                    "varying vec2 v_Texcoord;" +

                    "void main(){" +
                    //環境光の計算
                    "vec4 ambient=u_LightAmbient*u_MaterialAmbient;" +
                    //拡散光の計算
                    "vec3 P=vec3(u_MMatrix*a_Position);" +
                    "vec3 L=normalize(vec3(u_LightPos)-P);" +  //光源方向単位ベクトル
                    "vec3 N=normalize(mat3(u_MMatrix)*a_Normal);" +  //法線単位ベクトル
                    "float dotLN=max(dot(L,N),0.0);" +
                    "vec4 diffuseP=vec4(dotLN);" +
                    "vec4 diffuse=diffuseP*u_LightDiffuse*u_MaterialDiffuse;" +
                    //鏡面光の計算
                    "vec3 V=normalize(-P);" +  //視点方向単位ベクトル
                    "float dotNLEffect=ceil(dotLN);" +
                    "vec3 R=2.*dotLN*N-L;" +
                    "float specularP=pow(max(dot(R,V),0.0),u_MaterialShininess)*dotNLEffect;" +
                    "vec4 specular=specularP*u_LightSpecular*u_MaterialSpecular;" +
                    //色の指定
                    "v_Color=ambient+diffuse+specular;" +
                    //位置の指定
                    "gl_Position=u_PMatrix*u_MMatrix*a_Position;" +
                    "v_Texcoord = a_Texcoord;" +
                    "}";

    //フラグメントシェーダのコード
    private final static String TextureWithLight_FSCODE =
            "precision mediump float;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_Texcoord;" +
                    "varying vec4 v_Color;" +
                    "void main(){" +
                    "gl_FragColor = v_Color*texture2D(u_Texture, v_Texcoord);" +
                    "}";

    //シェーダプログラムID
    public static int SP_SimpleObject;         //照明なし，テクスチャなしのときのシェーダプログラム
    public static int SP_ObjectWithLight;     //照明あり，テクスチャなしのときのシェーダプログラム
    public static int SP_SimpleTexture;        //照明なし，テクスチャありのときのシェーダプログラム
    public static int SP_TextureWithLight;    //照明あり，テクスチャありのときのシェーダプログラム

    //システム
    public static int objectColorHandle;   //shadingを行わない時に使う単色ハンドル

    //光源のハンドル
    public static int lightAmbientHandle; //光源の環境光色ハンドル
    public static int lightDiffuseHandle; //光源の拡散光色ハンドル
    public static int lightSpecularHandle;//光源の鏡面光色ハンドル
    public static int lightPosHandle;     //光源の位置ハンドル

    //マテリアルのハンドル
    public static int materialAmbientHandle;  //マテリアルの環境光色ハンドル
    public static int materialDiffuseHandle;  //マテリアルの拡散光色ハンドル
    public static int materialSpecularHandle; //マテリアルの鏡面光色ハンドル
    public static int materialShininessHandle;//マテリアルの鏡面指数ハンドル

    //行列のハンドル
    public static int mMatrixHandle;     //モデルビュー行列ハンドル
    public static int pMatrixHandle;     //射影行列ハンドル

    //頂点のハンドル
    public static int positionHandle;//位置ハンドル
    public static int normalHandle;  //法線ハンドル

    //テクスチャのハンドル
    public static int texcoordHandle; //テクスチャコードハンドル
    public static int textureHandle;  //テクスチャハンドル

    //行列
    public static float[] cMatrix = new float[16];//視点変換直後のモデルビュー行列
    public static float[] mMatrix = new float[16];//モデルビュー行列
    public static float[] pMatrix = new float[16];//射影行列
    public static float[] pmMatrix = new float[16];//射影行列 pMatrix*mMatrix

    //光源
    private static float[] LightPos = new float[4];    //光源の座標　x,y,z　（ワールド座標）
    private static float[] CVLightPos = new float[4];    //光源の座標　x,y,z　（カメラビュー座標）
    private static float[] LightAmb = new float[4];    //光源の環境光
    private static float[] LightDif = new float[4];    //光源の乱反射光
    private static float[] LightSpc = new float[4];    //光源の鏡面反射反射光

    private static boolean useLighting = false;
    private static int currentProgram = 0;

    public static final int INVALID = 0;

    //プログラムの生成 trueなら成功
    public static boolean makeProgram() {
        SP_SimpleObject = makeProgram0(SimpleObject_VSCODE, SimpleObject_FSCODE);
        if (SP_SimpleObject == INVALID) return false;
        SP_ObjectWithLight = makeProgram0(ObjectWithLight_VSCODE, ObjectWithLight_FSCODE);
        if (SP_ObjectWithLight == INVALID) return false;
        SP_SimpleTexture = makeProgram0(SimpleTexture_VSCODE, SimpleTexture_FSCODE);
        if (SP_SimpleTexture == INVALID) return false;
        SP_TextureWithLight = makeProgram0(TextureWithLight_VSCODE, TextureWithLight_FSCODE);
        if (SP_TextureWithLight == INVALID) return false;
        return true;
    }

    public static void putLightAttribute(float[] amb, float[] dif, float[] spc) {
        System.arraycopy(amb, 0, LightAmb, 0, 4);
        System.arraycopy(dif, 0, LightDif, 0, 4);
        System.arraycopy(spc, 0, LightSpc, 0, 4);
    }

    public static void selectProgram(int programID) {
        currentProgram = programID;
        if (programID == SP_ObjectWithLight) {
            GLES20.glUseProgram(programID);
            //光源のハンドルの取得
            lightAmbientHandle = GLES20.glGetUniformLocation(programID, "u_LightAmbient");
            lightDiffuseHandle = GLES20.glGetUniformLocation(programID, "u_LightDiffuse");
            lightSpecularHandle = GLES20.glGetUniformLocation(programID, "u_LightSpecular");
            lightPosHandle = GLES20.glGetUniformLocation(programID, "u_LightPos");
            //マテリアルのハンドルの取得
            materialAmbientHandle = GLES20.glGetUniformLocation(programID, "u_MaterialAmbient");
            materialDiffuseHandle = GLES20.glGetUniformLocation(programID, "u_MaterialDiffuse");
            materialSpecularHandle = GLES20.glGetUniformLocation(programID, "u_MaterialSpecular");
            materialShininessHandle = GLES20.glGetUniformLocation(programID, "u_MaterialShininess");
            //頂点法線ベクトルのハンドルの取得
            normalHandle = GLES20.glGetAttribLocation(programID, "a_Normal");
            GLES20.glEnableVertexAttribArray(normalHandle);

            //光源位置の指定   (x, y, z, 1)
            //GLES20.glUniform4f(GLES.lightPosHandle,CVLightPos[0], CVLightPos[1], CVLightPos[2], 1.0f);
            //光源色の指定 (r, g, b,a)
            GLES20.glUniform4f(GLES.lightAmbientHandle, LightAmb[0], LightAmb[1], LightAmb[2], LightAmb[3]); //周辺光
            GLES20.glUniform4f(GLES.lightDiffuseHandle, LightDif[0], LightDif[1], LightDif[2], LightDif[3]); //乱反射光
            GLES20.glUniform4f(GLES.lightSpecularHandle, LightSpc[0], LightSpc[1], LightSpc[2], LightSpc[3]); //鏡面反射光
            useLighting = true;
        } else if (programID == SP_SimpleObject) {
            GLES20.glUseProgram(programID);
            //光源を使わない時のマテリアルの色のハンドルの取得
            objectColorHandle = GLES20.glGetUniformLocation(programID, "u_ObjectColor");
            useLighting = false;
        } else if (programID == SP_TextureWithLight) {
            GLES20.glUseProgram(programID);
            //光源のハンドルの取得
            lightAmbientHandle = GLES20.glGetUniformLocation(programID, "u_LightAmbient");
            lightDiffuseHandle = GLES20.glGetUniformLocation(programID, "u_LightDiffuse");
            lightSpecularHandle = GLES20.glGetUniformLocation(programID, "u_LightSpecular");
            lightPosHandle = GLES20.glGetUniformLocation(programID, "u_LightPos");
            //マテリアルのハンドルの取得
            materialAmbientHandle = GLES20.glGetUniformLocation(programID, "u_MaterialAmbient");
            materialDiffuseHandle = GLES20.glGetUniformLocation(programID, "u_MaterialDiffuse");
            materialSpecularHandle = GLES20.glGetUniformLocation(programID, "u_MaterialSpecular");
            materialShininessHandle = GLES20.glGetUniformLocation(programID, "u_MaterialShininess");
            //頂点法線ベクトルのハンドルの取得
            normalHandle = GLES20.glGetAttribLocation(programID, "a_Normal");
            GLES20.glEnableVertexAttribArray(normalHandle);
            //テクスチャのハンドルの取得
            texcoordHandle = GLES20.glGetAttribLocation(programID, "a_Texcoord");
            textureHandle = GLES20.glGetUniformLocation(programID, "u_Texture");
            GLES20.glEnableVertexAttribArray(texcoordHandle);

            //光源位置の指定   (x, y, z, 1)
            //GLES20.glUniform4f(GLES.lightPosHandle, CVLightPos[0], CVLightPos[1], CVLightPos[2], 1.0f);
            //光源色の指定 (r, g, b,a)
            GLES20.glUniform4f(GLES.lightAmbientHandle, LightAmb[0], LightAmb[1], LightAmb[2], LightAmb[3]); //周辺光
            GLES20.glUniform4f(GLES.lightDiffuseHandle, LightDif[0], LightDif[1], LightDif[2], LightDif[3]); //乱反射光
            GLES20.glUniform4f(GLES.lightSpecularHandle, LightSpc[0], LightSpc[1], LightSpc[2], LightSpc[3]); //鏡面反射光
            useLighting = true;
        } else if (programID == SP_SimpleTexture) {
            GLES20.glUseProgram(programID);
            //テクスチャのハンドルの取得
            texcoordHandle = GLES20.glGetAttribLocation(programID, "a_Texcoord");
            textureHandle = GLES20.glGetUniformLocation(programID, "u_Texture");
            GLES20.glEnableVertexAttribArray(texcoordHandle);
            useLighting = false;
        } else {
            //ここはエラー
        }
        //行列のハンドルの取得
        mMatrixHandle = GLES20.glGetUniformLocation(programID, "u_MMatrix");
        pMatrixHandle = GLES20.glGetUniformLocation(programID, "u_PMatrix");
        //頂点のハンドルの取得
        positionHandle = GLES20.glGetAttribLocation(programID, "a_Position");
        GLES20.glEnableVertexAttribArray(positionHandle);

    }

    public static void deselectProgram() {
        if (currentProgram == SP_SimpleObject) {
        } else if (currentProgram == SP_ObjectWithLight) {
            GLES20.glDisableVertexAttribArray(normalHandle);
        } else if (currentProgram == SP_SimpleTexture) {
            GLES20.glDisableVertexAttribArray(texcoordHandle);
        } else if (currentProgram == SP_TextureWithLight) {
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(texcoordHandle);
        } else {
            //ここはエラー
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glUseProgram(0);
    }

    public static int makeProgram0(String VertexCode, String FragmentCode) {
        int myProgram;//プログラムオブジェクト
        //シェーダオブジェクトの生成
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FragmentCode);

        //プログラムオブジェクトの生成
        myProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(myProgram, vertexShader);
        GLES20.glAttachShader(myProgram, fragmentShader);
        GLES20.glLinkProgram(myProgram);

        // リンクエラーチェック
        int[] linked = new int[1];
        GLES20.glGetProgramiv(myProgram, GLES20.GL_LINK_STATUS, linked, 0);
        if (linked[0] <= 0) {
            Log.e(" makeProgram0", "Failed in Linking");
            Log.e(" makeProgram0", GLES20.glGetProgramInfoLog(myProgram));
            myProgram=INVALID;
        }

        // シェーダの削除
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return myProgram;
    }

    //シェーダオブジェクトの生成
    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        // コンパイルチェック
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(" loadShader", "Failed in Compilation");
            Log.e(" loadShader", GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    //透視変換の指定
    public static void gluPerspective(float[] pm,
                                      float angle, float aspect, float near, float far) {
        float top, bottom, left, right;
        if (aspect < 1f) {
            top = near * (float) Math.tan(angle * (Math.PI / 360.0));
            bottom = -top;
            left = bottom * aspect;
            right = -left;
        } else {
            right = 1.1f * near * (float) Math.tan(angle * (Math.PI / 360.0));
            left = -right;
            bottom = left / aspect;
            top = -bottom;
        }
        Matrix.frustumM(pm, 0, left, right, bottom, top, near, far);
    }

    //ワールド座標系のLightPosを受け取る
    public static void setLightPosition(float[] lp) {
        System.arraycopy(lp, 0, LightPos, 0, 4);
    }

    //射影行列をシェーダに指定
    public static void setPMatrix(float[] pm) {
        System.arraycopy(pm, 0, pMatrix, 0, 16);
    }

    //カメラ視点変換行列をシェーダに指定
    public static void setCMatrix(float[] cm) {
        System.arraycopy(cm, 0, cMatrix, 0, 16);
    }

    //カメラ視点変換行列×モデルビュー行列をシェーダに指定
    public static void updateMatrix(float[] mm) {
        Matrix.multiplyMM(mMatrix, 0, cMatrix, 0, mm, 0);       //mMatrix = cMatrix * mm
        Matrix.multiplyMM(pmMatrix, 0, pMatrix, 0, mMatrix, 0); //pmMatrix = pMatrix * mMatrix
        //モデルビュー行列をシェーダに指定
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix, 0);

        //射影行列をシェーダに指定
        GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, pMatrix, 0);

        if (useLighting == true) {
            //光源位置をシェーダに指定
            Matrix.multiplyMV(CVLightPos, 0, cMatrix, 0, LightPos, 0);
            GLES20.glUniform4f(GLES.lightPosHandle, CVLightPos[0], CVLightPos[1], CVLightPos[2], 1.0f);
        }
    }

    public static void transformPCM(float[] result, float[] source) {
        Matrix.multiplyMV(result, 0, GLES.pmMatrix, 0, source, 0);
        result[0] /= result[3];
        result[1] /= result[3];
        result[2] /= result[3];
        result[3] = 1f;
    }

    public static boolean checkLiting() {
        return useLighting;
    }
}