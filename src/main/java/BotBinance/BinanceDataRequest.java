package BotBinance;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BinanceDataRequest {
	BinanceDataRequest(){};
	
public static String sendGetRequest(String urlS, String params) {
	System.out.println("CreateConnection");
	ExecutorService executor=Executors.newSingleThreadExecutor();
	 HttpClient client = (HttpClient) HttpClient.newBuilder().executor(executor).build();
	try {
		String response=null;
	
		   HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create(urlS+params))
			       
			        .GET()
			        .build();

	  HttpResponse<String> rsp = client.send(request, BodyHandlers.ofString());
	  
		
	
		
		



	if (rsp.statusCode()==200) {
	
	    response=rsp.body();
	    executor.shutdownNow();
	    client=null;
	   System.gc();
	} else {
	 
	    response=rsp.body();
	    executor.shutdownNow();
	    client=null;
	    System.gc();
	    throw new BinanceException(response);
	    
	}
	return response;
	}
		catch(Exception e){
			e.printStackTrace();
			executor.shutdownNow();
			 client=null;
			 
			System.gc();
			return null;
		}
	finally {executor.shutdownNow();
	 client=null;
	 
	System.gc();}
	
	
}

public static String sendHMACGetRequest(String urlS, String params) throws IOException {
	
	System.out.println("CreateConnection");
	String response="";
	ExecutorService executor=Executors.newSingleThreadExecutor();
	 HttpClient client = (HttpClient) HttpClient.newBuilder().executor(executor).build();
	 try {
		 
	byte[] encodeKey = BotBinance.SecretKey.getBytes();
	String querry_string = URLEncoder.encode(params,"UTF-8");
	
	 System.out.println(querry_string.toString());
	
	 byte[] encodeParam=params.getBytes("UTF-8");
	
	 String sig=Hex.encodeHexString(HMAC.calcHmacSha256(encodeKey,encodeParam));

	

		 
		   HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create(urlS+params+"&signature="+sig))
			       
			        .header("X-MBX-APIKEY", BotBinance.ApiKey)

			        .GET()
			        .build();

	  HttpResponse<String> rsp = client.send(request, BodyHandlers.ofString());
	  
		
	


		if (rsp.statusCode()==200) {
		
		    response=rsp.body();
		    executor.shutdownNow();
		    client=null;
		    System.gc();
		} else {
		 
		    System.out.print("ERROR SendHMAC"+rsp.body());
		    executor.shutdownNow();
		    client=null;
		    System.gc();
		    throw new BinanceException(response);
		    
		}
		return response;
		
	}
			catch(Exception e){
				  executor.shutdownNow();
				 client=null;
				 System.gc();
				e.printStackTrace();
				return "";
			}
	 finally {executor.shutdownNow();
	 client=null;
	 
	System.gc();}


}


public static String sendHMACPostRequest(String urlS, String params) throws IOException {
	
	System.out.println("CreateConnection");
	String response="";
	ExecutorService executor=Executors.newSingleThreadExecutor();
	 HttpClient client = (HttpClient) HttpClient.newBuilder().executor(executor).build();
	 try {
		 
	byte[] encodeKey = BotBinance.SecretKey.getBytes();
	String querry_string = URLEncoder.encode(params,"UTF-8");
	
	 System.out.println(querry_string.toString());
	
	 byte[] encodeParam=params.getBytes("UTF-8");
	
	 String sig=Hex.encodeHexString(HMAC.calcHmacSha256(encodeKey,encodeParam));

	

		 
		   HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create(urlS+params+"&signature="+sig))
			       
			        .header("X-MBX-APIKEY", BotBinance.ApiKey)

			        .POST(HttpRequest.BodyPublishers.noBody())
			        .build();

	  HttpResponse<String> rsp = client.send(request, BodyHandlers.ofString());
	  
		
	


		if (rsp.statusCode()==200) {
		
		    response=rsp.body();
		    executor.shutdownNow();
		    client=null;
		    System.gc();
		} else {
		 
		    System.out.print("ERROR SendHMAC"+rsp.body());
		    executor.shutdownNow();
		    client=null;
		    System.gc();
		    throw new BinanceException(response);
		    
		}
		return response;
		
	}
			catch(Exception e){
				  executor.shutdownNow();
				 client=null;
				 System.gc();
				e.printStackTrace();
				return "";
			}
	 finally {executor.shutdownNow();
	 client=null;
	 
	System.gc();}


}

public static void AccInfoInit() throws IOException{
	System.out.println("AccInfoInit");
	String param="timestamp="+String.valueOf(System.currentTimeMillis());
	
		
				
			
				
	
		

		JSONObject response = new JSONObject(sendHMACGetRequest("https://fapi.binance.com/fapi/v2/account?",param));
		
		System.out.println(response);
		BotBinance.AccBal= response.getDouble("totalWalletBalance");
		JSONArray positions = response.getJSONArray("positions");
		for(int i=0; i<positions.length();i++) {
			JSONObject pair= positions.getJSONObject(i);
			if(BotBinance.allCrypto.containsKey(pair.getString("symbol"))) {
				CryptoCoin c = BotBinance.allCrypto.get(pair.getString("symbol"));
				if(pair.getInt("leverage")!=c.getLeverage()) {
					try {
						OrderGenerator.ChangeLeverage(pair.getString("symbol"), c.getLeverage());
					} catch (JSONException | BinanceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				if(pair.getDouble("positionAmt")!=0) {if(pair.getDouble("positionAmt")>0) {
						c.setIsInTrade(true);
						
						c.setActiveSide("LONG");
						//c.setProfitPrice(pair.getDouble("entryPrice"));
						c.setActiveBalQtyUSD(Math.abs(pair.getDouble("positionAmt")*pair.getDouble("entryPrice")/c.getLeverage()));
						c.setActiveBalQtyCrypto(Math.abs(pair.getDouble("positionAmt")));
						BotBinance.pairsInTrade.put(c.getName(), c);
					
				}
				else{c.setIsInTrade(true);
				c.setActiveSide("SHORT");
				c.setProfitPrice(pair.getDouble("entryPrice"));
				c.setActiveBalQtyUSD(Math.abs(pair.getDouble("positionAmt")*pair.getDouble("entryPrice")));
				c.setActiveBalQtyCrypto(Math.abs(pair.getDouble("positionAmt")));
				BotBinance.pairsInTrade.put(c.getName(), c);
			}}
			
			}
}
	
	
}

public static JSONArray GetPrecision() throws JSONException, InterruptedException, ExecutionException, IOException, BinanceException {

JSONArray info=new JSONArray();




			
JSONObject response = new JSONObject(sendGetRequest("https://fapi.binance.com/fapi/v1/exchangeInfo",""));
info=response.getJSONArray("symbols");


return info;

}



}