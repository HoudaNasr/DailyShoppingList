package com.example.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.Bidi;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email , pass , confirmPass ;
    private TextView signin ;
    private Button signup ;
    private ProgressDialog mDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);


        email = findViewById(R.id.emailET);
        pass = findViewById(R.id.passwordET);
        confirmPass = findViewById(R.id.confirmPasswordET);
        signin = findViewById(R.id.loginTV);
        signup = findViewById(R.id.registerBTN);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String confirmpassword = confirmPass.getText().toString();
                if(TextUtils.isEmpty(mail)){
                    email.setError("Required Field..!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    email.setError("Required Field..!");
                    return;
                }
                if(TextUtils.isEmpty(confirmpassword)){
                    email.setError("Required Field..!");
                    return;
                }
                if (password.equals(confirmpassword)){
                    mDialog.setMessage("Processing");
                    mDialog.show();
                    mAuth.createUserWithEmailAndPassword(mail , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext() , HomeActivity.class));
                                Toast.makeText(getApplicationContext() , "Successful" , Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext() , task.getException().toString() , Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext() , "passwords not equal!" , Toast.LENGTH_LONG).show();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , MainActivity.class));
            }
        });

    }
}
