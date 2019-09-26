package mdad.restaurant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class AccountActivity extends Activity {
	private Boolean isAdmin;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account, menu);
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
		final SharedPreferences sp = getSharedPreferences("Account", 0);
		isAdmin = sp.getBoolean("IsAdmin", false);
		
		initLvAccountList();
	}
	
	private final void initLvAccountList() {
		final ListView lvAccountList = (ListView) findViewById(R.id.lvAccountList);
		final List<String> options = new ArrayList<String>();		
		//options.add("View Order History");
		if (isAdmin) options.add("Admin Manager Menu");
		options.add("Logout");
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		lvAccountList.setAdapter(adapter);
		lvAccountList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && isAdmin)
					startActivity(new Intent(AccountActivity.this, AdminMainActivity.class));
				else if (position == 0 && !isAdmin)
					startActivity(new Intent(AccountActivity.this, LoginActivity.class));
				else if (position == 1)
					startActivity(new Intent(AccountActivity.this, LoginActivity.class));
			}
		});
	}
}