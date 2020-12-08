package com.college.fitness;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.college.fitness.databinding.ActivityRegistrationBinding;
import com.college.fitness.databinding.ActivityUpdateUserBinding;
import com.college.fitness.model.User;

import io.realm.Realm;

public class UpdateUserActivity extends AppCompatActivity {

    private ActivityUpdateUserBinding binding;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Realm realm;
    private long userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_user);

//        getSupportActionBar().setTitle("User Registration");

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();

        userId = getIntent().getLongExtra("USER_ID", 0L);

        getUser();
    }

    public void getUser() {
        runOnUiThread(() -> realm.executeTransaction(realm -> {
            user = realm.where(User.class)
                    .equalTo("id", userId)
                    .findFirst();
            if (null != user) {
                Log.e("TAG", "onLogin: " + user.getName());
                runOnUiThread(() -> {
                    binding.etFullname.setText("" + user.getName());
                    binding.etPhoneNumber.setText("" + user.getPhoneNumber());
                    binding.etAge.setText("" + user.getAge());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(UpdateUserActivity.this, "No such user exists!", Toast.LENGTH_LONG).show());
            }
        }));
    }

    public void onUpdate(View view) {
        String fullname = binding.etFullname.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String age = binding.etAge.getText().toString().trim();

        if (fullname.isEmpty()) {
            binding.etFullname.setError("Enter fullname");
            binding.etFullname.requestFocus();
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Enter password");
            binding.etPassword.requestFocus();
        } else if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Enter confirm password");
            binding.etConfirmPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            binding.etPassword.setError("Password-ConfirmPassword mismatch!");
            binding.etPassword.requestFocus();
        } else if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.setError("Enter phone number");
            binding.etPhoneNumber.requestFocus();
        } else if (age.isEmpty()) {
            binding.etAge.setError("Enter age");
            binding.etAge.requestFocus();
        } else {

            runOnUiThread(() -> realm.executeTransactionAsync(realm -> {
                user = realm.where(User.class)
                        .equalTo("id", userId)
                        .findFirst();

                user.setName(fullname);
                user.setPassword(password);
                user.setPhoneNumber(phoneNumber);
                user.setAge(Integer.parseInt(age));
                realm.insertOrUpdate(user);
            }, () -> {
                runOnUiThread(() -> {
                    Toast.makeText(UpdateUserActivity.this, "User Details Updated!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }, error -> {
                runOnUiThread(() -> Toast.makeText(UpdateUserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());
            }));
        }
    }
}