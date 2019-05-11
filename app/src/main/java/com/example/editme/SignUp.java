package com.example.editme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//************************************************
public class SignUp extends AppCompatActivity
//************************************************
{
    private Button button;
    private Toolbar mTopToolbar;
    private TextView gotologin;
    private EditText Name;
    private EditText Email;
    private EditText password;

    //************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState)
    //************************************************
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button = findViewById(R.id.btn_signup);
        gotologin = findViewById(R.id.login);
        mTopToolbar = findViewById(R.id.toolbar);
        Name = findViewById(R.id.Name);
        Email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
            }
        });
        gotologin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToLoginScreen();
            }
        });
    }

    //************************************************
    private void goToLoginScreen()
    //************************************************
    {
        Intent loginScreen = new Intent(SignUp.this, LoginActivity.class);
        startActivity(loginScreen);
    }


    //************************************************
    public boolean onCreateOptionsMenu(Menu menu)
    //************************************************
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //************************************************
    public boolean onOptionsItemSelected(MenuItem item)
    //************************************************
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
