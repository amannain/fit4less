package com.college.fitness.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.college.fitness.AddItemActivity;
import com.college.fitness.databinding.FragmentAllListBinding;
import com.college.fitness.model.ListItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AllListFragment extends Fragment {

    private FragmentAllListBinding binding;

    private List<ListItem> arrItems = new ArrayList<>();
    private ItemListAdapter mAdapter;

    public AllListFragment() {
        // Required empty public constructor
    }

    public static AllListFragment newInstance() {
        AllListFragment fragment = new AllListFragment();
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
        binding = FragmentAllListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddItemActivity.class);
                startActivityForResult(intent, 102);
            }
        });

        search(binding.svAllList);

        recyclerAdapterSetup();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.svAllList.setQuery("", false);
        binding.homeFrame.requestFocus();

        refreshList();
    }

    private void recyclerAdapterSetup() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.rvAllList.setLayoutManager(mLayoutManager);
        binding.rvAllList.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ItemListAdapter(getContext(), arrItems);
        binding.rvAllList.setAdapter(mAdapter);

        refreshList();
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void refreshList() {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            arrItems.clear();
            List<ListItem> arrData = realm.where(ListItem.class).findAll();
            arrItems.addAll(arrData);
            Log.d("itemList", "" + arrItems.size());
            getActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                refreshList();
            }
        }
    }
}