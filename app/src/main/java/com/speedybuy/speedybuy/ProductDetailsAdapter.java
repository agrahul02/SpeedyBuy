package com.speedybuy.speedybuy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

  private final int totalTabs;
  private final String productDescription;
  private final String productOtherDetails;
  private final List<ProductSpecificationModel> productSpecificationModelList;


    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, String productOtherDetails, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm);
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecificationModelList = productSpecificationModelList;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
       switch (position) {
           case 0:
               ProductDescriptionFragment productDescriptionFragment1 = new ProductDescriptionFragment();
                productDescriptionFragment1.body = productDescription;
               return productDescriptionFragment1;

               case 1:
                   ProductSpacificationFragment productSpacificationFragment = new ProductSpacificationFragment();
                    productSpacificationFragment.productSpecificationModelList = productSpecificationModelList;
                   return productSpacificationFragment;


                   case 2:
                       ProductDescriptionFragment productDescriptionFragment2 = new ProductDescriptionFragment();
                       productDescriptionFragment2.body = productOtherDetails;
                       return productDescriptionFragment2;
                       default:
                           return null;
       }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
