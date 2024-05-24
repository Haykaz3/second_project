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
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.regex.Pattern;

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
    private String productGrifusUrl;
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
        binding.productNameDetailGrifus.setText(productName1);
        Picasso.get().load(image).into(binding.detailImage);
        binding.mobileCentreRef.setOnClickListener(v -> {
            Uri uri = Uri.parse(productUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        binding.grifusRef.setOnClickListener(v -> {
            Uri uri = Uri.parse(productGrifusUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        binding.appCompatButton.setOnClickListener(v -> {
            addToCart(productId,categoryId,productPrice);
        });
        FirebaseFirestore.getInstance().collection("grifus" + toCapitalCase(categoryId)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                String nameFromQuery = queryDocumentSnapshot.getString("name");
                                String nameFromDocument = productName;
                                if (nameFromQuery != null && nameFromDocument != null && Compare(nameFromQuery, nameFromDocument)) {
                                    productGrifusUrl = queryDocumentSnapshot.getString("url");
                                    binding.detailPriceGrifus.setText(queryDocumentSnapshot.getString("price"));
                                    binding.productNameDetailGrifus.setText(nameFromQuery);
                                    return; // Assuming you only need to set the text once
                                }
                            }
                        }
                    }
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





    public boolean Compare(String phoneName, String phoneName2) {
        // Define colors to remove
        String[] ToRemove = {"Yellow", "Beige", "Black", "Blue", "Brown", "Coral"
                ,"darkgreen", "Grey", "Pink", "Orange"
                ,"Green", "White", "Purple", "Red", "Gold", "Silver",
                "Teal", "Tiffany", "Violet", "Polar", "APPLE", "Canary",
                "Prism", "Light", "Dark", "Rose", "(LL/A)", "Titanium",
                "Marble", "Meadow", "Gray", "Space", ".A13", ".0", "AIR",
                "M1 chip", "with", "8-core", "and", "7-core","GPU", "SSD", "Apple",
                "WiFi", "2021", "Graphite", "X510", "Night", "Sea", "Charcoal",
                "Cobalt", "Mint", "Amber", "Starlight", "Stripe", "Mighty", "Lavender",
                "Onyx", "Star", "Copper", "Forest", "Lime", "Champion", "Glory",
                "Glacier", "Sky", "Carbon", "Ice", "Moonlight", "Astral", "Midnight",
                "Natural", "Cyan", "Sunshower", "Sunrise", "Rainy", "Rock", "Cream",
                "Ocean", "Sunny", "Oasis", "Navy", "Alpine", "Phantom", "5G", "4G",
                "PC", "Notebook", "Moonstone", "Pure", "13 inch","M2 chip", " ",
                "K193", "CPU", "10-core", "15 inch", "2023", "2024", "chip", "16-core",
                "M1 Pro", "inch", "11‑core", "14‑core", "RAM", "12‑core", "18‑core",
                "8‑core", "7‑core", "T220", "Cosmic", "LTE", "Mist", "Real", "X205",
                "X910", "2022", "Cellular", "X710", "F5", "Dual SIM", "TA-1235",
                "TA-1582", "2660","Flip", "Exynos", "X110", "X210", "T225", "X200",
                "X115", "X210", "X510", "X616", "X700", "X710", "X610", "X716", "X800", "X810", "X910", "X916"};


        // Build a regex pattern to match any of the colors
        StringBuilder regexBuilder = new StringBuilder();
        for (String color : ToRemove) {
            regexBuilder.append("\\b").append(Pattern.quote(color)).append("\\b|");
        }
        regexBuilder.deleteCharAt(regexBuilder.length() - 1); // Remove the last '|'
        String regex = regexBuilder.toString();

        String pattern = "\\s*\\d+GB\\s*";

        // Remove "256GB" from the phone name
        phoneName = phoneName.replaceAll(pattern, "");
        phoneName2 = phoneName2.replaceAll(pattern, "");

        String pattern1 = "\\s*[\\\\/()\\[\\]]|\\s*\\(.*?\\)\\s*|\\s*\\d+GB\\s*";

        // Remove any brackets and "GB" size from the phone name
        phoneName = phoneName.replaceAll(pattern1, "");
        phoneName2 = phoneName2.replaceAll(pattern1, "");

        // Use regex to remove colors from the phone name
        phoneName = phoneName.replaceAll(regex, "");
        phoneName = phoneName.replaceAll("\"", "");
        phoneName = phoneName.replaceAll("[^a-zA-Z0-9 ]", "");
        phoneName = phoneName.replaceAll("\\s{2,}", " ").trim();
        phoneName2 = phoneName2.replaceAll(regex, "");
        phoneName2 = phoneName2.replaceAll("\"", "");
        phoneName2 = phoneName2.replaceAll("[^a-zA-Z0-9 ]", "");
        phoneName2 = phoneName2.replaceAll("\\s{2,}", " ").trim();
        // Remove extra spaces
        phoneName = phoneName.trim().replaceAll("\\s+", " ");
        phoneName2 = phoneName2.trim().replaceAll("\\s+", " ");



        String pattern2 = "\\s*[\\\\/()\\[\\]]|\\s*\\(.*?\\)\\s*|\\s*\\d+TB\\s*";

        phoneName = phoneName.replaceAll(pattern2, "");
        phoneName = phoneName.replaceAll("G31U3", "");
        phoneName = phoneName.replaceAll("NXK6SER004", "");
        phoneName = phoneName.replaceAll("K193", "");
        phoneName = phoneName.replaceAll(" ", "");
        phoneName = phoneName.replaceAll("M3Pro", "");
        phoneName = phoneName.replaceAll("M3PRO", "");
        phoneName = phoneName.replaceAll("Air", "");
        phoneName = phoneName.replaceAll("M3", "");
        phoneName2 = phoneName2.replaceAll(pattern2, "");
        phoneName2 = phoneName2.replaceAll("A13", "");
        phoneName2 = phoneName2.replaceAll("WiFi", "");
        phoneName2 = phoneName2.replaceAll("79CL", "");
        phoneName2 = phoneName2.replaceAll("55KQ", "");
        phoneName2 = phoneName2.replaceAll("K183", "");
        phoneName2 = phoneName2.replaceAll("inchM3Pro", "");
        phoneName2 = phoneName2.replaceAll("M3Pro", "");
        phoneName2 = phoneName2.replaceAll("M3PRO", "");
        phoneName2 = phoneName2.replaceAll("M3", "");
        phoneName2 = phoneName2.replaceAll("10core", "");
        phoneName2 = phoneName2.replaceAll("NXK6SER004", "");
        phoneName2 = phoneName2.replaceAll("Air", "");
        phoneName2 = phoneName2.replaceAll(" ", "");
        phoneName2 = phoneName2.replaceAll("Marble", "");
        phoneName2 = phoneName2.replaceAll("Light", "");
        phoneName2 = phoneName2.replaceAll("Silver", "");
        String replacement = "";
        String Redmi = "Redmi";
        for (String item : ToRemove) {
            if (item.equals("Red") && (phoneName.toLowerCase().contains("redmi") || phoneName2.toLowerCase().contains("redmi"))) {
                continue;
            }
            replacement += Pattern.quote(item) + "|";
        }
        replacement = replacement.substring(0, replacement.length() - 1); // Remove the last '|'

// Use regex to replace all items in the ToRemove array
        phoneName = phoneName.replaceAll(replacement, "");
        phoneName2 = phoneName2.replaceAll(replacement, "");


        System.out.println("Phone name after removing colors: " + phoneName2);
        if (phoneName.trim().toLowerCase().equals(phoneName2.trim().toLowerCase())) {
            return true;
        }
        return false;
    }

    public String toCapitalCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1).toLowerCase());
                sb.append(" ");
            }
        }

        return sb.toString().trim();
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