package techwork.ami;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {
	//Method to send httpPostRequest
	//This method taking two arguments
	//First the URL of the script to which we will send the request
	//Second is an HashMap with name value pairs containing the data to be send with the request
	public String sendPostRequest(String requestURL,  HashMap<String, String> postDataParams) {
		//Creating a URL
		URL url;
		HttpURLConnection conn = null;

		//StringBuilder object to store the message retrieved from the server
		StringBuilder sb = new StringBuilder();
		try {
			//Initializing Url
			url = new URL(requestURL);

			//Creating an httmlurl connection
			conn = (HttpURLConnection) url.openConnection();

			//Configuring connection properties
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			//Creating an output stream
			OutputStream os = conn.getOutputStream();

			//Writing parameters to the request
			//We are using a method getPostDataString which is defined below
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
				sb = new StringBuilder();
				String response;
				//Reading server response
				while ((response = br.readLine()) != null){
					sb.append(response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
		// Close the connection
		finally {
			if (null != conn) conn.disconnect();
		}
		return sb.toString();
	}

	// Methods for get
	// To execute a get without params
	public String sendGetRequest(String requestURL){
		StringBuilder sb =new StringBuilder();
		HttpURLConnection con = null;
		try {
			URL url = new URL(requestURL);
			con = (HttpURLConnection) url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
			String s;
			while((s=bufferedReader.readLine())!=null){
				sb.append(s).append("\n");
			}
		} catch(Exception ignored){
		}
		// Close the connection
		finally {
			if (null != con) con.disconnect();
		}
		return sb.toString();
	}

	// To execute a get with id param
	public String sendGetRequestParam(String requestURL, String id){
		StringBuilder sb =new StringBuilder();
		HttpURLConnection con = null;
		try {
			URL url = new URL(requestURL+"?"+id);

			con = (HttpURLConnection) url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
			String s;
			while((s=bufferedReader.readLine())!=null){
				sb.append(s).append("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "-1";
		}
		// Close the connection
		finally {
			if (null != con) con.disconnect();
		}
		return sb.toString();
	}

	public boolean isConnectedToServer(View v, View.OnClickListener listener) {
		try{
			URL myUrl = new URL(Config.URL_GENERAL_SERVER);
			HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
			connection.setConnectTimeout(5000);
			connection.connect();
			HttpURLConnection connection2 = (HttpURLConnection) myUrl.openConnection();
			connection2.setConnectTimeout(5000);
			connection2.connect();
			return true;
		} catch (Exception e) {
			Snackbar.make(v, R.string.error_on_connection, Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.retry, listener).show();
			return false;
		}
	}

	// Internal method
	private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return result.toString();
	}
}