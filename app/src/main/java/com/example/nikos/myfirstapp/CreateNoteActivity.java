package com.example.nikos.myfirstapp;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CreateNoteActivity extends AppCompatActivity {



    EditText mTitle,mText;
    TextView weatherLoc,weatherTxt;
    ImageView imageView;
    Button btnAdd;



    //Firebase Database

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Note").child(uID);



       // weatherLoc = findViewById(R.id.weatherLocationText);
       // weatherTxt = findViewById(R.id.weatherText);
        mTitle = findViewById(R.id.titleText);
        mText = findViewById(R.id.noteText);
        btnAdd = findViewById(R.id.addNote);






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);
        return true;

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNote(View view) {
        //EditText plainText = findViewById(R.id.titleText);

        //myIntent.putExtra("title", plainText.getText());

                if (mTitle.getText().toString().isEmpty() || mText.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Feld darf nicht leer sein!", Toast.LENGTH_LONG).show();
                }else {

                    String id = databaseReference.push().getKey();

                    Notes notes = new Notes(id,mTitle.getText().toString(),mText.getText().toString());

                    databaseReference.child(id).setValue(notes);

                    Toast.makeText(getApplicationContext(),"Note created",Toast.LENGTH_LONG).show();

                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);

                    overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);

                }


    }






}