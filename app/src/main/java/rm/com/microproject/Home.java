package rm.com.microproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class Home extends AppCompatActivity {


    Button upload,view,logout;

    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = findViewById(R.id.upload);
        view = findViewById(R.id.view);
        logout = findViewById(R.id.logout);


        dbHandler = new DBHandler(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        dialog = new MaterialDialog.Builder(Home.this)
                .title("Enter You Choice")
                .content("Please Wait")
                .positiveText("Storage")
                .negativeText("Camera")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("positive clicked","............");
                    }
                })
                .show();





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
