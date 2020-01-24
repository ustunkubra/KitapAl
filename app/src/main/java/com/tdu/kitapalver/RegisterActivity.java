package com.tdu.kitapalver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName, InputEmail, InputPassword,InputConfirmPassword;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_input);
        InputEmail = findViewById(R.id.register_email_input);
        InputPassword =  findViewById(R.id.register_password_input);
        InputConfirmPassword =  findViewById(R.id.register_password_confirm_input);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });



    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();
        String passwordconfirm = InputConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write your name... ",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your e-mail... ",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password... ",Toast.LENGTH_SHORT).show();
        }else if(!(password.equals(passwordconfirm))){
            Toast.makeText(this,"Please confirm your password... ",Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait while we are checking your credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            ValidateeMail(name,email,password);
        }
    }

    private void ValidateeMail(final String name, final String email, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(!(dataSnapshot.child("Users").child(email).exists())){
                     HashMap<String, Object> userdataMap = new HashMap<>();
                     userdataMap.put("email", email);
                     userdataMap.put("name", name);
                     userdataMap.put("password", password);

                     RootRef.child("Users").child(email).updateChildren(userdataMap)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         Toast.makeText(RegisterActivity.this," Congratulations, your account has been created.",Toast.LENGTH_SHORT).show();
                                         loadingbar.dismiss();

                                         Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                         startActivity(intent);
                                     }else {
                                         loadingbar.dismiss();
                                         Toast.makeText(RegisterActivity.this," Network Error: Please try again after some time...",Toast.LENGTH_SHORT).show();

                                     }
                                 }
                             });
                 }else{
                     Toast.makeText(RegisterActivity.this," This " + email + "  already exists.",Toast.LENGTH_SHORT).show();
                     loadingbar.dismiss();
                     Toast.makeText(RegisterActivity.this," Please try again using another E-mail",Toast.LENGTH_SHORT).show();
                     Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                     startActivity(intent);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
