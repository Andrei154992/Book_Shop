package com.example.myapplication_1.Users;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Model.Nav;
import com.example.myapplication_1.Model.Products;
import com.example.myapplication_1.R;
import com.example.myapplication_1.databinding.ActivityProductPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product_Page_Activity extends AppCompatActivity {


    private DatabaseReference ProductsRef, rootRef;
    private FirebaseAuth f_auth;

    private ArrayAdapter<String> adapter;
    private List<String> listData;

    private ActivityProductPageBinding binding;

    private String Prod_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_product_page);

        binding = ActivityProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        Output_inf();

        binding.imageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nav_intent = new Intent(Product_Page_Activity.this, Home_Activity.class);
                startActivity(nav_intent);
            }
        });

       binding.navAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            int count = 0;
                            for(DataSnapshot ds : snapshot.getChildren())
                            {
                                Nav nav0 = ds.getValue(Nav.class);
                                assert nav0 != null;
                                if (nav0.getNname().equals(binding.nameBookRecord2.getText().toString()))
                                {
                                    count++;
                                }
                            }
                            if (count > 0)
                            {
                                Toast.makeText(Product_Page_Activity.this, "Товар уже добавлен в корзину", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Nav nav = new Nav(binding.nameBookRecord2.getText().toString());
                                rootRef.push().setValue(nav).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(Product_Page_Activity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(Product_Page_Activity.this, "Ошибка: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            Nav nav = new Nav(binding.nameBookRecord2.getText().toString());
                            rootRef.push().setValue(nav).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {

                                        Toast.makeText(Product_Page_Activity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Toast.makeText(Product_Page_Activity.this, "Ошибка: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void Output_inf() {

        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Products product = ds.getValue(Products.class);
                    assert product != null;
                    if (Prod_page.equals(product.getName())){

                        binding.nameBookRecord2.setText(product.getName());
                        binding.authorBookRecord.setText(product.getAuthor());
                        binding.textView22.setText(product.getDescription());
                        binding.textView42.setText(product.getPrice());

                        Picasso.get().load(product.getImage()).into(binding.selectProductImage2, new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso.get().load(product.getImage()).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        double width = bitmap.getWidth();
                                        double height = bitmap.getHeight();

                                        double proportion = height/width;

                                        int result_widht = 1100;

                                        Picasso.get().load(product.getImage()).resize(result_widht, (int) (result_widht * proportion)).into(binding.selectProductImage2);
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
                                Toast.makeText(Product_Page_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Product_Page_Activity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Check_Replay(){

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Products product = ds.getValue(Products.class);
                    assert product != null;

                    if (Objects.equals(binding.nameBookRecord2.getText(), product.getName())) {
                        count++;
                    }

                }
                if (count != 0){
                    Toast.makeText(Product_Page_Activity.this, "Товар уже добавлен в корзину", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Product_Page_Activity.this, "Товар добавлен", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(){

        Prod_page = getIntent().getExtras().get("product").toString();

        f_auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(f_auth.getCurrentUser()).getUid()).child("Избранное");
        ProductsRef = FirebaseDatabase.getInstance().getReference("Products");

        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

    }

}