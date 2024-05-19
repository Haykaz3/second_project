package com.example.MobileStore87;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.MobileStore87.databinding.FragmentHomeBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

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
        ImageView exit = (ImageView) binding.logout;

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

        exit.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().getSupportFragmentManager().beginTransaction().remove(HomeFragment.this).commit();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

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
//        binding.adsad.setOnClickListener(v -> {
//            CompareWithGemini("SAMSUNG GALAXY S24 ULTRA", "Samsung Galaxy S24 Ultra 512GB (Titanium Violet)");
//        });
        return binding.getRoot();

    }

    private void getProducts() {
        getComputer();
        getTablets();
        getPhones();
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
                        if (documentSnapshot.get("viewCount", Integer.class) != null) {
                            product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                        } else {
                            product.viewCount = 0;
                        }
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
                        if (documentSnapshot.get("viewCount", Integer.class) != null) {
                            product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                        } else {
                            product.viewCount = 0;
                        }
                        tablets.add(product);
                    }
                    dataList.addAll(tablets);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Cant load data", Toast.LENGTH_SHORT).show();
                });

    }

    private void getComputer() {
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
                        if (documentSnapshot.get("viewCount", Integer.class) != null) {
                            product.viewCount = documentSnapshot.get("viewCount", Integer.class);
                        } else {
                            product.viewCount = 0;
                        }
                        computers.add(product);
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
        }
    }
//    public void CompareWithGemini(String phoneName, String phoneName2) {
//        // For text-only input, use the gemini-pro model
//        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
//                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
//                "AIzaSyBa6txPera-uqkIfMChqzjp7kLGjQbNDDc");
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//
//        Content content = new Content.Builder()
//                .addText(phoneName + ", " + phoneName2 + ", " + "if they describe the same device write true otherwise write false (ignore storage and colors) write only true or false")
//                .build();
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//                @Override
//                public void onSuccess(GenerateContentResponse result) {
//                    String resultText = result.getText();
//                    // Assuming binding.bool is a TextView
//                    binding.asdasdtext.setText(resultText);
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    t.printStackTrace();
//                }
//            }, getActivity().getMainExecutor());
//        }
//    }
}