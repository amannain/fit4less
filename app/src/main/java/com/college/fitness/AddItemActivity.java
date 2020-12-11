package com.college.fitness;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.college.fitness.databinding.ActivityAddItemBinding;
import com.college.fitness.databinding.ActivityRegistrationBinding;
import com.college.fitness.model.ListItem;

import java.util.Random;

import io.realm.Realm;

public class AddItemActivity extends AppCompatActivity {

    private ActivityAddItemBinding binding;

    /*private String[] images = new String[]{"image_1.png", "image_2.png", "image_3.png",
            "image_4.jpg", "image_5.jpg", "image_6.jpg", "image_7.jpg", "image_8.jpg",
            "image_9.png", "image_10.png", "image_11.png"};*/

    private String[] images = new String[]{"image_1", "image_2", "image_3",
            "image_4", "image_5", "image_6", "image_7", "image_8",
            "image_9", "image_10", "image_11"};

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_item);

//        getSupportActionBar().setTitle("User Registration");

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
    }

    public void onAddItem(View view) {
        String name = binding.etName.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError("Enter name");
            binding.etName.requestFocus();
        } else {
            Random rn = new Random();
            int answer = rn.nextInt(10);

            ListItem item = new ListItem();
            item.setName(name);
            item.setFavorite(false);
            item.setImage(images[answer]);

            runOnUiThread(() -> realm.executeTransactionAsync(realm -> {
                Number maxId = realm.where(ListItem.class).max("id");
                int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                item.setId(nextId);
                realm.insert(item);
            }, () -> {
                runOnUiThread(() -> {
                    Toast.makeText(AddItemActivity.this, "Item Added!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }, error -> {
                runOnUiThread(() -> Toast.makeText(AddItemActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());
            }));
        }
    }
}