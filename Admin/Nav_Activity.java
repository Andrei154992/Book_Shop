package com.example.myapplication_1.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Model.Nav;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.Home_Activity;
import com.example.myapplication_1.Users.Product_Page_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private ImageButton bacK_btn, del_btn;

    private ArrayAdapter<String> adapter1;
    private List<String> listData1;
    private ListView listView1;
    private int product_price;

    private DatabaseReference RootRef1;
    private FirebaseAuth f_auth1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_nav);

        init();
        Output_nav();

        bacK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_intent = new Intent(Nav_Activity.this, Home_Activity.class);
                startActivity(back_intent);
            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RootRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Nav_Activity.this, "Корзина очищена", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Nav_Activity.this, "Ошибка: " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                        Toast.makeText(Nav_Activity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }



    private void Output_nav() {

        RootRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!listData1.isEmpty()){
                    listData1.clear();
                }
                String name = "";
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Nav user = ds.getValue(Nav.class);
                    assert user != null;
                    name = user.name;
                    int count = 0;

                    for (int i = 0; i < listData1.size(); i++){
                        if (Objects.equals(listData1.get(i), name))
                        {
                            count++;
                        }
                    }
                    if (count == 0)
                    {
                        product_price += Integer.parseInt(user.price);
                        listData1.add(user.name + ", " + user.price + " ₽");
                    }
                }
                listData1.add("Общая стоимость: " + product_price + " ₽");

                if(listData1.size() == 1){
                    listData1.clear();
                    listData1.add("Ваша корзина пуста");
                }

                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Nav_Activity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init(){

        bacK_btn = findViewById(R.id.back_btn1);
        del_btn = findViewById(R.id.delete_btn1);

        listView1 = findViewById(R.id.list1);
        listData1 = new ArrayList<>();
        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData1);
        listView1.setAdapter(adapter1);

        f_auth1 = FirebaseAuth.getInstance();
        RootRef1 = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(f_auth1.getCurrentUser()).getUid()).child("Избранное");
    }

}