package io.rezetopia.krito.rezetopiakrito.activities;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.fragments.AlertFragment;
import io.rezetopia.krito.rezetopiakrito.model.operations.UserOperations;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchResult;

public class SearchActivity extends AppCompatActivity {

    SearchResult searchResult;
    ArrayList<SearchItem> searchItems;
    RecyclerView.Adapter adapter;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    EditText searchBox;
    ImageView backView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        recyclerView = findViewById(R.id.searchRecView);
        progressBar = findViewById(R.id.searchProgress);
        searchBox = findViewById(R.id.searchbox);
        backView = findViewById(R.id.backView);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getSearchResult(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ppView;
        TextView searchUserName;
        TextView detailsView;

        public SearchViewHolder(View itemView) {
            super(itemView);

            ppView = itemView.findViewById(R.id.ppView);
            searchUserName = itemView.findViewById(R.id.searchUserName);
            detailsView = itemView.findViewById(R.id.detailsView);
        }

        public void bind(SearchItem item){
            if (item.getImageUrl() != null){
                Picasso.with(SearchActivity.this).load(item.getImageUrl()).into(ppView);
            }

            searchUserName.setText(item.getName());
            if (item.getDescription() != null)
                detailsView.setText(item.getDescription());
        }
    }

    private class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchViewHolder>{

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_card, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            holder.bind(searchItems.get(position));
        }

        @Override
        public int getItemCount() {
            return searchItems.size();
        }
    }

    private void getSearchResult(String q){

        String cursor = "0";

        if (searchResult != null){
            cursor = searchResult.getCursor();
        }

        UserOperations.search(q, cursor);
        progressBar.setVisibility(View.VISIBLE);
        UserOperations.setSearchCallback(new UserOperations.SearchCallback() {
            @Override
            public void onResponse(SearchResult result) {
                searchResult = result;
                searchItems = result.getSearchItems();
                progressBar.setVisibility(View.GONE);
                updateUi();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                AlertFragment.createFragment(error).show(getFragmentManager(), null);
            }
        });
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new SearchRecyclerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
