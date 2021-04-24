package com.creative.brain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

   String email, password, password2, name;

    private EditText userEmail,userPassword,userPAssword2,fullName;
    private Button regBtn;
    private TextView txt_login;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPAssword2 = findViewById(R.id.regPassword2);
        regBtn = findViewById(R.id.regBtn);
        txt_login = findViewById(R.id.signIn);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();



                email = userEmail.getText().toString();
                password = userPassword.getText().toString();
                password2 = userPAssword2.getText().toString();
                name = fullName.getText().toString();


                if (!validate()) {
                    // something goes wrong : all fields must be filled
                    // we need to display an error message
                    pd.dismiss();

                }
                else
                {
                    // everything is ok and all fields are filled now we can start creating user account
                    // CreateUserAccount method will try to create the user if the email is valid
                    register(name, email, password);
                }

            }
        });

    }

    public void register(final String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("fullname", fullname);
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                            map.put("bio", "");

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        /*
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        */
                                        sendEmailVerification();

                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            showMessage("You can't register with this email or password" );
                        }
                    }
                });
    }


    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    private void sendEmailVerification(){
        //  updateUserInfo( name ,pickedImgUri,FAuth.getCurrentUser());
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        showMessage("Successfully Registered, Verification mail sent!.");
                        auth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }else{
                        showMessage("Verification mail has'nt been sent! \ncheck if your email is correct.");
                    }
                }
            });
        }
    }

    public boolean validate() {
        boolean valid = true;

        email = userEmail.getText().toString();
        password = userPassword.getText().toString();
        password2 = userPAssword2.getText().toString();
        name = fullName.getText().toString();

        if(!validateMail()){
            valid = false;
        }

        if(!validatePass1()){
            valid = false;
        }

        if(!validatePass2()){
            valid = false;
        }

        if(!validateName()){
            valid = false;
        }

        return valid;
    }

    public  boolean validateName(){
        boolean valid = true;
        if (name.isEmpty()) {
            fullName.setError("Please enter your name");
            valid = false;
        } else if (name.length() < 3 || name.length() > 30) {
            fullName.setError("Please enter your correct name");
            valid = false;
        } else {
            fullName.setError(null);
        }
        return valid;
    }

    public  boolean validateMail(){
        boolean valid = true;
        if (email.isEmpty()) {
            userEmail.setError("Please add your Email Address");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter the correct Email");
            valid = false;
        } else {
            userEmail.setError(null);
        }
        return valid;
    }

    public  boolean validatePass1(){
        boolean valid = true;
        if (password.isEmpty()) {
            userPassword.setError("Please enter your password");
            valid = false;
        } else if (password.length()<8) {
            userPassword.setError("Please enter a password of at least 8 characters");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        return valid;
    }

    public  boolean validatePass2(){
        boolean valid = true;
        if (password2.isEmpty()) {
            userPAssword2.setError("Please retype your password");
            valid = false;
        } else if (!password.equals(password2)) {
            userPAssword2.setError("Password should be same");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        return valid;
    }

}
