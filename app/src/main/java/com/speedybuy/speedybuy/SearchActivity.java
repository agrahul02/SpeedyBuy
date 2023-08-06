package com.speedybuy.speedybuy;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView notAvailable;
    private RecyclerView recyclerView;
    private Dialog signInDialog;
    private Dialog loadingDialog;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.search_view);
        notAvailable = (TextView) findViewById(R.id.shop_not_available);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

         final List<WishlistModel> list = new ArrayList<WishlistModel>();
        final List<String> ids = new ArrayList<String>();

        final Adapter adapter = new Adapter(list,false);
        adapter.setFromSearch(true);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                list.clear();
                ids.clear();

                final String[] tags = newText.toLowerCase().split(" ");
                for (final String tag : tags) {
                    tag.trim();

                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags", tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                            WishlistModel model = new WishlistModel(documentSnapshot.getId(), documentSnapshot.get("product_image_1").toString()
                                                    , documentSnapshot.get("product_title").toString()
                                                    , documentSnapshot.get("average_rating").toString()
                                                    , documentSnapshot.get("total_rating").toString()
                                                    , documentSnapshot.get("product_price").toString()
                                                    , documentSnapshot.get("cutted_price").toString()
                                                    , (boolean) documentSnapshot.get("COD")
                                                    , true);

                                            model.setTags((ArrayList<String>) documentSnapshot.get("tags"));
                                            if (!ids.contains(model.getProductId())) {
                                                list.add(model);
                                                ids.add(model.getProductId());
                                            }
                                        }

                                        if (tag.equals(tags[tags.length - 1])) {
                                            if (list.size() == 0) {
                                                // textView
                                                notAvailable.setVisibility(View.VISIBLE);
                                                recyclerView.setVisibility(View.GONE);
                                            } else {
                                                notAvailable.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                adapter.getFilter().filter(newText);
                                            }
                                        }
                                    } else{
                                        String error = "Error retrieving data: " +  task.getException().getMessage();
                                        Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                return false;
            }
        });
    }

    class Adapter extends WishlistAdapter implements Filterable {

        private final List<WishlistModel> originalList;

        public Adapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
            super(wishlistModelList, wishlist);
            originalList = wishlistModelList;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                   FilterResults results = new FilterResults();

                    List<WishlistModel> filteredList = new ArrayList<>();

                   final String[] tags = constraint.toString().toLowerCase().split(" ");

                   for (WishlistModel model : originalList){
                       ArrayList<String> presentTags = new ArrayList<>();
                       for (String tag : tags){
                           if (model.getTags().contains(tag)){
                               presentTags.add(tag);
                           }
                       }
                       model.setTags(presentTags);
                   }
                   for (int i = tags.length; i > 0; i--){
                       for (WishlistModel model : originalList){
                           if (model.getTags().size() == i){
                               filteredList.add(model);
                           }
                       }
                   }
                   results.values = filteredList;
                   results.count = filteredList.size();
                    return results;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results.count > 0) {
                        setWishlistModelList((List<WishlistModel>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}