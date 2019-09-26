package mdad.restaurant.menu;

import java.util.List;

import mdad.restaurant.MenuSelectionActivity;
import mdad.restaurant.R;
import mdad.restaurant.sqlite.SQLiteHelper;

import modules.system.StringHelper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class MenuListAdapter extends ArrayAdapter<MenuItem> {
	public MenuListAdapter(Context context, int resource, List<MenuItem> objects) {
		super(context, resource, objects);
	}
	
	@Override
	public final View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		final MenuItem item = getItem(position);
		
		if (convertView == null)
			view = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_list, null);
		else
			view = convertView;
		
		if (item != null) {
			final TextView tvItemMenuListName = (TextView) view.findViewById(R.id.tvItemMenuListName);
			final TextView tvItemMenuListCategory = (TextView) view.findViewById(R.id.tvItemMenuListCategory);
			final TextView tvItemMenuListPrice = (TextView) view.findViewById(R.id.tvItemMenuListPrice);
			final ImageView ivItemMenuListPicture = (ImageView) view.findViewById(R.id.ivItemMenuListPicture);
			final Button btnItemMenuListAddToCart = (Button) view.findViewById(R.id.btnItemMenuListAddToCart);
			
			tvItemMenuListName.setText(StringHelper.isNullOrEmpty(item.getName()) ? "Unknown Menu" : item.getName());
			tvItemMenuListCategory.setText(StringHelper.isNullOrEmpty(item.getCategory()) ? "Unknown Category" : item.getCategory());
			tvItemMenuListPrice.setText(String.format("$%.2f", item.getPrice()));
			
			if (item.getImage() == null)
				ivItemMenuListPicture.setImageResource(R.drawable.unknown);
			else
				ivItemMenuListPicture.setImageBitmap(item.getImage());
			
			btnItemMenuListAddToCart.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(View v) {
					final int menuID = item.getId();
					final Intent intent = new Intent(v.getContext(), MenuSelectionActivity.class);
					intent.putExtra("index", 0);
					intent.putExtra("menuID", menuID);
					intent.putExtra("menuQuantity", SQLiteHelper.getMenuQuantity(menuID));
					intent.putExtra("menuAdditionalRequest", SQLiteHelper.getMenuAdditionalRequest(menuID));
					intent.putExtra("name", item.getName());
					intent.putExtra("image", item.getImageRaw());
					intent.putExtra("price", item.getPrice());
					v.getContext().startActivity(intent);
				}
			});
		}
		
		return view;
	}
}
