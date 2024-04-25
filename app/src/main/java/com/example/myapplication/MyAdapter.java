package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.RecyclerItemBinding;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    public static Context context;
    private List<Product> dataList;

    public void setSearchList(List<Product> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public MyAdapter(Context context, List<Product>dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBinding recyclerItemBinding = RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(recyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class MyViewHolder extends RecyclerView.ViewHolder{

    RecyclerItemBinding binding;
    int count = 1;

    public MyViewHolder(RecyclerItemBinding itemView){
        super(itemView.getRoot());
        binding = itemView;

    }
    void setData(Product dataClass) {
        if (dataClass.name.length() > 50) {
            binding.recTitle.setText(dataClass.name.substring(0, 50));
        } else {
            binding.recTitle.setText(dataClass.name);
        }
        binding.recDesc.setText(dataClass.price);
        if (dataClass.image != null) {
            Picasso.get().load(dataClass.image).into(binding.recImage);
        }
        binding.recCard.setOnClickListener(v -> {
            Intent intent = new Intent(MyAdapter.context, DetailActivity.class);
            intent.putExtra("productUrl", dataClass.productUrl);
            intent.putExtra("productPrice", dataClass.price);
            intent.putExtra("image", dataClass.image);
            intent.putExtra("productName", dataClass.name);
            intent.putExtra("productId", dataClass.productId);
            intent.putExtra("categoryId", dataClass.categoryId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyAdapter.context.startActivity(intent);
        });
        binding.appCompatButton.setOnClickListener(v -> {
            addToCart(dataClass.productId, dataClass.categoryId, dataClass.price, dataClass.productUrl);
        });
    }
    private void addToCart(String productId, String categoryId, String price, String productUrl) {
        HashMap<String, Object> cart = new HashMap<>();
        HashMap<String, Object> cartItem = new HashMap<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //cartId = FirebaseFirestore.getInstance().collection("carts").whereEqualTo("deviceId", user.getUid()).toString();
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
                                                                Toast.makeText(itemView.getContext(),"Added Second",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                   /* if (user.getUid().equals(cartId)) {
                                        HashMap<String, Object> newcart = new HashMap<>();
                                        FirebaseFirestore.getInstance().collection("cartItems").
                                    }*/
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
                                                            Toast.makeText(itemView.getContext(), "Added",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                }
                            }

                        }
                    });
        } else {
            Toast.makeText(itemView.getContext(),"User is not logged in",Toast.LENGTH_SHORT).show();
        }
    }
}
