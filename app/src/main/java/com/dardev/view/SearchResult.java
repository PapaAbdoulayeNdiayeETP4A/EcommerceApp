package com.dardev.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.dardev.R;
import com.dardev.adapter.search_result_adapter;
import com.dardev.databinding.SearchResultBinding;

public class SearchResult extends AppCompatActivity
{
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    search_result_adapter search_result_adapter;
    SearchResultBinding searchResultBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        searchResultBinding = DataBindingUtil.setContentView(this, R.layout.search_result);

        recyclerView = searchResultBinding.recyclerview;

        getWindow().setStatusBarColor(getResources().getColor(R.color.white,getTheme()));

        setUpRecyclerView();
        getProductsInCart();
    }

    private void setUpRecyclerView()
    {
        linearLayoutManager = new LinearLayoutManager(SearchResult.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // cartBinding.recyclerview.setHasFixedSize(true);
      //  cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void getProductsInCart()
    {
        /*list = new ArrayList<SearchProduct>();

        list.add(new SearchProduct("Cheries",R.drawable.shoes1,"₹300"));
        list.add(new SearchProduct("Cheries",R.drawable.shoes2,"₹400"));
        list.add(new SearchProduct("Cheries",R.drawable.shoes3,"₹500"));
        list.add(new SearchProduct("Cheries",R.drawable.shoes3,"₹600"));*/


        search_result_adapter = new search_result_adapter(recyclerView, SearchResult.this,new ArrayList<Integer>(),new ArrayList<String>(),new ArrayList<String>());


        search_result_adapter.update(R.drawable.shoes1,"Asian WNDR-13 Running Shoes for Men(Green, Grey)","₹300.00");
        search_result_adapter.update(R.drawable.shoes2,"Asian WNDR-13 Running Shoes for Men(Green, Grey)","₹500.00");

        recyclerView.setAdapter(search_result_adapter);
        search_result_adapter.notifyDataSetChanged();
    }
}