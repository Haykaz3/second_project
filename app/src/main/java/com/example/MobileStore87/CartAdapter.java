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
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                        }
                    });
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
                binding.detailDescriptionCompare.setText(String.join(" ",strings));
            }
        }
        void deleteItem(String cartId, String id) {
            FirebaseFirestore.getInstance().collection("carts").document(cartId).collection("cartItems").document(id).delete();
            notifyDataSetChanged();
        }
    }
}