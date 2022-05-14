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
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package handles.miniToolbars;

import java.awt.Point;

import applicationAdapters.CanvasMouseEvent;
import handles.IconHandle;
import selectedItemMenus.ColorMultiSelectionOperator;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;

/**A handle that shows a popup with a color array when clicked*/
public class ColoringButton extends IconHandle implements ColorInputListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColorMultiSelectionOperator item;
	private transient CanvasMouseEvent lastPress;

	/**Builds a new coloring button*/
	public ColoringButton(ColorMultiSelectionOperator itemForIcon, int handleNumber ) {
		
		super(itemForIcon.getIcon(), new Point(0,0));
		
		this.item=itemForIcon;
		this.xShift=5;
		this.yShift=5;
		this.setIcon(itemForIcon.getIcon());
		this.setHandleNumber(handleNumber);
	}
	
	@Override
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		showPopupMenu(canvasMouseEventWrapper);
	
	}

	/**shows the popup menu*/
	public void showPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
		lastPress=canvasMouseEventWrapper;
		String message="Change Fill Color";
		if(item.doesStroke()) message="Change Stroke Color";
		new ColorButtonHandleList(this).showInPopupPalete(lastPress, message);;
	}

	@Override
	public void ColorChanged(ColorInputEvent fie) {
		
		item.setSelector(fie.event.getSelectionSystem());
		item.onColorInput(fie);
		
		this.setIcon(item.getIcon());
		fie.event.getSelectionSystem().getWorksheet().updateDisplay();
		
		
	}

}