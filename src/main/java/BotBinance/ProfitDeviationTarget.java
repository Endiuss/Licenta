package BotBinance;

import org.json.JSONObject;

public class ProfitDeviationTarget {
private double rangeDeviation;
private double qtyPercent;
public double getRangeDeviation() {
	return rangeDeviation;
}
public void setRangeDeviation(double rangeDeviation) {
	this.rangeDeviation = rangeDeviation;
}
public double getQtyPercent() {
	return qtyPercent;
}
public void setQtyPercent(double qtyPercent) {
	this.qtyPercent = qtyPercent;
}
public ProfitDeviationTarget(double rangeDeviation, double qtyPercent) {
	super();
	this.rangeDeviation = rangeDeviation;
	this.qtyPercent = qtyPercent;
}
public JSONObject toJson() {
	JSONObject rez= new JSONObject();
	rez.put("rangeDeviation", rangeDeviation);
	rez.put("qtyPercent", qtyPercent);
	return rez;

	
}
}
