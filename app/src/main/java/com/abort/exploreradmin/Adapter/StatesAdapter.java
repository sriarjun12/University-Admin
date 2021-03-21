package com.abort.exploreradmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.exploreradmin.CallBacks.IRecyclerClickListener;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Eventbus.CategoryHomeClick;
import com.abort.exploreradmin.Eventbus.StatesClick;
import com.abort.exploreradmin.Model.StatesModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.MyViewHolder> {
    Context context;
    List<StatesModel> statesModelList;
    public StatesAdapter(Context context, List<StatesModel> statesModelList) {
        this.context = context;
        this.statesModelList = statesModelList;
    }
    @NonNull
    @Override
    public StatesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatesAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull StatesAdapter.MyViewHolder holder, int position) {

        Glide.with(context).load(statesModelList.get(position).getImage())
                .into(holder.category_image);

        holder.category_name.setText(new StringBuilder(statesModelList.get(position).getName()));
        //event
        holder.setListener((view, pos) -> {
            Common.stateSelected = statesModelList.get(pos);
            EventBus.getDefault().postSticky(new StatesClick(true, statesModelList.get(pos)));
        });
    }



    @Override
    public int getItemCount() {
        return statesModelList.size();
    }
    public List<StatesModel> getListCategory() {
        return statesModelList;
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
