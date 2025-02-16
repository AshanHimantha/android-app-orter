// AddressAdapter.java
package lk.jiat.orterclothing.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lk.jiat.orterclothing.CheckoutActivity;
import lk.jiat.orterclothing.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AdressViewholder> {
    private Context context;
    private List<UserAddress> addressList;
    private int selectedPosition = -1; // Variable to keep track of the selected position

    public AddressAdapter(Context context, List<UserAddress> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AdressViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new AdressViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdressViewholder holder, int position) {
        UserAddress address = addressList.get(position);
        holder.addressName.setText(address.getAddressName());
        holder.owner.setText(address.getOwner());
        holder.addressLine1.setText(address.getAddressLine1());
        holder.addressLine2.setText(address.getAddressLine2());
        holder.contact.setText(address.getContact());

        // Update the background based on the selected position
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.blackborder);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.light_border);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the selected position and notify the adapter
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (context instanceof CheckoutActivity) {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Address");
                builder.setMessage("Are you sure you want to delete this address?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    addressList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, addressList.size());

                    SQLiteDatabase db = context.openOrCreateDatabase("address.db", Context.MODE_PRIVATE, null);
                    db.delete("addresses", "display_name = ? AND owner_name = ? AND address1 = ? AND address2 = ?", new String[]{address.getAddressName(), address.getOwner(), address.getAddressLine1(), address.getAddressLine2()});
                    db.close();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AdressViewholder extends RecyclerView.ViewHolder {
        private TextView addressName;
        private TextView owner;
        private TextView addressLine1;
        private TextView addressLine2;
        private ImageView delete;
        private TextView contact;

        public AdressViewholder(@NonNull View itemView) {
            super(itemView);
            addressName = itemView.findViewById(R.id.textView45);
            owner = itemView.findViewById(R.id.textView44);
            addressLine1 = itemView.findViewById(R.id.textView46);
            addressLine2 = itemView.findViewById(R.id.textView47);
            contact = itemView.findViewById(R.id.textView56);
            delete = itemView.findViewById(R.id.imageView12);
        }
    }
}