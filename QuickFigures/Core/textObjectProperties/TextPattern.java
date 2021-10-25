/**
 * Author: Greg Mazo
 * Date Modified: Oct 24, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
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
	
	/**numbering starts from one*/
	private int startIndex=1;
	
	String prefix="";
	String suffix="";

	public static enum PatternType  {
		ABC, 
		ROMAN_NUMBERAL,
		NUMBERS;
	}
	
	PatternType curruentType=PatternType.NUMBERS;
	private boolean lowerCase=false;

	public TextPattern() {}
	
	public TextPattern(PatternType theType) {
		 curruentType=theType;
	}
	public TextPattern(PatternType theType, boolean lowerCase) {
		this(theType);
		this.lowerCase=lowerCase;
	}
	
	/**creates a copy of this pattern*/
	 public TextPattern copy() {
		 TextPattern textPattern = new  TextPattern(this.curruentType, lowerCase);
		 giveTraitsTo(textPattern);
		return textPattern;
	 }

	/** copies the properties of this pattern onto the given pattern
	 * @param textPattern
	 */
	protected void giveTraitsTo(TextPattern textPattern) {
		textPattern.setStartIndex(startIndex);
		 textPattern.prefix=prefix;
		 textPattern.suffix=suffix;
	}
	
	/**returns the summary of the pattern. For example: 'a, b, c... ' */
	public String getSummary() {
		return getSymbols(new int[] {1, 2, 3, 4}) +" ...";
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
		return prefix+getSymbol(n)+suffix;
	}
	
	/**returns the n-th symbol in the sequence*/
	public String getSymbol(int n) {
		if(getStartIndex()>1)
			n=n+getStartIndex()-1;
		
		String output=""+n;
		if(this.curruentType==PatternType.NUMBERS)
			return output;
		
		if(this.curruentType==PatternType.ABC) {
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
		if(this.curruentType==PatternType.ROMAN_NUMBERAL) {
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
		
		if(this.lowerCase)
			output=output.toLowerCase();
		
		
		return output;
	};
	
	
	
	
	/**returns a list of patterns that are the default options*/
	public static ArrayList<TextPattern> getList() {
		ArrayList<TextPattern> output = new ArrayList<TextPattern>();
		
		output.add(new TextPattern( PatternType.NUMBERS, false));
		
		PatternType[] patterns = new PatternType[] {PatternType.ABC, PatternType.ROMAN_NUMBERAL};
		boolean[] cases = new boolean[] {true, false};
		for(PatternType type: patterns) {
			
			for(boolean theCase: cases) {
				output.add(
						 new TextPattern( type, theCase)
						);
			}
			
		}
		
		
		return output;
		
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	
}