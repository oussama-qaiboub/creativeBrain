package com.creative.brain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPass extends AppCompatActivity {

    private EditText EditEmail;
    private Button frPassBtn;
    private ProgressDialog progressDialog;
    private String Email;
    private FirebaseAuth FAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        EditEmail = (EditText)findViewById(R.id.EditEmail);
        frPassBtn = (Button)findViewById(R.id.frPassBtn);

        progressDialog = new ProgressDialog(this);

        FAuth = FirebaseAuth.getInstance();

        frPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email = EditEmail.getText().toString().trim();
                if(!validate()){
                    frPassBtn.setEnabled(true);
                }else{
                    progressDialog.setMessage("please waiting ....");
                    progressDialog.show();
                    FAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                showMessage("Password reset email sent. check your email !");
                                finish();
                                startActivity(new Intent(ForgetPass.this, LoginActivity.class));
                            }else{
                                progressDialog.dismiss();
                                showMessage("Error in sending password reset email\ncheck if your email is correct.");
                            }
                        }
                    });
                }
            }
        });

    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        Email = EditEmail.getText().toString();

        if(!validateMail()){
            valid = false;
        }


        return valid;
    }


    public  boolean validateMail(){
        boolean valid = true;
        if (Email.isEmpty()) {
            EditEmail.setError("Please add your Email Address");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            EditEmail.setError("Please enter the correct Email");
            valid = false;
        } else {
            EditEmail.setError(null);
        }
        return valid;
    }
}
