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
 * Date Modified: Dec 23, 2021
 * Version: 2021.2
 */
package textObjectProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**this class describes a regular pattern of characters. 
 * Work in progress since roman numerals do not go up accurately after 
 * a certain number.*/
public class TextPattern implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**contains the first 20 roman numerals*/
	public static final String[] romanNumerals=new String[] {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"};
	
	/**contains the first 20 word numerals*/
	public static final String[] wordNumerals=new String[] {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"};
	
	
	/**numbering starts from one*/
	private int startIndex=1;
	
	private String prefix="";
	private String suffix="";
	private int minimunLength=1;//if the numbers must be a certain number of characters long (example 01, 0001 instead of 1). this can be set to numbers above 1
	private int countBy=1;
	
	public static enum PatternType  {
		ABC, 
		ROMAN_NUMBERAL,
		NUMBERS,
		ONE_TWO_THREE,
		MINUTES_SECONDS;
	}
	
	
	PatternType currentType=PatternType.NUMBERS;
	private SmartLabelDataType indexSystem=SmartLabelDataType.LOCATION_IN_FIGURE;
	
	private boolean lowerCase=false;

	public TextPattern() {}
	
	public TextPattern(PatternType theType) {
		 currentType=theType;
	}
	public TextPattern(PatternType theType, boolean lowerCase) {
		this(theType);
		this.lowerCase=lowerCase;
	}
	
	/**creates a pattern for numbers*/
	public TextPattern(int minLength) {
		this(PatternType.NUMBERS);
		this.minimunLength=minLength;
	}
	
	/**creates a copy of this pattern*/
	 public TextPattern copy() {
		 TextPattern textPattern = new  TextPattern(this.currentType, lowerCase);
		 giveTraitsTo(textPattern);
		return textPattern;
	 }

	/** copies the properties of this pattern onto the given pattern
	 * @param textPattern
	 */
	protected void giveTraitsTo(TextPattern textPattern) {
		textPattern.setStartIndex(startIndex);
		 textPattern.setPrefix(prefix);
		 textPattern.setSuffix(suffix);
	}
	
	/**returns the summary of the pattern. For example: 'a, b, c... ' */
	public String getSummary() {
	
		
		String output = getSymbols(new int[] {1, 2, 3, 4}) +" ...";
		if(currentType==PatternType.MINUTES_SECONDS) {
			output+= " (m:s)";
		}
		
		return output;
	}
	
	/**returns the list of symbols as a single String*/
	public String getSymbols(int[] list) {
		String output="";
		for(int i=0; i<list.length; i++) {
			if(i>0)
				output+=", ";
			output+=getSymbol(list[i]);
		} 
		return output;
	}
	
	/**returns the label for the n=th item*/
	public String getText(int n) {
		
		if(getStartIndex()!=1)
			n=n+getStartIndex()-1;
		
		if (n>getStartIndex())
			n=getStartIndex()+(n-getStartIndex())*getCountBy();
		
		String symbol = getSymbol(n);
		return prefixAndSuffix(symbol);
	}

	/**
	 * @param symbol
	 * @return
	 */
	public String prefixAndSuffix(String symbol) {
		return getPrefix()+symbol+getSuffix();
	}
	
	/**returns the n-th symbol in the sequence*/
	public String getSymbol(int n) {
		
	
		String output=""+n;
		if(this.currentType==PatternType.NUMBERS)
					{
						if(output.length()<this.getMinimunLength()) {
							int difference = getMinimunLength()-output.length();
							for(int i=0; i<difference; i++) {output="0"+output;}
						}
						
						return output;
					}
		
		if(this.currentType==PatternType.MINUTES_SECONDS) {
			String hour=null;
			String min =""+ (n/60);
			if(n/60>=60)
				{
				min=""+(n/60)%60;
				hour=""+(n/60)/60;
				if(min.length()<2) 
					min="0"+min;
				}
			String sec = ""+(n%60);
			if(sec.length()<2) 
				sec="0"+sec;
			String min_sec = min+":"+sec;
			if(hour!=null)
				return hour+":"+min_sec;
			return min_sec;
		}
		
		if(this.currentType==PatternType.ABC) {
			int i=(int) 'A';
			int fold = (n-1)%(26);
			int shift = (n-1)/26;
			i=i+fold;
			char c=(char) i;
			output= c+"";
			if(shift>0) 
				output=getSymbol(shift)+output;
		}
		
		/**return roman numerals up to 100*/
		if(this.currentType==PatternType.ROMAN_NUMBERAL) {
			
			if(n<=0)
				return " ";
			if(n<=20)
				output= romanNumerals[n-1];
			else
			if(n<30)
				output= "X"+romanNumerals[n-1-10];
			else if(n<40)
				output= "XX"+romanNumerals[n-1-20];
			else if (n==40)
				output="XL";
			else if(n<50)
				output= "XL"+romanNumerals[n-1-40];
			else if (n==50)
				output="L";
			else if(n<90)
				output= "L"+this.getSymbol(n-50);
			else if (n==90)
				output="XC";
			else if (n<100)
				output="XC"+romanNumerals[n-90];
			else if (n==100)
				output="C";
			else if (n>100&&n<400)
				output="C"+this.getSymbol(n-100);
			else if (n==400)
				output="CD";
			else if (n<500)
				output="CD"+this.getSymbol(n-400);
			else if (n==500)
				output="D";
			else if (n<900)
				output="D"+this.getSymbol(n-500);
			else if (n==900)
				output="CM";
			else if (n<1000)
				output="CM"+this.getSymbol(n-900);
			else if (n==1000)
				output="M";
			else if (n<4000)
				output="M"+this.getSymbol(n-1000);
		}
		
		if(currentType==PatternType.ONE_TWO_THREE) {
			if(n==0)
				return "Zero";
			if(n<=wordNumerals.length)
				output=wordNumerals[(n-1)];
		}
		
		if(this.lowerCase)
			output=output.toLowerCase();
		
		
		return output;
	};
	
	
	
	
	/**returns a list of patterns that are the default options*/
	public static ArrayList<TextPattern> getList() {
		ArrayList<TextPattern> output = new ArrayList<TextPattern>();
		
		output.add(new TextPattern( PatternType.NUMBERS, false));
		
		PatternType[] patterns = new PatternType[] {PatternType.ABC, PatternType.ROMAN_NUMBERAL, PatternType.ONE_TWO_THREE};
		boolean[] cases = new boolean[] {true, false};
		for(PatternType type: patterns) {
			
			for(boolean theCase: cases) {
				output.add(
						 new TextPattern( type, theCase)
						);
			}
			
		}
		
		output.add(new TextPattern( 2));
		output.add(new TextPattern( 3));
		
		/**Adds a minutes/second one*/
		TextPattern e = new TextPattern(PatternType.MINUTES_SECONDS);
		output.add(e);
		
	
		return output;
		
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getMinimunLength() {
		return minimunLength;
	}

	public void setMinimunLength(int minimunLength) {
		this.minimunLength = minimunLength;
	}

	public int getCountBy() {
		return countBy;
	}

	/**sets which integer the pattern advances by as it moves from the first index*/
	public void setCountBy(int countBy) {
		if(countBy<1)
			countBy=1;
		this.countBy = countBy;
	}
	
	
	
	public SmartLabelDataType getCurrentIndexSystem() {
		return indexSystem;
	}

	public void setCurrentIndexSystem(SmartLabelDataType currentNumber) {
		this.indexSystem = currentNumber;
	}
	
	
}