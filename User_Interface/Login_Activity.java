package com.example.myapplication_1.User_Interface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Admin.Admin_Category_Activity;
import com.example.myapplication_1.Admin.Admin_addnewproduct_Activity;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.Home_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class Login_Activity extends AppCompatActivity {

    private Button login_btn;
    private EditText phoneInput, passwordInput;
    private ProgressDialog loadingbar;
    private TextView admin_link, not_admin_link;
    private List<String> listData;

    private String parentDbName = "User";

    //private CheckBox checkBoxRememberMe;

    private FirebaseAuth f_auth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin_link.setVisibility(View.INVISIBLE);
                not_admin_link.setVisibility(View.VISIBLE);
                login_btn.setText("Вход для администратора");
                parentDbName = "Admin";
            }
        });

        not_admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin_link.setVisibility(View.VISIBLE);
                not_admin_link.setVisibility(View.INVISIBLE);
                login_btn.setText("Войти");
                parentDbName = "User";
            }
        });

    }

    private void loginUser() {

        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if((TextUtils.isEmpty(phone)))
        {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        }
        else if((TextUtils.isEmpty(password)))
        {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Вход в приложение");
            loadingbar.setMessage("Пожалуйста, подождите...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            if (parentDbName.equals("User")){
                ValidateUser(phone, password);
            }
            else if (parentDbName.equals("Admin")){
                ValidateAdmin(phone, password);
            }
        }
    }

    private void ValidateAdmin(String phone, String password){

        RootRef = FirebaseDatabase.getInstance().getReference().child(parentDbName);

        f_auth.signInWithEmailAndPassword(phone, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(Objects.requireNonNull(f_auth.getUid())).exists())
                            {
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Успешный вход, администратор!", Toast.LENGTH_SHORT).show();

                                Intent admin_category_intent = new Intent(Login_Activity.this, Admin_Category_Activity.class);
                                startActivity(admin_category_intent);
                            }
                            else{
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Вход только для администратора", Toast.LENGTH_LONG).show();
                                f_auth.signOut();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingbar.dismiss();
                            Toast.makeText(Login_Activity.this, "Ошибка", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{

                    loadingbar.dismiss();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }  catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(Login_Activity.this, "Предоставленные учетные данные для проверки подлинности неверны", Toast.LENGTH_LONG).show();
                        Toast.makeText(Login_Activity.this, "неправильно сформированы или срок их действия истек.", Toast.LENGTH_LONG).show();
                        phoneInput.requestFocus();
                    } catch(Exception e) {
                        Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    private void ValidateUser(String phone, String password) {

        RootRef = FirebaseDatabase.getInstance().getReference().child(parentDbName);

        f_auth.signInWithEmailAndPassword(phone, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(Objects.requireNonNull(f_auth.getUid())).exists())
                            {
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Успешный вход " + Objects.requireNonNull(f_auth.getCurrentUser()).getEmail(), Toast.LENGTH_SHORT).show();

                                Intent home_intent = new Intent(Login_Activity.this, Home_Activity.class);
                                startActivity(home_intent);
                            }
                            else{
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Вход только для пользователей", Toast.LENGTH_LONG).show();
                                f_auth.signOut();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingbar.dismiss();
                            Toast.makeText(Login_Activity.this, "Ошибка", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{

                    loadingbar.dismiss();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }  catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(Login_Activity.this, "Предоставленные учетные данные для проверки подлинности неверны", Toast.LENGTH_LONG).show();
                        Toast.makeText(Login_Activity.this, "неправильно сформированы или срок их действия истек.", Toast.LENGTH_LONG).show();
                        phoneInput.requestFocus();
                    } catch(Exception e) {
                        Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        /*final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){
                    Users userData = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            if (parentDbName.equals("Users")){
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Успешный вход", Toast.LENGTH_SHORT).show();

                                Intent home_intent = new Intent(Login_Activity.this, Home_Activity.class);
                                startActivity(home_intent);
                            }
                            else if (parentDbName.equals("Admins")){
                                loadingbar.dismiss();
                                Toast.makeText(Login_Activity.this, "Успешный вход", Toast.LENGTH_SHORT).show();

                                Intent admin_category_intent = new Intent(Login_Activity.this, Admin_Category_Activity.class);
                                startActivity(admin_category_intent);
                            }
                        }
                        else{
                            loadingbar.dismiss();
                            Toast.makeText(Login_Activity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        loadingbar.dismiss();
                        Toast.makeText(Login_Activity.this, "Неверный номер телефона", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    loadingbar.dismiss();
                    Toast.makeText(Login_Activity.this, "Аккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        /*if (checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }*/
    }

    private void init(){

        login_btn = findViewById(R.id.login_btn);
        phoneInput = findViewById(R.id.login_phone_input);
        passwordInput = findViewById(R.id.login_password_input);
        //checkBoxRememberMe = findViewById(R.id.login_checkbox);
        admin_link = findViewById(R.id.admin_panel_link);
        not_admin_link = findViewById(R.id.not_admin_panel_link);

        loadingbar = new ProgressDialog(this);

        Paper.init(this);

        f_auth = FirebaseAuth.getInstance();

    }
}