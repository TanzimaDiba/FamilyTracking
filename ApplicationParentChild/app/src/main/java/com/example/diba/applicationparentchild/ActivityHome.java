package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class ActivityHome extends ActionBarActivity {

    ImageView image,imageIcon;
    Animation down, up, animation, still;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        image = (ImageView) findViewById(R.id.image);
        imageIcon = (ImageView) findViewById(R.id.imageView_icon);

        toAnimation();
        toAddListener();
    }

    private void toAnimation() {

        still = new TranslateAnimation(0, 0, 0, 0);
        still.setDuration(1500);
        still.setFillAfter(true);

        down = new TranslateAnimation(0, 0, 0, 50);
        down.setDuration(700);
        down.setFillAfter(true);

        animation = new TranslateAnimation(0, 0, 50, -240);
        animation.setDuration(500);

        up = new TranslateAnimation(0, 0, -240, -190);
        up.setDuration(300);
        up.setFillAfter(true);
    }

    private void toAddListener() {

        still.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation still) {}
            @Override
            public void onAnimationEnd(Animation still) {
                image.startAnimation(down);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });


        down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation down) {}
            @Override
            public void onAnimationEnd(Animation down) {
                image.startAnimation(animation);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                image.startAnimation(up);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation up) { }
            @Override
            public void onAnimationEnd(Animation up) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    imageIcon.setVisibility(View.VISIBLE);
                    Intent i = new Intent(ActivityHome.this, ActivityLogin.class);
                    startActivity(i);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        image.startAnimation(still);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
