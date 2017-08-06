package com.crunchify.tutorials;

/**
 * @author Crunchify.com
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.gson.reflect.TypeToken;
import com.graphhopper.chilango.data.JsonHelper;
import com.graphhopper.chilango.data.MapMatchMessage;
import com.graphhopper.chilango.data.gps.GPSPoint;
import com.graphhopper.chilango.network.ConnectionContainer;
import com.graphhopper.chilango.network.ConnectionMessage;
import com.graphhopper.chilango.network.ConnectionMessage.ConnectionInformation;
import com.graphhopper.chilango.network.Constants;
import com.graphhopper.chilango.network.EasyCrypt;
import com.graphhopper.chilango.network.MapMatchingClient;
import com.graphhopper.chilango.network.RequestMessage;
import com.graphhopper.chilango.network.RequestType;
import com.graphhopper.chilango.network.ServerConnection;
import com.graphhopper.chilango.network.ServerMessageAuth;
import com.graphhopper.chilango.network.TCPServerConnection;

@Path("/")
public class Service {
	@POST
	@Path("/mapmatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder inputJSON = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				inputJSON.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + inputJSON.toString());
		try {
			Type type = new TypeToken<Map<Long, GPSPoint>>() {
			}.getType();
			Map<Long, GPSPoint> message = (Map<Long, GPSPoint>) JsonHelper.parseJson(inputJSON.toString(), type);

			MapMatchingClient client = new MapMatchingClient("mapmatch", Constants.PORT_MAP_MATCH_INTERN);
			Map<Long, GPSPoint> response = client.match(message);

			String outputJson = JsonHelper.createJsonFromObject(response);
			System.out.println("answer created: " + outputJson);

			client.close();

			return Response.ok(outputJson, MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}

	}

	@POST
	@Path("/main")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseToMainRequest(InputStream incomingData) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("main: Data Received: " + stringBuilder.toString());

		ConnectionContainer container = (ConnectionContainer) JsonHelper.parseJsonAndroid(stringBuilder.toString(),
				ConnectionContainer.class);

		ConnectionContainer resultContainer = null;
		EasyCrypt ec = null;
		try {
			ec = new EasyCrypt(null, EasyCrypt.aes);
			ec.setKey(ec.readKey(new File("aes.key")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RequestMessage requestResponse = null;

		TCPServerConnection connection;
		try {
			connection = new TCPServerConnection(ec, container.getAuth(), "server");

			ConnectionMessage validated = (ConnectionMessage) connection.getConnectionMessage();
			System.out.println((new Date(System.currentTimeMillis())).toString()+" : "+validated.getInfoConnection().name());
			if (validated.getInfoConnection().equals(ConnectionInformation.LOGIN_OK)
					|| validated.getInfoConnection().equals(ConnectionInformation.CORRECT_TOKEN)) {
				if (container.getMessage() == null) {
					try {
						requestResponse = connection.request(new RequestMessage(RequestType.close, null));
					} catch (Exception e) {
						System.out.println("Error:-------" + e.getMessage());
					}

				} else {
					try {
						requestResponse = connection.request(container.getMessage());
					} catch (Exception e) {
						System.out.println("Error:-------" + e.getMessage());
					}

					try {
						connection.request(new RequestMessage(RequestType.close, null));
					} catch (Exception e) {
						System.out.println("Error:-------" + e.getMessage());
					}
				}



			} 
			try {
				connection.close();
			} catch (Exception e) {
				System.out.println("Error:-------" + e.getMessage());
			}
			resultContainer = new ConnectionContainer(null, requestResponse, validated);
			return Response.ok(JsonHelper.createJsonFromObject(resultContainer), MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

		// return Response.status(200).entity(entity)
		// return HTTP response 200 in case of success
		// return
		// Response.status(200).entity(crunchifyBuilder.toString()).build();

		// Response json; //convert entity to json

	}

	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyService(InputStream incomingData) {
		String result = "Service running...";

		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

	// https://srv.chilango.me/WebService/api/mail?verification=VALUE
	@GET
	@Path("/mail")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyMail(@DefaultValue("?") @QueryParam("verification") String verification) {
		String result = "Service running...";
		EasyCrypt ec = null;
		try {
			ec = new EasyCrypt(null, EasyCrypt.aes);
			ec.setKey(ec.readKey(new File("aes.key")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			TCPServerConnection connection = new TCPServerConnection(ec, new ServerMessageAuth("", "", "", verification,
					ConnectionMessage.ConnectionInformation.VERFIY_MAIL), "server");

			ConnectionMessage validated = (ConnectionMessage) connection.getConnectionMessage();

			if (validated.getInfoConnection() == ConnectionMessage.ConnectionInformation.CORRECT_TOKEN)
				result = "mail confirmed";
			else if (validated.getInfoConnection() == ConnectionMessage.ConnectionInformation.WRONG_TOKEN)
				result = "wrong confirmation";
			else
				result = "some kind of error happened";
		} catch (IOException e) {
			e.printStackTrace();
			result = "Server error - confirmation could not be performed";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(verification);

		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

}