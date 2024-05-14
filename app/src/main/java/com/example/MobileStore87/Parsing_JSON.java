package com.example.MobileStore87;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

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
        String url = "https://notebookcentre.am/category/phones/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(url).get();

                Elements productElements = doc.select("#product-list > ul.thumbs.product-list.row.show-tech-chars > li");

                for (Element productElement : productElements) {
                    String productImage = productElement.select("div > a > div > div > img").attr("src");
                    String productName = productElement.select("li > div > a > h5 > span").text();
                    String productPrice = productElement.select("#price-skey-0 > div:nth-child(1) > span").text();
                    //System.out.println(productName + " " + productPrice + " " + productImage);

                    if (!productName.isEmpty() && !productPrice.isEmpty() && !productImage.isEmpty()) {
                        HashMap<String, Object> product = new HashMap<>();
                        product.put("name", productName);
                        product.put("price", productPrice);
                        product.put("image", productImage);

                        db.collection("NotebookCenterPhones")
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