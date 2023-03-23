package BotBinance;

import org.json.JSONObject;

public class TradingZone {
	private double tradeNr;
	private double priceDeviation;
	public double getTradeNr() {
		return tradeNr;
	}
	public void setTradeNr(double tradeNr) {
		this.tradeNr = tradeNr;
	}
	public double getPriceDeviation() {
		return priceDeviation;
	}
	public void setPriceDeviation(double priceDeviation) {
		this.priceDeviation = priceDeviation;
	}
	public TradingZone(double tradeNr, double priceDeviation) {
				this.tradeNr = tradeNr;
		this.priceDeviation = priceDeviation;
	}
	
public JSONObject toJson() {
	JSONObject rez= new JSONObject();
	rez.put("tradeNr", tradeNr);
	rez.put("priceDeviation", priceDeviation);
	return rez;

	
}
}
