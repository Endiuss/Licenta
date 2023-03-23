package BotBinance;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.asynchttpclient.*;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.asynchttpclient.Dsl.*;
import io.netty.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
public class CryptoCoin {
	
	
	protected String ActiveSide="OFF";


	private String Name;
	private boolean isOk=true;
	private int Leverage=1;
	private double PercentProfitTarget=0.5;
	private double ProfitPrice;
	private double MinLiqValue=1;
	private double LastTradedPrice=-1;
	private double LastOrderPrice;
	private double firstOrderPrice;
	private double MaxBalQty=999999;
	private double ActiveBalQtyUSD=0;
	private double ActiveBalQtyCrypto=0;
	private boolean isActive=false;
	private boolean isInTrade=false;
	private boolean perMode= false;
	private double tickSize=0.0001;
	private int PricePrecision=5;
	private int QtyPrecision=5;
	private double initialTradeValue=6.5;
	private int tradesNr=0;
	private double initialTradingRangeTarget=0.55;
	private double minInitialTradingRangeTarget=0.1;
	private double initialTradingRangeTargetDeviationStep=0.02;
	private ArrayList<ProfitDeviationTarget> profitDeviationsTargets;
	private ArrayList<TradingZone> tradingZones;
	 org.asynchttpclient.ws.WebSocket WsLiqProcess;
	 org.asynchttpclient.ws.WebSocket WsLastPrice;
	protected CompletableFuture<Void> Updater=null;
	protected CompletableFuture<Void> WsCheck=null;

	 AsyncHttpClient PriceControler= asyncHttpClient();
	 
	 
	 

	public double getFirstOrderPrice() {
		return firstOrderPrice;
	}
	public void setFirstOrderPrice(double firstOrderPrice) {
		this.firstOrderPrice = firstOrderPrice;
	}
	public double getActiveBalQtyUSD() {
		return ActiveBalQtyUSD;
	}
	public void setActiveBalQtyUSD(double activeBalQtyUSD) {
		ActiveBalQtyUSD = activeBalQtyUSD;
	}
	public double getActiveBalQtyCrypto() {
		return ActiveBalQtyCrypto;
	}
	public void setActiveBalQtyCrypto(double activeBalQtyCrypto) {
		ActiveBalQtyCrypto = activeBalQtyCrypto;
	}
	
	public ArrayList<ProfitDeviationTarget> getProfitDeviationsTargets() {
		return profitDeviationsTargets;
	}
	public void setProfitDeviationsTargets(ArrayList<ProfitDeviationTarget> profitDeviationsTargets) {
		this.profitDeviationsTargets = profitDeviationsTargets;
	}
	public ArrayList<TradingZone> getTradingZones() {
		return tradingZones;
	}
	public void setTradingZones(ArrayList<TradingZone> tradingZones) {
		this.tradingZones = tradingZones;
	}
	public double getMinInitialTradingRangeTarget() {
		return minInitialTradingRangeTarget;
	}
	public void setMinInitialTradingRangeTarget(double minInitialTradingRangeTarget) {
		this.minInitialTradingRangeTarget = minInitialTradingRangeTarget;
	}
	public double getInitialTradingRangeTargetDeviationStep() {
		return initialTradingRangeTargetDeviationStep;
	}
	public void setInitialTradingRangeTargetDeviationStep(double initialTradingRangeTargetDeviationStep) {
		this.initialTradingRangeTargetDeviationStep = initialTradingRangeTargetDeviationStep;
	}
	public double getInitialTradingRangeTarget() {
		return initialTradingRangeTarget;
	}
	public void setInitialTradingRangeTarget(double initialTradingRangeTarget) {
		this.initialTradingRangeTarget = initialTradingRangeTarget;
	}
	public int getTradesNr() {
		return tradesNr;
	}
	public void setTradesNr(int tradesNr) {
		this.tradesNr = tradesNr;
	}
	public double getInitialTradeValue() {
		return initialTradeValue;
	}
	public void setInitialTradeValue(double initialTradeValue) {
		this.initialTradeValue = initialTradeValue;
	}
	public boolean getIsOk() {
		return this.isOk;
	}
	public void setIsOk(boolean ok) {
		isOk = ok;
	}
	public double getTickSize() {
		return this.tickSize;
	}
	public void setTickSize(double TickSize) {
		tickSize = TickSize;
	}

	public int getQtyPrecision() {
		return QtyPrecision;
	}
	public void setQtyPrecision(int qtyPrecision) {
		QtyPrecision = qtyPrecision;
	}
	public double getPercentProfitTarget() {
		return PercentProfitTarget;
	}
	public void setPercentProfitTarget(double percentProfitTarget) {
		PercentProfitTarget = percentProfitTarget;
	}
	public double getLastOrderPrice() {
		return LastOrderPrice;
	}
	public void setLastOrderPrice(double lastOrderPrice) {
		LastOrderPrice = lastOrderPrice;
	}
	public int getPricePrecision() {
		return PricePrecision;
	}
	public void setPricePrecision(int pricePrecision) {
		PricePrecision = pricePrecision;
	}
	
	public double getProfitPrice() {
		return ProfitPrice;
	}
	public void setProfitPrice(double profitPrice) {
		ProfitPrice = profitPrice;
	}
	public String getActiveSide() {
		return ActiveSide;
	}
	public void setActiveSide(String activeSide) {
		ActiveSide = activeSide;
	}
	public void setPerMode(boolean perMod) {
		this.perMode = perMod;
	}
	public boolean getPerMode() {
		return perMode;
	}	
	
	
	public double getMinLiqValue() {
		return MinLiqValue;
	}
	public void setMinLiqValue(double minLiqValue) {
		MinLiqValue = minLiqValue;
	}

	public void setIsInTrade(boolean isInTrade) {
		this.isInTrade = isInTrade;
	}
	public boolean getIsInTrade() {
		return isInTrade;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getLastTradedPrice() {
		return LastTradedPrice;
	}
	public void setLastTradedPrice(double lastTradedPrice) {
		LastTradedPrice = lastTradedPrice;
	}

	
	public double getMaxBalQty() {
		return MaxBalQty;
	}
	public void setMaxBalQty(double maxBalQty) {
		MaxBalQty = maxBalQty;
	}
	
	public int getLeverage() {
		return Leverage;
	}
	public void setLeverage(int leverage) {
		Leverage = leverage;
		try {
			BotBinance.OrderGenerator.ChangeLeverage(this.getName(),leverage);
		} catch (BinanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
			


	CompletableFuture<Void> updater=null;

		
	public void Reset() {
		
		
		this.isOk=true;
	this.setIsInTrade(false);
	this.setActiveSide("OFF");
	this.setTradesNr(0);
	this.setActiveBalQtyUSD(0);
	this.setActiveBalQtyCrypto(0);
	}
	

	
	
	public  CryptoCoin(String nume,double MaxBQty,double perProfitTarget, int lev, boolean perMod,double initialTradeValue,ArrayList<ProfitDeviationTarget> profitDeviations,ArrayList<TradingZone>tradingZones) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException, InterruptedException, ExecutionException {
		this.Name=nume.toUpperCase();	this.MaxBalQty=MaxBQty;
		this.PercentProfitTarget=perProfitTarget;
		this.Leverage=lev;
		this.perMode= perMod;
		this.initialTradeValue=initialTradeValue;
		this.profitDeviationsTargets=profitDeviations;
		this.tradingZones=tradingZones;
		System.out.println("TRADDDING ZONE"+tradingZones.get(0).getPriceDeviation());
		try{
		this.WsLastPrice=BotBinance.Generator.WsCurrentPrice(this);}
		catch(Exception e) {
			this.WsLastPrice=BotBinance.Generator.WsCurrentPrice(this);}

	
	System.out.println("A MERS ASDASDASDASD");
		
	}
	
	
	
	
	
	
	
	
	public CryptoCoin(String nume) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException, InterruptedException, ExecutionException {this.Name=nume;

	try{
	this.WsLastPrice=BotBinance.Generator.WsCurrentPrice(this);}
	catch(Exception e) {e.printStackTrace();
		//this.WsLastPrice=BotBinance.Generator.WsCurrentPrice(this);
		}
}
	
	
	public void DeletePair() {
	
		this.WsLiqProcess=null;
		
this.WsLastPrice=null;
try {
this.Updater.cancel(true);}
catch(Exception e) {}
try {
this.WsCheck.cancel(true);}
catch(Exception e) {}
		
	}
	

	public void StartWsLiquidation(){
		try {
			this.WsLiqProcess=BotBinance.Generator.WsMarketLiq(this);
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		this.WsCheck=CompletableFuture.runAsync(()-> {
			while(true) {
				
				try {
					
					Thread.sleep(6000);
				
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				System.out.print("Check "+this.Name);
				try{if(this.WsLiqProcess==null) {
			
				this.WsLastPrice= BotBinance.Generator.WsCurrentPrice(this);}
				else {if(!this.WsLastPrice.isOpen()) {this.WsLastPrice= BotBinance.Generator.WsCurrentPrice(this);}}
				System.out.println("Restart WsPice");
		
		}
					catch(Exception e) {e.printStackTrace();
						}
		//amogus omanus		
			
			try {
			if(this.WsLiqProcess==null) {
				this.WsLiqProcess=BotBinance.Generator.WsMarketLiq(this);}
			else{if(!this.WsLiqProcess.isOpen()) {
				System.out.println("Restart WsLiq");
			
				this.WsLiqProcess=BotBinance.Generator.WsMarketLiq(this);
				
				}}}
				catch(Exception e) {e.printStackTrace();}
				
			}
			});
		
	}		
	public void StopWsLiquidation() {
		
		this.WsLiqProcess=null;
		this.WsCheck.cancel(true);
	}
	
	

	public JSONObject toJson() {
		
		JSONObject rez=new JSONObject();
		rez.put("pair", this.Name);
		rez.put("perMode", this.perMode);
		rez.put("lev",this.Leverage);
		rez.put("perProfitTarget",this.PercentProfitTarget);
		rez.put("maxBalQty",this.MaxBalQty);
		rez.put("minLiqValue",this.MinLiqValue);
		rez.put("initialTradeValue", this.initialTradeValue);
		JSONArray JsProfitDeviationsTargets=new JSONArray();
		for(ProfitDeviationTarget p : profitDeviationsTargets) {
			JsProfitDeviationsTargets.put(p);
		}
		rez.put("profitDeviationsTargets", JsProfitDeviationsTargets);
		JSONArray JsTradingZones = new JSONArray();
		for(TradingZone t : this.tradingZones) {
			JsProfitDeviationsTargets.put(t);
		}
		rez.put("tradingZones", JsTradingZones);
		return rez;
	}		
		
		
		
	}

