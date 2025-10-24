package com.example.myfirstapp_jesusvelasquez.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfirstapp_jesusvelasquez.R;
import com.example.myfirstapp_jesusvelasquez.data.Item;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {
    public interface OnItemClick { void onClick(Item it); }
    private final List<Item> data; private final OnItemClick click;
    public ItemAdapter(List<Item> data, OnItemClick click){ this.data=data; this.click=click; }

    static class VH extends RecyclerView.ViewHolder{
        TextView name, sku, qty, loc;
        VH(View v){ super(v);
            name=v.findViewById(R.id.tvName);
            sku=v.findViewById(R.id.tvSku);
            qty=v.findViewById(R.id.tvQty);
            loc=v.findViewById(R.id.tvLocation);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){
        // Use your row file name: item_inventory_row.xml (from your screenshot)
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_inventory_row, p, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH h,int pos){
        Item it = data.get(pos);
        h.name.setText(it.name);
        h.sku.setText("SKU: " + (it.sku==null?"—":it.sku));
        h.qty.setText("Qty: " + it.qty);
        h.loc.setText("Loc: " + (it.location==null?"—":it.location));
        h.itemView.setOnClickListener(v -> click.onClick(it));
    }
    @Override public int getItemCount(){ return data.size(); }
}

