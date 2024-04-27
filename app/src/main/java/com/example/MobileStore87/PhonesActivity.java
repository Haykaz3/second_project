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

public class PhonesActivity extends AppCompatActivity {
    List<Product> dataList;
    List<Product> phones;

    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phones);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPhones);
        SearchView searchView = findViewById(R.id.searchPhones);

        searchView.clearFocus();

        dataList = new ArrayList<>();
        phones = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        adapter = new MyAdapter(PhonesActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(PhonesActivity.this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        getPhones1();
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
                searchList1(newText);
                return true;
            }
        });
    }
    private void getPhones1() {
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
                    Toast.makeText(PhonesActivity.this, "All products are loaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PhonesActivity.this, "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }
    private void searchList1(String text) {
        List<Product> dataSearchList = new ArrayList<>();
        for (Product data : dataList) {
            if (data.name.toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (!dataSearchList.isEmpty()) {
            adapter.setSearchList(dataSearchList);
        } else {
            Toast.makeText(PhonesActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }
}