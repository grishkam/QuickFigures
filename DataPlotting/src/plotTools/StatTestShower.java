package plotTools;

import java.io.Serializable;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.util.Precision;

import dataSeries.DataSeries;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import logging.IssueLog;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextLineSegment;

public class StatTestShower implements Serializable {


TextGraphic model=new TextGraphic(); {model.setFontSize(10);}
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String lastPValue;
private int tTestType;
private int numberTails;
private int markType;


public StatTestShower() {
	
}


public StatTestShower(int tTestType, int numberTails, int markType) {
	this.tTestType=tTestType;
	this.numberTails=numberTails;
	this.markType=markType;
}


protected TextGraphic createTextForPValue(double pValue) {
		
		
		
		boolean useExponent = !pValueAsStars()&&pValue<0.00001;
		if (markType==2 &&pValue<0.0001) useExponent =true;
		
		if (useExponent) {
			return pValueExponentVersion(pValue);
			
		}
		
		TextGraphic text = new ComplexTextGraphic(pValueToString(pValue));
		text.copyAttributesFrom(model);
		return text;
	}

	private ComplexTextGraphic pValueExponentVersion(double pValue) {
		double pval = Math.log10(pValue);
		double exponent = Math.ceil(pval);
		
		if (pValue<Math.pow(10, exponent)) {
			ComplexTextGraphic text = new ComplexTextGraphic("p<10");
			TextLine line = text.getParagraph().get(0);
			line.addSegment(exponent+"", text.getTextColor(), TextLineSegment.SUPER_SCRIPT);
			text.copyAttributesFrom(model);
			if (markType==2) {
				TextLineSegment seg = text.getParagraph().get(0).get(0);
				TextLineSegment seg2 = text.getParagraph().get(0).get(1);
				String st=""+Precision.round(10*Math.pow(10, (pval-((int)pval))), 2)+"*10";
				IssueLog.log("pVal is "+pValue);
				seg.setText(st);
				seg2.setText(""+(exponent-1));
			}
			return text;
		}
		
		return null;
	}


	/**Returns a string for the p value summary*/
	private String pValueToString(double pValue) {
		
		if (pValueAsStars()) {
			if (pValue<0.0001) return "****";
			if (pValue<0.001) return "***";
			if (pValue<0.01) return "**";
			if (pValue<0.05) return "*";
			return "ns";
		}
		
		if (markType==2) {
			
			
			if (pValue<0.00001)
				return  ("p="+ Precision.round(pValue, 6));
			if (pValue<0.0001)
				return  ("p="+ Precision.round(pValue, 5));
			if (pValue<0.001)
				return  ("p="+ Precision.round(pValue, 4));
			
			if (pValue<0.10)
			return  ("p="+ Precision.round(pValue, 3));
			
			if (pValue<1)
				return  ("p="+ Precision.round(pValue, 2));
		}
		
		if (pValue<0.00001) return "p<10^-5";
		if (pValue<0.0001) return "p<0.0001";
		if (pValue<0.001) return "p<0.001";
		if (pValue<0.01) return "p<0.01";
		if (pValue<0.05) return "p<0.05";
		if (pValue>0.05) return  ("p="+ Precision.round(pValue, 3));
		
		return ""+ pValue;
	}
	
	

	protected double calculatePValue(DataSeries data1, DataSeries data2) throws Exception {
		double[] d1 = data1.getIncludedValues().getRawValues();
		double[] d2 = data2.getIncludedValues().getRawValues();
		if (d1.length<3||d2.length<3) throw new Exception();
		double pValue = new TTest().tTest(d1, d2);
		if(this.tTestType==1) pValue=new TTest().homoscedasticTTest(d1, d2);
		if(this.tTestType==2) pValue=new TTest().pairedTTest(d1, d2);
		if (numberTails==1) pValue=pValue/2;
		lastPValue="t-Test done "+pValue;
		return pValue;
	}
	

boolean pValueAsStars() {return markType==1;}
}
