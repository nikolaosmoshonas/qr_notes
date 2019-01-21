package com.example.nikos.myfirstapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, passRegister;

    private Button btnreg;
    private TextView openLogin;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        emailRegister = findViewById(R.id.email_register);
        passRegister = findViewById(R.id.password_register);

        btnreg = findViewById(R.id.register_btn);
        openLogin  = findViewById(R.id.toLoginTxt);

        openLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }

    public void btnRegister(View view){

        String mEmail = emailRegister.getText().toString().trim();
        String mPass = passRegister.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)){

            emailRegister.setError("Required Field..");
            return;
        }

        if (TextUtils.isEmpty(mPass)){

            passRegister.setError("Required Field..");
            return;
        }

        mDialog.setMessage("Processing..");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();

                    mDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }else {

                    Toast.makeText(getApplicationContext(),"Problem",Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                }
            }
        });
    }



}
