package BotBinance;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.*;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;


public class BotBinance {

	
	static boolean BotStatus=false;
	static String ApiKey = "JRgMYQodpB3is9GeqCQ9Sun7WPgdh18kuEteb1VpzIlLkhn5MlirFELjwkaNSj4y";
	static String SecretKey="hxtapTJmzvOpfRHRkQwfmk1hqgeRt5gooU4O0VsV9PB0QDoTihfwZ6LUJ05F3TQD";
	static Map<String, CryptoCoin> allCrypto = new HashMap<String, CryptoCoin>();
	static Map<String, CryptoCoin>  pairsInTrade= new HashMap<String, CryptoCoin>();

	static long startTime;
	static BinanceDataRequest dataRequest=new BinanceDataRequest();
	static WsGenerator Generator=new WsGenerator();
	static int nrActivePairs=0;
	static int MaxActivePairs=0;
	static double AccBal=0;
	static double MaxActiveBalance=0;
	static CompletableFuture<Void> checkPositions=null;
	static OrderGenerator OrderGenerator=new OrderGenerator();
	static JSONObject Settings=new JSONObject();
	static String ListenKey=null;
	
	static CompletableFuture<Void> DBReset=null;
	static CryptoCoin selectedPair=null;
	static long UnixOra4;
	
	

	static CompletableFuture<Void> KeepAlive;
	static CompletableFuture<Void> WsCheck;
	static CompletableFuture<Void> RestartWs;
	static AccWsClass WsAcc=new AccWsClass();
	@SuppressWarnings("unused")
	public static void SaveSettings() {
	JSONObject saveSetting = new JSONObject();
	saveSetting.put("MaxActivePairs", BotBinance.MaxActivePairs);
	saveSetting.put("ApiKey", BotBinance.ApiKey);
	saveSetting.put("SecretKey", BotBinance.SecretKey);
	JSONArray pairs=new JSONArray();
	for (Entry<String, CryptoCoin> entry : BotBinance.allCrypto.entrySet()) {
		pairs.put(entry.getValue().toJson());
		
		}
	saveSetting.put("pairs", pairs);
		PrintWriter writer;
		try {
			writer = new PrintWriter("Settings.json", "UTF-8");
			writer.print(saveSetting.toString());
			
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			SaveSettings();
			e.printStackTrace();
		}
		
		
	}
	
	public static void SavePairsData() {
		JSONArray savePairsData= new JSONArray();

	
	for (Entry<String, CryptoCoin> entry : BotBinance.allCrypto.entrySet()) {
		JSONObject p = new JSONObject();
		p.put("pair",BotBinance.allCrypto.get(entry.getKey()).getName());
		p.put("tradesNr", BotBinance.allCrypto.get(entry.getKey()).getTradesNr());
		p.put("lastOrderPrice", BotBinance.allCrypto.get(entry.getKey()).getLastOrderPrice());
		p.put("firstOrderPrice", BotBinance.allCrypto.get(entry.getKey()).getFirstOrderPrice());
		
		
		
		savePairsData.put(p);
		
		}

		PrintWriter writer;
		try {
			writer = new PrintWriter("PairsData.json", "UTF-8");
			writer.print(savePairsData.toString());
			
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			SaveSettings();
			e.printStackTrace();
		}
		
		
	}
	public static void ReadPairsData() throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String resourceName = "PairsData.json";
		 File initialFile = new File("PairsData.json");
        InputStream is =new FileInputStream(initialFile); ;
        JSONTokener tokener = new JSONTokener(is);
        JSONArray data = new JSONArray(tokener);
        for(int i=0;i<data.length();i++) {
        	JSONObject  p =data.getJSONObject(i);
        	String pairName =p.getString("pair");
        	if(BotBinance.pairsInTrade.containsKey(pairName)){
        		BotBinance.allCrypto.get(pairName).setLastOrderPrice(p.getDouble("lastOrderPrice"));
        		BotBinance.allCrypto.get(pairName).setFirstOrderPrice(p.getDouble("firstOrderPrice"));
        		
        		BotBinance.allCrypto.get(pairName).setTradesNr(p.getInt("tradesNr"));
        		
        	}
        		
        }
    	
    		
    		
    		
    		
    	
    		
	}
	

	
	public static void SetPairsPricePrecision() {
	
		try {
			JSONArray symbolList=BinanceDataRequest.GetPrecision();
			for(int i=0;i<symbolList.length();i++) {
				JSONObject symbol=symbolList.getJSONObject(i);
				if(allCrypto.containsKey(symbol.getString("symbol"))) {
					System.out.println("");
					allCrypto.get(symbol.getString("symbol")).setQtyPrecision(symbol.getInt("quantityPrecision"));
					allCrypto.get(symbol.getString("symbol")).setPricePrecision(symbol.getInt("pricePrecision"));
					JSONArray filters=symbol.getJSONArray("filters");
					for(int j=0;j<filters.length();j++) {
						JSONObject filter=filters.getJSONObject(j);
						if(filter.getString("filterType").equals("PRICE_FILTER")) {
							allCrypto.get(symbol.getString("symbol")).setTickSize(filter.getDouble("tickSize"));
						}
						
					}
				}
			}
			
		} catch (JSONException | InterruptedException | ExecutionException | IOException | BinanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void ReadSettings() throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	
		String resourceName = "Settings.json";
		 File initialFile = new File("Settings.json");
        InputStream is =new FileInputStream(initialFile); ;
        JSONTokener tokener = new JSONTokener(is);
        JSONObject settings = new JSONObject(tokener);
   int PairId=1;
   BotBinance.SecretKey=settings.getString("SecretKey");
   BotBinance.ApiKey=settings.getString("ApiKey");
        MaxActivePairs = settings.getInt("MaxActivePairs");
        JSONArray arrpairs=settings.getJSONArray("pairs");

        for(int i=0;i<arrpairs.length();i++) {
        	
        	JSONObject pair=arrpairs.getJSONObject(i);
       		
        
        		

         
        
       String PairName =  pair.getString("pair").toUpperCase();
       double perProfitTarget = pair.getDouble("perProfitTarget");
       System.out.println("PER PROFTI TARGET :"+perProfitTarget);
       double MaxBalQty = pair.getDouble("maxBalQty");
       int leverage = pair.getInt("lev");
       int MinLiqValue=pair.getInt("minLiqValue");
       boolean perMode = pair.getBoolean("perMode");
      double initialTradeValue=pair.getDouble("initialTradeValue");
      JSONArray profitDevJs = pair.getJSONArray("profitDeviationsTargets");
      
      ArrayList<ProfitDeviationTarget> profitDev = new  ArrayList<ProfitDeviationTarget>();
for(int j=0;j<profitDevJs.length();j++) {
	JSONObject dev =  profitDevJs.getJSONObject(j);
	ProfitDeviationTarget p  = new ProfitDeviationTarget(dev.getDouble("rangeDeviation"),
														 dev.getDouble("qtyPercent"));
	profitDev.add(p);}

JSONArray zonePriceJs = pair.getJSONArray("tradingZones");

ArrayList<TradingZone> tradingZones = new  ArrayList<TradingZone>();
for(int j=0;j<zonePriceJs.length();j++) {
JSONObject zone =  zonePriceJs.getJSONObject(j);
TradingZone t = new TradingZone(zone.getDouble("tradeNr"),
		zone.getDouble("priceDeviation"));
tradingZones.add(t);}
	

       try {CryptoCoin  c =new CryptoCoin(PairName,MaxBalQty,perProfitTarget,leverage,perMode,initialTradeValue,profitDev,tradingZones);
       allCrypto.put(PairName, c);
       }
       catch(Exception e) {
    	   e.printStackTrace();
       }}
       
        	
        	
        	
        }
       
	
public static void Init() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, InterruptedException, ExecutionException {

					
ReadSettings();

}

public static void getAccListenKey() throws IOException {


    JSONObject resp = new JSONObject(BinanceDataRequest.sendHMACPostRequest("https://fapi.binance.com/fapi/v1/listenKey", ""));

  

ListenKey= resp.toMap().get("listenKey").toString();

}

static void KeepAlive() throws IOException {
	
	
	
	String rawString =BotBinance.SecretKey;
	byte[] encodeKey = StringUtils.getBytesUtf8(rawString);
	String param="?";
	String querry_string= URLEncoder.encode(param,"UTF-8");

	byte[] encodeParam=StringUtils.getBytesUtf8(querry_string);


	String sig=Hex.encodeHexString(HMAC.calcHmacSha256(encodeKey, encodeParam));
	Map<String, String> parameters = new HashMap<String,String>();
	parameters.put("signature", sig);
	HttpClient client = HttpClient.newHttpClient();



		URL url=new URL("https://fapi.binance.com/fapi/v1/listenKey"+"?"+ParameterStringBuilder.getParamsString(parameters));
	System.out.println(url);

	HttpURLConnection con = (HttpURLConnection) url.openConnection();

	con.setRequestMethod("PUT");
	con.setRequestProperty("X-MBX-APIKEY", BotBinance.ApiKey);
	int status = con.getResponseCode();
	BufferedReader in = new BufferedReader(
			  new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			in.close();
			in=null;
			
			encodeParam=null;
			encodeKey=null;

}



public static void StopBot() {
	/**
	 * Start Websockets processes for all pairs
	 */
	for (Entry<String, CryptoCoin> entry : BotBinance.allCrypto.entrySet()) {
	   entry.getValue().StopWsLiquidation();
	}
}
public static void StartBot() {
	/**
	 * Stop Websockets processes for all pairs
	 */
	for (Entry<String, CryptoCoin> entry : BotBinance.allCrypto.entrySet()) {
	   entry.getValue().StartWsLiquidation();
	}
}
public static void StartAccWs() {
	/**
	 * Start AccountWebsocket using Account Listen key
	 */
	try {
		getAccListenKey();
		WsAcc.AccStatusWs = BotBinance.Generator.WsAccStatus(ListenKey);
	} catch (IOException | InterruptedException | ExecutionException e1) {
		//StartAccWs();
		e1.printStackTrace();
	}
	
		
		
		
 WsCheck=CompletableFuture.runAsync(()-> {
		while(true) {
			
			try {
				
				Thread.sleep(60000);
			
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		
		if(!WsAcc.AccStatusWs.isOpen()) {
			try{System.out.println("Start Accw");
			WsAcc.AccStatusWs=Generator.WsAccStatus(ListenKey);}
				catch(Exception e) {
					e.printStackTrace();
					}
			
		}}});
	
	  
		 KeepAlive=CompletableFuture.runAsync(()-> {
			while(true) {
				
				try {
					
					Thread.sleep(3000000);
					KeepAlive();
				
				} catch (InterruptedException | IOException e) {
					
					e.printStackTrace();
				}
			
			
				
			}
		
		
		
		});
			
		
		
		
		
		
		
		
		
					
	
}
static void setCheckPosition() {
	checkPositions=CompletableFuture.runAsync(()-> {
		while(true) {
			
			try {
				
				Thread.sleep(2000);
		
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			

		Set<String> activeSet=pairsInTrade.keySet();
		
		try {
			
			
			String parameters="timestamp="+System.currentTimeMillis();
			
					
					
			JSONArray PositionsList=new JSONArray(BinanceDataRequest.sendHMACGetRequest("https://fapi.binance.com/fapi/v2/positionRisk?", parameters));
			
			for(int i=0;i<PositionsList.length();i++) {
				JSONObject position=PositionsList.getJSONObject(i);
		if(allCrypto.containsKey(position.getString("symbol"))) {CryptoCoin c=allCrypto.get(position.getString("symbol"));
			if(pairsInTrade.containsKey(c.getName())){
				System.out.println("positionAmmount:"+position.getDouble("positionAmt"));
	System.out.println("Last Traded price "+c.getName()+": "+c.getLastOrderPrice());
													if(position.getDouble("entryPrice")!=c.getProfitPrice()&&position.getDouble("positionAmt")!=0) {
														OrderGenerator.CancelAllOrdersPair(c.getName());
														c.setActiveBalQtyUSD(Math.abs(position.getDouble("positionAmt")*position.getDouble("entryPrice")));
														c.setActiveBalQtyCrypto(Math.abs(position.getDouble("positionAmt")));
														BotBinance.SavePairsData();

														if(c.getActiveSide().equals("LONG")) {
															double price=position.getDouble("entryPrice")+(position.getDouble("entryPrice")*c.getPercentProfitTarget()/100);
															price=(price-(price%c.getTickSize()));
															 price=BigDecimal.valueOf(price).setScale(c.getPricePrecision(),RoundingMode.HALF_UP).doubleValue();
															 allCrypto.get(position.getString("symbol")).setProfitPrice(position.getDouble("entryPrice"));
															OrderGenerator.takeProfit(allCrypto.get(position.getString("symbol")));
															c.setIsOk(true);
															
															}
														if(c.getActiveSide().equals("SHORT")) {
															double price=position.getDouble("entryPrice")-(position.getDouble("entryPrice")*c.getPercentProfitTarget()/100);
															price=(price-(price%c.getTickSize()));
															 price=BigDecimal.valueOf(price).setScale(c.getPricePrecision(),RoundingMode.HALF_UP).doubleValue();
															 allCrypto.get(position.getString("symbol")).setProfitPrice(position.getDouble("entryPrice"));
															 OrderGenerator.takeProfit(allCrypto.get(position.getString("symbol")));
															c.setIsOk(true);
													}
														
														
														
													}
		}
		if(position.getDouble("positionAmt")==0) {
			c.setIsOk(true);
			c.Reset();
			
			double qt=(6.5)/allCrypto.get(position.getString("symbol")).getLastTradedPrice();
			System.out.println("PAIR PRECISION:"+c.getQtyPrecision());
			double qty=BigDecimal.valueOf(qt).setScale(c.getQtyPrecision(),RoundingMode.HALF_UP).doubleValue();
			
			BotBinance.OrderGenerator.BuyMarketOrderGenerator(qty, c);
			c.setActiveSide("LONG");
			c.setIsInTrade(true);
			pairsInTrade.put(position.getString("symbol"), allCrypto.get(position.getString("symbol")));
			}	
		
		}
		
		}
		

		}catch(Exception e) {
		
			e.printStackTrace();}}});
	
	
}
/////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException  
	{
	/**
	 * Start and control the bot
	 */
System.gc();
	
		
		
		
System.out.println(System.currentTimeMillis());

startTime=new VWAPStartTime().getTime();
Init();

BinanceDataRequest.AccInfoInit();
BotBinance.ReadPairsData();

SetPairsPricePrecision();

//StartAccWs();
MainWindow.showWindow();

RestartWs=CompletableFuture.runAsync(()-> {
	while(true) {
		System.out.println("Restartat");
		try {
			
			Thread.sleep(600000);
	
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		try {
		if(WsAcc.AccStatusWs.isOpen()) {WsAcc.AccStatusWs.sendCloseFrame();}}
		catch(Exception e) {e.printStackTrace();}
		for (String key: allCrypto.keySet()) {
			
			CryptoCoin c =allCrypto.get(key);
		System.out.println(c.getName()+" RESTART PAIR");
		try {c.WsLiqProcess.sendCloseFrame();
		c.WsLiqProcess=null;}
		catch(Exception e) {e.printStackTrace();}
		try {c.WsLastPrice.sendCloseFrame();
		c.WsLastPrice=null;
		}
		catch(Exception e) {e.printStackTrace();}
			}
		
		
		
	
		
	}});
setCheckPosition();





}}
//wss://fstream.binance.com/ws/BTCUSDT