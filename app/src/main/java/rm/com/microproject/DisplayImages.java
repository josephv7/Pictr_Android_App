package rm.com.microproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DisplayImages extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;;
    private RecyclerView.LayoutManager mLayoutManager;


    List<ImageDetails> imageList;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        dbHandler = new DBHandler(getApplicationContext());
        imageList = new ArrayList<ImageDetails>();


        imageList = dbHandler.getAllImages();



        mAdapter = new DisplayAdapter(getApplicationContext(),imageList);
        recyclerView.setAdapter(mAdapter);





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(DisplayImages.this,Home.class);
        startActivity(backIntent);
        finish();
    }
}
