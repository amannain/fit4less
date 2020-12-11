package com.college.fitness;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.college.fitness.model.User;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.college.fitness.ui.home.SectionsPagerAdapter;

import io.realm.Realm;

public class HomeActivity extends AppCompatActivity {

    static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public Realm realm;
    public long userId;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getIntent().getLongExtra("USER_ID", 0L);
        
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        getUser();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void getUser() {
        runOnUiThread(() -> realm.executeTransaction(realm -> {
            user = realm.where(User.class)
                    .equalTo("id", userId)
                    .findFirst();
            if (null != user) {
                Log.e("TAG", "onLogin: " + user.getName());
                runOnUiThread(() -> {
                });
            } else {
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "No such user exists!", Toast.LENGTH_LONG).show());
            }
        }));
    }
}