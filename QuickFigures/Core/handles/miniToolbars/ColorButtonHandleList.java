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
 * Version: 2023.1
 */
package handles.miniToolbars;

import java.awt.Color;

import javax.swing.JColorChooser;

import applicationAdapters.CanvasMouseEvent;
import handles.SmartHandle;
import locatedObject.ColorDimmer;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;

/**A set of colored rectangles to be drawn in a color chooser style array. User will be able to select color. 
 */
public class ColorButtonHandleList extends ActionButtonHandleList {
	private Color[] standardColor=new Color[] { Color.black, Color.white, Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , 
			new Color(155, 255, 0), new Color(155, 0, 255), new Color( 0,155, 255), new Color( 255,155, 0),new Color( 0,255, 155), new Color( 255,0, 155)};

	
	private static final long serialVersionUID = 1L;
	private ColorInputListener coloring;//responds to a color choice
	
	/**builds an array for color handles*/
	public  ColorButtonHandleList(ColorInputListener c) {
		this.coloring=c;
		
		for(int i=4; i>=0; i--) {
			for(Color color: standardColor) {
				Color theC = color;
				for(int j=i; j>-1; j--) theC=ColorDimmer.desaturateColor(theC);
				addColor(theC);
			}
			}
		
		for(int i=0; i<8; i++) {
		for(Color color: standardColor) {
			Color theC = color;
			for(int j=i; j>0; j--) theC=theC.darker();
			addColor(theC);
		}
		}
		
		add(new ColorHandle(new Color(0,0,0,0)));
		add(new ColorHandle(true));
		
	}
	
	void addColor(Color c) {
		add(new ColorHandle(c));
	}
	
	/**An individual color handle*/
	public class ColorHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color theColor;//the color for this handle
		private boolean moreColors;//set to true if a Jcolorchooser should be shown instead of 
		{super.handleStrokeColor=Color.DARK_GRAY.darker().darker();}

	

		public ColorHandle(Color c) {
			this(false);
			this.setHandleColor(c);
			this.setTheColor(c);
			if(c==null||c.getAlpha()==0)
				{
				this.setHandleColor(Color.white);
				super.setSpecialFill(CROSS_FILL);
				super.decorationColor=Color.red;
			} else
			super.setSpecialFill(NORMAL_FILL);
		}
		
		
		/**builds a handle. if the boolean is set to true
		 * this handle will be a special handle that appears as a rainbow and 
		 * shows a color chooser when clicked*/
		public ColorHandle(boolean rainbow) {
			this.handlesize=8;
			maxGrid=standardColor.length;
			this.setHandleColor(Color.white);
			if (rainbow) {
				super.setSpecialFill(SmartHandle.RAINBOW_FILL);
				this.moreColors=true;
			}
		}
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			if (moreColors) {
				theColor=JColorChooser.showDialog(null, "Color", getTheColor());
				addColor(theColor);
			}
				ColorInputEvent fie = new ColorInputEvent(null, canvasMouseEventWrapper.getComponent(),  this.getTheColor());
				fie.event=canvasMouseEventWrapper;
				coloring.ColorChanged(fie);
		}

		public Color getTheColor() {
			return theColor;
		}

		public void setTheColor(Color theColor) {
			this.theColor = theColor;
		}
	}

}
