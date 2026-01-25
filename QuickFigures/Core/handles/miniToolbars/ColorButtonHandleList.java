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
 * Date Modified: Jan 25, 2026
 * Version: 2026.1
 */
package handles.miniToolbars;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JColorChooser;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_SpecialObjects.KeyCharInput;
import handles.SmartHandle;
import locatedObject.ColorDimmer;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;

/**A set of colored rectangles to be drawn in a color chooser style array. User will be able to select color.
Adapted to include a character selection feature.
 */
public class ColorButtonHandleList extends ActionButtonHandleList {
	private Color[] standardColor=new Color[] { Color.black, Color.white, Color.blue, Color.green, Color.red,  Color.cyan, Color.magenta, Color.yellow , 
			new Color(155, 255, 0), new Color(155, 0, 255), new Color( 0,155, 255), new Color( 255,155, 0),new Color( 0,255, 155), new Color( 255,0, 155)};

	
	private static final long serialVersionUID = 1L;
	private ColorInputListener coloring;//responds to a color choice
	private KeyCharInput lettering;//responds to a letter
	
	/**builds an array for color handles*/
	public  ColorButtonHandleList(ColorInputListener colorListener) {
		this.coloring=colorListener;
		
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
		
		add(new ColorPalleteHandle(new Color(0,0,0,0)));
		add(new ColorPalleteHandle(true));
		
	}
	
	public ColorButtonHandleList(KeyCharInput listendsForKeys, Iterable<Character> letters) {
		lettering=listendsForKeys;
		for(char c: letters) {
			addLetter(c);
		}
	}
	
	
	

	void addColor(Color c) {
		add(new ColorPalleteHandle(c));
	}
	
	ColorPalleteHandle addLetter(char c) {
		ColorPalleteHandle e = new ColorPalleteHandle(c);
		
		add(e);
		return e;
	}
	
	/**An individual color handle*/
	public class ColorPalleteHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color theColor;//the color for this handle
		Character letter=null;
		private boolean moreColors;//set to true if a Jcolorchooser should be shown instead of 
		{super.handleStrokeColor=Color.DARK_GRAY.darker().darker();}

	
		/**Attempted*/
		public ColorPalleteHandle(char c) {
			this(Color.white);
			letter=c;
			if(letter!=null) {
				this.message=letter+"";
				message_over_handle = true;
				message_font = new Font("Helvetica", Font.BOLD, 12);
			}
		}

		public ColorPalleteHandle(Color c) {
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
		public ColorPalleteHandle(boolean rainbow) {
			this.handlesize=8;
			maxGrid=standardColor.length;
			this.setHandleColor(Color.white);
			if (rainbow) {
				super.setSpecialFill(SmartHandle.RAINBOW_FILL);
				this.moreColors=true;
			}
		}
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			
			if(letter!=null) {
				lettering.handleKeyPressEvent(new KeyEvent(canvasMouseEventWrapper.getComponent(), 0, System.currentTimeMillis(), 0, 0, letter));
			} else
			afterColorPress(canvasMouseEventWrapper);
		}

		/**
		 * @param canvasMouseEventWrapper
		 */
		private void afterColorPress(CanvasMouseEvent canvasMouseEventWrapper) {
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
