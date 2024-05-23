package com.example.MobileStore87;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.MobileStore87.databinding.ActivityDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private com.example.MobileStore87.databinding.ActivityDetailBinding binding;
    private String productUrl;
    private String productUrl1;
    private String productPrice;
    private String productName;
    private String productName1;
    private String productId;
    private String categoryId;
    private String image;
    int count = 1;
    List<String> strings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productUrl = getIntent().getStringExtra("productUrl");
        productUrl1 = getIntent().getStringExtra("url");
        productPrice = getIntent().getStringExtra("productPrice");
        productName = getIntent().getStringExtra("productName");
        productName1 = getIntent().getStringExtra("productName");
        productId = getIntent().getStringExtra("productId");
        categoryId = getIntent().getStringExtra("categoryId");
        image = getIntent().getStringExtra("image");
        binding.detailPrice.setText(productPrice);
        binding.productNameDetail.setText(productName);
        binding.productNameDetail4.setText(productName);
        Picasso.get().load(image).into(binding.detailImage);
        binding.mobileCentreRef.setOnClickListener(v -> {
            Uri uri = Uri.parse(productUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        binding.appCompatButton.setOnClickListener(v -> {
            addToCart(productId,categoryId,productPrice);
        });
        new FetchTask1().execute();
    }
    private void addToCart(String productId, String categoryId, String price) {
        HashMap<String, Object> cart = new HashMap<>();
        HashMap<String, Object> cartItem = new HashMap<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore.getInstance().collection("carts").whereEqualTo("deviceId", user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                if(!task.getResult().isEmpty()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference docRef = document.getReference().collection("cartItems").document();
                                        document.getReference().collection("cartItems").whereEqualTo("itemId", productId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (!task.getResult().isEmpty()) {
                                                                docRef.update("itemCount", ++count);
                                                            } else {
                                                                Map<String, Object> newData = new HashMap<>();
                                                                newData.put("itemCount", count);
                                                                newData.put("price", price);
                                                                newData.put("itemId", productId);
                                                                newData.put("categoryId", categoryId);
                                                                newData.put("productUrl", productUrl);
                                                                docRef.set(newData);
                                                                count++;
                                                                Toast.makeText(getApplicationContext(),"Added Second",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }else {
                                    cart.put("deviceId", user.getUid());
                                    cartItem.put("itemCount", count);
                                    cartItem.put("price", price);
                                    cartItem.put("itemId", productId);
                                    cartItem.put("categoryId", categoryId);
                                    cartItem.put("productUrl", productUrl);

                                    FirebaseFirestore.getInstance().collection("carts")
                                            .add(cart)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    documentReference.collection("cartItems").add(cartItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(getApplicationContext(), "Added",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                }
                            }

                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),"User is not logged in",Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchTask1 extends AsyncTask<Void, Void, Void> {
        String url = productUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(url).get();

                for (int k = 1; k <= 7; k++) {
                    int i = 2, j = i + 1;
                    while (doc.selectFirst("body > div:nth-child(10) > div > div:nth-child(" + k + ") > div:nth-child(" + i + ")") != null && doc.selectFirst("body > div:nth-child(10) > div > div:nth-child(1) > div:nth-child(" + j + ")") != null) {
                        Element productDescName = doc.selectFirst("body > div:nth-child(10) > div > div:nth-child(" + k + ") > div:nth-child(" + i + ")");
                        Element productDescValue = doc.selectFirst("body > div:nth-child(10) > div > div:nth-child(" + k + ") > div:nth-child(" + j + ")");
                        strings.add(productDescName.text() + " " + productDescValue.text() + "\n");
                        i+=2;
                        j+=2;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            binding.detailDescription.setText(String.join(" ",strings));
        }
    }

}