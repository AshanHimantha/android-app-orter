package lk.jiat.orterclothing.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lk.jiat.orterclothing.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AdressViewholder> {
    private Context context;
    private List<UserAddress> addressList;

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
        holder.addressLine1.setText(address.getAddressLine1());
        holder.addressLine2.setText(address.getAddressLine2());

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AdressViewholder extends RecyclerView.ViewHolder{

        private TextView addressName;
        private TextView addressLine1;
        private TextView addressLine2;

        public AdressViewholder(@NonNull View itemView) {
            super(itemView);
            addressName = itemView.findViewById(R.id.textView45);
            addressLine1 = itemView.findViewById(R.id.textView46);
            addressLine2 = itemView.findViewById(R.id.textView47);
        }


    }

}


