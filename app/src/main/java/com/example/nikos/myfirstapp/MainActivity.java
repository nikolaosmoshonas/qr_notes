package com.example.nikos.myfirstapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;


public class MainActivity extends AppCompatActivity {


    int layout = R.layout.layout_notes;

    private RecyclerView recyclerView;

    Intent intent;

    ImageView imageView;


    IntentIntegrator intentIntegrator = new IntentIntegrator(this);


    ArrayList<String> qrList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    String title, text, post_key;
    private View myview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uID = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Note").child(uID);
        mDatabase.keepSynced(true);

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        myview = inflater.inflate(R.layout.activity_clicknote, null);

        //weatherLoc = findViewById(R.id.weatherLocationText);
        //weatherTxt = findViewById(R.id.weatherText);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FabSpeedDial fabSpeedDial = findViewById(R.id.fabSpeedDial);

        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.action_add:
                        startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class));
                        break;

                    case R.id.action_scan:

                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        intentIntegrator.setPrompt("Scan");
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setBeepEnabled(false);
                        intentIntegrator.setBarcodeImageEnabled(true);
                        intentIntegrator.initiateScan();

                        break;
                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });


    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout:

                firebaseAuth.signOut();
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(R.transition.right_slide_out_right, R.transition.right_slide_out_left);
                startActivity(myIntent);

        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Notes, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Notes, MyViewHolder>(

                Notes.class,
                R.layout.layout_notes,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Notes model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setText(model.getText());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key = getRef(position).getKey();
                        title = model.getTitle();
                        text = model.getText();

                        Intent i = new Intent(MainActivity.this, ClickNoteActivity.class);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(recyclerView, "recyclerView");

                        android.support.v4.util.Pair pair = new android.support.v4.util.Pair<View, String>(recyclerView, "recyclerView");

                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pair);

                        i.putExtra("key", post_key);
                        i.putExtra("title", title);
                        i.putExtra("text", text);

                        startActivity(i, activityOptions.toBundle());


                    }
                });


            }

        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;


        }

        public void setTitle(String title) {

            TextView mTitle = view.findViewById(R.id.textViewTitle);
            mTitle.setText(title);
        }

        public void setText(String text) {

            TextView mText = view.findViewById(R.id.textViewShortDesc);
            mText.setText(text);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();

            } else {

                if (!result.getContents().contains("&&")) {

                    String id = mDatabase.push().getKey();

                    mDatabase.child(id).setValue(new Notes(id, result.getContents(), null));

                    finish();
                    startActivity(getIntent());

                    Toast.makeText(this, "Note inserted", Toast.LENGTH_LONG).show();
                } else {

                    qrList.add(result.getContents());
                    String[] qrTitleText = result.getContents().split("&&");
                    Log.e("qrTitle", qrTitleText[0].trim());
                    String id = mDatabase.push().getKey();
                    mDatabase.child(id).setValue(new Notes(id, qrTitleText[0].trim(), qrTitleText[1].trim()));

                    finish();
                    startActivity(getIntent());

                    Toast.makeText(this, "Note inserted", Toast.LENGTH_LONG).show();

                    System.out.println(qrList.size());

                }

            }


        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }

    }


}







