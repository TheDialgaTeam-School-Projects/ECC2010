package mdad.restaurant.menu;

import modules.android.graphics.BitmapHelper;
import modules.system.StringHelper;

import android.graphics.Bitmap;

public final class MenuItem {
	private final int id;
	private final String name;
	private final int categoryID;
	private final String category;
	private final double price;
	private final Bitmap image;
	private final Boolean isActive;
	
	public MenuItem(int id, String name, int categoryID, String category, double price, String image, Boolean isActive) {
		this.id = id;
		this.name = name;
		this.categoryID = categoryID;
		this.category = category;
		this.price = price;
		this.image = StringHelper.isNullOrEmpty(image) ? null : BitmapHelper.convert(image);
		this.isActive = isActive;
	}
	
	public final int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final int getCategoryID() {
		return categoryID;
	}

	public final String getCategory() {
		return category;
	}

	public final double getPrice() {
		return price;
	}

	public final Bitmap getImage() {
		return image;
	}
	
	public final String getImageRaw() {
		return BitmapHelper.convert(getImage());
	}

	public final Boolean getIsActive() {
		return isActive;
	}
}
