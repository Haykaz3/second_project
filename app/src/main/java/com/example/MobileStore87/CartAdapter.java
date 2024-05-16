package com.example.MobileStore87;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MobileStore87.databinding.ActivityCartAdapterBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.regex.Pattern;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    ImageView imageViewdelete;
    int count1;
    private final List<CartItem> cartitems;
    private static Context context;


    public CartAdapter(List<CartItem> cartitems, Context context) {
        this.cartitems = cartitems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityCartAdapterBinding itemContainerCartBinding = ActivityCartAdapterBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CartViewHolder(itemContainerCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.setCartData(cartitems.get(position));
    }


    @Override
    public int getItemCount() {
        return cartitems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        ActivityCartAdapterBinding binding;

        CartViewHolder(ActivityCartAdapterBinding ActivityCartAdapterBinding) {
            super(ActivityCartAdapterBinding.getRoot());
            binding = ActivityCartAdapterBinding;
        }

        void setCartData(CartItem cartData) {
            //binding.detailDescriptionCompare.setText(cartData.productURL);
            new CartViewHolder.FetchTask11(cartData.productURL).execute();
            FirebaseFirestore.getInstance().collection(cartData.categoryId).document(cartData.itemId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            if (documentSnapshot.getString("image") != null) {
                                Picasso.get().load(documentSnapshot.getString("image")).into(binding.imageView1);
                            }
                            binding.textView1.setText(documentSnapshot.getString("name"));
                            String numericString = cartData.price.replaceAll("[^0-9]", "");

                            int price = Integer.parseInt(numericString);
                            binding.textView2.setText(Integer.toString(price));

                            binding.imageViewdelete.setOnClickListener(v -> {
                                deleteItem(cartData.cartId, cartData.cartItemId);
                            });

                            FirebaseFirestore.getInstance().collection("grifus" + toCapitalCase(cartData.categoryId)).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                                    binding.grifusUrl.setText("loading");
                                                    String nameFromQuery = queryDocumentSnapshot.getString("name");
                                                    String nameFromDocument = documentSnapshot.getString("name");
                                                    if (nameFromQuery != null && nameFromDocument != null && Compare(nameFromQuery, nameFromDocument)) {
                                                        binding.grifusUrl.setText(queryDocumentSnapshot.getString("url"));
                                                        binding.grifusPrice.setText(queryDocumentSnapshot.getString("price"));
                                                        return; // Assuming you only need to set the text once
                                                    }
                                                }
                                                // If the loop finishes without finding a match
                                                binding.grifusUrl.setText("False");
                                            }
                                        }
                                    });
                        }
                    });
        }

        public boolean Compare(String phoneName, String phoneName2) {
            // Define colors to remove
            String[] colorsToRemove = {"Yellow", "Beige", "Black", "Blue", "Brown", "Coral"
                    ,"darkgreen", "Grey", "Pink", "Orange"
                    ,"Green", "White", "Purple", "Red", "Gold", "Silver",
                    "Teal", "Tiffany", "Violet", "Polar", "APPLE", "Canary",
                    "Prism", "Light", "Dark", "Rose", "(LL/A)", "Titanium",
                    "Marble", "Meadow", "Gray", "Space", ".A13", ".0", "Air", "AIR",
                    "M1 chip", "with", "8-core CPU", "and", "7-core GPU", "SSD", "Apple",
                    "WiFi", "2021", "Graphite", "X510", "Night", "Sea", "Charcoal",
                    "Cobalt", "Mint", "Amber", "Starlight", "Stripe", "Mighty", "Lavender",
                    "Onyx", "Star", "Copper", "Forest", "Lime", "Champion", "Glory",
                    "Glacier", "Sky", "Carbon", "Ice", "Moonlight", "Astral", "Midnight",
                    "Natural", "Cyan", "Sunshower", "Sunrise", "Rainy", "Rock", "Cream",
                    "Ocean", "Sunny", "Oasis", "Navy", "Alpine", "Phantom"};

            // Build a regex pattern to match any of the colors
            StringBuilder regexBuilder = new StringBuilder();
            for (String color : colorsToRemove) {
                regexBuilder.append("\\b").append(Pattern.quote(color)).append("\\b|");
            }
            regexBuilder.deleteCharAt(regexBuilder.length() - 1); // Remove the last '|'
            String regex = regexBuilder.toString();

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

            String pattern = "\\s*\\d+GB\\s*";

            // Remove "256GB" from the phone name
            phoneName = phoneName.replaceAll(pattern, "");
            phoneName2 = phoneName2.replaceAll(pattern, "");

            String pattern1 = "\\s*[\\\\/()\\[\\]]|\\s*\\(.*?\\)\\s*|\\s*\\d+GB\\s*";

            // Remove any brackets and "GB" size from the phone name
            phoneName = phoneName.replaceAll(pattern1, "");
            phoneName2 = phoneName2.replaceAll(pattern1, "");

            String pattern2 = "\\s*[\\\\/()\\[\\]]|\\s*\\(.*?\\)\\s*|\\s*\\d+TB\\s*";

            phoneName = phoneName.replaceAll(pattern2, "");
            phoneName2 = phoneName2.replaceAll(pattern2, "");
            phoneName2 = phoneName2.replaceAll("A13", "");
            phoneName2 = phoneName2.replaceAll("WiFi", "");


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

        public class FetchTask11 extends AsyncTask<Void, Void, Void> {
            String url;
            List<String> strings = new ArrayList<>();

            public FetchTask11(String url) {
                this.url = url;
            }

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
                            i += 2;
                            j += 2;
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
                binding.detailDescriptionCompare.setText(String.join(" ", strings));
            }
        }

        void deleteItem(String cartId, String id) {
            FirebaseFirestore.getInstance().collection("carts").document(cartId).collection("cartItems").document(id).delete();
            notifyDataSetChanged();
        }
    }
}