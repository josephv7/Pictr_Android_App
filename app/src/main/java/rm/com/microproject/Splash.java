package rm.com.microproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Splash extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Boolean loggedIn;

    TextView splashText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        splashText = findViewById(R.id.textView);




        Typeface bebas = Typeface.createFromAsset(getAssets(),  "fonts/bebasneue.ttf");

        splashText.setTypeface(bebas);


        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loggedIn = sharedPreferences.getBoolean("loggedIn",false);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!loggedIn){
                    Intent loginIntent = new Intent(Splash.this,LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }else{
                    Intent homeIntent = new Intent(Splash.this,Home.class);
                    startActivity(homeIntent);
                    finish();
                }


            }
        },3000);






    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        editor.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        editor.commit();
    }
}
