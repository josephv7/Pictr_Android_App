package rm.com.microproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {


    Button upload,view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = findViewById(R.id.upload);
        view = findViewById(R.id.view);


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


    }
}
