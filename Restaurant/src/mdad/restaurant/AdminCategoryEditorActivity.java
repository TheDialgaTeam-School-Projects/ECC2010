package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public final class AdminCategoryEditorActivity extends Activity {
	private int id;
	private String name;
	private Boolean isActive;
	
	private EditText etAdminCategoryEditorName;
	private CheckBox cbAdminCategoryEditorActive;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_category_editor);
		init(savedInstanceState);
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.admin_category_editor, menu);
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
			isActive = bundle.getBoolean("isActive");
		}
		
		initEtAdminCategoryEditorName();
		initCbAdminCategoryEditorActive();
		initBtnAdminCategoryEditorSubmit();
		initBtnAdminCategoryEditorDelete();
	}
	
	private final void initEtAdminCategoryEditorName() {
		etAdminCategoryEditorName = (EditText) findViewById(R.id.etAdminCategoryEditorName);
		etAdminCategoryEditorName.setText(name);
	}
	
	private final void initCbAdminCategoryEditorActive() {
		cbAdminCategoryEditorActive = (CheckBox) findViewById(R.id.cbAdminCategoryEditorActive);
		cbAdminCategoryEditorActive.setChecked(isActive);
	}
	
	private final void initBtnAdminCategoryEditorSubmit() {
		final Button btnAdminCategoryEditorSubmit = (Button) findViewById(R.id.btnAdminCategoryEditorSubmit);
		btnAdminCategoryEditorSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public final void onClick(View v) {
				name = etAdminCategoryEditorName.getText().toString().trim();
				isActive = cbAdminCategoryEditorActive.isChecked();
				
				if (name.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Error: Name is empty.", Toast.LENGTH_LONG).show();
					System.out.println("Error: Name is empty.");
				} else {
					NetworkHelper.execute(getApplicationContext(), new NetworkTaskDelegate() {
						@Override
						public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
							postData.put("action", "setMenuCategoryList");
							postData.put("id", String.valueOf(id));
							postData.put("name", name);
							postData.put("isActive", isActive.toString());
							return NetworkHelper.doPostRequest(url, postData);
						}
						
						@Override
						public final void onSuccess(List<String> result) throws Exception {
							startActivity(new Intent(AdminCategoryEditorActivity.this, AdminCategoryActivity.class));
							Toast.makeText(getApplicationContext(), "Menu category updated!", Toast.LENGTH_LONG).show();
							System.out.println("Menu category updated!");
						}
					});
				}
			}
		});
	}
	
	private final void initBtnAdminCategoryEditorDelete() {
		final Button btnAdminCategoryEditorDelete = (Button) findViewById(R.id.btnAdminCategoryEditorDelete);
		
		if (id == 0)
			btnAdminCategoryEditorDelete.setEnabled(false);
		else {
			btnAdminCategoryEditorDelete.setOnClickListener(new OnClickListener() {
				@Override
				public final void onClick(View v) {
					NetworkHelper.execute(getApplicationContext(), new NetworkTaskDelegate() {
						@Override
						public final HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
							postData.put("action", "deleteMenuCategoryList");
							postData.put("id", String.valueOf(id));
							return NetworkHelper.doPostRequest(url, postData);
						}
						
						@Override
						public final void onSuccess(List<String> result) throws Exception {
							startActivity(new Intent(AdminCategoryEditorActivity.this, AdminCategoryActivity.class));
							Toast.makeText(getApplicationContext(), "Menu category deleted!", Toast.LENGTH_LONG).show();
							System.out.println("Menu category deleted!");
						}
					});
				}
			});
		}
	}
}
