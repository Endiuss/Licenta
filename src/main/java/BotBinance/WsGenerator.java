package BotBinance;
import java.io.BufferedReader; 


import org.json.*;

import com.mysql.cj.xdevapi.JsonArray;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.*;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.util.Map;

import org.asynchttpclient.AsyncHandler.State;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ws.*;
import org.asynchttpclient.ws.WebSocket;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
public class WsGenerator {
	/**************************************************************************************************************************************************/
	
		public  WebSocketUpgradeHandler.Builder upgradeHandlerBuilderMarketLiq = new WebSocketUpgradeHandler.Builder();
		public  WebSocketListener wsListner=new WebSocketListener() {
			public void onBinaryFrame(byte[] payload, boolean finalFragment, int rsv){
				System.out.println("payload"+payload);
			}
	      public void onOpen(WebSocket websocket) {
	    	System.out.println("WsLiqStart");
	    	
	    	  }

	   
	      public void onClose(WebSocket websocket, int code, String reason) {
	   
	    	  System.out.println("Close:"+reason);
	    	  

	    	
	      }
	      
	      public void  onTextFrame(String payload, boolean finalFragment, int rsv) {
	    	  JSONObject js=new JSONObject(payload);
	    
	    	  JSONObject data=js.getJSONObject("o");
	    	  String pair=data.getString("s");
	    	  String side=data.getString("S");
	    	  CryptoCoin c=BotBinance.allCrypto.get(pair);
	    	  double ap=data.getDouble("ap");
	    	  double q=data.getDouble("q");
	    	  
	    	  double LiqValue=ap*q;
	    	  if(LiqValue>=c.getMinLiqValue()&&c.getIsOk()&&ap<c.getLastOrderPrice()) {
	    		  
	    		 
	    	
	    	  
	    		  if((BotBinance.nrActivePairs <= BotBinance.MaxActivePairs) || c.getIsInTrade()==true) {
	    			  System.out.println("CHECK IS IN TRADE");
	    			  double  range=c.getInitialTradingRangeTarget()-(c.getInitialTradingRangeTargetDeviationStep()*c.getTradesNr());
	    		    	 if(range>0.5) {range=0.5;}
	    	    	  range=Math.max(range,c.getMinInitialTradingRangeTarget());
	    	    	  double targetPrice=c.getLastTradedPrice()+((c.getFirstOrderPrice()-c.getLastTradedPrice())*range);
	    	    	  double targetQty=((c.getProfitPrice()*c.getActiveBalQtyCrypto())-(targetPrice*c.getActiveBalQtyCrypto()))/(targetPrice-c.getLastTradedPrice());
	 if(targetQty*c.getLastTradedPrice()<6.5) {targetQty=6.6/c.getLastTradedPrice();}
	    	    	  System.out.println("TARGET QTYY:"+targetQty);
	    			System.out.println("ok2");
	    			double pairqty=0;
	    			double usdqty=0;
	    			 
	    			pairqty=BigDecimal.valueOf(targetQty).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
	    		
	    			  System.out.println(pairqty);
	    			  double totalDevRange=0;
	    			  double totalDevNr=0;
	    			 for(TradingZone  t:c.getTradingZones()) {
	    				 if(c.getTradesNr()<t.getTradeNr()) {
	    					 totalDevRange=t.getPriceDeviation();
	    					 break;
	    				 }
	    		
	    			 }
	    			 
	    			  System.out.println("totalDevRange"+totalDevRange);
	    			 if(side.equals("SELL")) {
	    				 double deviationValue= c.getFirstOrderPrice()*totalDevRange;
		    			  System.out.println("CHECK POSITION-> deviationValue: " + deviationValue+" PAIR TRADE NR: " + c.getTradesNr());
		    			  
		    			  System.out.println("CHECK PRICE DEV:" + (c.getLastOrderPrice()-deviationValue));
	    				 if( !c.getActiveSide().equals("SHORT")&&(ap<=c.getLastOrderPrice()-deviationValue)){
	   	    			 

	    				 					try{BotBinance.OrderGenerator.BuyMarketOrderGenerator(Math.abs(pairqty), c);
	    									c.setIsOk(false);								

	    			 						System.out.println("A ajuns pana aici LONG");
	    			 						if(!c.getIsInTrade()) {
	    			 						BotBinance.pairsInTrade.put(c.getName(), c);
	    			 						c.setIsInTrade(true);
	    			 						c.setActiveSide("LONG");}
	    			 						c.setLastOrderPrice(c.getLastTradedPrice());
	    			 						
	    			 						}
	    			 					
	    			 						catch(Exception e) {
	    			 							System.out.println(e);
	    			 							}
	    			 }}
	    			
	    		  
	    		
	    		  }
	      
	    	  
	     
	      
	      }
	    	  System.out.println("Liq "+pair+" "+LiqValue+" "+side);}
	    
	      public void onError(Throwable t) {
	    	  System.out.println("Error wsHandlerMarketLiq");
	    	  System.out.println(t);
	    	  
	      }
	      public void onPingFrame(byte[] payload){}
			public void onPongFrame(byte[] payload) {}
	  };
		 public  WebSocketUpgradeHandler wsHandlerMarketLiq  = upgradeHandlerBuilderMarketLiq.addWebSocketListener(wsListner
			
				 ).build();
	/**************************************************************************************************************************************************/
		 
		protected WebSocketUpgradeHandler.Builder upgradeHandlerBuilderAccStatus = new WebSocketUpgradeHandler.Builder();
		protected WebSocketUpgradeHandler wsHandlerAccStatus  = upgradeHandlerBuilderAccStatus
		  .addWebSocketListener(new WebSocketListener() {
			  
			  public void onPingFrame(byte[] payload){}
				public void onPongFrame(byte[] payload) {}
			  public void onBinaryFrame(byte[] payload, boolean finalFragment, int rsv){
					System.out.println("payload"+payload);
				}
		      public void onOpen(WebSocket websocket) {
		    	
		    	  }

		   
		      public void onClose(WebSocket websocket, int code, String reason) {
		        
		    	  
		      }
		      
		      public void  onTextFrame(java.lang.String payload, boolean finalFragment, int rsv) {
		    
		      JSONObject js=new JSONObject(payload);
		      
		      
		      if(js.getString("e").equals("ORDER_TRADE_UPDATE")) {
		    	  System.out.println(payload);
		    	JSONObject o =js.getJSONObject("o");
		    	if(BotBinance.pairsInTrade.containsKey(o.getString("s"))) {
		    	
		    		if(o.getString("X").equals("FILLED")) {
		    			CryptoCoin c= BotBinance.pairsInTrade.get(o.getString("s"));
		    			c.setIsOk(true);
		    			if((o.getString("S").equals("SELL")&&c.getActiveSide().equals("SHORT"))||(o.getString("S").equals("BUY")&&c.getActiveSide().equals("LONG"))) {
		    			System.out.println("Last Order Price"+c.getName()+": "+o.getDouble("ap"));
		    				c.setLastOrderPrice(o.getDouble("ap"));
		    				
		    				
		    				
		    			}
		    		
		    	}
		    	
		    	  
		    	  
		      }}
		      
		     
		    
		      
		      if(js.getString("e").equals("ACCOUNT_UPDATE")) {
		    	  System.out.println(payload);
		    	 JSONObject a=js.getJSONObject("a");
		    	  JSONArray B=a.getJSONArray("B");
		    	  for(int i=0;i<B.length();i++) {
		    		  if(B.getJSONObject(i).getString("a").equals("USDT")) {
		    			  BotBinance.AccBal=B.getJSONObject(i).getDouble("wb");
		    		 break; }
		    	  }
		    	  
		    	/*  JSONArray P=a.getJSONArray("P");
		    	  for(int i=0;i<P.length();i++) {
		    		  		    		  if(BotBinance.allCrypto.containsKey(P.getJSONObject(i).getString("s"))) {
		    			  CryptoCoin c=BotBinance.allCrypto.get(P.getJSONObject(i).getString("s"));
		    			  BotBinance.allCrypto.get(P.getJSONObject(i).getString("s")).setProfitPrice(P.getJSONObject(i).getDouble("ep"));
		    			  if(P.getJSONObject(i).getDouble("pa")==0) {
		    				  if(BotBinance.pairsInTrade.containsKey(c.getName())) {
		    				  														}
		    				  
		    			  }
		    			  if(P.getJSONObject(i).getDouble("pa")>0) {if(!BotBinance.pairsInTrade.containsKey(c.getName())) {BotBinance.pairsInTrade.put(c.getName(), c);}
		    				  										c.setActiveSide("LONG");
		    				  										c.setPairBalance(P.getJSONObject(i).getDouble("pa"));
		    				  										c.setActiveBalQty(P.getJSONObject(i).getDouble("pa")*P.getJSONObject(i).getDouble("ep"));
		    				  									  c.setIsInTrade(true);}
		    			  else {if(P.getJSONObject(i).getDouble("pa")<0) {if(!BotBinance.pairsInTrade.containsKey(c.getName())) {
		    				  BotBinance.pairsInTrade.put(c.getName(), c);}
		    				  c.setActiveSide("SHORT");
		    				  c.setPairBalance(P.getJSONObject(i).getDouble("pa")*(-1));
		    				  c.setActiveBalQty(P.getJSONObject(i).getDouble("pa")*(-1)*P.getJSONObject(i).getDouble("ep"));}}
		    			  	  c.setIsInTrade(true);
		    		  }
		    	  }*/
		    	  
		    	  
		    	  
		    	  
		    	
		      }
		      }
		    
		      public void onError(Throwable t) {
		    	  System.out.println("Error wsHandlerAccStatus");
		    	  System.out.println(t);
		      }
		      }).build();
		/**************************************************************************************************************************************************/	
		protected WebSocketUpgradeHandler.Builder upgradeHandlerBuilderPrice = new WebSocketUpgradeHandler.Builder();
		 protected WebSocketUpgradeHandler wsHandlerPrice = upgradeHandlerBuilderPrice.addWebSocketListener(new WebSocketListener() {
			  public void onPingFrame(byte[] payload){}
				public void onPongFrame(byte[] payload) {}
			
		      public void onOpen(WebSocket websocket) {
		    	System.out.println("Start Price Ws");
		    	  }

		   
		      public void onClose(WebSocket websocket, int code, String reason) {
		          
		    	  System.out.println("Close: "+reason);
		      }
		      
		      public void  onTextFrame(String payload, boolean finalFragment, int rsv) {
		    	  
		    	  JSONObject js=new JSONObject(payload);
		    	  String pair = js.getString("s");
		   
		    	
		    	  JSONObject data=js.getJSONObject("k");
		    	  double price = data.getDouble("c");
		    	  
		    	  BotBinance.allCrypto.get(pair).setLastTradedPrice(price);
		    /*		if(BotBinance.pairsInTrade.containsKey(c.Name)) {
		    	  if(c.getActiveSide().equals("LONG")) {
		    			if(price<(c.ProfitPrice*0.80)) {
		    				double qt=(6.5)/price;
		    				double qty=BigDecimal.valueOf(qt).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
		    				
		    				try {
								BotBinance.OrderGenerator.BuyMarketOrderGenerator(qty, pair);
								c.setLastOrderPrice(price);
							} catch (BinanceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			}
						}
		    		else{if(c.getActiveSide().equals("SHORT")){
		    			if(price>(c.ProfitPrice*1.2)) {
		    				double qt=(6.5)/price;
		    				double qty=BigDecimal.valueOf(qt).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
		    				
		    				try {
								BotBinance.OrderGenerator.SellMarketOrderGenerator(qty, pair);
								c.setLastOrderPrice(price);
							} catch (BinanceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    			}}
		    		}}*/
		    	  
		    	  
		    	  
		    	 }
		    
		      public void onError(Throwable t) {
		    	  System.out.println("Error wsHandlerPrice");
		    	  System.out.println(t);
		      }
		  }).build();
		
		/**************************************************************************************************************************************************/
		
		public org.asynchttpclient.ws.WebSocket WsMarketLiq(CryptoCoin pair) throws InterruptedException, ExecutionException{
		/**
		 * Initiate the MarketLiquidation websocket.It receives liquidation feed signals and compare the liquidation values with pair parameters and it make the buy/sell/stay decision!
		 * 
		 * 	
		 */
			System.out.println(pair.getName()+" WS MARK LIQ START ");
			 org.asynchttpclient.ws.WebSocket webSocketClient = Dsl.asyncHttpClient()
					  .prepareGet("wss://fstream3.binance.com/ws/"+pair.getName().toLowerCase()+"@forceOrder").
					  

						
					  execute(wsHandlerMarketLiq)
					  .get();
			 return webSocketClient;
			
		}
	/**************************************************************************************************************************************************/
		
		public org.asynchttpclient.ws.WebSocket WsCurrentPrice(CryptoCoin pair) throws InterruptedException, ExecutionException{
			/**
			 * Initiate the WEbsocket that receives current price of the pair and update it in the CryptoCoin object.
			 */
			 org.asynchttpclient.ws.WebSocket webSocketClient = Dsl.asyncHttpClient().
					  prepareGet("wss://fstream.binance.com/ws/"+pair.getName().toLowerCase()+"@kline_1m")

						
					  .execute(wsHandlerPrice)
					  .get();
			 return webSocketClient;
			 
		}
		
	/**************************************************************************************************************************************************/
public  org.asynchttpclient.ws.WebSocket WsAccStatus(String ListenKey) throws InterruptedException, ExecutionException {
	/**
	 * Initiate the Websocket that listen to AccountListeKey and receives Account changes.
	 * 
	 */
	String x = "wss://fstream.binance.com/ws/"+ListenKey;
	 
	 System.out.println(x);

			 org.asynchttpclient.ws.WebSocket webSocketClient = Dsl.asyncHttpClient()
					  .prepareGet(x).
					  execute(wsHandlerAccStatus)
					  .get();
			 return webSocketClient;
			
		}
		
	}

