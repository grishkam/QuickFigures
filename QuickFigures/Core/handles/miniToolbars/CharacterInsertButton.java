/*******************************************************************************
 * Copyright (c) 2026 Gregory Mazo
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
 * Date Created: Jan 24, 2026
 * Date Modified: Jan 24, 2026
 * Version: 2026.1
 */
package handles.miniToolbars;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_SpecialObjects.KeyCharInput;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.IconHandle;
import iconGraphicalObjects.ColorIcon;
import messages.ShowMessage;

/**A handle that shows a popup with a character array when clicked. based on a color array version*/
public class CharacterInsertButton extends IconHandle implements KeyCharInput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic item;
	private transient CanvasMouseEvent lastPress;
	private static final int hadleNum=94204;
	static char[] default_insertChars = new char[] {'µ', '\u0394', '\u2642', '\u2640', '\u00C5',/**math*/'\u00D7', '\u00B1', '\u00B0', '\u00B9','\u00B2', '\u00B3', '\u00BD', '\u00A9','\u221E' };
	
	
	/**Builds a new coloring button*/
	public CharacterInsertButton(TextGraphic itemForIcon ) {
		
		super(createTheIcon(), new Point(0,0));
		
		this.item=itemForIcon;
		this.xShift=5;
		this.yShift=5;
		this.setIcon(createTheIcon());
		this.setHandleNumber(hadleNum);
	}

	/**
	 * @return
	 */
	private static ColorIcon createTheIcon() {
		return new ColorIcon(Color.white,default_insertChars[0]+"", -3, 18);
	}
	
	@Override
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		showPopupMenu(canvasMouseEventWrapper);
	
	}

	/**shows the popup menu*/
	public void showPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
		lastPress=canvasMouseEventWrapper;
		String message="Insert charcter";
		
		
		getHandleListForPopup().showInPopupPalete(lastPress, message);;
	}

	/**
	 * @return
	 */
	private ColorButtonHandleList getHandleListForPopup() {
		return new ColorButtonHandleList(this, getDefaultInsertChars());
	}
	
	

	@Override
	public void handleKeyPressEvent(KeyEvent fie) {
		ShowMessage.showOptionalMessage("you have inserted your first symbol!", true, "You can now insert symbols in QuickFigures."," Note: Not every symbol works with every font","If your symbol does not appear, try changing the font (example 'Helvetica')","Also, not every  symbol is available in every export format");
		item.handleKeyPressEvent(fie);
		
		lastPress.getSelectionSystem().getWorksheet().updateDisplay();
		
		
	}
	
	public boolean isHidden() {
		if(item.isEditMode()) return false;
		return true;
		}
	
	public static ArrayList<Character> getDefaultInsertChars() {
		ArrayList<Character> output= new ArrayList<Character>();
		for(char c: default_insertChars) {
			output.add(c);
		}
	
		output.addAll(getDefaultSeriesChar('\u03B1', 25) ); //lowercase greek
		//output.addAll(getDefaultSeriesChar('\u0391', 25) ); //upercase greek
		output.addAll(getDefaultSeriesChar('\u2074', 27) );//subscripts and super scripts
		output.addAll(getDefaultSeriesChar('\u25A0', 60) );//shapes
		
		return output;
	}
	
	
	static ArrayList<Character> getDefaultSeriesChar(char c, int n) {
		ArrayList<Character> output= new ArrayList<Character>();
		
		for(int i=0; i<n; i++) {
			output.add(c);
			c=nextCharacter(c);
		}
		
		return output;
	}
	static char nextCharacter(char c) { return (char)(1+(int)c);}
	
	
	public static void main(String[] args) {
		for(char i: getDefaultInsertChars()) {
			System.out.println(i);
			//System.out.println((i=='\u00B5')+" is micron symbol");
		}
	}

}