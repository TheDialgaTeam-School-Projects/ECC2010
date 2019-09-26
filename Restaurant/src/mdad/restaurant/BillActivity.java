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
import modules.system.StringHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BillActivity extends Activity {

	private Button btnOk;
	private ListView lvViewList;
	private TextView tvBillGst, tvBillTotalPrice, tvBillSubTotal;
	
	private List<mdad.restaurant.menu.MenuItem> menuItems = new ArrayList<mdad.restaurant.menu.MenuItem>();
	private List<MenuOrderItem> menuOrderItems = new ArrayList<MenuOrderItem>();
	
	private double totalPrice = 0;
	private int submitIndex = 0;
	private long submitDateTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill);
		
		lvViewList = (ListView) findViewById(R.id.lvBillList);
		tvBillSubTotal = (TextView) findViewById(R.id.tvBillSubTotal);
		tvBillGst = (TextView) findViewById(R.id.tvBillGst);
		tvBillTotalPrice = (TextView) findViewById(R.id.tvBillTotalPrice);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setEnabled(false);
		
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
							items.add(name + " x " + String.valueOf(qty) + " = " + String.format("$%.2f", item.getPrice() * qty));
							menuOrderItems.add(new MenuOrderItem(item, qty, cursor.getString(3)));
							totalPrice += item.getPrice() * qty;
						}
					}
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(BillActivity.this, android.R.layout.simple_list_item_1, items);
				lvViewList.setAdapter(adapter);
				
				tvBillSubTotal.setText(String.format("$%.2f", totalPrice));
				tvBillGst.setText(String.format("$%.2f", totalPrice * 0.07));
				tvBillTotalPrice.setText(String.format("$%.2f", totalPrice + (totalPrice * 0.07)));
				
				btnOk.setEnabled(true);
				btnOk.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						submitOrder();
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bill, menu);
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
	
	private void submitOrder() {
		SharedPreferences preferences = getSharedPreferences("Account", 0);
		final int accountID = preferences.getInt("AccountID", 0);
		final int menuID = menuOrderItems.get(submitIndex).getMenuItem().getId();
		final int quantity = menuOrderItems.get(submitIndex).getQuantity();
		final String additionalRequest = menuOrderItems.get(submitIndex).getAdditionalRequest();
		
		if (submitDateTime == 0)
			submitDateTime = System.currentTimeMillis() / 1000L;
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "setOrder");
				postData.put("accountID", String.valueOf(accountID));
				postData.put("menuID", String.valueOf(menuID));
				postData.put("quantity", String.valueOf(quantity));
				postData.put("additionalRequest", StringHelper.isNullOrEmpty(additionalRequest) ? "NULL" : Base64.encodeToString(additionalRequest.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE));
				postData.put("dateTime", String.valueOf(submitDateTime));
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public void onSuccess(List<String> result) throws Exception {
				submitIndex++;
				
				if (submitIndex >= menuOrderItems.size()) {
					SQLiteHelper.resetOrder();
					startActivity(new Intent(BillActivity.this, MainActivity.class));
				} else
					submitOrder();
			}
		});
	}
}
