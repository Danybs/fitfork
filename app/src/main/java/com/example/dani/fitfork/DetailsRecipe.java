package com.example.dani.fitfork;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dani.fitfork.Objetos.FirebaseReferences;
import com.example.dani.fitfork.Objetos.ImageAdapter;
import com.example.dani.fitfork.Objetos.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DetailsRecipe extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefDetail;

    private TextView tv_hashtag,tv_ingredients,tv_instructions,tv_name,tv_user;
    private ImageView iv_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_recipe);

        //TOOLBAR ACTIVIDAD
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

         tv_hashtag = (TextView) findViewById(R.id.tv_hashtag);
         tv_ingredients = (TextView) findViewById(R.id.tv_ingredients);
         tv_instructions = (TextView) findViewById(R.id.tv_instructions);
         tv_user = (TextView) findViewById(R.id.tv_user);
         tv_name = (TextView) findViewById(R.id.tv_name);
         iv_URL = (ImageView) findViewById(R.id.iv_URL);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id_URL = null;
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    id_URL = extras.getString("id");
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot1 : postSnapshot.getChildren()) {

                        //URL
                        String keyURL = postSnapshot1.getKey().toString();
                        if (keyURL.equalsIgnoreCase("imageURL")) {
                            String valueURL = postSnapshot1.getValue().toString().trim();
                            if (valueURL.equalsIgnoreCase(id_URL)) {

                                //Database
                                mDatabaseRefDetail = postSnapshot.getRef();
                                mDatabaseRefDetail.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            String keyName = postSnapshot.getKey().toString();
                                            if (keyName.equalsIgnoreCase("hashTag")) {
                                                tv_hashtag.setText(postSnapshot.getValue().toString());
                                            }
                                            if (keyName.equalsIgnoreCase("imageUrl")) {
                                            Picasso.get().load(postSnapshot.getValue().toString())
                                                    .placeholder(R.mipmap.ic_launcher)
                                                    .fit()
                                                    .centerCrop()
                                                    .into(iv_URL);

                                            }
                                            if (keyName.equalsIgnoreCase("ingredients")) {
                                                tv_ingredients.setText(postSnapshot.getValue().toString());
                                            }
                                            if (keyName.equalsIgnoreCase("instructions")) {
                                                tv_instructions.setText(postSnapshot.getValue().toString());
                                            }
                                            if (keyName.equalsIgnoreCase("name")) {
                                                tv_name.setText(postSnapshot.getValue().toString());
                                            }
                                            if (keyName.equalsIgnoreCase("user")) {
                                                tv_user.setText(postSnapshot.getValue().toString());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("ERROR", "FAIL READ DATABASE");
                                    }
                                });
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "FAIL READ DATABASE");
            }
        });

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
    protected String user() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    private void openImagesActivity(String USER) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("USER", USER);
        startActivity(intent);
    }
}
