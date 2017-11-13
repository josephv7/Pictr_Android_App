package rm.com.microproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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


            imageView = itemView.findViewById(R.id.imageView);
            imageId = itemView.findViewById(R.id.imageId);

            Log.d("inside","viewholder......");
        }
    }


    @Override
    public DisplayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.list_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;


    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageDetails data = imageDetails.get(position);

        holder.imageId.setText(data.getImageId());

        Log.d("position",Integer.toString(position));

        Glide.with(context)
                .load(data.getImageUrl())
//                .placeholder(R.drawable.user_male)
//                .error(R.drawable.user_male)
                .crossFade()
                .override(600,600)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .centerCrop()
                .into(holder.imageView);
    }




    @Override
    public int getItemCount() {
        return imageDetails.size();
    }
}
