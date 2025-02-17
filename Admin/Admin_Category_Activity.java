package com.example.myapplication_1.Admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication_1.Model.Products;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.MainActivity;
import com.example.myapplication_1.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class Admin_Category_Activity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private ImageView logout, edit_btn;
    private FirebaseAuth f_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_category);

        ProductsRef = FirebaseDatabase.getInstance().getReference("Products");
        f_auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_menu_1);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        logout = findViewById(R.id.imageView_1);
        edit_btn = findViewById(R.id.imageView_2);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                f_auth.signOut();
                Toast.makeText(Admin_Category_Activity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

                Intent admin_category_intent = new Intent(Admin_Category_Activity.this, MainActivity.class);
                startActivity(admin_category_intent);
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent admin_category_intent = new Intent(Admin_Category_Activity.this, Admin_addnewproduct_Activity.class);
                startActivity(admin_category_intent);
            }
        });

    }

    protected void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductsRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int i, @NonNull @NotNull Products model) {

                holder.txtProductionName.setText(model.getName());
                holder.txtProductionAuthor.setText(model.getAuthor());
                holder.txtProductionPrice.setText(model.getPrice());
                holder.cardlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent admin_category_intent = new Intent(Admin_Category_Activity.this, Admin_change_Activity.class);
                        admin_category_intent.putExtra("add_product", model.getName());
                        startActivity(admin_category_intent);
                    }
                });

                Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get().load(model.getImage()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                double width = bitmap.getWidth();
                                double height = bitmap.getHeight();

                                double proportion = height/width;

                                int result_widht = 700;

                                Picasso.get().load(model.getImage()).resize(result_widht, (int) (result_widht * proportion)).into(holder.imageView);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(Admin_Category_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_category, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}