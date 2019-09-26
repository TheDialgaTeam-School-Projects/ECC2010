package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mdad.restaurant.category.MenuCategoryItem;
import mdad.restaurant.menu.MenuListAdapter;
import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public final class MenuActivity extends Activity {
	private Spinner spMenuDropDownList;
	private ListView lvMenuList;
	
	private final List<MenuCategoryItem> menuCategoryItems = new ArrayList<MenuCategoryItem>();
	private final List<mdad.restaurant.menu.MenuItem> menuItems = new ArrayList<mdad.restaurant.menu.MenuItem>();
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return false;
	}

	@Override
	public final boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private final void init(Bundle savedInstanceState) {
		initSpMenuDropDownList();
	}
	
	private void initSpMenuDropDownList() {
		spMenuDropDownList = (Spinner) findViewById(R.id.spMenuDropDownList);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuCategoryList");
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public final void onSuccess(List<String> result) throws Exception {
				final List<String> items = new ArrayList<String>();
				
				for (int i = 1; i < result.size(); i += 3) {
					final int id = Integer.parseInt(result.get(i));
					final String name = result.get(i + 1);
					final Boolean isActive = result.get(i + 2).contentEquals("1") ? true : false;
					
					if (isActive) {
						items.add(name);
						menuCategoryItems.add(new MenuCategoryItem(id, name, isActive));
					}
				}
				
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuActivity.this, android.R.layout.simple_dropdown_item_1line, items);
				spMenuDropDownList.setAdapter(adapter);
				spMenuDropDownList.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						if (lvMenuList == null)
							initLvMenuList();
						else {
							final int categoryID = menuCategoryItems.get(position).getId();
							final List<mdad.restaurant.menu.MenuItem> items = new ArrayList<mdad.restaurant.menu.MenuItem>();
							
							for (mdad.restaurant.menu.MenuItem item : menuItems) {
								if (item.getCategoryID() == categoryID)
									items.add(item);
							}
							
							final ListAdapter adapter = new MenuListAdapter(MenuActivity.this, R.layout.item_menu_list, items);
							lvMenuList.setAdapter(adapter);
						}
					}

					@Override
					public final void onNothingSelected(AdapterView<?> parent) {
						
					}
				});
			}
		});
	}
	
	private final void initLvMenuList() {
		lvMenuList = (ListView) findViewById(R.id.lvMenuList);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuList");
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public final void onSuccess(List<String> result) throws Exception {
				for (int i = 1; i < result.size(); i += 7) {
					final int id = Integer.parseInt(result.get(i));
					final String name = result.get(i + 1);
					final int categoryID = Integer.parseInt(result.get(i + 2));
					final String category = result.get(i + 3);
					final double price = Double.parseDouble(result.get(i + 4));
					final String image = result.get(i + 5);
					final Boolean isActive = result.get(i + 6).contentEquals("1") ? true : false;
					
					if (isActive)
						menuItems.add(new mdad.restaurant.menu.MenuItem(id, name, categoryID, category, price, image.contentEquals("NULL") ? null : image, isActive));
				}
				
				final List<mdad.restaurant.menu.MenuItem> items = new ArrayList<mdad.restaurant.menu.MenuItem>();
				
				for (mdad.restaurant.menu.MenuItem item : menuItems) {
					if (item.getCategoryID() == menuCategoryItems.get(0).getId())
						items.add(item);
				}
				
				final ListAdapter adapter = new MenuListAdapter(MenuActivity.this, R.layout.item_menu_list, items);
				lvMenuList.setAdapter(adapter);
			}
		});
	}
}
