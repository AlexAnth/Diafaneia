package com.example.alex.diafaneia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Alex on 27/7/2016.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        // animation
        final ImageView iv= (ImageView) findViewById(R.id.load);
        final Animation anim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation anim2 = AnimationUtils.loadAnimation(getBaseContext(),android.R.anim.fade_out);

        iv.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(anim2);
                Intent i = new Intent(com.example.alex.diafaneia.SplashScreen.this,MainActivity.class);
                iv.setVisibility(View.GONE);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
