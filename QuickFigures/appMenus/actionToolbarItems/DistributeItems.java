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
 * Version: 2022.2
 */
package actionToolbarItems;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import locatedObject.LocatedObject2D;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.UndoMoveItems;

/**Implements the distribute items menu options, complete with icons.
 * will move a set of objects to make them evenly spaced
 */
public class DistributeItems extends BasicMultiSelectionOperator {

	private static final long serialVersionUID = 1L;
	private boolean vertical;
	
	public DistributeItems(boolean vert) {
		vertical=vert;
	}

	@Override
	public String getMenuCommand() {
		return "Distribute Evenly "+(vertical? "Vertical" : "Horizontal");
	}

	@Override
	public void run() {
		
		ArrayList<LocatedObject2D> allObjects = super.getAllObjects();
		
		UndoMoveItems undo = new UndoMoveItems(allObjects, true);//undo
		if (allObjects==null||allObjects.size()<=1) {
			ShowMessage.showOptionalMessage("no object", true, "this option requires user to select more than one object");
			return;
		}
		distributeArray(allObjects);
		
		undo.establishFinalLocations();
		
		
		
		selector.getWorksheet().getUndoManager().addEdit(undo);
		
		
	}

	
	public void distributeArray(ArrayList<LocatedObject2D> all) { 
		Rectangle b= new AlignItem(0).getPanelBoundsCombined(all);
		if (all.size()<3) return;
		distributeArray(all, b);
	}
	
	public void distributeArray(ArrayList<LocatedObject2D> all, Rectangle b) {
			
		if (all.size()<2) return;//makes no sense to distribute less than 3
			
			UndoMoveItems undo = new UndoMoveItems(all);
			
			
			if (vertical)  {
				Collections.sort(all, new verticalComparison());
			} else  {
				Collections.sort(all, new horizontalComparison());
				}
			
			
			
			int border = 0;
			
			if (vertical)  {
				border=(b.height-totalHeights(all))/ (all.size()-1);
			} else  {
				border=(b.width-totalWidths(all))/(all.size()-1);
				}
			
			
			if (!vertical) {
				int x=b.x;
				for(LocatedObject2D l: all) {
					if (l instanceof PanelLayoutGraphic) {
						((PanelLayoutGraphic) l).generateCurrentImageWrapper();
					
						Rectangle panBounds = AlignItem.panelsCombined(l);
						PanelLayoutGraphic pan=(PanelLayoutGraphic) l;
						pan.moveLayoutAndContents( x-panBounds.x, 0);
						x+=border+panBounds.getWidth();
					} else {
					l.setLocationUpperLeft(x, l.getLocationUpperLeft().getY());				
					x+=border+l.getBounds().getWidth();
					}
					
				}
			}
			
			if (vertical) {
				int y=b.y;
				for(LocatedObject2D l: all) {
					if (l instanceof PanelLayoutGraphic) {
						((PanelLayoutGraphic) l).generateCurrentImageWrapper();
					
						Rectangle panBounds = AlignItem.panelsCombined(l);
						PanelLayoutGraphic pan=(PanelLayoutGraphic) l;
						pan.moveLayoutAndContents(0, y-panBounds.y);
						
						y+=border+panBounds.getHeight();
					} else {
						l.setLocationUpperLeft( l.getLocationUpperLeft().getX(), y);
						y+=border+l.getBounds().getHeight();
					}
				}
			}
			
			

		undo.establishFinalLocations();
		if (selector!=null) selector.getWorksheet().getUndoManager().addEdit(undo);
	}

	
	int totalWidths(Collection<LocatedObject2D> ob) { 
		int output = 0;
		for(LocatedObject2D object: ob) {
			
			//if (layout) output+=AlignMenuItem.panelsCombined(object).getWidth();
			//else
				output+=new AlignItem(0).getAligningRect(object).getBounds().width;
		}
		return output;
	}
	
	/**Adds up the heights of all the objects*/
	int totalHeights(Collection<LocatedObject2D> ob) { 
		int output = 0;
		for(LocatedObject2D object: ob) {
			 output+=new AlignItem(0).getAligningRect(object).getBounds().height;
		}
		return output;
	}
	
	
	class verticalComparison implements Comparator<LocatedObject2D> {

		@Override
		public int compare(LocatedObject2D arg0, LocatedObject2D arg1) {
			return (int) -(arg1.getBounds().getCenterY()-arg0.getBounds().getCenterY());
		
		}}
	
	class horizontalComparison implements Comparator<LocatedObject2D> {

		@Override
		public int compare(LocatedObject2D arg0, LocatedObject2D arg1) {
			return (int) -(arg1.getBounds().getCenterX()-arg0.getBounds().getCenterX());
		}}
	
	
	@Override
	public String getMenuPath() {
		return "Align";
	}
	

	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectangles();
		Color[] colors=new Color[] {Color.cyan, Color.magenta, Color.yellow.darker(), new Color((float)0.0,(float)0.0,(float)0.0, (float)0)};
		
		for(int i=0; i<rects.size(); i++ ) {
			Rectangle r=rects.get(i);
			
			RectangularGraphic rect = RectangularGraphic.blankRect(r, colors[i]);
			rect.setStrokeWidth(1);
			gg.getTheInternalLayer().add(rect);
				}
		
		
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	private ArrayList<Rectangle> getRectangles() {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		if(!vertical) {
			output.add(new Rectangle(0,2,5,8));
			output.add(new Rectangle(7,1,5,8));
			output.add(new Rectangle(14,2,5,5));
			output.add(new Rectangle(0,7,18,0));
						} else   {
							output.add(new Rectangle(4,0,6,4));
							output.add(new Rectangle(3,6,7,5));
							output.add(new Rectangle(4,13,6,5));
							output.add(new Rectangle(6,0,0,18));
					}  
		
		return output;
	}
	
	
	public Icon getIcon() {
		return  getItemIcon(true);
	}
	

}
