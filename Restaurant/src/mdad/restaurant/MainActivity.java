package mdad.restaurant;

import mdad.restaurant.sqlite.SQLiteHelper;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public final class MainActivity extends ActivityGroup {
	private int index;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
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
		final Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			index = bundle.getInt("index");
		}
		
		SQLiteHelper.initDatabase();
		initThMain();
	}
	
	private final void initThMain() {
		final TabHost thMain = (TabHost) findViewById(R.id.thMain);
		thMain.setup(getLocalActivityManager());
		thMain.addTab(thMain.newTabSpec("menu").setIndicator("Our Menu", null).setContent(new Intent(MainActivity.this, MenuActivity.class)));
		thMain.addTab(thMain.newTabSpec("order").setIndicator("Current Order", null).setContent(new Intent(MainActivity.this, OrderActivity.class)));
		thMain.addTab(thMain.newTabSpec("account").setIndicator("Account", null).setContent(new Intent(MainActivity.this, AccountActivity.class)));
		thMain.setCurrentTab(index);
	}
}
