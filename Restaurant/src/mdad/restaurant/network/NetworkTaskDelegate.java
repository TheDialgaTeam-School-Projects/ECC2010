package mdad.restaurant.network;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface NetworkTaskDelegate {
	public HttpURLConnection initConnection(URL url, Map<String, String> postData) throws Exception;
	
	public void onSuccess(List<String> result) throws Exception;
}
