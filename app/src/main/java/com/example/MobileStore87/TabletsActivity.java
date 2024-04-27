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

public class TabletsActivity extends AppCompatActivity {

    List<Product> dataList;
    List<Product> tablets;

    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablets);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTablets);
        SearchView searchView = findViewById(R.id.searchTablets);

        searchView.clearFocus();

        dataList = new ArrayList<>();
        tablets = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        adapter = new MyAdapter(TabletsActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(TabletsActivity.this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        getTablets1();
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
                searchList2(newText);
                return true;
            }
        });
    }
    private void getTablets1() {
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
                        tablets.add(product);
                    }
                    dataList.addAll(tablets);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(TabletsActivity.this, "All products are loaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TabletsActivity.this, "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }
    private void searchList2(String text) {
        List<Product> dataSearchList = new ArrayList<>();
        for (Product data : dataList) {
            if (data.name.toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (!dataSearchList.isEmpty()) {
            adapter.setSearchList(dataSearchList);
        } else {
            Toast.makeText(TabletsActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }
}