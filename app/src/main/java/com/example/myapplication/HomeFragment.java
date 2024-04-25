package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.Toast;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment  {

    List<Product> dataList;
    List<Product> phones;
    List<Product> tablets;
    List<Product> computers;


    MyAdapter adapter;

    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = (RecyclerView) binding.recyclerView;
        SearchView searchView = (SearchView) binding.search;
        ImageButton phoneBtn = (ImageButton) binding.phoneBtnImage;
        ImageButton tabletBtn = (ImageButton) binding.tabletBtnImage;
        ImageButton computerBtn = (ImageButton) binding.laptopBtnImage;
        ImageButton allBtn = (ImageButton) binding.allBtnImage;

        searchView.clearFocus();

        dataList = new ArrayList<>();
        phones = new ArrayList<>();
        tablets = new ArrayList<>();
        computers = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        adapter = new MyAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        getProducts();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setVisibility(View.VISIBLE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhonesActivity.class);
                startActivity(intent);
            }
        });
        tabletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabletsActivity.class);
                startActivity(intent);
            }
        });
        computerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComputersActivity.class);
                startActivity(intent);
            }
        });
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();

    }

    private void getProducts(){
        getComputer();
        getTablets();
        getPhones();
        Toast.makeText(getActivity(), "All products are loaded", Toast.LENGTH_SHORT).show();
    }

    private void getPhones() {
        FirebaseFirestore.getInstance().collection("phones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    phones.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Product product = new Product();
                        product.name = documentSnapshot.getString("name");
                        product.price = documentSnapshot.getString("price");
                        product.image = documentSnapshot.getString("image");
                        product.categoryId = "phones";
                        product.productId = documentSnapshot.getId();
                        product.productUrl = documentSnapshot.getString("productUrl");
                        phones.add(product);
                    }
                    dataList.addAll(phones);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }

    private void getTablets() {
        FirebaseFirestore.getInstance().collection("tablets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tablets.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Product product = new Product();
                        product.name = documentSnapshot.getString("name");
                        product.price = documentSnapshot.getString("price");
                        product.image = documentSnapshot.getString("image");
                        product.categoryId = "tablets";
                        product.productId = documentSnapshot.getId();
                        product.productUrl = documentSnapshot.getString("productUrl");
                        phones.add(product);
                    }
                    dataList.addAll(tablets);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }

    private void getComputer(){
        FirebaseFirestore.getInstance().collection("computers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    computers.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Product product = new Product();
                        product.name = documentSnapshot.getString("name");
                        product.price = documentSnapshot.getString("price");
                        product.image = documentSnapshot.getString("image");
                        product.categoryId = "computers";
                        product.productId = documentSnapshot.getId();
                        product.productUrl = documentSnapshot.getString("productUrl");
                        phones.add(product);
                    }
                    dataList.addAll(computers);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Cant load data", Toast.LENGTH_SHORT).show();
                });
    }



    private void searchList(String text) {
        List<Product> dataSearchList = new ArrayList<>();
        for (Product data : dataList) {
            if (data.name.toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (!dataSearchList.isEmpty()) {
            adapter.setSearchList(dataSearchList);
        } else {
            Toast.makeText(getActivity(), "Not Found", Toast.LENGTH_SHORT).show();
        }
    }
}