package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mdad.restaurant.category.MenuCategoryItem;
import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class AdminCategoryActivity extends Activity {
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_category);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_category_list, menu);
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
		initLvAdminCategoryMenu();
	}

	private final void initLvAdminCategoryMenu() {
		final ListView lvAdminCategoryMenu = (ListView) findViewById(R.id.lvAdminCategoryMenu);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuCategoryList");
				return NetworkHelper.doPostRequest(url, postData);
			}

			@Override
			public final void onSuccess(List<String> result) throws Exception {
				final List<MenuCategoryItem> menuCategories = new ArrayList<MenuCategoryItem>();
				final List<String> items = new ArrayList<String>();
				items.add("=== Add new category ===");
				
				for (int i = 1; i < result.size(); i += 3) {
					final int id = Integer.parseInt(result.get(i));
					final String name = result.get(i + 1);
					final Boolean isActive = result.get(i + 2).contentEquals("1") ? true : false;
					
					items.add(name + (isActive ? " [Active]" : " [Not Active]"));
					menuCategories.add(new MenuCategoryItem(id, name, isActive));
				}
				
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminCategoryActivity.this, android.R.layout.simple_list_item_1, items);
				lvAdminCategoryMenu.setAdapter(adapter);
				lvAdminCategoryMenu.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (position == 0) {
							Intent intent = new Intent(AdminCategoryActivity.this, AdminCategoryEditorActivity.class);
							intent.putExtra("id", 0);
							intent.putExtra("name", "");
							intent.putExtra("isActive", true);
							startActivity(intent);
						} else {
							Intent intent = new Intent(AdminCategoryActivity.this, AdminCategoryEditorActivity.class);
							intent.putExtra("id", menuCategories.get(position - 1).getId());
							intent.putExtra("name", menuCategories.get(position - 1).getName());
							intent.putExtra("isActive", menuCategories.get(position - 1).getIsActive());
							startActivity(intent);
						}
					}
				});
			}
		});
	}
}
