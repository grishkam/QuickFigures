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
 * Version: 2021.1
 */
package genericMontageLayoutToolKit;
import layout.PanelContentExtract;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import layout.plasticPanels.PlasticPanelLayout;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import icons.IconSet;

/**A tool that inserts rows and columns at specific positions in a figure. can also extract them*/
public class PanelGrabberTool extends GeneralLayoutEditorTool implements 
		LayoutSpaces{
	
	static IconSet set1=new PanelGrabberToolIcon(0, COLS).generateIconSet();//=new IconSet("icons/PannelGrabber.jpg","icons/PannelGrabberPressed.jpg","icons/PannelGrabberRollOver.jpg");
	static IconSet set2=new PanelGrabberToolIcon(0, ROWS).generateIconSet();// new IconSet("icons/PannelGrabber2.jpg","icons/PannelGrabberPressed2.jpg","icons/PannelGrabber2RollOver.jpg");
	static IconSet set3=new PanelGrabberToolIcon(0, PANELS).generateIconSet();//=new IconSet("icons/PannelGrabber3.jpg","icons/PannelGrabberPressed3.jpg","icons/PannelGrabber3RollOver.jpg");
	
	
	public int mode=COLS;
	{this.removalPermissive=true;setIconSet(set1);}
	
	public PanelGrabberTool(int mode) {
		this.mode=mode;
		
	}

	
	public void mouseMoved() {
		super.mouseMoved();
		setCursorBasedOnExcerpt();
	} 
	
	/**either adds a row/column or removes one depending on whether shift is down*/
	public void  performPressEdit()  {
		
		boolean shift=!shiftDown();
		
		try{
			
		if (mode==COLS) {
			int index= getCurrentLayout().makeAltered(COLS).getPanelIndex(getClickedCordinateX(), getClickedCordinateY());

			if (!shift)  getLayoutEditor().lastCol= getLayoutEditor().removeColumn(getCurrentLayout(), index);
			else {		 getLayoutEditor().addColumn(getCurrentLayout(), index,  getLayoutEditor().lastCol);
						 getLayoutEditor().lastCol=null;
						}

		}
		
		if (mode==ROWS) {
			int index= getCurrentLayout().makeAltered(ROWS).getPanelIndex(getClickedCordinateX(), getClickedCordinateY());
		if (!shift)  getLayoutEditor().lastRow= getLayoutEditor().removeRow(getCurrentLayout(), index);
		else {		 getLayoutEditor().addRow(getCurrentLayout(), index,  getLayoutEditor().lastRow);
					 getLayoutEditor().lastRow=null;
					}
		}
		if (mode==PANELS) {
			 getLayoutEditor().deleteInsertPanel(getCurrentLayout(), getCurrentLayout().getPanelIndex(getClickedCordinateX(), getClickedCordinateY()), shift, PANELS); 
		}
		
		
		
		setCursorBasedOnExcerpt();
		
		} catch (Exception ex) {IssueLog.log("Exception occured when trying to edit monatage ", ex);}
		
		
	}
	@Override
	public int markerType() {
		return mode;
	}
	
	
	public void setCursorBasedOnExcerpt() {
	
		if (getLast()==null){ 
			
			return;}
		Image last = getLast().getFittedImage(new Dimension(60,60));
	
		setCursorIcon(last);
		

	}
	
	
	
	public PanelContentExtract getLast() {
		if (mode==COLS) return  getLayoutEditor().lastCol;
		if (mode==ROWS) return  getLayoutEditor().lastRow;
		if (mode==PANELS) return getLayoutEditor().lastPanel;
		return null;
	}
	
	private String getTextBase() {
		if (mode==COLS) return  "Column";
		if (mode==ROWS) return  "Row";
		if (mode==PANELS) return "Panel";
		return "";
	}
	
	//public 
	
	/**
	public void setCurrentCursorTolastClicked() {
		setCursorIcon(Toolkit.getDefaultToolkit().createCustomCursor(getCursorIcon(), new Point(0,0), "excerpt"), 0);
		//ImageCanvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon, new Point(0,0), "excerpt") , 0);
	}
	*/
	public void showOptionsDialog() {
		StandardDialog gd=new StandardDialog(getClass().getName().replace("_", " "), true);
		
		String[] option2=new String[] {"Panel Adder (shift to cut)", "Column Adder (shift to remove)", "Row Adder (shift to remove)"};
		gd.add("Adjust", new ChoiceInputPanel("Adjust ", option2, mode));
		gd.showDialog();
		
		if (gd.wasOKed()) {	
			mode=gd.getChoiceIndex("Adjust");
				}
	}
	

	@Override
	public IconSet getIconSet() {
		if (mode==COLS) return set1;
		if (mode==PANELS) return set3;
		if (mode==ROWS) return set2;
		return set1;
	}
	
	public String getToolName() {return this.getTextBase() +" Grabber";}

	
	@Override
	public String getToolTip() {
			
			return "Take "+ getTextBase()+" in and out (hold shift)";
		}
	
	@Override
	public String getToolSubMenuName() {
		return "Expert Tools";
	}

	
	/**the icon for the panel grabbing tool. written to replace icon that looked good on some computers
	 * but not others*/
	static class PanelGrabberToolIcon extends GeneralLayoutToolIcon {

		private int theMode=COLS;

		/**
		 * @param type
		 */
		public PanelGrabberToolIcon(int type, int mode) {
			super(type);
			this.theMode=mode;
			super.paintBoundry=false;
			super.panelColor=new Color[] {BLUE_TONE, RED_TONE, BLUE_TONE};
			if(mode==PANELS) {
				super.panelColor=new Color[] {BLUE_TONE, RED_TONE, GREEN_TONE, YELLOW_TONE, MAGENTA_TONE};
			}
			
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			int size=6;
			if (theMode==COLS) {
				if (type==NORMAL_ICON_TYPE) {
					int yLoc=15;
					
					Rectangle r1 = new Rectangle(1, yLoc, size, size);
					Rectangle r2 = new Rectangle(8, 2, size, size);
					Rectangle r3 = new Rectangle(15, yLoc, size, size);
					
					PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2, r3);
					
					return layout2;
				}
				
				BasicLayout layout = new BasicLayout(3, 1, size, size, 2,2, true);
				layout.setLabelSpaces(2, 2,2,2);
				layout.move(0,10);
				return layout;
			}
			
			if (theMode==ROWS) {
				if (type==NORMAL_ICON_TYPE) {
					int xLoc=15;
					
					Rectangle r1 = new Rectangle(xLoc, 1, size, size);
					Rectangle r2 = new Rectangle(2, 8, size, size);
					Rectangle r3 = new Rectangle(xLoc, 15, size, size);
					
					PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2, r3);
					
					return layout2;
				}
				
				BasicLayout layout = new BasicLayout(1, 3, size, size, 2,2, true);
				layout.setLabelSpaces(2, 2,2,2);
				layout.move(14,0);
				return layout;
			}
			
			if (theMode==PANELS) {
				size=size-1;
				BasicLayout layout = new BasicLayout(2, 2, size, size, 2,2, true);
				layout.setLabelSpaces(2, 2,2,2);
				layout.move(4,8);
				
				if (type==NORMAL_ICON_TYPE) {
					int xLoc=10;
					int yLoc=2;
					
					Rectangle2D r1 = layout.getPanel(1);
					Rectangle r2 = new Rectangle(xLoc, yLoc, size, size);
					Rectangle2D r3 = layout.getPanel(2);
					Rectangle2D r4 = layout.getPanel(3);
					Rectangle2D r5 = layout.getPanel(4);
					
					PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2, r3, r4, r5);
					
					return layout2;
				}
				
				
				return layout;
			}
			
			else return super.createSimpleIconLayout(type);
		}
		
		protected void drawPanel(Graphics2D g2d, Rectangle2D p, int count) { 
			super.drawPanel(g2d, p, count);
			if (count==1)
				paintArrows(g2d, p);
			
		}

		/**
		 * @param g2d
		 * @param p
		 * @param count
		 */
		protected void paintArrows(Graphics2D g2d, Rectangle2D p) {
			Point2D loc = RectangleEdges.getLocation(RectangleEdges.CENTER, p);
			int length = 9;
			int headSize = 2;
			if (type==NORMAL_ICON_TYPE) {
				if(theMode==ROWS)
					super.paintArrow(g2d, (int)loc.getX()+3, (int)loc.getY(), length, RectangleEdges.RIGHT, headSize);
				if(theMode==COLS)
					super.paintArrow(g2d, (int)loc.getX(), (int)loc.getY()+3, length, RectangleEdges.BOTTOM, headSize);
				if(theMode==PANELS)
					super.paintArrow(g2d, (int)loc.getX(), (int)loc.getY(), length-1, RectangleEdges.BOTTOM, headSize);
			}
			else  {
				if(theMode==ROWS)
					super.paintArrow(g2d, (int)loc.getX()-3, (int)loc.getY(), length, RectangleEdges.LEFT, headSize);
				if(theMode==COLS)
					super.paintArrow(g2d, (int)loc.getX(), (int)loc.getY()-3, length, RectangleEdges.TOP, headSize);
				if(theMode==PANELS)
					super.paintArrow(g2d, (int)loc.getX(), (int)loc.getY(), length-1, RectangleEdges.TOP, headSize);
			}
		}
		
		/**
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new PanelGrabberToolIcon(type, theMode);
		}
		
		public GeneralLayoutToolIcon copy(int type) {
			GeneralLayoutToolIcon another = generateAnother(type);
			return another;
		}
	}
	

}
