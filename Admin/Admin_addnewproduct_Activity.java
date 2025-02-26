package com.example.myapplication_1.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication_1.Model.Products;
import com.example.myapplication_1.R;
import com.example.myapplication_1.Users.Home_Activity;
import com.example.myapplication_1.Users.Product_Page_Activity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Admin_addnewproduct_Activity extends AppCompatActivity {

    public String Description, Price, Name, Author, saveCurrentDate, saveCurrentTime, productRandomKey;

    private ImageView productImage, back_btn;
    private EditText productName, productAuthor, productDescription, productPrice;
    private Button addNewproduct_btn;

    private String download_image_URL;
    private static final int galery_pick = 1;
    private Uri ImageUri;

    private StorageReference ProductImageref;
    private DatabaseReference ProductsRef;

    private ProgressDialog loadingbar;
    private String Key;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_addnewcategory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main7), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent admin_addnewproduct_intent = new Intent(Admin_addnewproduct_Activity.this, Admin_Category_Activity.class);
                startActivity(admin_addnewproduct_intent);
            }
        });

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

    private void validate_product_data() {

        Description = productDescription.getText().toString().trim();
        Price = productPrice.getText().toString().trim();
        Name = productName.getText().toString().trim();
        Author = productAuthor.getText().toString().trim();

        if (ImageUri == null){
            Toast.makeText(this, "Добавтье изображение обложки книги", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Добавтье описание книги", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Добавтье стоимость книги", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Добавтье название книгиа", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Author)){
            Toast.makeText(this, "Добавтье автора книги", Toast.LENGTH_LONG).show();
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

        SimpleDateFormat currentDate = new SimpleDateFormat("dd.MM.yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH.mm.ss");
        saveCurrentTime = currentTime.format(calendar.getTime());


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
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Фото сохранено", Toast.LENGTH_SHORT).show();
                    ImageUri = task.getResult();
                    SaveProductIhfoToDatabase();
                }
                else{
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Ошибка сохранения изображения: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
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

        Products product = new Products(Key, Name, Author, Description, Price, ImageUri.toString(), saveCurrentDate, saveCurrentTime);

        ProductsRef.setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    loadingbar.dismiss();
                    Toast.makeText(Admin_addnewproduct_Activity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();

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

        calendar = Calendar.getInstance();

        ProductImageref = FirebaseStorage.getInstance().getReference("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference("Products").push();
        Key = ProductsRef.getKey();

        productImage = findViewById(R.id.select_product_image2);
        productName = findViewById(R.id.product_name);
        productAuthor = findViewById(R.id.product_author);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_name_out9);
        addNewproduct_btn = findViewById(R.id.button_add_newproduct);
        back_btn = findViewById(R.id.imageView123);

        loadingbar = new ProgressDialog(this);
    }

}