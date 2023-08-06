package com.speedybuy.speedybuy;

import static com.speedybuy.speedybuy.DBqueries.lists;
import static com.speedybuy.speedybuy.DBqueries.loadFragmentData;
import static com.speedybuy.speedybuy.DBqueries.loadedCategoriesName;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private final List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        Toolbar toolbar = findViewById(R.id.toolbarn);
        setSupportActionBar(toolbar); //No Problerm
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String title = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////////// home page fake list //////////////////
        List<SliderModel> sliderModelFakeList = new ArrayList<SliderModel>();
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#ffffff"));
        homePageModelFakeList.add(new HomePageModel(2,"","#ffffff",horizontalProductScrollModelFakeList,new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"","#ffffff",horizontalProductScrollModelFakeList));
        ////////// home page fake list //////////////////

        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);

        adapter = new HomePageAdapter(homePageModelFakeList);


        int listPosition = 0;
        for (int x = 0; x < loadedCategoriesName.size(); x++) {
            if (loadedCategoriesName.get(x).equals(title.toUpperCase())){
              listPosition = x;
            }
        }
        if (listPosition == 0) {
            loadedCategoriesName.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(categoryRecyclerView,this,loadedCategoriesName.size() - 1,title);
        }else {
            adapter = new HomePageAdapter(lists.get(listPosition));
        }
        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.main_search_icon) {
//            Intent searchIntent = new Intent(this, SearchActivity.class);
//            startActivity(searchIntent);
//            return true;
//        }else
        if (id == android.R.id.home){
            finish();
            return true;
        }

        NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }
}