package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
    class MyAddHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyAddHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);

        }
    }


    private List<ItemFactory> itemFactories;
    private Activity activity;
    private Context context;

    public ImageRecyclerAdapter(List<ItemFactory> itemFactories, Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.itemFactories = itemFactories;
        setImageScale();
    }

    private void setImageScale() {
        for (final ItemFactory itemFactory : itemFactories) {
            if (itemFactory.getType().ordinal() == 0 || itemFactory.getType().ordinal() == 2){
                notifyDataSetChanged();
            }else {
                if (itemFactory.getScale() == 0 ) {
                    Glide.with(context).load(itemFactory.getURL()).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            float scale = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                            itemFactory.setScale(scale);
                            notifyDataSetChanged();
                        }
                    });
                }else {
                    notifyDataSetChanged();
                }
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //如果有需要，这里也可以改
        if (viewType == 0){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_add,parent,false);
            MyAddHolder addHolder = new MyAddHolder(itemView);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            return addHolder;
        }else
        if (viewType == 2){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_add,parent,false);
            MyAddHolder addHolder = new MyAddHolder(itemView);
            return addHolder;
        }else {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image,parent,false);
            MyHolder holder=new MyHolder(itemView);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder == null){
            return;
        }
        ItemFactory itemFactory = itemFactories.get(position);
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onClick(position);
                }
                return true;
            }
        });
        //

        if (itemFactory.getType().ordinal() == 0 || itemFactory.getType().ordinal() == 2){
            MyAddHolder addHolder = (MyAddHolder)holder;
            addHolder.textView.setText(itemFactory.getTitle());
            addHolder.textView.setTextSize(itemFactory.getTextSize());
        }else{
            MyHolder holder1=(MyHolder)holder;
            final ViewGroup.LayoutParams layoutParams = holder1.imageView.getLayoutParams();
            layoutParams.width = DisplayUtils.getScreenWidth(activity) / itemFactory.getColumn() - DisplayUtils.dp2px(context,8);
            if(itemFactory.getScale()!=0){
                layoutParams.height = (int) (layoutParams.width/ itemFactory.getScale());
            }
            holder1.imageView.setBackgroundColor(Color.BLUE);
            Glide.with(context)
                    .load(itemFactory.getURL())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder1.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return itemFactories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemFactories.get(position).getType().ordinal();

    }
    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemLongClickListener {
        void onClick(int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

}
