package com.abort.exploreradmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.exploreradmin.CallBacks.IRecyclerClickListener;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Eventbus.PlaceClick;
import com.abort.exploreradmin.Eventbus.StatesClick;
import com.abort.exploreradmin.Model.PlacesModel;
import com.abort.exploreradmin.Model.StatesModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {
    Context context;
    List<PlacesModel> placesModelList;

    public PlacesAdapter(Context context, List<PlacesModel> placesModels) {
        this.context = context;
        this.placesModelList = placesModels;
    }
    @NonNull
    @Override
    public PlacesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlacesAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(placesModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(placesModelList.get(position).getName()));
        //event
        holder.setListener((view, pos) -> {
            placesModelList.get(pos).setPosition(pos);
            Common.placeSelected = placesModelList.get(pos);
            EventBus.getDefault().postSticky(new PlaceClick(true, placesModelList.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        if(placesModelList==null){
            return 0;
        }
        return placesModelList.size();
    }
    public List<PlacesModel> getListCategory() {
        return placesModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.img_category)
        SelectableRoundedImageView category_image;
        @BindView(R.id.txt_category)
        TextView category_name;

        IRecyclerClickListener listener;
        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());
        }
    }

}
