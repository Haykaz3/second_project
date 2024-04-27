package com.example.MobileStore87;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.MobileStore87.databinding.FragmentHomeBinding;
import com.example.MobileStore87.databinding.FragmentProfileBinding;
import com.example.MobileStore87.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsFragment extends Fragment {

    FragmentStatisticsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());
        PieChart pieChart = binding.chart;
        AtomicInteger phonesViewCount = new AtomicInteger(0);
        AtomicInteger tabletsViewCount = new AtomicInteger(0);
        AtomicInteger computersViewCount = new AtomicInteger(0);
        ArrayList<PieEntry> entries = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("phones").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            if (queryDocumentSnapshot.get("viewCount", Integer.class) != null) {
                                phonesViewCount.addAndGet(queryDocumentSnapshot.get("viewCount", Integer.class));
                            }
                        }

                        entries.add(new PieEntry((float) phonesViewCount.get(), "Phones"));
                        PieDataSet pieDataSet = new PieDataSet(entries, "Technique");
                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);

                        pieChart.getDescription().setEnabled(true);
                        pieChart.animateY(1000);
                        pieChart.invalidate();
                    }
                });
        FirebaseFirestore.getInstance().collection("tablets").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            if (queryDocumentSnapshot.get("viewCount", Integer.class) != null) {
                                tabletsViewCount.addAndGet(queryDocumentSnapshot.get("viewCount", Integer.class));
                            }
                        }
                        entries.add(new PieEntry((float) tabletsViewCount.get(), "tablets"));
                        PieDataSet pieDataSet = new PieDataSet(entries, "Technique");
                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);

                        pieChart.getDescription().setEnabled(true);
                        pieChart.animateY(1000);
                        pieChart.invalidate();
                    }
                });

        FirebaseFirestore.getInstance().collection("computers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            if (queryDocumentSnapshot.get("viewCount", Integer.class) != null) {
                                computersViewCount.addAndGet(queryDocumentSnapshot.get("viewCount", Integer.class));
                            }
                        }
                        entries.add(new PieEntry((float) computersViewCount.get(), "Computers"));
                        PieDataSet pieDataSet = new PieDataSet(entries, "Technique");
                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);

                        pieChart.getDescription().setEnabled(true);
                        pieChart.animateY(1000);
                        pieChart.invalidate();
                    }
                });


        return binding.getRoot();
    }
}