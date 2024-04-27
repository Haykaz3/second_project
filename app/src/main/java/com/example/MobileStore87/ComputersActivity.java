package com.example.MobileStore87;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ComputersActivity extends AppCompatActivity {

    List<Product> dataList;
    List<Product> computers;

    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computers);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewComputers);
        SearchView searchView = findViewById(R.id.searchComputers);

        searchView.clearFocus();

        dataList = new ArrayList<>();
        computers = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        adapter = new MyAdapter(ComputersActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(ComputersActivity.this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        getComputers1();
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
                searchList3(newText);
                return true;
            }
        });
    }
    private void getComputers1() {
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
                        computers.add(product);
                    }
                    dataList.addAll(computers);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ComputersActivity.this, "All products are loaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ComputersActivity.this, "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }
    private void searchList3(String text) {
        List<Product> dataSearchList = new ArrayList<>();
        for (Product data : dataList) {
            if (data.name.toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (!dataSearchList.isEmpty()) {
            adapter.setSearchList(dataSearchList);
        } else {
            Toast.makeText(ComputersActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }
}