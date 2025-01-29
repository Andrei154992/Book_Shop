package com.example.myapplication_1.Admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Model.Nav;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.Home_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Nav_Activity extends AppCompatActivity {

    private TextView app_s;
    private ProgressDialog loadingbar;
    private ImageButton bacK_btn;

    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private ListView listView;

    private DatabaseReference RootRef;
    private FirebaseAuth f_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_nav);
        init();
        ValidateUser();

        bacK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_intent = new Intent(Nav_Activity.this, Home_Activity.class);
                startActivity(back_intent);
            }
        });

    }

    private void ValidateUser() {

        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Nav user = ds.getValue(Nav.class);
                    assert user != null;
                    listData.add(user.getNname());
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init(){

        bacK_btn = findViewById(R.id.back_btn);

        loadingbar = new ProgressDialog(this);
        listView = findViewById(R.id.list);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        f_auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(f_auth.getCurrentUser()).getUid()).child("Избранное");
    }

}