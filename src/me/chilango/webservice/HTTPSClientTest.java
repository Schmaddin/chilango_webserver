package me.chilango.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.graphhopper.chilango.data.JsonHelper;
import com.graphhopper.chilango.data.gps.GPSPoint;

public class HTTPSClientTest {
	public static void main(String[] args) {
		String string = "";
		try {
			Map<Long,GPSPoint> message=new HashMap<Long,GPSPoint>();
			String jsontest=JsonHelper.createJsonFromObject(message);

 
			// Step2: Now pass JSON File Data to REST Service
			try {
				URL url = new URL("https://srv.chilango.me/WebService/api/mapmatch");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(2000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsontest);
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String a=in.readLine();
				while (a!=null) {
					System.out.println(a);
					a=in.readLine();
				}
				System.out.println("\nREST Service successfully");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling REST Service");
				System.out.println(e);
			}
 
		//	br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
