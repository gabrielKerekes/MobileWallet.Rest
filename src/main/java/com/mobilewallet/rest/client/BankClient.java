package com.mobilewallet.rest.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import com.mobilewallet.rest.bank.messages.BankMessage;

public class BankClient {
//	private static final String AddressBase = "http://127.0.0.1:8080/bank/rest/";
	private static String AddressBase = null;

	private static void initializeAddressBaseFromFile() {
		System.out.println("Initializing BankAddressBase");
		if (AddressBase != null) {
			System.out.println("BankBaseAddress already initialized");
			return;
		}

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(BankClient.class.getResourceAsStream("/bank.conf")));
			AddressBase = br.readLine();
			br.close();
		} catch (Exception e) {
			AddressBase = "http://127.0.0.1:8080/bank/rest/";
			e.printStackTrace();
			System.out.println("IP config file not found");
		}

		return;
	}

	public static int executePost(String request, BankMessage message) {
		System.out.println("BANK POST - " + message.getAccountNumber() + " " + message.getTimestamp());

		initializeAddressBaseFromFile();

		try {
	        URL url = new URL(AddressBase + request);
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	
	        if (!request.equals("getTest")) {
		        urlConnection.setRequestMethod("POST");
		        urlConnection.setRequestProperty("Content-Type", "application/json");
		        urlConnection.setDoOutput(true);
			        	
		        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
		        ObjectMapper mapper = new ObjectMapper();
		        
		        Gson gson = new Gson();
		        String json = gson.toJson(message);
		        
		        wr.writeBytes(json);
		        wr.flush();
		        wr.close();
	        }
	        else {
		        urlConnection.setRequestMethod("POST");
	        }
	        
	        int responseCode = urlConnection.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }
            String response = sb.toString();      
            System.out.println(response);
            
            return responseCode;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return 500;
	}
}
