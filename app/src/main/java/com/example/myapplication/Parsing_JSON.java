package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parsing_JSON extends AppCompatActivity {
    Button button;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_json);


        button = findViewById(R.id.getData);

        button.setOnClickListener(v -> {
            new FetchTask().execute();
        });
    }

    private class FetchTask extends AsyncTask<Void, Void, Void> {
        String url = "https://www.mobilecentre.am/category/computers/144/0/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(url).get();

                Elements productElements = doc.select("body > div.container.listing > div > div.col-lg-9.col-md-9.col-sm-12.product-list.product-list-filter > div");

                for (Element productElement : productElements) {
                    String productImage = productElement.select("div.equalsize > a > img").attr("data-src");
                    String productName = productElement.select("h3").text();
                    String productPrice = productElement.select("div.item-body > div.price > span").text();

                    //System.out.println(productName + " " + productPrice + " " + productImage);
                    if (!productName.isEmpty() && !productPrice.isEmpty() && !productImage.isEmpty()) {
                        HashMap<String, Object> product = new HashMap<>();
                        product.put("name", productName);
                        product.put("price", productPrice);
                        product.put("image", productImage);

                        db.collection("computers")
                                .add(product)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(Parsing_JSON.this, "ADDED", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
        }
    }
}