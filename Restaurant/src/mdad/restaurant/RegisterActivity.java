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
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private Button btnSubmit;
	private EditText etRegName, etRegPwd;
	
	private String username, pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		etRegName = (EditText) findViewById (R.id.etRegName);
		etRegPwd = (EditText) findViewById (R.id.etRegPwd);
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = etRegName.getText().toString().trim();
				pwd = etRegPwd.getText().toString();
				
				if (username.isEmpty() || pwd.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Please enter username and password.", Toast.LENGTH_LONG).show();
					System.out.println("Please enter username and password.");
				} else {
					NetworkHelper.execute(getApplicationContext(), new NetworkTaskDelegate() {
						@Override
						public HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception {
							postData.put("action", "setAccount");
							postData.put("username", username);
							postData.put("password", pwd);
							return NetworkHelper.doPostRequest(url, postData);
						}
						
						@Override
						public void onSuccess(List<String> result) throws Exception {
							startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
							Toast.makeText(getApplicationContext(), "You are registered!", Toast.LENGTH_LONG).show();
							System.out.println("You are registered!");
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
