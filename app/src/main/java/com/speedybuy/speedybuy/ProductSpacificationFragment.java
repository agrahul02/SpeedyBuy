package com.speedybuy.speedybuy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductSpacificationFragment extends Fragment {



    public ProductSpacificationFragment() {
        // Required empty public constructor
    }

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationModel> productSpecificationModelList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_spacification, container, false);

        productSpecificationRecyclerView = view.findViewById(R.id.product_specification_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);


        /*productSpecificationModelList.add(new ProductSpecificationModel(0,"Gernal"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(0,"Display"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(0,"Gernal"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(0,"Display"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB"));
        productSpecificationModelList.add(new ProductSpecificationModel(1,"RAM","4GB")); */


        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();

        return view;
    }
}