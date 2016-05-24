package com.example.control.gles2sample11;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class MainActivity extends Activity {
    private MyGLSurfaceView glView;
    private GestureDetector gesDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //GLサーフェスビュー
        glView=new MyGLSurfaceView(this);
        setContentView(glView); //これでviewを設置している

        //glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //これを有効にすると，描画させたいタイミングで、
        // 描画させたいタイミングで、GLSurfaceViewのrequestRenderを呼び出す必要がある。

        gesDetector = new GestureDetector(this, (GestureDetector.OnGestureListener) glView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //glView.requestRender();
        return gesDetector.onTouchEvent(event);
    }

    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
