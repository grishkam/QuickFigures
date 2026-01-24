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

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_SpecialObjects.KeyCharInput;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.IconHandle;
import iconGraphicalObjects.ColorIcon;

/**A handle that shows a popup with a character array when clicked. based on a color array version*/
public class CharacterInsertButton extends IconHandle implements KeyCharInput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic item;
	private transient CanvasMouseEvent lastPress;
	private static final int hadleNum=94204;
	static char[] default_insertChars = new char[] {'µ', '\u0394', '\u2642', '\u2640', '\u00C5',/**math*/'\u00D7', '\u00B1', '\u00B0', '\u00B2', '\u00B3', '\u00B5', '\u00B9'/**greek*/,'\u03B1','\u03B2','\u03B3', '\u03B4','\u03B5', '\u03B6','\u03B7', '\u03B8','\u03B9'};

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
		return new ColorButtonHandleList(this, default_insertChars);
	}
	
	

	@Override
	public void handleKeyPressEvent(KeyEvent fie) {
		
		item.handleKeyPressEvent(fie);
		
		lastPress.getSelectionSystem().getWorksheet().updateDisplay();
		
		
	}
	
	public boolean isHidden() {
		if(item.isEditMode()) return false;
		return true;
		}

}