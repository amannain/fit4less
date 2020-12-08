package com.college.fitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.college.fitness.databinding.ActivityRegistrationBinding;
import com.college.fitness.model.ListItem;
import com.college.fitness.model.User;

import io.realm.Realm;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

//        getSupportActionBar().setTitle("User Registration");

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    public void onRegister(View view) {
        String fullname = binding.etFullname.getText().toString().trim();
        String emailId = binding.etEmailId.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String age = binding.etAge.getText().toString().trim();

        if (fullname.isEmpty()) {
            binding.etFullname.setError("Enter fullname");
            binding.etFullname.requestFocus();
        } else if (emailId.isEmpty() || !emailId.matches(emailPattern)) {
            binding.etEmailId.setError("Enter valid emailId");
            binding.etEmailId.requestFocus();
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
            User user = new User();
            user.setName(fullname);
            user.setEmailId(emailId);
            user.setPassword(password);
            user.setPhoneNumber(phoneNumber);
            user.setAge(Integer.parseInt(age));

            runOnUiThread(() -> realm.executeTransactionAsync(realm -> {
                Number maxId = realm.where(ListItem.class).max("id");
                int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                user.setId(nextId);
                realm.insert(user);
            }, () -> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrationActivity.this, "User registered!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }, error -> {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }));
        }
    }
}