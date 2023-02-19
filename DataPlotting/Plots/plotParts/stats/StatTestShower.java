/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: April 28, 2021
 * Version: 2023.1
 */
package plotParts.stats;

import java.io.Serializable;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.util.Precision;

import dataSeries.DataSeries;
import dataSeries.KaplanMeierDataSeries;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;

/**A class that performs T-tests on the data series from plots
  and creates labels and marks to indicate significance*/
public class StatTestShower implements Serializable {
	private static final long serialVersionUID = 1L;
	
	TextGraphic model=new TextGraphic(); {model.setFontSize(10);}

	public static final int STAR_MARK=1, LESS_THAN_MARK=0, ROUNDED_NUMBER=2;;
	private int markType=LESS_THAN_MARK;




	
boolean showMessages=false;//set to true if details of tests should be printed to the log window.
private String lastPValue;


public static final int NORMAL_T_TEST=0, PAIRED=2, HOMOSCEDASTIC = 1;
private int tTestType=NORMAL_T_TEST;

public static final int TW0_TAIL=0, ONE_TAIL=1;
private int numberTails=TW0_TAIL;




public StatTestShower() {
	
}


public StatTestShower(int tTestType, int numberTails, int markType) {
	this.tTestType=tTestType;
	this.numberTails=numberTails;
	this.markType=markType;
}

/**returns a text item to display the p value given*/
public ComplexTextGraphic createTextForPValue(double pValue) {
		
		
		
		boolean useExponent = !pValueAsStars()&&pValue<0.00001;
		if (markType==ROUNDED_NUMBER &&pValue<0.0001) useExponent =true;
		
		if (useExponent) {
			return pValueExponentVersion(pValue);
			
		}
		
		ComplexTextGraphic text = new ComplexTextGraphic(pValueToString(pValue));
		text.copyAttributesFrom(model);
		return text;
	}

	/**returns a text item to display the p value given in scientific notation*/
	private ComplexTextGraphic pValueExponentVersion(double pValue) {
		double pval = Math.log10(pValue);
		double exponent = Math.ceil(pval);
		
		if (pValue<Math.pow(10, exponent)) {
			ComplexTextGraphic text = new ComplexTextGraphic("p<10");
			TextLine line = text.getParagraph().get(0);
			line.addSegment(exponent+"", text.getTextColor(), TextLineSegment.SUPER_SCRIPT);
			text.copyAttributesFrom(model);
			if (markType==ROUNDED_NUMBER) {
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
		
		if (markType==ROUNDED_NUMBER) {
			
			
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
	
	
	/**returns the p value for a t test beteen the data series*/
	public double calculatePValue(DataSeries data1, DataSeries data2) throws Exception {
		if  ((data1 instanceof KaplanMeierDataSeries)&&(data2 instanceof KaplanMeierDataSeries)) {
			return new LogRank((KaplanMeierDataSeries)data1, (KaplanMeierDataSeries)data2).getPValue();
		}
		
		
		double[] d1 = data1.getIncludedValues().getRawValues();
		double[] d2 = data2.getIncludedValues().getRawValues();
		if (d1.length<3||d2.length<3) throw new Exception();
		double pValue = new TTest().tTest(d1, d2);
		if(this.tTestType==HOMOSCEDASTIC) pValue=new TTest().homoscedasticTTest(d1, d2);
		if(this.tTestType==PAIRED) pValue=new TTest().pairedTTest(d1, d2);
		if (numberTails==1) pValue=pValue/2;
		lastPValue="t-Test done "+pValue;
		if (showMessages) {
			IssueLog.log(lastPValue);
		}
		return pValue;
	}
	

boolean pValueAsStars() {return markType==STAR_MARK;}
}
