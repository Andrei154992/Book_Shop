package com.example.myapplication_1.ViewHolder;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication_1.Admin.Admin_Category_Activity;
import com.example.myapplication_1.Interface.ItemClicklistener;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.Home_Activity;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductionName, txtProductionDescription, txtProductionPrice;
    public ImageView imageView;
    public ItemClicklistener listener;
    public Button nav;

    public ProductViewHolder(View itemView)
    {
        super(itemView);
        
        imageView = itemView.findViewById(R.id.product_image);
        txtProductionName = itemView.findViewById(R.id.product_name);
        txtProductionDescription = itemView.findViewById(R.id.product_description);
        txtProductionPrice = itemView.findViewById(R.id.product_price);
        nav = itemView.findViewById(R.id.nav_btn);
        
    }

    
    public void setItemClickListener(ItemClicklistener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view){
        listener.onclick(view, getAdapterPosition(), true);
    }


}
