package com.example.fotpulse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // For showing messages

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotpulse.R;
import com.example.fotpulse.NewsAdapter;
import com.example.fotpulse.DatabaseHelper; // IMPORTANT: Changed to use your main DatabaseHelper
import com.example.fotpulse.NewsItem;

import java.util.List;

public class NewsCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private DatabaseHelper dbHelper;

    public NewsCategoryFragment() {

    }

    public static NewsCategoryFragment newInstance(String category) {
        NewsCategoryFragment fragment = new NewsCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        TextView categoryTextView = view.findViewById(R.id.categoryTextView);
        if (categoryTextView != null) {
            categoryTextView.setText("Displaying " + category + " News");
            categoryTextView.setVisibility(View.VISIBLE);
        }

        recyclerView = view.findViewById(R.id.recyclerViewNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        dbHelper = new DatabaseHelper(getContext());

        loadNewsData();

        return view;
    }

    private void loadNewsData() {
        if (category == null || category.isEmpty()) {
            Toast.makeText(getContext(), "Category not specified.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<NewsItem> newsList = dbHelper.getAllNewsByCategory(category);

        if (newsAdapter == null) {
            newsAdapter = new NewsAdapter(newsList);
            recyclerView.setAdapter(newsAdapter);
        } else {

            newsAdapter.updateData(newsList);
        }


        if (newsList.isEmpty()) {
            Toast.makeText(getContext(), "No news found for " + category, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}