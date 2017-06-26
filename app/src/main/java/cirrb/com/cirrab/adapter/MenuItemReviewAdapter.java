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


public class MenuItemReviewAdapter extends RecyclerView.Adapter<MenuItemReviewAdapter.RestaurentHolder> {

    ArrayList<JSONObject> arrayList;
    Context context;

    public MenuItemReviewAdapter(Context context, ArrayList<JSONObject> lstData) {
        this.context = context;
        this.arrayList = lstData;
    }

    @Override
    public RestaurentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_review_item, parent, false);
        RestaurentHolder imh = new RestaurentHolder(v);
        return imh;
    }

    @Override
    public void onBindViewHolder(final RestaurentHolder viewHolder, final int position) {
        try {
            viewHolder.txtMenuItemTitle.setText(""+arrayList.get(position).getString("name"));
            viewHolder.txtMenuItemQuantity.setText(""+arrayList.get(position).getString("quantity"));
            viewHolder.txtMenuItemPrice.setText("SR: "+arrayList.get(position).getString("per_menu_cost"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public static class RestaurentHolder extends RecyclerView.ViewHolder {

        TextView txtMenuItemTitle,txtMenuItemQuantity,txtMenuItemPrice;


        RestaurentHolder(View itemView) {
            super(itemView);
            txtMenuItemTitle = (TextView) itemView.findViewById(R.id.txtMenuTitle);
            txtMenuItemQuantity = (TextView) itemView.findViewById(R.id.txtMenuQuantity);
            txtMenuItemPrice = (TextView) itemView.findViewById(R.id.txtMenuPrice);

        }
    }
}
