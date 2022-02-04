package edu.unicauca.uplace.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import edu.unicauca.uplace.R;
import edu.unicauca.uplace.activities.AddPlaceActivity;
import edu.unicauca.uplace.activities.MainActivity;
import edu.unicauca.uplace.database.DatabaseHandler;
import edu.unicauca.uplace.models.UPlaceModel;
import edu.unicauca.uplace.utils.FragmentPlaces;

public class UPlaceAdapter  extends RecyclerView.Adapter<UPlaceAdapter.MyViewHolder> {

    Context context;
    ArrayList<UPlaceModel> list;

    private UPlaceAdapter.onClickListener onClickListener = null;

    public UPlaceAdapter(Context ct, ArrayList<UPlaceModel> lt){
        context = ct;
        list = lt;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_single_place,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UPlaceModel model = list.get(position);


        holder.iv_place_image.setImageURI(Uri.parse(model.image));
        holder.tvTitle.setText(model.title);
        holder.tvDescription.setText(model.description);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener != null){
                    onClickListener.onClick(position,model);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        ImageView iv_place_image;
        TextView tvTitle;
        TextView tvDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_place_image = itemView.findViewById(R.id.id_place_image);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }

    public interface onClickListener{
        public void onClick(int position, UPlaceModel model);
    }

    public void notifyEditItem(Activity activity, int position,int requestCode){
        Intent intent = new Intent(context, AddPlaceActivity.class);
        intent.putExtra(FragmentPlaces.PLACE_DETAILS, list.get(position));
        activity.startActivityForResult(intent,requestCode);
        Log.e("position", ""+position);

        notifyItemChanged(position); //Recarga la data en la activity

        //no RECARGA el Recyclerview
        //notifyAll();
    };

    public void removeAt(int position){
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        int isDeleted = dbHandler.deletePlace(list.get(position));

        if(isDeleted>0){
            list.remove(position);
            notifyItemRemoved(position);
        }

    }


}
