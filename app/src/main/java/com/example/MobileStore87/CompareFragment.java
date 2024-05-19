package com.example.MobileStore87;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MobileStore87.databinding.FragmentBasketBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

public class CompareFragment extends Fragment {

    RecyclerView recyclerView;
    public Cart cart;
    private CartAdapter cartAdapter;
    private String categoryId;
    private String itemcount;
    private String itemprice;
    TextView price;
    private List<CartItem> cartItemList;
    TextView name;
    FragmentBasketBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBasketBinding.inflate(inflater, container, false);
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, getActivity());
        recyclerView = binding.recyclerView;
        recyclerView.setAdapter(cartAdapter);
        getProducts();
        return binding.getRoot();

    }
    private void getProducts(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = user.getUid(); // Assuming user is already defined

// Query for carts with the specified device ID
            db.collection("carts")
                    .whereEqualTo("deviceId", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get the cart ID
                            String cartId = documentSnapshot.getId();

                            // Query for cart items within the cart
                            db.collection("carts").document(cartId).collection("cartItems")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                        if (!queryDocumentSnapshots1.isEmpty()) {
                                            for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1) {
                                                // Create a CartItem object and add it to the list
                                                CartItem cartItem = new CartItem();
                                                cartItem.itemCount =  documentSnapshot1.get("itemCount", Integer.class);
                                                cartItem.itemId = documentSnapshot1.getString("itemId");
                                                cartItem.categoryId = documentSnapshot1.getString("categoryId");
                                                cartItem.price = documentSnapshot1.getString("price");
                                                cartItem.productURL = documentSnapshot1.getString("productUrl");
                                                cartItem.cartId = cartId;
                                                cartItem.cartItemId = documentSnapshot1.getId();
                                                cartItemList.add(cartItem);
                                            }
                                            // Notify the adapter after adding all cart items for the current cart
                                            cartAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), "Document snapshot is empty", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Log.e("TAG", "Error getting carts: " + e.getMessage());
                    });

        }
    }
}
