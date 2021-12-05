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
 * Date Modified: Dec 5, 2021
 * Version: 2021.2
 */
package actionToolbarItems;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.CombinedEdit;


/**Implements the align objects size menu options, complete with icons.
 work in progresss
 TODO: add align image panel size option*/
public class AlignSize extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The constant values that are used to indicate what the align item operator will do*/
	public static final int WIDTH = 102, HEIGHT = 103, BOTH=104;;

	
	private int type;

	/**set to a grey tone if the dark grey version of the icon should be used*/
	public Color darkFillForIcon;
	
	
	public static ArrayList<AlignSize> getAllPossibleAligns() {
		ArrayList<AlignSize> out=new ArrayList<AlignSize> ();
		for(int i: new int[] {WIDTH, HEIGHT, BOTH}) {
			out.add(new AlignSize(i));
		}
		return out;
	}
	
	public AlignSize(int alignerType) {
		this.type=alignerType;
	}
	
	

	@Override
	public String getMenuCommand() {
		
		
		if (type== WIDTH) return "Widths";
		if (type==HEIGHT) return "Heights";
		
		
		
		return "Sizes";
	}



	@Override
	public void run() {
		if (selector==null||selector.getWorksheet()==null) {
			ShowMessage.showOptionalMessage("", true, "To use this tool, you must have a worksheet open. (and items selected) first");
			return;
		}
		
		
		
		
		setSelection(this.selector.getSelecteditems());
		ArrayList<LocatedObject2D> all = getAllObjects();
	
		CombinedEdit undo = new CombinedEdit();//undo
		
		if (all==null||all.size()<=1) {
			ShowMessage.showOptionalMessage("no object", true, "this option requires user to select more than one object");
			return;
		}
		
		allignArray(all, undo);
		
		undo.establishFinalState();
		if(selector!=null&&selector.getWorksheet()!=null)
			selector.getWorksheet().getUndoManager().addEdit(undo);
		
	}



	/**Alligns the items in the array*/
	public void allignArray(ArrayList<LocatedObject2D> all, CombinedEdit c) { 
		if(all.size()<2) return;
		
		Rectangle2D b =findRectangle(all);		
		
		allignArray(all, b, c);
	}
	
	
	
	/**
	 * @param all
	 * @return
	 */
	private Rectangle2D findRectangle(ArrayList<LocatedObject2D> all) {
		Rectangle2D output=null;
		for(LocatedObject2D a: all) {
			if(a instanceof RectangularGraphic) {
				output=((RectangularGraphic) a).getRectangle();
			}
		}
		return output;
	}



	public void allignArray(ArrayList<LocatedObject2D> all, Rectangle2D b, CombinedEdit c) {
			
			
			
		for(LocatedObject2D a: all) {
			
			
			if (type==AlignSize.WIDTH)  {
				allignWidths(a, b, c);
			} else
				if (type==AlignSize.HEIGHT)  {
					allignToHeights(a, b, c);
				} else
					if (type==AlignSize.BOTH)  {
						allignWidths(a, b, c);
						allignToHeights(a, b, c);
					} 
			
		}
		
		
		
		if (selector!=null)
		selector.getWorksheet().getUndoManager().addEdit(c);
	}
	


	
	
	

	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	private void allignToHeights(LocatedObject2D a, Rectangle2D b, CombinedEdit c) {
		
			if(a instanceof RectangularGraphic) {
				RectangularGraphic rectangularGraphic = (RectangularGraphic) a;
				c.addEditToList( rectangularGraphic.provideDragEdit());
				rectangularGraphic.setHeight(b.getHeight());;
			}
		
		
	}



	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	private void allignWidths(LocatedObject2D a, Rectangle2D b, CombinedEdit c) {
		if(a instanceof RectangularGraphic) {
			RectangularGraphic rectangularGraphic = (RectangularGraphic) a;
			c.addEditToList( rectangularGraphic.provideDragEdit());
			rectangularGraphic.setWidth(b.getWidth());;
		}
		
	}



	@Override
	public String getMenuPath() {
		
		return "Align<Shape Size";
	}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		ArrayList<Rectangle> rects = getRectanglesForIcon();
		Color[] colors=new Color[] {Color.red, Color.green, Color.blue, new Color((float)0.0,(float)0.0,(float)0.0, (float)0.5)};
	
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
		if(this.type==WIDTH) {
				output.add(new Rectangle(0,0,8,3));
				output.add(new Rectangle(0,4,8,8));
				output.add(new Rectangle(0,14,8,6));
				//output.add(new Rectangle(0,0,0,18));
						} else  if(this.type==RectangleEdges.RIGHT) {
							output.add(new Rectangle(8,0,8,4));
							output.add(new Rectangle(0,6,16,6));
							output.add(new Rectangle(12,14,4,4));
							output.add(new Rectangle(16,0,0,18));
					} /** else  if(this.type==RectangleEdges.BOTTOM) {
						output.add(new Rectangle(0,6,4,8));
						output.add(new Rectangle(6,0,6,14));
						output.add(new Rectangle(14,11,4,3));
						output.add(new Rectangle(0,14,18,0));
					} else if (this.type==RectangleEdges.TOP) {
						output.add(new Rectangle(0,0,4,8));
						output.add(new Rectangle(6,0,6,14));
						output.add(new Rectangle(14,0,4,3));
						output.add(new Rectangle(0,0,18,0));
					}*/else if (this.type==HEIGHT) {
						output.add(new Rectangle(1,3,3,8));
						output.add(new Rectangle(4,3,9,8));
						output.add(new Rectangle(14,3,6,8));
						;
					}else if (this.type==BOTH) {
						output.add(new Rectangle(6,15,8,5));
						output.add(new Rectangle(0,5,8,5));
						output.add(new Rectangle(10,5,8,5));
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
