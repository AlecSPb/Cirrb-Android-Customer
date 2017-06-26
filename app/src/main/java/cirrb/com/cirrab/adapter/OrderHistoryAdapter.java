package cirrb.com.cirrab.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cirrb.com.cirrab.R;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.RestaurentHolder> {

    ArrayList<JSONObject> arrayList;
    Context context;
    View.OnClickListener clickListener;
    public OrderHistoryAdapter(Context context, ArrayList<JSONObject> lstData, View.OnClickListener clickListener) {
        this.context = context;
        this.arrayList = lstData;
        this.clickListener = clickListener;
    }

    @Override
    public RestaurentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history, parent, false);
        RestaurentHolder imh = new RestaurentHolder(v);
        return imh;
    }

    @Override
    public void onBindViewHolder(final RestaurentHolder viewHolder, final int position) {
        try {
            viewHolder.txtOrderId.setText("#"+arrayList.get(position).getString("id"));
            viewHolder.txtOrderStatus.setText(""+arrayList.get(position).getString("status"));
            viewHolder.txtOrderAmt.setText("SR: "+arrayList.get(position).getString("total"));

            viewHolder.txtOrderId.setTag(String.valueOf(position));
            viewHolder.txtOrderStatus.setTag(String.valueOf(position));
            viewHolder.txtOrderAmt.setTag(String.valueOf(position));

            viewHolder.txtOrderId.setOnClickListener(clickListener);
            viewHolder.txtOrderStatus.setOnClickListener(clickListener);
            viewHolder.txtOrderAmt.setOnClickListener(clickListener);
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

        TextView txtOrderId,txtOrderStatus,txtOrderAmt;


        RestaurentHolder(View itemView) {
            super(itemView);
            txtOrderId = (TextView) itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = (TextView) itemView.findViewById(R.id.txtOrderStatus);
            txtOrderAmt = (TextView) itemView.findViewById(R.id.txtOrderAmt);

        }
    }
}
