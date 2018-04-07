package com.cryptosasa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText userid;
    EditText password;
    EditText repassword;
    EditText name;
    EditText email;

    Button signUpButton;
    SQLiteDatabase myDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userid = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.userPass);
        repassword = (EditText) findViewById(R.id.userRePass);
        name = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.userEmail);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        myDataBase = openOrCreateDatabase("creden.db", Context.MODE_PRIVATE, null);


    }

    public void signClick(View view) {

        String uid = userid.getText().toString();
        String upass = password.getText().toString();
        String urepass = repassword.getText().toString();
        String uname = name.getText().toString();
        String uemail = email.getText().toString();
        int flag = 0;

        if(uid.isEmpty() || upass.isEmpty() || urepass.isEmpty() || uname.isEmpty() || uemail.isEmpty()) {
            Toast.makeText(this, "All Fields are mandatory", Toast.LENGTH_SHORT).show();
        }

        else {
            if(!upass.equals(urepass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else {
                if(upass.length() < 6) {
                    Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (uname.matches("[0-9]+")) {
                        Toast.makeText(this, "Name Invalid", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!uemail.matches("^[a-zA-z0-9][a-zA-z._0-9]*@[a-zA-z0-9]+\\.[a-zA-Z]+")) {
                            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            try {
                                myDataBase.execSQL("INSERT INTO userData (username, password,name,email,progress) VALUES ('" + uid + "','" + upass + "','"+uname+"','"+uemail+"','-1');");
                                //myDataBase.execSQL("INSERT INTO userDetails (username, name, email) VALUES ('" + uid + "','" + uname + "','" + uemail + "');");
                                flag = 1;
                            }
                            catch (Exception e){
                                Toast.makeText(this, "User Already Registered.", Toast.LENGTH_SHORT).show();
                                myDataBase.execSQL("DELETE FROM userData WHERE username = '" + uid + "';");
                            }
                            if(flag == 1) {
                                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                this.finish();
                            }
                        }
                    }
                }
            }
        }


    }
}

