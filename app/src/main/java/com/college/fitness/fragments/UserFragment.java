package com.college.fitness.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.fitness.HomeActivity;
import com.college.fitness.UpdateUserActivity;
import com.college.fitness.databinding.FragmentUserBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDisplay();

        binding.editFab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), UpdateUserActivity.class);
            intent.putExtra("USER_ID", ((HomeActivity) getActivity()).userId);
            startActivityForResult(intent, 101);
        });
    }

    private void updateDisplay() {
        binding.etName.setText(((HomeActivity) getActivity()).user.getName());
        binding.etEmailId.setText(((HomeActivity) getActivity()).user.getEmailId());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                ((HomeActivity) getActivity()).getUser();
                updateDisplay();
            }
        }
    }
}
