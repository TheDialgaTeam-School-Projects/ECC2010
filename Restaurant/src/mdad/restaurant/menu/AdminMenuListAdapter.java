package mdad.restaurant.menu;

import java.util.List;

import mdad.restaurant.AdminMenuEditorActivity;
import mdad.restaurant.R;

import modules.system.StringHelper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class AdminMenuListAdapter extends ArrayAdapter<MenuItem> {
	public AdminMenuListAdapter(Context context, int resource, List<MenuItem> objects) {
		super(context, resource, objects);
	}
	
	@Override
	public final View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		final MenuItem item = getItem(position);
		
		if (convertView == null)
			view = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_menu_list, null);
		else
			view = convertView;
		
		if (item != null) {
			final TextView tvItemAdminMenuListName = (TextView) view.findViewById(R.id.tvItemAdminMenuListName);
			final TextView tvItemAdminMenuListCategory = (TextView) view.findViewById(R.id.tvItemAdminMenuListCategory);
			final TextView tvItemAdminMenuListPrice = (TextView) view.findViewById(R.id.tvItemAdminMenuListPrice);
			final ImageView ivItemAdminMenuListPicture = (ImageView) view.findViewById(R.id.ivItemAdminMenuListPicture);
			final Button btnItemAdminMenuListEdit = (Button) view.findViewById(R.id.btnItemAdminMenuListEdit);
			
			tvItemAdminMenuListName.setText(StringHelper.isNullOrEmpty(item.getName()) ? "Add New Menu" : item.getName());
			tvItemAdminMenuListCategory.setText(StringHelper.isNullOrEmpty(item.getCategory()) ? "Unknown Category" : item.getCategory());
			tvItemAdminMenuListPrice.setText(String.format("$%.2f", item.getPrice()));
			btnItemAdminMenuListEdit.setText(StringHelper.isNullOrEmpty(item.getName()) ? "Add" : "Edit");
			
			if (item.getImage() == null)
				ivItemAdminMenuListPicture.setImageResource(R.drawable.unknown);
			else
				ivItemAdminMenuListPicture.setImageBitmap(item.getImage());
			
			btnItemAdminMenuListEdit.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(View v) {
					if (position == 0) {
						final Intent intent = new Intent(v.getContext(), AdminMenuEditorActivity.class);
						intent.putExtra("id", 0);
						intent.putExtra("name", "");
						intent.putExtra("categoryID", 0);
						intent.putExtra("price", 0.0);
						intent.putExtra("image", "");
						intent.putExtra("isActive", true);
						v.getContext().startActivity(intent);
					} else {
						final Intent intent = new Intent(v.getContext(), AdminMenuEditorActivity.class);
						intent.putExtra("id", item.getId());
						intent.putExtra("name", item.getName());
						intent.putExtra("categoryID", item.getCategoryID());
						intent.putExtra("price", item.getPrice());
						intent.putExtra("image", item.getImageRaw());
						intent.putExtra("isActive", item.getIsActive());
						v.getContext().startActivity(intent);
					}
				}
			});
		}
		
		return view;
	}
}
