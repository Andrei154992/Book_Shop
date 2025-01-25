package com.example.myapplication_1.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication_1.Model.Products;
import com.example.myapplication_1.Model.Users;
import com.example.myapplication_1.R;
import com.example.myapplication_1.User_Interface.Login_Activity;
import com.example.myapplication_1.User_Interface.Registration_Activity;
import com.example.myapplication_1.ViewHolder.ProductViewHolder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.drawable.CircularProgressDrawable;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Admin_addnewproduct_Activity extends AppCompatActivity {

    public String Category_name, Description, Price, Name, saveCurrentDate, saveCurrentTime, productRandomKey;

    private String download_image_URL;
    private List<String> listData;
    private ArrayAdapter<String> adapter;
    private ImageView productImage;
    private EditText productName, productDescription, productPrice;
    private Button addNewproduct_btn;
    private static final int galery_pick = 1;
    private Uri ImageUri;
    private StorageReference ProductImageref;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingbar;
    private String Key = "Products";
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_addnewcategory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();

        Output_inf();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGalery();
            }
        });

        addNewproduct_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate_product_data();
            }
        });

    }

    private void Output_inf() {

        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Products product = ds.getValue(Products.class);
                    assert product != null;
                    if (product.getCategory().equals(Category_name)){
                        productName.setText(product.getPname());
                        productDescription.setText(product.getDescription());
                        productPrice.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).into(productImage);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validate_product_data() {

        Description = productDescription.getText().toString();
        Price = productPrice.getText().toString();
        Name = productName.getText().toString();

        if (ImageUri == null){
            Toast.makeText(this, "Добавтье изображение товара", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Добавтье описание товара", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Добавтье стоимость товара", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Добавтье название товара", Toast.LENGTH_LONG).show();
        }
        else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingbar.setTitle("Загрузка данных");
        loadingbar.setMessage("Пожалуйста, подождите...");
        loadingbar.setCanceledOnTouchOutside(true);
        loadingbar.show();


        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
//

        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();


        final StorageReference filePath = ProductImageref.child(System.currentTimeMillis() + "mu_image");

        UploadTask uploadTask = filePath.putBytes(byteArray);


        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                //download_image_URL = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Фото сохранено", Toast.LENGTH_LONG).show();
                    ImageUri = task.getResult();
                    SaveProductIhfoToDatabase();
                }
                else{
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Ошибка сохранения изображения", Toast.LENGTH_LONG).show();
                }
            }
        });
        /*uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(Admin_addnewproduct_Activity.this, "Ошибка " + message, Toast.LENGTH_LONG).show();
                loadingbar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Admin_addnewproduct_Activity.this, "Изображение успешно загружено ", Toast.LENGTH_LONG).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        download_image_URL = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Admin_addnewproduct_Activity.this, "Фото сохранено", Toast.LENGTH_LONG).show();

                            SaveProductIhfoToDatabase();
                        }
                    }
                });
            }
        });*/
    }


    private void SaveProductIhfoToDatabase() {

        Products product = new Products(Name, Description, Price, ImageUri.toString(), Category_name, productRandomKey, saveCurrentDate, saveCurrentTime);

        ProductsRef.push().setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    loadingbar.dismiss();
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Товар добавлен", Toast.LENGTH_LONG).show();

                    Intent adminadd_intent = new Intent(Admin_addnewproduct_Activity.this, Admin_Category_Activity.class);
                    startActivity(adminadd_intent);
                }
                else{
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Ошибка: " + message, Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }
        });
                
    }

    private void OpenGalery() {

        Intent galery_intent = new Intent();
        galery_intent.setType("image/*");
        galery_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galery_intent, galery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galery_pick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            productImage.setImageURI(data.getData());
        }
    }

    private void Init(){

        Category_name = getIntent().getExtras().get("category").toString();
        //holder = new ProductViewHolder(productImage);
        calendar = Calendar.getInstance();

        ProductImageref = FirebaseStorage.getInstance().getReference("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference(Key);


        productImage = findViewById(R.id.select_product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        addNewproduct_btn = findViewById(R.id.button_add_newproduct);


        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

        loadingbar = new ProgressDialog(this);
    }
}