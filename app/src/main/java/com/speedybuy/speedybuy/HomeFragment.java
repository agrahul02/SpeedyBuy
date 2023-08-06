package com.speedybuy.speedybuy;

import static com.speedybuy.speedybuy.DBqueries.categoryModelList;
import static com.speedybuy.speedybuy.DBqueries.lists;
import static com.speedybuy.speedybuy.DBqueries.loadCategories;
import static com.speedybuy.speedybuy.DBqueries.loadFragmentData;
import static com.speedybuy.speedybuy.DBqueries.loadedCategoriesName;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private final List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private final List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter;
    private ImageView noInternetConnection;
    private Button retryBtn;

    private FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);
        retryBtn = view.findViewById(R.id.retry_btn);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.teal_200),getContext().getResources().getColor(R.color.teal_200),getContext().getResources().getColor(R.color.teal_200));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);


        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);


        /////////// category fake list
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        ///////////// category fake list

        /////////// home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<SliderModel>();
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null","#dfdfdf"));

        List<HorizontalProductScrollModel> horizontalProductScrollModeFakelList = new ArrayList<HorizontalProductScrollModel>();
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModeFakelList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#dfdfdf"));
        homePageModelFakeList.add(new HomePageModel(2,"","#dfdfdf",horizontalProductScrollModeFakelList,new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"","#dfdfdf", horizontalProductScrollModeFakelList));

        /////////// home page fake list

        categoryAdapter = new CategoryAdapter(categoryModelFakeList);
        adapter = new HomePageAdapter(homePageModelFakeList);

         connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
         networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true){
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
         noInternetConnection.setVisibility(View.GONE);
         retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

         if (categoryModelList.size() == 0) {
            loadCategories(categoryRecyclerView,getContext());
         }else {
             categoryAdapter = new CategoryAdapter(categoryModelList);
             categoryAdapter.notifyDataSetChanged();
         }
            categoryRecyclerView.setAdapter(categoryAdapter);
         if (lists.size() == 0) {
             loadedCategoriesName.add("HOME");
             lists.add(new ArrayList<HomePageModel>());
             loadFragmentData(homePageRecyclerView,getContext(),0,"Home");
         }else {
             adapter = new HomePageAdapter(lists.get(0));
             adapter.notifyDataSetChanged();
         }
            homePageRecyclerView.setAdapter(adapter);
        }else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.loading).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
        }

        //////////// refresh layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
               reloadPage();
            }
        });
        /////////// refresh layout

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return view;
    }
    private void reloadPage() {
        networkInfo = connectivityManager.getActiveNetworkInfo();
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesName.clear();
        DBqueries.clearData();
        if (networkInfo != null && networkInfo.isConnected() == true){
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageRecyclerView.setAdapter(adapter);

            loadCategories(categoryRecyclerView,getContext());

            loadedCategoriesName.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecyclerView,getContext(),0,"Home");

        }else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "No internet connection !", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.loading).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

   /* private void searchPosts(final String search) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryModelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    CategoryModel modelPost = dataSnapshot1.getValue(CategoryModel.class);
                    if (CategoryModel.getCategoryName().toLowerCase().contains(search.toLowerCase()) ||
                            modelPost.getCategoryIconLink().toLowerCase().contains(search.toLowerCase())) {
                        categoryModelList.add(modelPost);
                    }
                    CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categoryModelList);

                    homePageRecyclerView.setAdapter(categoryAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/

}
