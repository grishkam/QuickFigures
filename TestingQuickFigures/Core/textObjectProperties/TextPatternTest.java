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

import org.junit.jupiter.api.Test;

/**
 
 * 
 */
class TextPatternTest {

	@Test
	void test() {
		boolean lowercase = true;
		boolean upercase = false;
		TextPattern pattern1 = new TextPattern(TextPattern.PatternType.ABC, lowercase);
		
		assert("a".equals(pattern1.getSymbol(1)));
		assert("b".equals(pattern1.getSymbol(2)));
		assert("z".equals(pattern1.getSymbol(26)));
		checkAllPossibleResults(pattern1);
		
		pattern1 = new TextPattern(TextPattern.PatternType.ABC, upercase);
		
		assert("A".equals(pattern1.getSymbol(1)));
		assert("B".equals(pattern1.getSymbol(2)));
		assert("Z".equals(pattern1.getSymbol(26)));
		assert("AA".equals(pattern1.getSymbol(27)));
		checkAllPossibleResults(pattern1);
		
		pattern1 = new TextPattern(TextPattern.PatternType.ROMAN_NUMBERAL, upercase);
		assert("I".equals(pattern1.getSymbol(1)));
		assert("IV".equals(pattern1.getSymbol(4)));
		assert("V".equals(pattern1.getSymbol(5)));
		assert("XXX".equals(pattern1.getSymbol(30)));
		assert("XXXV".equals(pattern1.getSymbol(35)));
		assert("XL".equals(pattern1.getSymbol(40)));
		assert("XLV".equals(pattern1.getSymbol(45)));
		assert("L".equals(pattern1.getSymbol(50)));
		assert("LX".equals(pattern1.getSymbol(60)));
		assert("LXX".equals(pattern1.getSymbol(70)));
		assert("LXXX".equals(pattern1.getSymbol(80)));
		assert("C".equals(pattern1.getSymbol(100)));
		assert("CC".equals(pattern1.getSymbol(200)));
		assert("CCC".equals(pattern1.getSymbol(300)));
		
		assert("MMM".equals(pattern1.getSymbol(3000)));
		
		
		checkAllPossibleResults(pattern1);
	}

	/**this method is run to make sure that no number will cause an exception
	 * @param pattern1
	 */
	protected void checkAllPossibleResults(TextPattern pattern1) {
		for(int i=-2; i<1000000; i++) {
			for(int c=0; c<5; c++) { 
			for(int a=0; a<5; a++) { 
					pattern1.setStartIndex(a);
					pattern1.setCountBy(c);
					pattern1.getText(i);
			  }
			}
		}
	}

}
