package com.abort.exploreradmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.exploreradmin.CallBacks.IRecyclerClickListener;
import com.abort.exploreradmin.Common.Common;
import com.abort.exploreradmin.Eventbus.CategoryHomeClick;
import com.abort.exploreradmin.Model.CategoryModel;
import com.abort.exploreradmin.R;
import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.MyViewHolder> {
    Context context;
    List<CategoryModel> categoryModelList;
    public CategoryHomeAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }
    @NonNull
    @Override
    public CategoryHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryHomeAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryHomeAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(categoryModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(categoryModelList.get(position).getName()));
        //event
        holder.setListener((view, pos) -> {
            Common.categorySelected = categoryModelList.get(pos);
            EventBus.getDefault().postSticky(new CategoryHomeClick(true,categoryModelList.get(pos)));
        });
    }
    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }
    public List<CategoryModel> getListCategory() {
        return categoryModelList;
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
