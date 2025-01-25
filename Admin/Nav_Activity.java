package com.example.myapplication_1.Admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Model.Nav;
import com.example.myapplication_1.Model.Users;
import com.example.myapplication_1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Nav_Activity extends AppCompatActivity {

    private TextView app_s;
    private ProgressDialog loadingbar;
    private Button button_add_newproduct, read_btn;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private ListView listView;
    private DatabaseReference RootRef;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fff);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_fff);
        init();
        ValidateUser();

    }


    /*private void Vvv() {

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users userData = snapshot.child("User").child("-OE65va9Gg8ZZfU88n4y").getValue(Users.class);
                Product_name.setText(userData.getPassword());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

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

        loadingbar = new ProgressDialog(this);
        button_add_newproduct = findViewById(R.id.button_add_newproduct);
        listView = findViewById(R.id.list_view);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
        RootRef = FirebaseDatabase.getInstance().getReference("Nav");
    }
}