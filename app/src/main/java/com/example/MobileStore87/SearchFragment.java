package com.example.MobileStore87;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.MobileStore87.DetailActivity;
import com.example.MobileStore87.MyAdapter;
import com.example.MobileStore87.databinding.FragmentSearchBinding;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentSearchBinding binding;
    private List<Product> products;
    private FirebaseFirestore firebaseFirestore;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        firebaseFirestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();
        getPhones();
        getTablets();
        getComputers();
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText.toString());
                return false;
            }
        });
        return binding.getRoot();
    }

    private void getPhones() {
        loading(true);
        firebaseFirestore.collection("phones").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = new Product();
                            product.name = documentSnapshot.getString("name");
                            product.price = documentSnapshot.getString("price");
                            product.image = documentSnapshot.getString("image");
                            product.categoryId = "phones";
                            product.productId = documentSnapshot.getId();
                            product.productUrl = documentSnapshot.getString("productUrl");
                            if (documentSnapshot.get("viewCount", Integer.class) != null) {
                                product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                            } else {
                                product.viewCount = 0;
                            }
                            products.add(product);
                        }
                    }
                });
        loading(false);
    }

    private void getTablets() {
        loading(true);
        firebaseFirestore.collection("tablets").get()

                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots){
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = new Product();
                            product.name = documentSnapshot.getString("name");
                            product.price = documentSnapshot.getString("price");
                            product.image = documentSnapshot.getString("image");
                            product.categoryId = "tablets";
                            product.productId = documentSnapshot.getId();
                            product.productUrl = documentSnapshot.getString("productUrl");
                            if (documentSnapshot.get("viewCount", Integer.class) != null) {
                                product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                            } else {
                                product.viewCount = 0;
                            }
                            products.add(product);
                        }
                    }
                });
        loading(false);
    }

    private void getComputers() {
        loading(true);
        firebaseFirestore.collection("computers").get()

                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots){
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = new Product();
                            product.name = documentSnapshot.getString("name");
                            product.price = documentSnapshot.getString("price");
                            product.image = documentSnapshot.getString("image");
                            product.categoryId = "computers";
                            product.productId = documentSnapshot.getId();
                            product.productUrl = documentSnapshot.getString("productUrl");
                            if (documentSnapshot.get("viewCount", Integer.class) != null) {
                                product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                            } else {
                                product.viewCount = 0;
                            }
                            products.add(product);
                        }
                    }
                });
        loading(false);
    }

    private void filterProducts(String text) {
        loading(true);
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.name.trim().toLowerCase().contains(text.trim().toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        if (filteredProducts.size() > 0) {
            MyAdapter productAdapter = new MyAdapter(getActivity(), filteredProducts);
            binding.usersRecyclerView.setAdapter(productAdapter);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            binding.usersRecyclerView.setLayoutManager(layoutManager);
            binding.usersRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        }
        loading(false);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.INVISIBLE);
        }
    }
}