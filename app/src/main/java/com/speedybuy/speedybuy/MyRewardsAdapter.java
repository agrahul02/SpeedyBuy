package com.speedybuy.speedybuy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.ViewHolder>{

    private final List<RewardModel> rewardModelList;
    private Boolean useMiniLayout = false;
    private RecyclerView coupenRecyclerView;
    private LinearLayout selectedCoupen;
    private String productOriginalPrice;
    private TextView selectedcoupenTitle;
    private TextView selectedcoupenExpiryDate;
    private TextView selectedcoupenBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;
    private List<CartItemModel> cartItemModelList;

    public MyRewardsAdapter(List<RewardModel> rewardModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }
    public MyRewardsAdapter(List<RewardModel> rewardModelList, boolean useMiniLayout, RecyclerView coupenRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView coupenTitle, TextView coupenExpiryDate, TextView coupenBody, TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.coupenRecyclerView = coupenRecyclerView;
        this.selectedCoupen = selectedCoupen;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedcoupenTitle = coupenTitle;
        this.selectedcoupenExpiryDate = coupenExpiryDate;
        this.selectedcoupenBody = coupenBody;
        this.discountedPrice = discountedPrice;

    }
    public MyRewardsAdapter(int cartItemPosition,List<RewardModel> rewardModelList, boolean useMiniLayout, RecyclerView coupenRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView coupenTitle, TextView coupenExpiryDate, TextView coupenBody, TextView discountedPrice,List<CartItemModel>cartItemModelList) {

        this.cartItemPosition = cartItemPosition;
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.coupenRecyclerView = coupenRecyclerView;
        this.selectedCoupen = selectedCoupen;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedcoupenTitle = coupenTitle;
        this.selectedcoupenExpiryDate = coupenExpiryDate;
        this.selectedcoupenBody = coupenBody;
        this.discountedPrice = discountedPrice;
        this.cartItemModelList = cartItemModelList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;
       if (useMiniLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
       }else {
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, true);
       }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String coupenId = rewardModelList.get(position).getCoupenId();
        String type = rewardModelList.get(position).getType();
        Date validity = rewardModelList.get(position).getTimestamp();
        String body = rewardModelList.get(position).getCoupenBody();
        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discORamt = rewardModelList.get(position).getDiscORamt();
        Boolean alreadyUsed = rewardModelList.get(position).getAlreadyUsed();
     //   holder.setData(coupenId,type,validity,body,upperLimit,lowerLimit,discORamt,alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       /* private final TextView coupenTitle;
        private final TextView coupenExpiryDate;
        private final TextView coupenBody;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            coupenTitle = itemView.findViewById(R.id.coupen_title);
       //     coupenExpiryDate = itemView.findViewById(R.id.coupen_validity_reward);
          //  coupenBody = itemView.findViewById(R.id.coupen_body_reward);
        }
       /* private void setData(final String coupenId,final String type, final Date validity,final String body, String upperLimit, String lowerLimit,String discORamt,boolean alreadyUsed){
          if (type.equals("Discount")){
              coupenTitle.setText(type);
          }else {
              coupenTitle.setText("FLAT Rs."+discORamt+"OFF");
          }
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
          if (alreadyUsed){
             coupenExpiryDate.setText("Already Used");
             coupenExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.teal_200));
              coupenBody.setTextColor(Color.parseColor("#50ffffff"));
              coupenTitle.setTextColor(Color.parseColor("#50ffffff"));
          }else {
              coupenBody.setTextColor(Color.parseColor("#ffffff"));
              coupenTitle.setTextColor(Color.parseColor("#ffffff"));
              coupenExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.coupenPurpul));

              coupenExpiryDate.setText("till "+simpleDateFormat.format(validity));

          }
          coupenBody.setText(body);

          if (useMiniLayout){
              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      if (!alreadyUsed) {
                          selectedcoupenTitle.setText(type);
                          selectedcoupenExpiryDate.setText(simpleDateFormat.format(validity));
                          selectedcoupenBody.setText(body);

                          if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                              if (type.equals("Discount")) {
                                  Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discORamt) / 100;
                                  discountedPrice.setText("-" + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "-");
                              } else {
                                  discountedPrice.setText("-" + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discORamt)) + "-");
                              }
                              if (cartItemPosition != -1) {
                                  cartItemModelList.get(cartItemPosition).setSelectedCoupenId(coupenId);
                              }
                          } else {
                              if (cartItemPosition != -1) {
                                  cartItemModelList.get(cartItemPosition).setSelectedCoupenId(null);
                              }
                              discountedPrice.setText("Invalid");
                              Toast.makeText(itemView.getContext(), "Product does not match", Toast.LENGTH_SHORT).show();
                          }

                          if (coupenRecyclerView.getVisibility() == View.GONE) {
                              coupenRecyclerView.setVisibility(View.VISIBLE);
                              selectedCoupen.setVisibility(View.GONE);
                          } else {
                              coupenRecyclerView.setVisibility(View.GONE);
                              selectedCoupen.setVisibility(View.VISIBLE);
                          }
                      }
                  }
              });
          }

        }*/
    }
}
