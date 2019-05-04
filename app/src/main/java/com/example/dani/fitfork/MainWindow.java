package com.example.dani.fitfork;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.example.dani.fitfork.Objetos.ImageAdapter;
import com.example.dani.fitfork.Objetos.Upload;
import com.github.clans.fab.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainWindow extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef;
    private static String USER;

    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private TextView mHashtag;

    private List<Upload> mUploads;

    private final String hashTag = "hashTag";
    private boolean show = false;


    //RECORDAR LOGIN AL REAUNUDAR LA APP
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        USER = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    protected String user() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        mAuth = FirebaseAuth.getInstance();

        mHashtag = (EditText) findViewById(R.id.hashtagText);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        //RECORDAR LOGIN
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    startActivity(new Intent(MainWindow.this, EmailPasswordActivity.class));
            }
        };

        //TOOLBAR ACTIVIDAD
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        //BUSQUEDA HASHTAGS
        mHashtag.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                    showRecyclerviewSearch();
            }
        });


        //BOTON BUSCAR
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showRecyclerviewSearch();
                mHashtag.setVisibility(View.VISIBLE);
            }
        });



        //BOTON AÑADIR
        floatingActionButton2.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                Intent intent = new Intent(MainWindow.this, UploadRecipeImg.class);
                intent.putExtra("USER", USER);
                startActivity(intent);
            }
        });

        //BOTON INICIO
        floatingActionButton3.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                mHashtag.setVisibility(View.INVISIBLE);
                mHashtag.setText("");
                mHashtag.clearComposingText();
                showRecyclerviewAll();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        showRecyclerviewAll();
    }

    public void showRecyclerviewAll() {
        //RECYCLER VIEW HOME
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                //Damos la vuelta a la lista para que los mas recientes aparezcan arriba
                Collections.reverse(mUploads);

                mAdapter = new ImageAdapter(MainWindow.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(MainWindow.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "FAIL READ DATABASE");
            }
        });
    }


    public void showRecyclerviewSearch() {
        //RECYCLER VIEW SEARCH
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot1 : postSnapshot.getChildren()) {
                        String key = postSnapshot1.getKey();
                        if (key.equalsIgnoreCase(hashTag)) {
                            String hashTagsServer[] = postSnapshot1.getValue().toString().trim().split("#");
                            String hashTagsUser[] = mHashtag.getText().toString().trim().split("#");
                            for (int i = 1; i < hashTagsUser.length; i++) {
                                for (int j = 1; j < hashTagsServer.length; j++) {
                                    if (hashTagsUser[i].trim().equalsIgnoreCase(hashTagsServer[j].trim())) {
                                        Upload upload = postSnapshot.getValue(Upload.class);
                                        mUploads.add(upload);
                                    }
                                }

                            }
                        }
                    }
                }
                //Damos la vuelta a la lista para que los mas recientes aparezcan arriba
                Collections.reverse(mUploads);

                mAdapter = new ImageAdapter(MainWindow.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(MainWindow.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "FAIL READ DATABASE");
            }
        });
    }

    //ACCION DE LAS RECETAS
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onWhatEverClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    //ACCIONES DE LA TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //MENU TOOLBAR
    @Override
    public boolean onOptionsItemSelected(MenuItem optionmenu) {

        switch (optionmenu.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.action_settings:

                return true;
            case R.id.my_account_perfil:

                return true;
            case R.id.my_recipes:
                openImagesActivity(user());
                return true;
        }
        return super.onOptionsItemSelected(optionmenu);
    }

    private void openImagesActivity(String USER) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("USER", USER);
        startActivity(intent);
    }

}
