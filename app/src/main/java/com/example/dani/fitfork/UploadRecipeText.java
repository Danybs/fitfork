package com.example.dani.fitfork;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dani.fitfork.Objetos.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class UploadRecipeText extends AppCompatActivity {

    private Button mButtonUpload;
    private EditText et_hashtag,et_ingredientes,et_instrucciones;

    private ProgressBar mProgressBar;

    private Uri mImageUri;
    private String user,nombreReceta;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe_text);

        mButtonUpload = findViewById(R.id.button_upload);
        et_hashtag = findViewById(R.id.et_hashtag);
        et_ingredientes = findViewById(R.id.et_ingredientes);
        et_instrucciones = findViewById(R.id.et_instrucciones);
        mProgressBar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //Obtenemos la URI de la IMG
        Intent intent = getIntent();
        mImageUri = intent.getData();
        user = intent.getStringExtra("USER");
        nombreReceta = intent.getStringExtra("nombreReceta");

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(UploadRecipeText.this, "Ya hay una subida en progreso", Toast.LENGTH_SHORT).show();
                } else {
                    if(nombreReceta != null && mImageUri!=null && user!=null && et_hashtag!=null && et_ingredientes!=null && et_instrucciones!=null) {
                    uploadFile();
                    } else {
                        Toast.makeText(UploadRecipeText.this, "Campos por rellenar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(UploadRecipeText.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(
                                    nombreReceta,
                                    taskSnapshot.getDownloadUrl().toString(),
                                    user,
                                    et_hashtag.getText().toString().trim(),
                                    et_ingredientes.getText().toString().trim(),
                                    et_instrucciones.getText().toString().trim());

                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadRecipeText.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                            //Cuando el progreso llege al final volvemos a la actividad principal
                            if (mProgressBar.getProgress()==mProgressBar.getMax()){
                                startActivity(new Intent(UploadRecipeText.this, MainWindow.class));
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No seleccionaste una imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
