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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailET , passET;
    private TextView register ;
    private Button login ;
    private ProgressDialog mDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        mDialog = new ProgressDialog(this);

        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passwordET);
        register = findViewById(R.id.RegisterTV);
        login = findViewById(R.id.loginBTN);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String pass = passET.getText().toString();
                if(TextUtils.isEmpty(email)){
                    emailET.setError("Requiered Field!");
                }
                if (TextUtils.isEmpty(pass)){
                    passET.setError("Required Field!");
                }
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    mDialog.setMessage("Processing");
                    mDialog.show();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                Toast.makeText(getApplicationContext() , "Successful" , Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                            }
                            else {
                                Toast.makeText(getApplicationContext() , "Failed to Login" + task.getException() , Toast.LENGTH_LONG).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}
