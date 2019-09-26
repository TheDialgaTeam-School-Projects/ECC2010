package mdad.restaurant.menu;

public final class MenuOrderItem {
	private final mdad.restaurant.menu.MenuItem menuItem;
	private final int quantity;
	private final String additionalRequest;
	
	public MenuOrderItem(mdad.restaurant.menu.MenuItem menuItem, int quantity, String additionalRequest) {
		this.menuItem = menuItem;
		this.quantity = quantity;
		this.additionalRequest = additionalRequest;
	}
	
	public final mdad.restaurant.menu.MenuItem getMenuItem() {
		return menuItem;
	}
	
	public final int getQuantity() {
		return quantity;
	}

	public final String getAdditionalRequest() {
		return additionalRequest;
	}
}
