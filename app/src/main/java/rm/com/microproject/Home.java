package rm.com.microproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {


    Button upload,view,logout;

    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = findViewById(R.id.upload);
        view = findViewById(R.id.view);
        logout = findViewById(R.id.logout);


        dbHandler = new DBHandler(this);




        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadIntent = new Intent(Home.this,Upload.class);
                startActivity(uploadIntent);

                finish();

            }
        });



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(Home.this, DisplayImages.class);
                startActivity(viewIntent);
                finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.onDrop();
                editor.putBoolean("loggedIn",false);
                Intent goToLogin = new Intent(Home.this,LoginActivity.class);
                startActivity(goToLogin);
                finish();

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.commit();
    }
}
