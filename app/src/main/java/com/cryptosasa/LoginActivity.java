package com.cryptosasa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;

    Button loginButton;
    Button signupButton;
    SharedPreferences sharedpreferences;
    String userId;
    public static final String MyPREFERENCES = "coin";
    SQLiteDatabase myDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPass);
        this.sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signUpButton);

        myDataBase = openOrCreateDatabase("creden.db", Context.MODE_PRIVATE, null);
        myDataBase.execSQL("CREATE TABLE IF NOT EXISTS userData (username TEXT PRIMARY KEY, password TEXT,name TEXT,email TEXT,progress TEXT);");

        //myDataBase.execSQL("INSERT INTO userData VALUES ('sprasad','test1234');");

    }

    public void loginClick(View view) {

        String user = username.getText().toString();
        String pass = password.getText().toString();

        if(user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();
        }

        Cursor cur = myDataBase.rawQuery("SELECT * FROM userData where username = '"+ user + "';", null);

        if(cur != null) {
            if(cur.getCount() > 0) {

                if (cur.moveToFirst()) {
                    String upass = cur.getString(cur.getColumnIndex("password"));
                    if (upass.equals(pass)) {
                        //HomeActivity.username = user;
                        Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("username", user);
                        editor.commit();
                        Intent in = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(in);
                    } else {
                        Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(this, "User Not Found. Please Sign Up First.", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    public void signupClick(View view) {
        Intent in = new Intent(this, SignUpActivity.class);
        startActivity(in);
    }


}
