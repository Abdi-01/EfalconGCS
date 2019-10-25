package com.example.al_ghifar.efalcongcs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Welcome extends Activity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageView welcome = (ImageView) findViewById(R.id.welcome);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        welcome.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(Welcome.this, Efalcongcs.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT );
    }
}
