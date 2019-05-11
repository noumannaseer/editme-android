package com.example.editme;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Pastorderlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastorderlist);

        BottomNavigationView navigation = findViewById(R.id.bottomNav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.postorderlist, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
          //  Intent setting = new Intent(Pastorderlist.this, Setting_Screen.class);
            //startActivity(setting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadFragment(Fragment fragment)

    {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        transaction.commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected( MenuItem item){
            switch (item.getItemId())
            {
                case R.id.action_news:
                    loadFragment( new HomeFragment());
                    return true;
                case R.id.action_person:
                    loadFragment(new OrderFragment());
                    return true;
                case R.id.action_company:
                    loadFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

}
