package com.example.myapplication_1.Users;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication_1.Admin.Admin_Category_Activity;
import com.example.myapplication_1.Admin.Nav_Activity;
import com.example.myapplication_1.Model.Products;
import com.example.myapplication_1.R;
import com.example.myapplication_1.ViewHolder.ProductViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

import io.paperdb.Paper;

public class Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference ProductsRef, rootRef;
    private FirebaseAuth f_auth;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ProductsRef = FirebaseDatabase.getInstance().getReference("Products");
        f_auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Товары");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent home_intent = new Intent(Home_Activity.this, Nav_Activity.class);
                startActivity(home_intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }



    @Override
    protected void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(ProductsRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int i, @NonNull @NotNull Products model) {

                holder.txtProductionName.setText(model.getName());
                holder.txtProductionAuthor.setText(model.getAuthor());
                holder.txtProductionPrice.setText(model.getPrice() + " ₽");
                holder.cardlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent home_category_intent = new Intent(Home_Activity.this, Product_Page_Activity.class);

                        home_category_intent.putExtra("product", model.getName());
                        startActivity(home_category_intent);
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
                        Toast.makeText(Home_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            /*Transformation transformation = new Transformation() {

                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = holder.imageView.getWidth();

                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "transformation" + " desiredWidth";
                    }
                };

                //mMessage_pic_url = message_pic_url;

                Picasso.get()
                        .load(model.getImage())
                        .error(android.R.drawable.stat_notify_error)
                        .transform(transformation)
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(Home_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });*/

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

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_cart)
        {
            Intent come_nav = new Intent(Home_Activity.this, Nav_Activity.class);
            startActivity(come_nav);
        }
        else if(id == R.id.nav_categories)
        {

        }
        else if(id == R.id.nav_logout)
        {
            Paper.book().destroy();
            f_auth.signOut();
            Toast.makeText(Home_Activity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

            Intent home_category_intent = new Intent(Home_Activity.this, MainActivity.class);
            startActivity(home_category_intent);
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}