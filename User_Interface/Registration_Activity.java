package com.example.myapplication_1.User_Interface;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Admin.Admin_Category_Activity;
import com.example.myapplication_1.Admin.Admin_addnewproduct_Activity;
import com.example.myapplication_1.Model.Users;
import com.example.myapplication_1.R;
import com.example.myapplication_1.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Registration_Activity extends AppCompatActivity {

    private Button register_btn, read_btn;
    private EditText usernameInput, phoneInput, passwordInput;
    private ProgressDialog loadingbar;
    private DatabaseReference RootRef;
    private ArrayAdapter<String> adapter;
    private List<String> listData;
    private ActivityRegistrationBinding binding;
    private String Key = "User";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccunt();
            }
        });
    }


    private void CreateAccunt() {
        String username = usernameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if ((TextUtils.isEmpty(username))) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        } else if ((TextUtils.isEmpty(phone))) {
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        } else if ((TextUtils.isEmpty(password))) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Создание аккаунта");
            loadingbar.setMessage("Пожалуйста, подождите...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            ValidatePhone(username, phone, password);
        }
    }


    private void ValidatePhone1(final String username, final String phone, final String password)    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone );
                    userDataMap.put("name", username );
                    userDataMap.put("password", password );

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        loadingbar.dismiss();
                                        Toast.makeText(Registration_Activity.this, "Регистрация прошла успешно.", Toast.LENGTH_SHORT).show();

                                        Intent loginIntent = new Intent(Registration_Activity.this, Login_Activity.class);
                                        startActivity(loginIntent);
                                    }
                                    else {
                                        loadingbar.dismiss();
                                        Toast.makeText(Registration_Activity.this, "Ошибка.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    loadingbar.dismiss();
                    Toast.makeText(Registration_Activity.this, "Номер" + phone + "уже зарегистрирован", Toast.LENGTH_SHORT).show();

                    Intent loginIntent = new Intent(Registration_Activity.this, Login_Activity.class);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ValidatePhone(final String username, final String phone, final String password) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.registerPhoneInput.getText().toString(), binding.registerPasswordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    loadingbar.dismiss();
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("phone", binding.registerPhoneInput.getText().toString());
                    userInfo.put("password", binding.registerPasswordInput.getText().toString());
                    userInfo.put("name", binding.registerUsernamepInput.getText().toString());
                    RootRef.child(Key).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(userInfo);

                    Toast.makeText(Registration_Activity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    Intent login_intent = new Intent(Registration_Activity.this, Login_Activity.class);
                    startActivity(login_intent);

                } else {

                    loadingbar.dismiss();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch(FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(Registration_Activity.this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_LONG).show();
                        passwordInput.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(Registration_Activity.this, "Некорректный формат адреса электронной почты", Toast.LENGTH_LONG).show();
                        phoneInput.requestFocus();
                    } catch(FirebaseAuthUserCollisionException e) {
                        Toast.makeText(Registration_Activity.this, "Аккаунт с таким адресом уже существует", Toast.LENGTH_LONG).show();
                        phoneInput.requestFocus();
                    } catch(Exception e) {
                        Toast.makeText(Registration_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void init() {

        register_btn = findViewById(R.id.login_btn);
        usernameInput = findViewById(R.id.register_usernamep_input);
        phoneInput = findViewById(R.id.register_phone_input);
        passwordInput = findViewById(R.id.register_password_input);
        loadingbar = new ProgressDialog(this);
        RootRef = FirebaseDatabase.getInstance().getReference();

    }

}