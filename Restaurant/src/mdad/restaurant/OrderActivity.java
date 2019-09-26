package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mdad.restaurant.menu.MenuOrderItem;
import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;
import mdad.restaurant.sqlite.SQLiteHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class OrderActivity extends Activity {

	private Button btnConfirm;
	private ListView lvOrderList;
	private List<mdad.restaurant.menu.MenuItem> menuItems = new ArrayList<mdad.restaurant.menu.MenuItem>();
	private List<MenuOrderItem> menuOrderItems = new ArrayList<MenuOrderItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		
		lvOrderList = (ListView) findViewById(R.id.lvOrderList);
		btnConfirm = (Button) findViewById(R.id.btnOrderConfirm);
		btnConfirm.setEnabled(false);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuList");
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public void onSuccess(List<String> result) throws Exception {
				for (int i = 1; i < result.size(); i += 7) {
					int id = Integer.parseInt(result.get(i));
					String name = result.get(i + 1);
					int categoryID = Integer.parseInt(result.get(i + 2));
					String category = result.get(i + 3);
					double price = Double.parseDouble(result.get(i + 4));
					String image = result.get(i + 5);
					Boolean isActive = result.get(i + 6).contentEquals("1") ? true : false;
					
					menuItems.add(new mdad.restaurant.menu.MenuItem(id, name, categoryID, category, price, image.contentEquals("NULL") ? null : image, isActive));
				}
				
				List<String> items = new ArrayList<String>();
				Cursor cursor = SQLiteHelper.getOrderItem();
				while(cursor.moveToNext()) {
					String name;
					int qty;
					
					for(mdad.restaurant.menu.MenuItem item : menuItems) {
						if(item.getId() == cursor.getInt(1)) {
							name = item.getName();
							qty = cursor.getInt(2);
							items.add(name + " x " + String.valueOf(qty));
							menuOrderItems.add(new MenuOrderItem(item, qty, cursor.getString(3)));
						}
					}
				}
				
				if (menuOrderItems.size() > 0) {
					btnConfirm.setEnabled(true);
					btnConfirm.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) {
							Intent msg = new Intent(OrderActivity.this, BillActivity.class);
							startActivity(msg);
						}	
					});
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderActivity.this, android.R.layout.simple_list_item_1, items);
				lvOrderList.setAdapter(adapter);
				lvOrderList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent intent = new Intent(OrderActivity.this, MenuSelectionActivity.class);
						intent.putExtra("index", 1);
						intent.putExtra("menuID", menuOrderItems.get(position).getMenuItem().getId());
						intent.putExtra("menuQuantity", menuOrderItems.get(position).getQuantity());
						intent.putExtra("menuAdditionalRequest", menuOrderItems.get(position).getAdditionalRequest());
						intent.putExtra("name", menuOrderItems.get(position).getMenuItem().getName());
						intent.putExtra("image", menuOrderItems.get(position).getMenuItem().getImageRaw());
						intent.putExtra("price", menuOrderItems.get(position).getMenuItem().getPrice());
						startActivity(intent);
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
}
