package cirrb.com.cirrab.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.R;


public class MenuItemListAdapter extends RecyclerView.Adapter<MenuItemListAdapter.RestaurentHolder> {

    ArrayList<JSONObject> arrayList;
    View.OnClickListener clickListener;
    Context context;
    int count = 0;


    public MenuItemListAdapter(Context context, ArrayList<JSONObject> arrayList, View.OnClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    @Override
    public RestaurentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_selection, parent, false);
        RestaurentHolder imh = new RestaurentHolder(v);
        return imh;
    }

    @Override
    public void onBindViewHolder(final RestaurentHolder viewHolder, final int position) {

        String name = null;
        try {
            name = arrayList.get(position).getString("name");
            int resId = arrayList.get(position).getInt("restaurant_id");
            String price = arrayList.get(position).getString("price");
            String image = arrayList.get(position).getString("image");
            int quanttiy = arrayList.get(position).getInt("quantity");

            viewHolder.tvfoodName.setText(name);
            viewHolder.tvfoodprice.setText(price);
            viewHolder.tvFoodCount.setText(""+quanttiy);


            if (image != null) {
                Picasso.with(context)
                        .load(image)
                        .into(viewHolder.foodImage);
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.tvFoodplus.setOnClickListener(clickListener);
        viewHolder.tvFoodplus.setTag(String.valueOf(position));

        viewHolder.tvFoodMinus.setOnClickListener(clickListener);
        viewHolder.tvFoodMinus.setTag(String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public static class RestaurentHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView tvfoodName, tvfoodprice, tvFoodplus, tvFoodMinus, tvFoodCount;


        RestaurentHolder(View itemView) {
            super(itemView);

            foodImage = (ImageView) itemView.findViewById(R.id.food_imv);
            tvfoodName = (TextView) itemView.findViewById(R.id.tv_food_name);
            tvfoodprice = (TextView) itemView.findViewById(R.id.tv_food_price);
            tvFoodplus = (TextView) itemView.findViewById(R.id.foodPlus);
            tvFoodCount = (TextView) itemView.findViewById(R.id.foodCount);
            tvFoodMinus = (TextView) itemView.findViewById(R.id.foodMinus);

        }
    }
}
