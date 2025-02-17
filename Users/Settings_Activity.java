package com.example.myapplication_1.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Prevalent.Prevalent;
import com.example.myapplication_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity {

    private CircleImageView Profile_image_view;
    private EditText Name, Phone, Address;
    private ImageView Save_set, Close_set;
    private String checker;

    private DatabaseReference rootRef;
    private FirebaseAuth f_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        Close_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent close_btn = new Intent(Settings_Activity.this, Home_Activity.class);
                startActivity(close_btn);
            }
        });

        Save_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }
                else{
                    updateOnlyUserInfo();
                }
            }
        });

        Profile_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker = "clicked";
            }
        });

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(Name.getText().toString()))
        {
            Toast.makeText(this, "Заполните имя.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Phone.getText().toString()))
        {
            Toast.makeText(this, "Заполните адрес", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Address.getText().toString()))
        {
            Toast.makeText(this, "Заполните номер", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            //uploadImage();
        }
    }

    private void updateOnlyUserInfo() {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init(){

        Profile_image_view = findViewById(R.id.settings_account_image);

        rootRef = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(f_auth.getCurrentUser()).getUid()).child("Профиль");
        f_auth = FirebaseAuth.getInstance();

        Name = findViewById(R.id.settings_fulname);
        Phone = findViewById(R.id.settings_phone);
        Address = findViewById(R.id.settings_address);

        Save_set = findViewById(R.id.save_settings);
        Close_set = findViewById(R.id.close_settings);
    }
}