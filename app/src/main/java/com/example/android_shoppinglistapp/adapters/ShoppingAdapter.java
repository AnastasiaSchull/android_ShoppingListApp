package com.example.android_shoppinglistapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_shoppinglistapp.DetailActivity;
import com.example.android_shoppinglistapp.data.Shopping;
import com.example.android_shoppinglistapp.MainActivity;
import com.example.android_shoppinglistapp.data.DBHelper;
import com.example.android_shoppinglistapp.databinding.ItemShoppingBinding;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {
	private final MainActivity mainActivity;
	private final List<Shopping> list;

	public ShoppingAdapter(MainActivity mainActivity, List<Shopping> list) {
		this.mainActivity = mainActivity;
		this.list = list;
	}

	@NonNull
	@Override
	public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ItemShoppingBinding binding = ItemShoppingBinding.inflate(
				LayoutInflater.from(parent.getContext()),
				parent,
				false
		);
		return new ShoppingViewHolder(binding);
	}

	@SuppressLint("NewApi")
    @Override
	public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
		Shopping shopping = list.get(position);

		holder.binding.idItem.setText(String.valueOf(shopping.getId()));
		holder.binding.nameItem.setText(shopping.getName());

		holder.itemView.setOnClickListener(v -> {

		/*	//UPDATE
		    Intent intent = new Intent(mainActivity, ShoppingActivity.class);
			intent.putExtra(ShoppingActivity.SHOPPING, list.get(position));
			intent.putExtra(ShoppingActivity.MODE, ShoppingActivity.UPDATE);
			intent.putExtra(ShoppingActivity.POSITION, position);
			mainActivity.activityResultLauncher.launch(intent);*/

			// отправка SMS
			mainActivity.attemptToSendSMS("1234567890", "Purchased: " + shopping.getName());

            // запуск DetailActivity
			Intent intent = new Intent(mainActivity, DetailActivity.class);

			intent.putExtra("SHOPPING_ID", shopping.getId());
			mainActivity.startActivity(intent);
		});

		holder.itemView.setOnLongClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
			builder.setTitle("Delete");
			builder.setMessage("Are you sure you want to delete this item?");
			builder.setNegativeButton("Cancel", (dialog, which) -> {
			});
			builder.setPositiveButton("Delete", (dialog, which) -> {
				try (DBHelper helper = new DBHelper(mainActivity)) {
					helper.deleteById(shopping.getId());
				}
				list.remove(position);
				notifyItemRemoved(position);
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			return true;
		});

	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public void insertData(Shopping shopping) {
		list.add(shopping);
		notifyItemInserted(list.size() - 1);
	}

	public void updateData(int position, Shopping shopping) {
		list.set(position, shopping);
		notifyItemChanged(position);
	}

	public static class ShoppingViewHolder extends RecyclerView.ViewHolder {
		final ItemShoppingBinding binding;

		public ShoppingViewHolder(ItemShoppingBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
