package com.example.myapplication_1.ViewHolder;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication_1.Interface.ItemClicklistener;
import com.example.myapplication_1.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductionName, txtProductionAuthor, txtProductionPrice;
    public ImageView imageView;
    public ItemClicklistener listener;
    public Button nav;
    public LinearLayout cardlayout;

    public ProductViewHolder(View itemView)
    {
        super(itemView);
        
        imageView = itemView.findViewById(R.id.product_image9);
        txtProductionName = itemView.findViewById(R.id.product_name_out9);
        txtProductionAuthor = itemView.findViewById(R.id.product_author_out9);
        txtProductionPrice = itemView.findViewById(R.id.product_price_out9);


        cardlayout = itemView.findViewById(R.id.card_layout_9);
        
    }

    
    public void setItemClickListener(ItemClicklistener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view){
        listener.onclick(view, getAdapterPosition(), true);
    }


}
