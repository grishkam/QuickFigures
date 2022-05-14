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
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import includedToolbars.StatusPanel;
import layout.PanelContentExtract;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import locatedObject.LocatedObject2D;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;


/**A tool to change the number of rows and cols in a layout. when shift is held down
  the content of the panels (not only the layout) will also be moved*/
public class RowColNumberTool extends GeneralLayoutEditorTool {

	/**this determines if the objects within a panel are effected*/
	boolean movePanelContent=false;
	
	/**the layout being worked on. not always the same as the layout that was clicked on*/
	DefaultLayoutGraphic ml;
	BasicLayout bm;
	
	
	private LocatedObject2D r;
	private List<PanelContentExtract> extrapanel;
	
	/**undo that may be added*/
	private UndoLayoutEdit undo;
	private UndoAddItem undo2;
	
	
	
	/**sets up the fields in this class*/
	public void mousePressed() {
		findClickedLayout(true);
		extrapanel=null;
		if (this.hasALayoutBeenClicked() ) {
			
			ml=layoutGraphic;
			bm=super.getCurrentLayout();
			undo=new UndoLayoutEdit(layoutGraphic);
			undo2=null;
		}
		else
		{
			Rectangle bounds = this.getImageClicked().getOverlaySelectionManagger().getSelectionBounds1();
			if (bounds==null) return;
			if (bounds.getWidth()==0) return;
			
			bm=new BasicLayout();
			bm.setLayoutBasedOnRect(bounds);
			
			 ml = new DefaultLayoutGraphic(bm);
			this.getImageClicked().getTopLevelLayer().add(ml);
			super.layoutGraphic=ml;
			undo2=new UndoAddItem(getImageClicked().getTopLevelLayer(), ml);
			undo=null;
		}
		
		
		getImageClicked().updateDisplay();
	
		
	}
	
	/**performs the edit*/
	public void performDragEdit(boolean b) {
		int[] rowcol=new int[] {};
		int dragCordinateY = this.getDragCordinateY();
		int dragCordinateX = this.getDragCordinateX();
		BasicLayout bm = getCurrentLayout();
		rowcol = findAddedRowsCols(dragCordinateX, dragCordinateY, bm);
		
		ArrayList<PanelContentExtract> panels=null;
		if (movePanelContent) {
			panels = getLayoutEditor().cutStack(bm);
			
		}
		
		/**this part actually changes the number of rows and columns*/
		if (rowcol[0]+bm.nRows()>=1)getLayoutEditor().addRows(bm, rowcol[0]);
		if (rowcol[1]+bm.nColumns()>=1)getLayoutEditor().addCols(bm, rowcol[1]);
		
		/**after the change, the former content of the layout panels must be put back*/
		if (movePanelContent&&panels!=null) {
			
						/**handles loose panels*/
						if(bm.nPanels()<panels.size()) {
							List<PanelContentExtract> oldextra = extrapanel;//if former extra panels exits best not to forget them
							extrapanel=panels.subList(bm.nPanels(), panels.size());//stores extra panels
							if (oldextra!=null)extrapanel.addAll(oldextra);//completes the list to include new and former extra
						}
						else {
							if(bm.nPanels()>panels.size()&&extrapanel!=null) {
								int nneeded = bm.nPanels()-panels.size();
								if (nneeded>extrapanel.size()) nneeded=extrapanel.size();//in case the extra panel list is too short
								panels.addAll(extrapanel.subList(0, nneeded ));//adds as many extra panels as needed or available
										if(nneeded<extrapanel.size()) {
											extrapanel=extrapanel.subList(nneeded, extrapanel.size());
										} else
														extrapanel=null;
							}
						}
						
			
			getLayoutEditor().pasteStack(bm, panels );//puts the panels back
		}
		
		
		if (undo!=null) {
			undo.establishFinalLocations();
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
		if (undo2!=null) {
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo2);
		}
	}

	/**When given an x value, a y value and a layout. returns the number of rows and columns that
	  would need to be added or subtracted in order for the layout's last row or column to be near the 
	  x and y position. required so that the number of columns and rows can be changed in response to mouse drags 
	  At the moment this method is accurate for layouts that have rows/columns of uniform width. 
	  Tools is still usable for non-uniform layouts*/
	public static int[] findAddedRowsCols(int dragCordinateX, int dragCordinateY, BasicLayout bm) {
		int[] rowcol;
		Rectangle bound = bm.getBoundry().getBounds();
		
		
		double col=dragCordinateX-bound.getWidth()-bound.getX();
		double row=dragCordinateY-bound.getHeight()-bound.getY();
		
		col/=bm.getStandardPanelWidth();
		row/=bm.getStandardPanelHeight();
		int row2=(int)Math.ceil(row);
		int col2=(int)Math.ceil(col);
		rowcol=new int[] {row2, col2};
		return rowcol;
	}
	
	public void mouseEntered() {
		r=this.getImageClicked().getOverlaySelectionManagger().getSelection(0);
	}
	
	
	public LocatedObject2D markerRoi() { 
		LocatedObject2D mroi = super.markerRoi();
		if (mroi==null)
			return r;
		
		return mroi;
	}
	
	@Override
	public boolean keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			StatusPanel.updateStatus("Shift down, will include panel contends in move");
			movePanelContent=true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			movePanelContent=false;
		}
		return false;
	}
	
	@Override
	public String getToolTip() {
			return "Change the number of Rows and Columns (Try holding SHIFT to move content)";
		}
	
	@Override
	public String getToolName() {
			return "Panel Number Change Tool";
		}
	
	
	{this.setIconSet(new  NRowNColNumberIcon(0).generateIconSet());}
	
	/**the icon to use for this tool*/
	class NRowNColNumberIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public NRowNColNumberIcon(int type) {
			super(type);
			super.paintBoundry=false;
			super.panelColor=new Color[] {GREEN_TONE};
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			BasicLayout layout = new BasicLayout(1, 2, 5, 5, 2,2, true);
			
			if(type!=NORMAL_ICON_TYPE) {
				layout = new BasicLayout(3, 2, 5, 5, 2,2, true);
			}
			layout.setLabelSpaces(1, 1,1,1);
			layout.move(2,2);
			
			return layout;
		}
		
		/**
		 * @param type
		 * @return
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new NRowNColNumberIcon(type);
		}
	}
}
