package rm.com.microproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Joseph on 13/11/17.
 */

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder>{


    private List<ImageDetails> imageDetails;
    private Context context;


    public DisplayAdapter(Context context, List<ImageDetails> imageDetails) {
        this.imageDetails = imageDetails;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView imageId;

        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
        }
    }




}
