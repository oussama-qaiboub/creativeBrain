package com.creative.brain;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {





    private static final int REQUEST_SIGNUP = 0;

    private EditText userMail,userPassword;
    private Button btnLogin;
    private TextView txt_signup, forgotPass;
    private int counter = 5;
    private String mail , pass;

    private ProgressDialog pd;

    private Intent MainActivity;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        txt_signup = findViewById(R.id.signUp);
        forgotPass = findViewById(R.id.forgotPass);

        MainActivity = new Intent(this,com.creative.brain.MainActivity.class);

        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent forgetPass = new Intent(getApplicationContext(),ForgetPass.class);
                startActivity(forgetPass);
                finish();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Please wait...");
                    pd.show();

                    if (!validate()) {
                        onLoginFailed();
                    }
                    else
                    {

                        onLoginSuccess();
                    }

                }
        });
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = auth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();


        if(emailflag){
            finish();
            updateUI();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Email verification")
                    .setMessage("Please verify your email.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            auth.signOut();
        }
    }

    private void updateUI() {

        MainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(MainActivity);
        finish();
    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    private void signIn(String mail, String password) {

       /*auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(auth.getCurrentUser().getUid());

                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    pd.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    pd.dismiss();
                                }
                            });
                        } else {
                            pd.dismiss();
                            showMessage("Authentication failed.");
                        }
                    }
                });

*/

        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    pd.dismiss();
                    checkEmailVerification();
                    //updateUI();

                }
                else {
                    showMessage(task.getException().getMessage());
                    pd.dismiss();

                    counter--;
                   // countPass.setText("No of attempts remaining: " + String.valueOf(counter));
                    if(counter == 0){
                        btnLogin.setEnabled(false);
                    }
                }


            }
        });



    }

    public void onLoginSuccess() {
        //   btnLogin.setEnabled(true);
        signIn(mail,pass);

    }

    public void onLoginFailed() {
        pd.dismiss();
        //   btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        mail = userMail.getText().toString();
        pass = userPassword.getText().toString();

        if(!validateMail()){
            valid = false;
        }

        if(!validatePass()){
            valid = false;
        }

        return valid;
    }

    public  boolean validateMail(){
        boolean valid = true;
        if (mail.isEmpty()) {
            userMail.setError("Please add your Email Address");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            userMail.setError("Please enter the correct Email");
            valid = false;
        } else {
            userMail.setError(null);
        }
        return valid;
    }

    public  boolean validatePass(){
        boolean valid = true;
        if (pass.isEmpty()) {
            userPassword.setError("Please enter your password");
            valid = false;
        } else if (pass.length()<8) {
            userPassword.setError("Please enter the correct password");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        return valid;
    }
}
