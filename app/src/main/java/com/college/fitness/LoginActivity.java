package com.college.fitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.college.fitness.databinding.ActivityLoginBinding;
import com.college.fitness.model.User;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    public void onLogin(View view) {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        if (username.isEmpty() || !username.matches(emailPattern)) {
            binding.etUsername.setError("Enter valid Username/EmailId");
            binding.etUsername.requestFocus();
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Enter Password");
            binding.etPassword.requestFocus();
        } else {
            runMethod(username, password);
        }
    }

    private void runMethod(String username, String password) {
        runOnUiThread(() -> realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class)
                    .beginGroup()
                    .equalTo("emailId", username)
                    .and()
                    .equalTo("password", password)
                    .endGroup()
                    .findFirst();

            if (null != user) {
                Log.e("TAG", "onLogin: " + user.getName());
                long id = user.getId();
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("USER_ID", id);
                    startActivity(intent);
                    finishAffinity();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "No such user exists!", Toast.LENGTH_LONG).show());
            }
        }, () -> {
            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "User Login!", Toast.LENGTH_LONG).show());
        }, error -> {
            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());
        }));
    }

    public void onSignUp(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        realm.close();
    }
}