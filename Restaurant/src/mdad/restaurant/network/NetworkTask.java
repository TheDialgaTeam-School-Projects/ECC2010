package mdad.restaurant.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public final class NetworkTask extends AsyncTask<String, Void, List<String>> {
	private final Context context;
	private final NetworkTaskDelegate networkTaskDelegate;
	private final List<String> result = new ArrayList<String>();
	private final Map<String, String> postData = new HashMap<String, String>();

	public NetworkTask(Context context, NetworkTaskDelegate networkTaskDelegate) {
		this.context = context;
		this.networkTaskDelegate = networkTaskDelegate;
	}

	protected final List<String> doInBackground(String... params) {
		try {
			final URL url = new URL("http://10.0.2.2:8080/MDAD/servlet/server.DatabaseManagerServlet");

			if (networkTaskDelegate != null) {
				final HttpURLConnection connection = networkTaskDelegate.initConnection(url, postData);
				final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String temp;

				while ((temp = reader.readLine()) != null)
					result.add(temp);
				
				reader.close();
				connection.disconnect();
			} else {
				result.add("false");
				result.add("Unknown action defined.");
			}
		} catch (Exception ex) {
			result.add("false");
			result.add(ex.getMessage());
		}

		return result;
	}

	protected final void onPostExecute(List<String> result) {
		try {
			if (result.get(0).contentEquals("false"))
				catchError(result.get(1));
			else if (result.get(0).contentEquals("true"))
				networkTaskDelegate.onSuccess(result);
			else
				catchError("Unknown result");
		} catch (Exception ex) {
			catchError(ex.getMessage(), ex.getStackTrace());
		}
	}

	private final void catchError(String error) {
		Toast.makeText(context, "Error: " + (error == null ? "NullPoinerException" : error), Toast.LENGTH_LONG).show();
		System.out.println(error == null ? "NullPoinerException" : error);
	}
	
	private final void catchError(String error, StackTraceElement[] stacktrace) {
		Toast.makeText(context, "Error: " + (error == null ? "NullPoinerException" : error), Toast.LENGTH_LONG).show();
		System.out.println(error == null ? "NullPoinerException" : error);
		
		for (StackTraceElement item : stacktrace) {
			System.out.println(item);
		}
	}
}
