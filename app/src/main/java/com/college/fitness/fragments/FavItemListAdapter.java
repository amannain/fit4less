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

public class FavItemListAdapter extends RecyclerView.Adapter<FavItemListAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private List<ListItem> itemList;
    private List<ListItem> itemListFiltered;

    public FavItemListAdapter(Context context, List<ListItem> itemList) {
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
                if (item.isFavorite()) {
                    removeItemFromFav(item.getId(), item);
                } else {
                    Toast.makeText(mContext, "Item already removed from Favorites.", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

    }

    public void removeItemFromFav(long id, ListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Remove Favorite");

        builder.setMessage("Are you sure, you want to remove this item from favorites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        item.setFavorite(false);
                        realm.insertOrUpdate(item);
                        //item.setFavorite(false);
                        Toast.makeText(mContext, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                        itemList.remove(item);
                        itemListFiltered.remove(item);
                        notifyDataSetChanged();
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
