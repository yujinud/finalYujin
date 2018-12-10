package com.example.tjgh1.new002;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


       super.onCreate(savedInstanceState);

        try {   //3초 대기
            Thread.sleep(3000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        //메인 액티비티로 넘어감
        startActivity(new Intent(this, MainActivity.class));
        finish();


    }

}
