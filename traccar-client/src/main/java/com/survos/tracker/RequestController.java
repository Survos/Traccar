package com.survos.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.survos.tracker.Constants.Constants;
import com.survos.tracker.data.Utils;

public class RequestController {

	private Map<String, String> map;
	private int service_code;
	private boolean showProgress;
	private boolean isGetMethod = false;
	
	Context context;

	public RequestController(Context context, Map<String, String> map, int service_code) {
		this.map = map;
		this.service_code = service_code;
		this.showProgress = true;
		this.isGetMethod = false;
        this.context=context;

		// is Internet Connection Available...

		if (Utils.isNetworkAvailable(context)) {

			new AsyncHttpRequest().execute(map.get("url"));

		}

	}



	class AsyncHttpRequest extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			try {

			} catch (IllegalArgumentException ie) {
				ie.printStackTrace();
			} catch (RuntimeException re) {
				re.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... urls) {
			map.remove("url");
			// System.out.println("request=======>");
			HttpClient httpclient = null;
			try {
				if (!isGetMethod) {
					HttpPost httppost = new HttpPost(urls[0]);
					httpclient = new DefaultHttpClient();

					HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 30000);

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					for (String key : map.keySet()) {
						// System.out.println(key + "  < === >  " +
						// map.get(key));

						nameValuePairs.add(new BasicNameValuePair(key, map.get(key)));
					}

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ActivityManager manager = null;
					if(null != context)
						manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
					else
						manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

					if (manager.getMemoryClass() < 25) {
						System.gc();
					}
					HttpResponse response = httpclient.execute(httppost);

					//String responseBody = EntityUtils.toString(response.getEntity());

					return EntityUtils.toString(response.getEntity());

				} else {
					httpclient = new DefaultHttpClient();

					HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 30000);
					HttpGet httpGet = new HttpGet(urls[0]);

					HttpResponse httpResponse = httpclient.execute(httpGet);
					HttpEntity httpEntity = httpResponse.getEntity();

					String responseBody = EntityUtils.toString(httpEntity);
					return responseBody;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError oume) {
				System.gc();

			}
			finally {
				if (httpclient != null) {
					httpclient.getConnectionManager().shutdown();
					httpclient = null;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			try {

				
					if (null != response) {
						System.out.println("Divyesh here response got =>"+ response);

					} else {
						

					}
				
				response = null;
			} catch (IllegalArgumentException ie) {

				ie.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
