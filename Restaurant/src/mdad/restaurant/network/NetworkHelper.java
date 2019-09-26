package mdad.restaurant.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.content.Context;

public final class NetworkHelper {
	public static final void execute(Context context, NetworkTaskDelegate doInBackground) {
		new NetworkTask(context, doInBackground).execute();
	}
	
	public static final HttpURLConnection doPostRequest(URL url, Map<String, String> postData) throws IOException {
		final HttpURLConnection connection;
		final StringBuilder postDataString = new StringBuilder();
		final byte[] postDataBytes;

		for (Map.Entry<String, String> param : postData.entrySet()) {
			if (postDataString.length() != 0)
				postDataString.append('&');
			
			postDataString.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postDataString.append('=');
			postDataString.append(URLEncoder.encode(param.getValue(), "UTF-8"));
		}
		
		postDataBytes = postDataString.toString().getBytes("UTF-8");
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(5000);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		connection.getOutputStream().write(postDataBytes);
		
		return connection;
	}
}
