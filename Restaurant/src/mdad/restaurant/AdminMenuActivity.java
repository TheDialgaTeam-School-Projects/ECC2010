package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mdad.restaurant.menu.AdminMenuListAdapter;
import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

public final class AdminMenuActivity extends Activity {
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_menu);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu, menu);
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
		initLvAdminMenuList();
	}
	
	private final void initLvAdminMenuList() {
		final ListView lvAdminMenuList = (ListView) findViewById(R.id.lvAdminMenuList);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuList");
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public final void onSuccess(List<String> result) throws Exception {
				final List<mdad.restaurant.menu.MenuItem> menuItems = new ArrayList<mdad.restaurant.menu.MenuItem>();
				menuItems.add(new mdad.restaurant.menu.MenuItem(0, null, 0, null, 0, null, true));
				
				for (int i = 1; i < result.size(); i += 7) {
					final int id = Integer.parseInt(result.get(i));
					final String name = result.get(i + 1);
					final int categoryID = Integer.parseInt(result.get(i + 2));
					final String category = result.get(i + 3);
					final double price = Double.parseDouble(result.get(i + 4));
					final String image = result.get(i + 5);
					final Boolean isActive = result.get(i + 6).contentEquals("1") ? true : false;
					
					menuItems.add(new mdad.restaurant.menu.MenuItem(id, name, categoryID, category, price, image.contentEquals("NULL") ? null : image, isActive));
				}
				
				final ListAdapter adapter = new AdminMenuListAdapter(AdminMenuActivity.this, R.layout.item_admin_menu_list, menuItems);
				lvAdminMenuList.setAdapter(adapter);
			}
		});
	}
}
