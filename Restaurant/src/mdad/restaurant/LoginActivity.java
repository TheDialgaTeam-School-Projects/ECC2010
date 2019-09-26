package mdad.restaurant;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import mdad.restaurant.network.NetworkHelper;
import mdad.restaurant.network.NetworkTaskDelegate;
import modules.system.StringHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText etName, etPwd;
	private Button btnRegister, btnLogin;
	private String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		etName = (EditText) findViewById(R.id.etName);
		etPwd = (EditText) findViewById(R.id.etPwd);
		
		btnRegister = (Button) findViewById (R.id.btnRegister);
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent msg = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(msg);
			}
		});
		
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = etName.getText().toString().trim();
				password = etPwd.getText().toString();
				
				if (StringHelper.isNullOrEmpty(username) || StringHelper.isNullOrEmpty(password)) {
					Toast.makeText(getApplicationContext(), "Enter username and password if you are an existing user. If not, please register.", Toast.LENGTH_LONG).show();
				} else {
					NetworkHelper.execute(LoginActivity.this, new NetworkTaskDelegate() {
						@Override
						public HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
							postData.put("action", "getAccount");
							postData.put("name", username);
							postData.put("password", password);
							return NetworkHelper.doPostRequest(url, postData);
						}
						
						@Override
						public void onSuccess(List<String> result) throws Exception {
							SharedPreferences sp = getSharedPreferences("Account", 0);
							Editor edit = sp.edit();
							edit.putInt("AccountID", Integer.valueOf(result.get(1)));
							edit.putBoolean("IsAdmin", result.get(2).contentEquals("1") ? true : false);
							edit.commit();
							
							if (result.get(3).contentEquals("1"))
								Toast.makeText(getApplicationContext(), "Sorry you are not allowed to login.", Toast.LENGTH_LONG).show();
							else {
								Intent msg = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(msg);
							}
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
