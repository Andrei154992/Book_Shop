package com.example.myapplication_1.Users;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Admin.Admin_Category_Activity;
import com.example.myapplication_1.Admin.Admin_addnewproduct_Activity;
import com.example.myapplication_1.User_Interface.Login_Activity;
import com.example.myapplication_1.Model.Users;
import com.example.myapplication_1.Prevalent.Prevalent;
import com.example.myapplication_1.R;
import com.example.myapplication_1.User_Interface.Registration_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button join_btn, login_btn, mmm;
    private ProgressDialog loadingbar;
    private FirebaseAuth f_auth = FirebaseAuth.getInstance();
    private DatabaseReference RootRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        join_btn = findViewById(R.id.main_join_btn_);
        login_btn = findViewById(R.id.main_login_btn);
        mmm = findViewById(R.id.mmm);

        loadingbar = new ProgressDialog(this);

        Paper.init(this);

        FirebaseUser cuser = f_auth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("Admin");

        if (cuser != null)
        {
            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(cuser.getUid()).exists())
                    {
                        Toast.makeText(MainActivity.this, "Успешный вход, администратор!", Toast.LENGTH_SHORT).show();

                        Intent admin_category_intent = new Intent(MainActivity.this, Admin_Category_Activity.class);
                        startActivity(admin_category_intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Успешный вход " + cuser.getEmail(), Toast.LENGTH_SHORT).show();

                        Intent registration_intent = new Intent(MainActivity.this, Home_Activity.class);
                        startActivity(registration_intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Необходимо войти", Toast.LENGTH_SHORT).show();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(MainActivity.this, Login_Activity.class);
                startActivity(login_intent);
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regisrter_intent = new Intent(MainActivity.this, Registration_Activity.class);
                startActivity(regisrter_intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                ValidateUser(UserPhoneKey, UserPasswordKey);

                loadingbar.setTitle("Вход в приложение");
                loadingbar.setMessage("Пожалуйста, подождите...");
                loadingbar.setCanceledOnTouchOutside(true);
                loadingbar.show();
            }
        }
    }

    private void ValidateUser(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()) {
                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)) {
                        if (userData.getPassword().equals(password)) {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();

                            Intent home_intent = new Intent(MainActivity.this, Home_Activity.class);
                            startActivity(home_intent);
                        } else {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loadingbar.dismiss();
                        Toast.makeText(MainActivity.this, "Неверный номер телефона", Toast.LENGTH_SHORT).show();
                    }
                } else if (snapshot.child("Admins").child(phone).exists()) {
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "Добро пожаловать, Админ!!!", Toast.LENGTH_SHORT).show();

                    Intent registration_intent = new Intent(MainActivity.this, Registration_Activity.class);
                    startActivity(registration_intent);
                } else {
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();

                    //Intent registration_intent = new Intent(MainActivity.this, Registration_Activity.class);
                    //startActivity(registration_intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}