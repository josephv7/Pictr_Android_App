package rm.com.microproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Display extends AppCompatActivity {


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


        dbHandler = new DBHandler(getApplicationContext());
        imageList = new ArrayList<ImageDetails>();


        imageList = dbHandler.getAllImages();





    }
}
