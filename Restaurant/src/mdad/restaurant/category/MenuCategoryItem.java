package mdad.restaurant.category;

public final class MenuCategoryItem {
	private final int id;
	private final String name;
	private final Boolean isActive;
	
	public MenuCategoryItem(int id, String name, Boolean isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}

	public final int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final Boolean getIsActive() {
		return isActive;
	}
}
