package com.college.fitness.fragments;

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

import com.college.fitness.databinding.FragmentMyFavoriteBinding;
import com.college.fitness.model.ListItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MyFavoriteFragment extends Fragment {

    private FragmentMyFavoriteBinding binding;

    private List<ListItem> arrItems = new ArrayList<>();
    private FavItemListAdapter mAdapter;


    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    public static MyFavoriteFragment newInstance() {
        MyFavoriteFragment fragment = new MyFavoriteFragment();
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
        binding = FragmentMyFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        search(binding.svMyFav);

        recyclerAdapterSetup();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.svMyFav.setQuery("", false);
        binding.homeFrame.requestFocus();

        refreshList();
    }

    private void recyclerAdapterSetup() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.rvMyFav.setLayoutManager(mLayoutManager);
        binding.rvMyFav.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FavItemListAdapter(getContext(), arrItems);
        binding.rvMyFav.setAdapter(mAdapter);

        refreshList();
    }

    private void refreshList() {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            arrItems.clear();
            List<ListItem> arrData = realm.where(ListItem.class)
                    .equalTo("favorite", true).findAll();
            arrItems.addAll(arrData);
            Log.d("itemList", "" + arrItems.size());
            getActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());
        });
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
}