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
package actionToolbarItems;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import layout.basicFigure.LayoutSpaces;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoReorder;


/**Implements the align objects menu options, complete with icons.
 * Can also move objects forward and backward in their parent layers
   Also includes code for arrange: moving and item between front and back.*/
public class AlignItem extends BasicMultiSelectionOperator implements  LayoutSpaces {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The constant values that are used to indicate what the align item operator will do*/
	public static final int MOVE_TO_FRONT = 102;
	public static final int MOVE_TO_BACK = 103;
	public static final int MOVE_FORWARD = 100;
	public static final int MOVE_BACKWARD = 101;
	
	private int type;

	/**set to a grey tone if the dark grey version of the icon should be used*/
	public Color darkFillForIcon;
	
	public AlignItem(int leftSpace) {
		this.type=leftSpace;
	}
	
	

	@Override
	public String getMenuCommand() {
		if (type==RectangleEdges.RIGHT) return "Right";
		if (type==RectangleEdges.TOP) return "Top";
		if (type==RectangleEdges.BOTTOM) return "Bottom";
		if (type==RectangleEdges.LEFT) return "Left";
		
		if (type==RectangleEdges.CENTER) return "Center Horizontal";
		if (type==RectangleEdges.CENTER+1) return "Center Vertical";
		
		if (type==MOVE_FORWARD) return "Move Forward";
		if (type==MOVE_BACKWARD) return "Move Backward";
		if (type==MOVE_TO_FRONT) return "Move to Front";
		if (type==MOVE_TO_BACK) return "Move to Back";
		
		return "Unknown";
	}

	/**returns the parent layer of the object*/
	private GraphicLayer findParentLayer(ZoomableGraphic item ) {
		ArrayList<GraphicLayer> layers = selector.getWorksheet().getTopLevelLayer().getSubLayers();
		if(selector.getWorksheet().getTopLevelLayer().getItemArray().contains(item)) return selector.getWorksheet().getTopLevelLayer();
		for(GraphicLayer l: layers) {
			if(l.getItemArray().contains(item)) return l;
		}
		return null;
	}

	@Override
	public void run() {
		if (selector==null||selector.getWorksheet()==null) {
			ShowMessage.showOptionalMessage("", true, "To use this tool, you must have a worksheet open. (and items selected) first");
			return;
		}
		if(type>99) {
			
			performOrderChange();
			return;
		}
		
		
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
		//allLayouts=allLayouts(all);
		UndoMoveItems undo = new UndoMoveItems(all, true);//undo
		
		if (all==null||all.size()<=1) {
			ShowMessage.showOptionalMessage("no object", true, "this option requires user to select more than one object");
			return;
		}
		
		allignArray(all);
		
		undo.establishFinalLocations();
		if(selector!=null&&selector.getWorksheet()!=null)
			selector.getWorksheet().getUndoManager().addEdit(undo);
		
	}



	/**
	 * 
	 */
	public void performOrderChange() {
		ArrayList<ZoomableGraphic> items = selector.getSelecteditems();
		if (items==null||items.size()<=0) {
			ShowMessage.showOptionalMessage("no object selected", true, "this option requires user to select an object");
			return;
		}
		items=performReplace(items);
		
		/**when moving multiple consecutive objects forward, need to reverse the order
		 If not, the first object might be be switched in front of the second 
		 in the first cycle of the loop, only to be passed by the second in the next cycle */
		boolean forwardOrder=true;
		if (type==MOVE_TO_BACK ||type==MOVE_FORWARD) forwardOrder=false;
		
		CombinedEdit edit = new CombinedEdit();//for the undo manager
		
		if (forwardOrder) 
		for(int i=0; i<items.size(); i++)
			{
				ZoomableGraphic item=items.get(i);
			   
				edit.addEditToList(
						moveItemForwardOrBack(item, type)//moves the item
						);
			}
		else 
			for(int i=items.size()-1; i>=0; i--)
			{
				ZoomableGraphic item=items.get(i);
				edit.addEditToList(
						moveItemForwardOrBack(item, type)
				);
			}
		if (selector!=null&&selector.getWorksheet()!=null) 
		selector.getWorksheet().getUndoManager().addEdit(edit);
	}
	
	/**
	 under certain circumstances, the items to be moved forward and backward change
	 */
	private ArrayList<ZoomableGraphic> performReplace(ArrayList<ZoomableGraphic> items) {
		/**if the item is alone in its parent layer, makes more sense if the parent layer is moved forward or back*/
		if (items.size()==1&&items.get(0).getParentLayer()!=null&&items.get(0).getParentLayer().getItemArray().size()==1) {
			ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic> ();
			output.add(items.get(0).getParentLayer());
			return output;
		}
		return items;
	}



	/**moves the item either forward or backward in the array*/
	UndoReorder moveItemForwardOrBack(ZoomableGraphic item, int type) {
		if (item==null) return null;
		GraphicLayer layer = findParentLayer(item);
		if(layer==null) return null;
		
		return moveItemInLayer(item, type, layer);
		}



	public static UndoReorder moveItemInLayer(ZoomableGraphic item, int type, GraphicLayer layer) {
		int index = layer.getItemArray().indexOf(item);
		
		UndoReorder undo = new UndoReorder(layer);
		
		if(type==MOVE_BACKWARD&&index>0) {
			layer.moveItemBackward(item);
		}
		if(type==MOVE_FORWARD&&index<layer.getItemArray().size()-1) {
			layer.moveItemForward(item);
		
		}
		if(type==MOVE_TO_BACK&&index>0) {
			while(layer.getItemArray().indexOf(item)>0)layer.moveItemBackward(item);
		}
		if(type==MOVE_TO_FRONT&&index<layer.getItemArray().size()-1) {
			while(layer.getItemArray().indexOf(item)<layer.getItemArray().size()-1) layer.moveItemForward(item);///layer.swapmoveObjectPositionsInArray(item, layer.getItemArray().get(index+1));
		}
		
		undo.saveNewOrder();
	
		return undo;
	}
	
	/**Alligns the items in the array*/
	public void allignArray(ArrayList<LocatedObject2D> all) { 
		if(all.size()<2) return;
		
		Rectangle b =combineOutLines(all).getBounds();		
		
		allignArray(all, b);
	}
	
	public Shape combineOutLines(ArrayList<LocatedObject2D> arrayList) {
		Area a=new Area();
		for(LocatedObject2D l:arrayList)  {
			a.add(new Area(getAligningRect(l)));
		}
		return a;
	}
	
	public void allignArray(ArrayList<LocatedObject2D> all, Rectangle b) {
			
			UndoMoveItems undo = new UndoMoveItems(all);
			
		for(LocatedObject2D a: all) {
			
			
			if (type==RectangleEdges.RIGHT)  {
				allignToRight(a, b);
			} else
				if (type==RectangleEdges.TOP)  {
					allignToTop(a, b);
				} else
					if (type==RectangleEdges.BOTTOM)  {
						allignToBottom(a, b);
					} else if (type==RectangleEdges.LEFT)  {
						allignToLeft(a, b);
						}
					else if (type==RectangleEdges.CENTER)  {
						allignToCenterH(a, b);
						} else {
							allignToCenterV(a, b);
						}
			
		}
		
		fixNegatives(all);
		
		
		undo.establishFinalLocations();
		if (selector!=null)
		selector.getWorksheet().getUndoManager().addEdit(undo);
	}
	
	/**If any objects have a negative location, this moves them.
	   Aligning is not super useful if objects go off screen*/
	void fixNegatives(ArrayList<LocatedObject2D> all) {
		Shape b = ArrayObjectContainer.combineBounds(all);
		if (b.getBounds().x<0 ) {
			int dx = -b.getBounds().x;
			for(LocatedObject2D l: all) {
				this.moveLocation(l, dx, 0);
			}
		}
		if (b.getBounds().y<0 ) {
			int dy = -b.getBounds().y;
			for(LocatedObject2D l: all) {
				this.moveLocation(l, 0, dy);
			}
		}
		
	}

	protected void moveLocation(LocatedObject2D a, double dx, double dy) {
		if (a instanceof PanelLayoutGraphic) {
			PanelLayoutGraphic layout=(PanelLayoutGraphic) a;
			
			layout.getPanelLayout().resetPtsPanels();
			
			layout.getEditor().moveMontage2(((DefaultLayoutGraphic)layout).getPanelLayout(), (int)dx,(int)dy);
			
			return;
		}
		
		a.moveLocation(dx, dy);
	}
	
	private void allignToCenterH(LocatedObject2D a, Rectangle b) {
		double dy = b.getCenterY()-getAligningRect(a).getCenterY();
		moveLocation(a,0, dy);
	}

	private void allignToCenterV(LocatedObject2D a, Rectangle b) {
		double dx = b.getCenterX()-getAligningRect(a).getCenterX();
		moveLocation(a,dx, 0);
	}

	protected void allignToTop(LocatedObject2D a, Rectangle b) {
		double dy = b.y-getAligningRect(a).getY();
		moveLocation(a,0, dy);
	}
	
	protected void allignToBottom(LocatedObject2D a, Rectangle b) {
		double dy = b.getMaxY()-getAligningRect(a).getMaxY();
		moveLocation(a,0, dy);
	}

	protected void allignToRight(LocatedObject2D a, Rectangle b) {
		double dx = b.getMaxX()-getAligningRect(a).getMaxX();
		moveLocation(a,dx, 0);
	}

	protected void allignToLeft(LocatedObject2D a, Rectangle b) {
		double dx = b.x-getAligningRect(a).getX();
		moveLocation(a,dx, 0);
	}
	
	protected Rectangle getAligningRect(LocatedObject2D a) {
		if (a instanceof PanelLayoutGraphic) {
			return panelsCombined(a);
		}
		return a.getBounds();
	}

	@Override
	public String getMenuPath() {
		if(type>=100) return "Arrange";
		return "Align";
	}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectanglesForIcon();
		Color[] colors=new Color[] {Color.red, Color.green, Color.blue, new Color((float)0.0,(float)0.0,(float)0.0, (float)0.5)};
		if(type==MOVE_FORWARD) {
			colors=new Color[] {Color.red.darker(), Color.red.darker(), Color.blue};
		}
		if(type==MOVE_BACKWARD) {
			colors=new Color[] {Color.blue, Color.red.darker(), Color.red.darker()};
		}
		
		if(type==MOVE_TO_FRONT) {
			colors=new Color[] {Color.gray.darker(), Color.gray.darker(), Color.blue};
		}
		if(type==MOVE_TO_BACK) {
			colors=new Color[] {Color.blue, Color.gray, Color.gray};
		}
		for(int i=0; i<rects.size(); i++ ) {
			Rectangle r=rects.get(i);
			
			RectangularGraphic rect = RectangularGraphic.blankRect(r, colors[i]);
			if(type>99) {
				rect = RectangularGraphic.filledRect(r); rect.setFillColor(colors[i]);rect.setDashes(null);rect.setStrokeWidth(1);
				}
			
				rect.setStrokeWidth(1);
				gg.getTheInternalLayer().add(rect);
				
				if (darkFillForIcon!=null) {
					rect.setFillColor(darkFillForIcon);rect.setFilled(true);
					rect.setStrokeWidth(0);
				}
				}
		
		
		if(darkFillForIcon!=null) {
			gg.moveLocation(0, 2);
		}
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	private ArrayList<Rectangle> getRectanglesForIcon() {
		ArrayList<Rectangle> output = new ArrayList<Rectangle>();
		if(this.type==RectangleEdges.LEFT) {
				output.add(new Rectangle(0,0,8,4));
				output.add(new Rectangle(0,6,16,6));
				output.add(new Rectangle(0,14,4,4));
				output.add(new Rectangle(0,0,0,18));
						} else  if(this.type==RectangleEdges.RIGHT) {
							output.add(new Rectangle(8,0,8,4));
							output.add(new Rectangle(0,6,16,6));
							output.add(new Rectangle(12,14,4,4));
							output.add(new Rectangle(16,0,0,18));
					}  else  if(this.type==RectangleEdges.BOTTOM) {
						output.add(new Rectangle(0,6,4,8));
						output.add(new Rectangle(6,0,6,14));
						output.add(new Rectangle(14,11,4,3));
						output.add(new Rectangle(0,14,18,0));
					} else if (this.type==RectangleEdges.TOP) {
						output.add(new Rectangle(0,0,4,8));
						output.add(new Rectangle(6,0,6,14));
						output.add(new Rectangle(14,0,4,3));
						output.add(new Rectangle(0,0,18,0));
					}else if (this.type==RectangleEdges.CENTER) {
						output.add(new Rectangle(1,3,4,8));
						output.add(new Rectangle(7,0,6,14));
						output.add(new Rectangle(14,5,4,4));
						output.add(new Rectangle(0,7,18,0));
					}else if (this.type==RectangleEdges.CENTER+1) {
						output.add(new Rectangle(4,0,8,4));
						output.add(new Rectangle(0,6,16,6));
						output.add(new Rectangle(6,14,4,4));
						output.add(new Rectangle(8,0,0,18));
					} else
						if(this.type==MOVE_FORWARD) {
							output.add(new Rectangle(0,0,8,8));
							output.add(new Rectangle(8,8,8,8));
							output.add(new Rectangle(4,4,8,8));
						}
						else
							if(this.type==MOVE_BACKWARD) {
								output.add(new Rectangle(4,4,8,8));
								output.add(new Rectangle(0,0,8,8));
								output.add(new Rectangle(8,8,8,8));
								
							}
							else
								if(this.type==MOVE_TO_FRONT) {
									
									output.add(new Rectangle(0,0,8,8));
									output.add(new Rectangle(8,8,8,8));
									output.add(new Rectangle(4,4,8,8));
									
								}
								else
									if(this.type==MOVE_TO_BACK) {
										output.add(new Rectangle(4,4,8,8));
										output.add(new Rectangle(0,0,8,8));
										output.add(new Rectangle(8,8,8,8));
										
									}
					else  {
						output.add(new Rectangle(0,0,4,8));
						output.add(new Rectangle(6,0,6,14));
						output.add(new Rectangle(14,0,4,3));
								}
		
		return output;
	}
	
	
	public Icon getIcon() {
		return  getItemIcon(true);
	}
	
	public Icon getDarkIcon() {
		this.darkFillForIcon=Color.darkGray;
		return  getItemIcon(true);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**returns true if all the items are layouts*/
	public static boolean allLayouts(ArrayList<LocatedObject2D> all) {
		
		for(LocatedObject2D a: all)  {
			if (a==null) continue;
			if (a instanceof PanelLayoutGraphic) {
				((PanelLayoutGraphic) a).generateCurrentImageWrapper();
				 
				continue;
				}
			
			return false;
		}
		
		return true;
	}
	
	
	/**For layout alignment */
	public Rectangle getPanelBoundsCombined(ArrayList<LocatedObject2D> all) {
		Area area=new Area();
		for(LocatedObject2D ob: all) {
			area.add(new Area( getAligningRect(ob)));
		}
		return  area.getBounds();
	}
	
	
	/**Returns the combined bounds of all the objects*/
	static Rectangle panelsCombined(LocatedObject2D ob) {
		Area area=new Area();
		
		if (ob instanceof PanelLayoutGraphic) {
				PanelLayoutGraphic layout = ((PanelLayoutGraphic) ob);
				layout.getPanelLayout().resetPtsPanels();
				layout.generateCurrentImageWrapper();
				for(Rectangle2D rect: layout.getPanelLayout().getPanels()) {
					area.add(new Area(rect));
				}
		}
		
		return area.getBounds();
		
	}


}
