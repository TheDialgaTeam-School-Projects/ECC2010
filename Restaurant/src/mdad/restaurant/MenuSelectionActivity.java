package mdad.restaurant;

import java.util.ArrayList;
import java.util.List;

import mdad.restaurant.sqlite.SQLiteHelper;
import modules.android.graphics.BitmapHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public final class MenuSelectionActivity extends Activity {
	private int index;
	private int menuID;
	private int menuQuantity;
	private String menuAdditionalRequest;
	private String name;
	private String image;
	private double price;
	
	private Spinner spMenuSelectionQuantity;
	private TextView tvMenuSelectionTotalPrice;
	private TextView etMenuSelectionRequest;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_selection);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_selection, menu);
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
			menuID = bundle.getInt("menuID");
			menuQuantity = bundle.getInt("menuQuantity");
			menuAdditionalRequest = bundle.getString("menuAdditionalRequest");
			name = bundle.getString("name");
			image = bundle.getString("image");
			price = bundle.getDouble("price");
		} else {
			Toast.makeText(getApplicationContext(), "Unexpected error has occured. Please try again later.", Toast.LENGTH_LONG).show();
			startActivity(new Intent(MenuSelectionActivity.this, MainActivity.class));
		}
		
		initTvMenuSelectionTitle();
		initIvMenuSelectionImage();
		initTvMenuSelectionPrice();
		initTvMenuSelectionTotalPrice();
		initSpMenuSelectionQuantity();
		initEtMenuSelectionRequest();
		initBtnMenuSelectionAdd();
		initBtnMenuSelectionClear();
	}
	
	private final void initTvMenuSelectionTitle() {
		final TextView tvMenuSelectionTitle = (TextView) findViewById(R.id.tvMenuSelectionTitle);
		tvMenuSelectionTitle.setText(name);
	}
	
	private final void initIvMenuSelectionImage() {
		final ImageView ivMenuSelectionImage = (ImageView) findViewById(R.id.ivMenuSelectionImage);
		ivMenuSelectionImage.setImageBitmap(BitmapHelper.convert(image));
	}
	
	private final void initTvMenuSelectionPrice() {
		final TextView tvMenuSelectionPrice = (TextView) findViewById(R.id.tvMenuSelectionPrice);
		tvMenuSelectionPrice.setText(String.format("$%.2f", price));
	}
	
	private final void initSpMenuSelectionQuantity() {
		spMenuSelectionQuantity = (Spinner) findViewById(R.id.spMenuSelectionQuantity);
		
		final List<String> quantity = new ArrayList<String>();
		for (int i = 1; i <= 10; i++) {
			quantity.add(String.valueOf(i));
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuSelectionActivity.this, android.R.layout.simple_spinner_dropdown_item, quantity);
		spMenuSelectionQuantity.setAdapter(adapter);
		spMenuSelectionQuantity.setSelection(menuQuantity - 1);
		spMenuSelectionQuantity.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				menuQuantity = position + 1;
				tvMenuSelectionTotalPrice.setText(String.format("$%.2f", price * menuQuantity));
			}

			@Override
			public final void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private final void initTvMenuSelectionTotalPrice() {
		tvMenuSelectionTotalPrice = (TextView) findViewById(R.id.tvMenuSelectionTotalPrice);
	}
	
	private final void initEtMenuSelectionRequest() {
		etMenuSelectionRequest = (TextView) findViewById(R.id.etMenuSelectionRequest);
		etMenuSelectionRequest.setText(menuAdditionalRequest);
	}
	
	private final void initBtnMenuSelectionAdd() {
		final Button btnMenuSelectionAdd = (Button) findViewById(R.id.btnMenuSelectionAdd);
		btnMenuSelectionAdd.setOnClickListener(new OnClickListener() {
			@Override
			public final void onClick(View v) {
				final String additionalRequest = etMenuSelectionRequest.getText().toString();
				SQLiteHelper.setOrder(menuID, menuQuantity, additionalRequest);
				
				final Intent intent = new Intent(MenuSelectionActivity.this, MainActivity.class);
				intent.putExtra("index", index);
				startActivity(intent);
				Toast.makeText(MenuSelectionActivity.this, "Menu added/updated into the order list.", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private final void initBtnMenuSelectionClear() {
		final Button btnMenuSelectionClear = (Button) findViewById(R.id.btnMenuSelectionClear);
		
		if (SQLiteHelper.getMenuQuantity(menuID) == 0)
			btnMenuSelectionClear.setEnabled(false);
		else {
			btnMenuSelectionClear.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(View v) {
					SQLiteHelper.deleteOrder(menuID);
					
					final Intent intent = new Intent(MenuSelectionActivity.this, MainActivity.class);
					intent.putExtra("index", index);
					startActivity(intent);
					Toast.makeText(MenuSelectionActivity.this, "Menu removed from the order list.", Toast.LENGTH_LONG).show();
				}
			});
		}
	}
}
