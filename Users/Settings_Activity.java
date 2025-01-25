package com.example.myapplication_1.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Prevalent.Prevalent;
import com.example.myapplication_1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_Activity extends AppCompatActivity {

    private CircleImageView Profile_image_view;
    private EditText Name, Phone, Address;
    private TextView Save_set, Close_set;
    private String checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", Name.getText().toString());
        userMap.put("phoneOrder", Phone.getText().toString());
        userMap.put("address", Address.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(Settings_Activity.this, Home_Activity.class));
        Toast.makeText(Settings_Activity.this, "Успешно сохранено", Toast.LENGTH_LONG).show();
        finish();

    }

    private void init(){
        Profile_image_view = findViewById(R.id.settings_account_image);

        Name = findViewById(R.id.settings_fulname);
        Phone = findViewById(R.id.settings_phone);
        Address = findViewById(R.id.settings_address);

        Save_set = findViewById(R.id.save_settings_tw);
        Close_set = findViewById(R.id.close_settings_tw);
    }
}