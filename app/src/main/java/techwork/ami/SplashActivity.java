package techwork.ami;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {

    ProgressBar progressBarHorizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        progressBarHorizontal = (ProgressBar)findViewById(R.id.progressBarSplash);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                for (int progress=0;progress<=100;progress+=1){
                    try{
                        Thread.sleep(30);
                        progressBarHorizontal.setProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startActivity(intent);
                finish();
            }
        }).start();



    }
}
