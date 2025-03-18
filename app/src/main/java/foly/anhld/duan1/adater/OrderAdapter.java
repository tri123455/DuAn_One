package foly.anhld.duan1.adater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import foly.anhld.duan1.Modol.Order;
import foly.anhld.duan1.OrderDetailActivityUser;
import foly.anhld.duan1.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("Mã đơn hàng: " + order.getOrderId());
        holder.orderDateTextView.setText("Ngày đặt: " + order.getOrderDate());
        holder.statusTextView.setText("Trạng thái: " + order.getStatus());
        holder.totalPriceTextView.setText("Tổng tiền: " + order.getTotalPrice() + " VND");
        holder.txtOder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy context từ view
                Context context = v.getContext();

                // Tạo Intent và truyền dữ liệu qua putExtra
                Intent intent = new Intent(context, OrderDetailActivityUser.class);
                intent.putExtra("ORDER_ID", order.getOrderId()); // Gửi mã đơn hàng
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderDateTextView;
        TextView statusTextView;
        TextView totalPriceTextView;
        TextView txtOder;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tvOrderId);
            orderDateTextView = itemView.findViewById(R.id.tvOrderDate);
            statusTextView = itemView.findViewById(R.id.tvStatus);
            totalPriceTextView = itemView.findViewById(R.id.tvTotalPrice);
            txtOder = itemView.findViewById(R.id.txtOder);
        }
    }
}
