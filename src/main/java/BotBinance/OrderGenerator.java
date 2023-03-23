package BotBinance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

public class OrderGenerator {
	
	/**
	 * 
	 * Contains methods that generate Buy, Sell and TakeProfit request orders.
	 * @param qty
	 * @param pair
	 * @throws BinanceException
	 */


	public void BuyMarketOrderGenerator(double qty, CryptoCoin pair) throws BinanceException {

	
		byte[] encodeKey = BotBinance.SecretKey.getBytes();
	
		/*parameters.put("recvWindow","5000");*/
String time=String.valueOf(System.currentTimeMillis());	
String param="symbol="+pair.getName()+"&side=BUY"+"&type=MARKET"+"&quantity="+qty+"&newOrderRespType=RESULT"+"&timestamp="+time;

		
		 
		 
		try {
		String response=sendPostRequestHMAC("https://fapi.binance.com/fapi/v1/order?",param);
					JSONObject resp = new JSONObject(response);
					if(pair.getTradesNr()==0) {
						pair.setFirstOrderPrice(resp.getDouble("avgPrice"));
					}
					pair.setTradesNr(pair.getTradesNr()+1);
					pair.setLastOrderPrice(resp.getDouble("avgPrice"));
					BotBinance.SavePairsData();
					System.out.println("AICI BUY:"+response.toString());
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		
			
/*req.addQueryParam("symbol", pair);
req.addQueryParam("side", "buy");
req.addQueryParam("timestamp",time);
req.addQueryParam("type", "market");
req.addQueryParam("quantity", String.valueOf(qty));
req.addQueryParam("recvWindow", "5000");
req.addQueryParam("signature", sig);*/

//Response response = whenResponse.get();

		
		
		}
		

		

		
		
		
		
		
		
	
	public void SellMarketOrderGenerator(double qty, String pair) throws BinanceException {
		byte[] encodeKey = BotBinance.SecretKey.getBytes();

		/*parameters.put("recvWindow","5000");*/
String time=String.valueOf(System.currentTimeMillis());	
String param="symbol="+pair+"&side=SELL"+"&type=MARKET"+"&quantity="+qty+"&newOrderRespType=RESULT"+"&timestamp="+time;

		
		 
		 
		try {
		
			
			String querry_string = URLEncoder.encode(param,"UTF-8");
			
			 System.out.println(querry_string.toString());
			
			 byte[] encodeParam=param.getBytes("UTF-8");
			 System.out.println(encodeParam.length);
			 String sig=Hex.encodeHexString(HMAC.calcHmacSha256(encodeKey,encodeParam));

			 System.out.println(param);
			 System.out.println(sig);
	







			 URL url=new URL("https://fapi.binance.com/fapi/v1/order?"+param+"&signature="+sig);
				
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				System.out.println(con.getRequestMethod());
			con.setRequestProperty("X-MBX-APIKEY", BotBinance.ApiKey);
			
			if (con.getResponseCode() == 200) {
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					in.close();
					in=null;
					con.disconnect();
					System.out.println(content.toString());
			}
			else {String msg=new String("");
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			    String strCurrentLine;
			        while ((strCurrentLine = br.readLine()) != null) {
			              msg+=strCurrentLine;
			        }
			        br.close();
			        br=null;
			        throw new BinanceException(msg);}
		
/*req.addQueryParam("symbol", pair);
req.addQueryParam("side", "buy");
req.addQueryParam("timestamp",time);
req.addQueryParam("type", "market");
req.addQueryParam("quantity", String.valueOf(qty));
req.addQueryParam("recvWindow", "5000");
req.addQueryParam("signature", sig);*/

//Response response = whenResponse.get();
			con.getInputStream().close();
			
		} catch (IOException e) {
			
			throw new BinanceException(e);
		
		
		
		}

	}
	public static void ChangeLeverage(String pair,int lev) throws BinanceException {
		String time=String.valueOf(System.currentTimeMillis());	
		String param="symbol="+pair+"&leverage="+lev+"&timestamp="+time;
		
		try {
			

			URL url=new URL("https://fapi.binance.com/fapi/v1/leverage?"+param);
			
			
			
			
			sendPostRequestHMAC("https://fapi.binance.com/fapi/v1/leverage?",param);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
	
	
	public void CancelAllOrdersPair(String pair) throws BinanceException {

		
		byte[] encodeKey = BotBinance.SecretKey.getBytes();

		/*parameters.put("recvWindow","5000");*/
String time=String.valueOf(System.currentTimeMillis());	
String param="symbol="+pair+"&timestamp="+time;

		
		 
		 
		try {
		
			
		
			 sendDeleteRequestHMAC("https://fapi.binance.com/fapi/v1/allOpenOrders?",param);
			
		
		
		}
		catch(Exception e) {e.printStackTrace();}
		
}
	
	public String sendDeleteRequestHMAC(String urlS, String params) {
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

				        .DELETE()
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
					 executor.shutdownNow();
					client=null;
					 System.gc();
					e.printStackTrace();
					return response;
				}
	}
	
	
	public static String sendPostRequestHMAC(String urlS, String params) {
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
			 
			    response=rsp.body();
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
					return response;
				}
	}
	public void takeProfit(CryptoCoin c) throws BinanceException {
		

		/*parameters.put("recvWindow","5000");*/
		String side;
		String param = "";
		int ok=0;
		String time=String.valueOf(System.currentTimeMillis());	
		if(c.getActiveSide().equals("LONG")) {
			double devSum=0;
			System.out.println("GET PROFIT PRICE IN TAKE PROFIT: "+c.getProfitPrice());
			if(c.getTradesNr()>1) {
			for(ProfitDeviationTarget  p:c.getProfitDeviationsTargets()) {
			
			if((c.getProfitPrice()+(c.getProfitPrice()*c.getPercentProfitTarget()))<=((c.getFirstOrderPrice()-c.getLastOrderPrice())*p.getQtyPercent())) {
			ok++;	
			
			
			double price =((c.getFirstOrderPrice()-c.getLastOrderPrice())*p.getRangeDeviation());
			System.out.println("STANDARD TAKE PROFIT:"+( c.getProfitPrice()+ (c.getProfitPrice()*c.getPercentProfitTarget()) ));
			System.out.println("TARGETED TAKE PROFIT:"+( (c.getProfitPrice()+((c.getFirstOrderPrice()-c.getLastOrderPrice())*p.getQtyPercent()))));
			
			price=BigDecimal.valueOf(price).setScale(c.getPricePrecision(),RoundingMode.HALF_UP).doubleValue();
			price=BigDecimal.valueOf(price).setScale(BigDecimal.valueOf(c.getTickSize()).scale()+1,RoundingMode.HALF_UP).doubleValue();
			double qty=c.getActiveBalQtyCrypto()*( devSum+p.getQtyPercent());
			qty=BigDecimal.valueOf(qty).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
			param="symbol="+c.getName()+"&side=SELL"+"&type=LIMIT"+"&timeInForce=GTC"+"&quantity="+qty+"&reduceOnly=true"+"&price="+price+"&newOrderRespType=RESULT"+"&timestamp="+time;
			devSum=0;
		

		}
			
			else {devSum=devSum+p.getQtyPercent();}
		}
	
			}

	}
		if(ok==0)
		
		
		
		{System.out.println("GET PROFIT PRICE IN TAKE PROFIT OK: "+c.getProfitPrice());
			double price = c.getProfitPrice()+ (c.getProfitPrice()*c.getPercentProfitTarget());
			price=BigDecimal.valueOf(price).setScale(BigDecimal.valueOf(c.getTickSize()).scale(),RoundingMode.HALF_UP).doubleValue();
		System.out.println("PROFIT PRICE BEFORE PRECISION "+price);
		price=BigDecimal.valueOf(price).setScale(c.getPricePrecision(),RoundingMode.HALF_UP).doubleValue();
		

		double qty=BigDecimal.valueOf(c.getActiveBalQtyCrypto()).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
		param="symbol="+c.getName()+"&side=SELL"+"&type=LIMIT"+"&timeInForce=GTC"+"&quantity="+qty+"&reduceOnly=true"+"&price="+price+"&newOrderRespType=RESULT"+"&timestamp="+time;

	

	
	 
	
	}
	System.out.println("TAKE PROFIT PRICE:"+param+" OK="+ok);
		sendPostRequestHMAC("https://fapi.binance.com/fapi/v1/order?",param);}

}
