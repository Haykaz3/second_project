package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityCartAdapterBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

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
            binding.textViewcount.setText(Integer.toString(cartData.itemCount));
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

                            binding.imageViewplus.setOnClickListener(view -> {

                                int count1 = cartData.itemCount++;
                                FirebaseFirestore.getInstance().collection("carts")
                                        .document(cartData.cartId).collection("cartItems").document(cartData.cartItemId).update("itemCount", count1);
                                binding.textView2.setText(Integer.toString(price * count1));
                                binding.textViewcount.setText(Integer.toString(count1));


                            });
                            binding.imageViewplus.setOnClickListener(view -> {
                                count1++;

                                FirebaseFirestore.getInstance().collection("carts")
                                        .document(cartData.cartId).collection("cartItems").document(cartData.cartItemId).update("itemCount", count1);
                                binding.textView2.setText(Integer.toString(price * count1));
                                binding.textViewcount.setText(Integer.toString(count1));


                            });
                            binding.imageViewminus.setOnClickListener(view -> {

                                if (cartData.itemCount > 0) {
                                    count1--;
                                    FirebaseFirestore.getInstance().collection("carts")
                                            .document(cartData.cartId).collection("cartItems").document(cartData.cartItemId).update("itemCount", count1);
                                    binding.textView2.setText(Integer.toString(price * count1));
                                    binding.textViewcount.setText(Integer.toString(count1));
                                }
                            });
                            binding.imageViewdelete.setOnClickListener(v -> {
                                deleteItem(cartData.cartId, cartData.cartItemId);
                            });
                        }
                    });
        }
        void deleteItem(String cartId, String id) {
            FirebaseFirestore.getInstance().collection("carts").document(cartId).collection("cartItems").document(id).delete();
            notifyDataSetChanged();
        }
    }
}