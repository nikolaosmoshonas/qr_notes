package com.example.nikos.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class ClickNoteActivity extends AppCompatActivity {


    EditText titlefromnote,textfromnote;
    View save,edit;

    ImageView qrView;
    Notes n;

    FirebaseAuth firebaseAuth;

    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicknote);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titlefromnote = findViewById(R.id.TitleFromNote);
        textfromnote = findViewById(R.id.TextFromNote);

        titlefromnote.setFocusable(false);
        titlefromnote.setFocusableInTouchMode(false);
        titlefromnote.setClickable(false);

        textfromnote.setFocusable(false);
        textfromnote.setFocusableInTouchMode(false);
        textfromnote.setClickable(false);

         //save = findViewById(R.id.saveButton);
         //edit = findViewById(R.id.editButton);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uID = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Note").child(uID);

        setText();
        //getIncomingIntent();

        onFabspeedDialPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_items,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_saveNote:
                if (titlefromnote.getText().toString().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Feld darf nicht leer sein!", Toast.LENGTH_LONG).show();


                }else {

                    Intent i = getIntent();

                    String postkey = i.getStringExtra("key");

                    String title = titlefromnote.getText().toString().trim();
                    String text = textfromnote.getText().toString().trim();

                    Notes notes = new Notes(postkey,title,text);

                    mDatabase.child(postkey).setValue(notes);

                    titlefromnote.setActivated(true);
                    titlefromnote.setPressed(true);


                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                    overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);


                }

                break;

                default:



                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                    overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);

        }

            return true;

    }


    private void setText(){

        Intent i = getIntent();

        String title = i.getStringExtra("title");
        String text = i.getStringExtra("text");


        TextView titleFromNote = findViewById(R.id.TitleFromNote);
        titleFromNote.setText(title);

        TextView textFromNote = findViewById(R.id.TextFromNote);
        textFromNote.setText(text);

    }

    public void onFabspeedDialPressed(){

        FabSpeedDial fabSpeedDial = findViewById(R.id.fabSpeedDialEdit);



        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.action_makeQR:

                        String title = titlefromnote.getText().toString();
                        String text = textfromnote.getText().toString();

                        String qrString = title+"&&"+text;

                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                        qrView = findViewById(R.id.qrCodeView);

                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(qrString,BarcodeFormat.QR_CODE,
                                    350,350
                            );

                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            Intent myIntent = new Intent(getApplicationContext(), QRCodeActivity.class);

                            myIntent.putExtra("qr",bitmap);

                            startActivity(myIntent);


                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        break;

                    case R.id.action_edit:

                        titlefromnote.setFocusable(true);
                        titlefromnote.setFocusableInTouchMode(true);
                        titlefromnote.setClickable(true);

                        textfromnote.setFocusable(true);
                        textfromnote.setFocusableInTouchMode(true);
                        textfromnote.setClickable(true);



                        break;
                    case R.id.action_delete:



                        Intent i = getIntent();

                        String postKey = i.getStringExtra("key");

                        mDatabase.child(postKey).removeValue();


                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent);
                        overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);
                        break;


                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });


    }


}
