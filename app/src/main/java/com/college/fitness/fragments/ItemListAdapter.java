package com.college.fitness.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.college.fitness.R;
import com.college.fitness.databinding.ItemRowBinding;
import com.college.fitness.model.ListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private List<ListItem> itemList;
    private List<ListItem> itemListFiltered;


    public ItemListAdapter(Context context, List<ListItem> itemList) {
        mContext = context;
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemRowBinding itemBinding = ItemRowBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListItem item = itemListFiltered.get(position);
        holder.binding.name.setText(item.getName());

        try {
            String path = item.getImage();
            if (null != path) {
                if (path.isEmpty()) {
                    Picasso.get().load(R.drawable.image_1).into(holder.binding.imgItem);
                } else {
                    int id = mContext.getResources().getIdentifier("com.college.termproject:drawable/" + path, null, null);
                    holder.binding.imgItem.setImageResource(id);
                }
            } else
                Picasso.get().load(R.drawable.image_1).into(holder.binding.imgItem);
        } catch (Exception e) {
            Log.e("TAG", "onItemViewBindViewHolder: " + e.getMessage());
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (item.isFavorite())
                    Toast.makeText(mContext, "Item already in Favorites.", Toast.LENGTH_LONG).show();
                else
                    addItemToFav(item.getId(), item);
                return false;
            }
        });

    }

    public void addItemToFav(long id, ListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Add to Favorite");

        builder.setMessage("You want to add this item to favorites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        item.setFavorite(true);
                        realm.insertOrUpdate(item);
                        Toast.makeText(mContext, "Added to Favorite", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void setData(List<ListItem> newData) {
        if (itemListFiltered != null) {
            itemListFiltered.clear();
            itemListFiltered.addAll(newData);
        } else {
            // first initialization
            itemListFiltered = newData;
        }
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<ListItem> filteredList = new ArrayList<>();
                    for (ListItem row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFiltered = (ArrayList<ListItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ItemRowBinding binding;

        public MyViewHolder(ItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            /*binding.getRoot().setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onVisitorSelected(itemListFiltered.get(getAdapterPosition()));
            });*/
        }
    }

   /* public interface VisitorAdapterListener {
        void onVisitorSelected(ItemAttendee contact);
    }*/
}
