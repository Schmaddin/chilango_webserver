package com.crunchify.tutorials;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.graphhopper.chilango.data.JsonHelper;
import com.graphhopper.chilango.data.MapMatchMessage;
import com.graphhopper.chilango.data.gps.GPSPoint;
import com.graphhopper.chilango.network.HTTPSRequest;

/**
 * @author Crunchify.com
 * 
 */

public class ClientTest {
	public static void main(String[] args) {
		String string = "";
		//try {
			Map<Long,GPSPoint> message=new HashMap<Long,GPSPoint>();
			String jsontest="{\"0\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.311889,\"lon\":-99.153902},\"15\4\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.317327,\"lon\":-99.151801},\"244\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.318757,\"lon\":-99.148557},\"320\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.320592,\"lon\":-99.150915},\"405\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.321779,\"lon\":-99.154087},\"541\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.325638,\"lon\":-99.150543},\"626\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.328849,\"lon\":-99.150457},\"730\":{\"accuracy\":-1.0,\"alt\":0.0,\"lat\":19.328404,\"lon\":-99.154588}}";


			String json=HTTPSRequest.call("https://srv.chilango.me/WebService/api/mapmatch", jsontest);
			System.out.println(json);
			//=JsonHelper.createJsonFromObject(message);

 
			// Step2: Now pass JSON File Data to REST Service
			/*try {
				URL url = new URL("https://service.chilango.me/WebService/api/mapmatch");
				System.out.println("hier simma");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				System.out.print("hier auch");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsontest);
				out.close();
				System.out.println("geschrieben haben wir auch ");
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String a=in.readLine();
				while (a!=null) {
					System.out.println(a);
					a=in.readLine();
				}
				System.out.println("\nCrunchify REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}
 
		//	br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
}