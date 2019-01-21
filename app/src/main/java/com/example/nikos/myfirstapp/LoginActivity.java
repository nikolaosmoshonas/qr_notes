package com.example.nikos.myfirstapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView toRegisterTxt,passwordResetTxt;

    private EditText emailLogin;

    private EditText passLogin;

    private Button btnLogin;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    Context c = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        emailLogin = findViewById(R.id.email_login);
        passLogin = findViewById(R.id.password_login);

        btnLogin  = findViewById(R.id.login_btn);

        passwordResetTxt = findViewById(R.id.passwordResetEmail);

        toRegisterTxt = findViewById(R.id.toRegisterTxt);

        toRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });


        resetPassword();

        checkUserLoggedIn();

    }

    public void resetPassword(){

        passwordResetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = LayoutInflater.from(c);

                View myview = layoutInflater.inflate(R.layout.password_reset_dialog, null);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);


                alertDialog.setView(myview);
                alertDialog.setCancelable(true);

                final EditText resetEmialInput = myview.findViewById(R.id.userInputEmail);

                alertDialog
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mAuth.sendPasswordResetEmail(resetEmialInput.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(),"Password sent to your Email",Toast.LENGTH_LONG).show();

                                                }else {
                                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();
                                    }
                                });

                AlertDialog setalertDialog = alertDialog.create();
                setalertDialog.show();

            }
        });



    }



    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

        public void loginBtnClick(View view){


        String mEmail = emailLogin.getText().toString().trim();
        String mPass = passLogin.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)){

            emailLogin.setError("Required Field..");
            return;
        }

        if (TextUtils.isEmpty(mPass)){

            passLogin.setError("Required Field..");
            return;
        }

        mDialog.setMessage("Processing..");
        mDialog.show();

        mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    mDialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(),"Problem",Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                }



            }
        });



        }

        public void checkUserLoggedIn(){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user !=null){

                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                this.finish();
            }
            }
        }

