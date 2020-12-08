/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicalObjectHandles;

import java.awt.Color;

import javax.swing.JColorChooser;

import applicationAdapters.CanvasMouseEvent;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import utilityClassesForObjects.ColorDimmer;

/**A set of objects to be drawn in a color chooser style array. User will be able to select color. 
 */
public class ColorButtonHandleList extends ActionButtonHandleList {
	private Color[] standardColor=new Color[] { Color.black, Color.white, Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , 
			new Color(155, 255, 0), new Color(155, 0, 255), new Color( 0,155, 255), new Color( 255,155, 0),new Color( 0,255, 155), new Color( 255,0, 155)};
	


	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private ColorInputListener coloring;
	
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
		add(new ColorHandle());
		
	}
	
	void addColor(Color c) {
		add(new ColorHandle(c));
	}
	
	public class ColorHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color theColor;
		private boolean moreColors;
		{super.handleStrokeColor=Color.DARK_GRAY.darker().darker();}

		public ColorHandle(int x, int y) {
			super(x, y);
			this.handlesize=8;
			maxGrid=standardColor.length;
		}

		public ColorHandle(Color c) {
			this(0,0);
			this.setHandleColor(c);
			this.setTheColor(c);
			if(c==null||c.getAlpha()==0)
				{
				this.setHandleColor(Color.white);
				super.setSpecialFill(SmartHandle.CROSS_FILL);
				super.decorationColor=Color.red;
			}
		}
		
		public ColorHandle() {
			this(0,0);
			this.setHandleColor(Color.white);
			super.setSpecialFill(SmartHandle.RAINBOW_FILL);
			this.moreColors=true;
			
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
