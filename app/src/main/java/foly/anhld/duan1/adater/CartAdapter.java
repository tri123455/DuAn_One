package foly.anhld.duan1.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import foly.anhld.duan1.Modol.CartItem;
import foly.anhld.duan1.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final CartItemRemoveListener listener;
    private final CartItemQuantityChangeListener quantityChangeListener; // Listener để thay đổi số lượng
    private final TextView totalPriceTextView; // Reference to the total price TextView

    public interface CartItemRemoveListener {
        void onCartItemRemove(CartItem cartItem);
    }

    public interface CartItemQuantityChangeListener {
        void onCartItemQuantityChanged(CartItem cartItem, int newQuantity);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemRemoveListener listener,
                       CartItemQuantityChangeListener quantityChangeListener, TextView totalPriceTextView) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.quantityChangeListener = quantityChangeListener;
        this.totalPriceTextView = totalPriceTextView; // Pass the reference of the total price TextView
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Method to calculate and update the total price
    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        totalPriceTextView.setText("Tổng tiền: " + totalPrice + " VND");
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        private final TextView productName;
        private final TextView productPrice;
        private final TextView productQuantity;
        private final TextView productSize;
        private final TextView productId;
        private final ImageButton removeButton;
        private final ImageButton btnIncrease;
        private final ImageButton btnDecrease;
        private final ImageView productImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvPrice);
            productQuantity = itemView.findViewById(R.id.tvQuantity);
            productSize = itemView.findViewById(R.id.tvSize);
            productId = itemView.findViewById(R.id.tvProductid);
            removeButton = itemView.findViewById(R.id.btnRemove);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            productImage = itemView.findViewById(R.id.ivproductImage);
        }

        public void bind(CartItem cartItem) {
            productName.setText(cartItem.getProductName());
            productPrice.setText("Giá: " + cartItem.getProductPrice() + " VND");
            productQuantity.setText("" + cartItem.getQuantity());
            productSize.setText("Size: " + cartItem.getSize());
            productId.setText("Product ID: " + cartItem.getProductId());
            Picasso.get()
                    .load(cartItem.getProductImage())  // Assuming CartItem has a getProductImageUrl() method
                    .placeholder(R.drawable.ic_placeholder) // Placeholder image while loading
                    .error(R.drawable.baseline_email_24) // Error image if loading fails
                    .into(productImage);
            // Update total price whenever an item is added/removed/quantity changes
            updateTotalPrice();

            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCartItemRemove(cartItem);
                    cartItems.remove(cartItem); // Remove item from the cart
                    notifyDataSetChanged(); // Update the RecyclerView
                    updateTotalPrice(); // Update the total price after removing item
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int newQuantity = cartItem.getQuantity() + 1;
                cartItem.setQuantity(newQuantity);
                productQuantity.setText("" + newQuantity);
                if (quantityChangeListener != null) {
                    quantityChangeListener.onCartItemQuantityChanged(cartItem, newQuantity);
                }
                updateTotalPrice(); // Update total price after quantity change
            });

            btnDecrease.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    cartItem.setQuantity(newQuantity);
                    productQuantity.setText("" + newQuantity);
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onCartItemQuantityChanged(cartItem, newQuantity);
                    }
                    updateTotalPrice(); // Update total price after quantity change
                }
            });
        }
    }
}


