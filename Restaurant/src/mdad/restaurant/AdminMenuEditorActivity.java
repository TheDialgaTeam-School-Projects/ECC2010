package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mdad.restaurant.category.MenuCategoryItem;
import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;

import modules.android.graphics.BitmapHelper;
import modules.system.StringHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public final class AdminMenuEditorActivity extends Activity {
	private int id;
	private String name;
	private int categoryID;
	private double price;
	private String image;
	private Boolean isActive;
	
	private EditText etAdminMenuEditorName;
	private Spinner spAdminMenuEditorCategory;
	private EditText etAdminMenuEditorPrice;
	private ImageView ivAdminMenuEditorImage;
	private CheckBox cbAdminMenuEditorActive;
	
	private final List<MenuCategoryItem> menuCategories = new ArrayList<MenuCategoryItem>();
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_menu_editor);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_menu_editor, menu);
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
			id = bundle.getInt("id");
			name = bundle.getString("name");
			categoryID = bundle.getInt("categoryID");
			price = bundle.getDouble("price");
			image = bundle.getString("image");
			isActive = bundle.getBoolean("isActive");
		}
		
		initEtAdminMenuEditorName();
		initSpAdminMenuEditorCategory();
		initEtAdminMenuEditorPrice();
		initIvAdminMenuEditorImage();
		initBtnAdminMenuEditorImageUpload();
		initCbAdminMenuEditorActive();
		initBtnAdminMenuEditorSubmit();
		initBtnAdminMenuEditorDelete();
	}
	
	private final void initEtAdminMenuEditorName() {
		etAdminMenuEditorName = (EditText) findViewById(R.id.etAdminMenuEditorName);
		etAdminMenuEditorName.setText(name);
	}
	
	private final void initSpAdminMenuEditorCategory() {
		spAdminMenuEditorCategory = (Spinner) findViewById(R.id.spAdminMenuEditorCategory);
		
		NetworkHelper.execute(this, new NetworkTaskDelegate() {
			@Override
			public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
				postData.put("action", "getMenuCategoryList");
				return NetworkHelper.doPostRequest(url, postData);
			}
			
			@Override
			public final void onSuccess(List<String> result) throws Exception {
				final List<String> items = new ArrayList<String>();
				int selectedIndex = 0;
				
				for (int i = 1; i < result.size(); i += 3) {
					final int id = Integer.parseInt(result.get(i));
					final String name = result.get(i + 1);
					final Boolean isActive = result.get(i + 2).contentEquals("1") ? true : false;
					
					if (isActive) {
						items.add(name);
						menuCategories.add(new MenuCategoryItem(id, name, isActive));
						
						if (id == categoryID)
							selectedIndex = (i - 1) > 0 ? (i - 1) / 3 : 0;
					}
				}
				
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminMenuEditorActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
				spAdminMenuEditorCategory.setAdapter(adapter);
				spAdminMenuEditorCategory.setSelection(selectedIndex);
			}
		});
	}
	
	private final void initEtAdminMenuEditorPrice() {
		etAdminMenuEditorPrice = (EditText) findViewById(R.id.etAdminMenuEditorPrice);
		etAdminMenuEditorPrice.setText(String.valueOf(price));
	}
	
	private final void initIvAdminMenuEditorImage() {
		ivAdminMenuEditorImage = (ImageView) findViewById(R.id.ivAdminMenuEditorImage);
		
		if (!StringHelper.isNullOrEmpty(image))
			ivAdminMenuEditorImage.setImageBitmap(BitmapHelper.convert(image));
	}
	
	private final void initBtnAdminMenuEditorImageUpload() {
		final Button btnAdminMenuEditorImageUpload = (Button) findViewById(R.id.btnAdminMenuEditorImageUpload);
		btnAdminMenuEditorImageUpload.setOnClickListener(new OnClickListener() {
			@Override
			public final void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
			}
		});
	}
	
	@Override
	protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
			final Uri selectedImage = data.getData();
            final String[] filePathColumn = { MediaColumns.DATA };
            
            final Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToNext();
            
            final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            final Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            image = BitmapHelper.convert(imageBitmap);
            ivAdminMenuEditorImage.setImageBitmap(BitmapHelper.convert(image));
		}
	}

	private final void initCbAdminMenuEditorActive() {
		cbAdminMenuEditorActive = (CheckBox) findViewById(R.id.cbAdminMenuEditorActive);
		cbAdminMenuEditorActive.setChecked(isActive);
	}
	
	private final void initBtnAdminMenuEditorSubmit() {
		final Button btnAdminMenuEditorSubmit = (Button) findViewById(R.id.btnAdminMenuEditorSubmit);
		btnAdminMenuEditorSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public final void onClick(View v) {
				name = etAdminMenuEditorName.getText().toString().trim();
				categoryID = menuCategories.get(spAdminMenuEditorCategory.getSelectedItemPosition()).getId();
				price = Double.parseDouble(etAdminMenuEditorPrice.getText().toString());
				isActive = cbAdminMenuEditorActive.isChecked();
				
				NetworkHelper.execute(getApplicationContext(), new NetworkTaskDelegate() {
					@Override
					public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
						postData.put("action", "setMenuList");
						postData.put("id", String.valueOf(id));
						postData.put("name", name);
						postData.put("categoryID", String.valueOf(categoryID));
						postData.put("price", String.valueOf(price));
						postData.put("image", StringHelper.isNullOrEmpty(image) ? "NULL" : image);
						postData.put("isActive", isActive.toString());
						return NetworkHelper.doPostRequest(url, postData);
					}
					
					@Override
					public final void onSuccess(List<String> result) throws Exception {
						startActivity(new Intent(AdminMenuEditorActivity.this, AdminMenuActivity.class));
						Toast.makeText(getApplicationContext(), "Menu updated!", Toast.LENGTH_LONG).show();
						System.out.println("Menu updated!");
					}
				});
			}
		});
	}
	
	private final void initBtnAdminMenuEditorDelete() {
		final Button btnAdminMenuEditorDelete = (Button) findViewById(R.id.btnAdminMenuEditorDelete);
		
		if (id == 0)
			btnAdminMenuEditorDelete.setEnabled(false);
		else {
			btnAdminMenuEditorDelete.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(View v) {
					NetworkHelper.execute(getApplicationContext(), new NetworkTaskDelegate() {
						@Override
						public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
							postData.put("action", "deleteMenuList");
							postData.put("id", String.valueOf(id));
							return NetworkHelper.doPostRequest(url, postData);
						}
						
						@Override
						public final void onSuccess(List<String> result) throws Exception {
							startActivity(new Intent(AdminMenuEditorActivity.this, AdminMenuActivity.class));
							Toast.makeText(getApplicationContext(), "Menu deleted!", Toast.LENGTH_LONG).show();
							System.out.println("Menu deleted!");
						}
					});
				}
			});
		}
	}
}
