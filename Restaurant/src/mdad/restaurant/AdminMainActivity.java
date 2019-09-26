package mdad.restaurant;

import java.util.ArrayList;
import java.util.List;

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

public final class AdminMainActivity extends Activity {
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_main);
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
		initLvAdminMainMenu();
	}

	private final void initLvAdminMainMenu() {
		final ListView lvAdminMainMenu = (ListView) findViewById(R.id.lvAdminMainMenu);

		final List<String> item = new ArrayList<String>();
		item.add("Manage Menu Category");
		item.add("Manage Menu List");
		item.add("Exit Admin Menu");

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
		lvAdminMainMenu.setAdapter(adapter);
		lvAdminMainMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0)
					startActivity(new Intent(AdminMainActivity.this, AdminCategoryActivity.class));
				else if (position == 1)
					startActivity(new Intent(AdminMainActivity.this, AdminMenuActivity.class));
				else if (position == 2) {
					final Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
					intent.putExtra("index", 2);
					startActivity(intent);
				}
			}
		});
	}
}
