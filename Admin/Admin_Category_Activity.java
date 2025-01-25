package com.example.myapplication_1.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.R;
import android.widget.ImageView;

public class Admin_Category_Activity extends AppCompatActivity {

    private ImageView sweather, tshirts, shoess, hats;
    private ImageView pong, bascketball, football, bowling;
    private ImageView chair, chart, pencil, printer;
    private ImageView hand, pruners, backet, shovel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();

        sweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_sweather = new Intent(Admin_Category_Activity.this, Admin_addnewproduct_Activity.class);
                intent_sweather.putExtra("category", "sweather");
                startActivity(intent_sweather);
            }
        });

        pong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_pong = new Intent(Admin_Category_Activity.this, Admin_addnewproduct_Activity.class);
                intent_pong.putExtra("category", "pong");
                startActivity(intent_pong);
            }
        });

        chair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_chair = new Intent(Admin_Category_Activity.this, Admin_addnewproduct_Activity.class);
                intent_chair.putExtra("category", "chair");
                startActivity(intent_chair);
            }
        });

        hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_hand = new Intent(Admin_Category_Activity.this, Admin_addnewproduct_Activity.class);
                intent_hand.putExtra("category", "hand");
                startActivity(intent_hand);
            }
        });

    }

    private void Init()
        {
        sweather = findViewById(R.id.sweather);
        tshirts = findViewById(R.id.tshirts);
        shoess = findViewById(R.id.shoess);
        hats = findViewById(R.id.hats);

        pong = findViewById(R.id.ping_pong);
        bascketball = findViewById(R.id.basketball);
        football = findViewById(R.id.american_football);
        bowling = findViewById(R.id.bowling_pin);

        chair = findViewById(R.id.chair);
        chart = findViewById(R.id.flip_chart);
        pencil = findViewById(R.id.pencil_box);
        printer = findViewById(R.id.printer);

        hand = findViewById(R.id.hand_gloves);
        pruners = findViewById(R.id.pruners);
        backet = findViewById(R.id.bucket);
        shovel = findViewById(R.id.shovel);
    }

}