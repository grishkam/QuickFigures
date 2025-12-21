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
 * Date Created: Dec 5, 2021
 * Date Modified: Dec 5, 2021
 * Version: 2023.2
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
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import messages.ShowMessage;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.CombinedEdit;


/**Implements the align objects size menu options, complete with icons.
 work in progresss.*/
public class AlignSize extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The constant values that are used to indicate what the align item operator will do*/
	public static final int WIDTH = 102, HEIGHT = 103, BOTH=104, PANEL_SCALE=105;

	
	private int type;

	/**set to a grey tone if the dark grey version of the icon should be used*/
	public Color darkFillForIcon;

	
	
	/**returns each possible align size object*/
	public static ArrayList<AlignSize> getAllPossibleAligns() {
		ArrayList<AlignSize> out=new ArrayList<AlignSize> ();
		for(int i: new int[] {WIDTH, HEIGHT, BOTH, PANEL_SCALE}) {
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
		if(panelScaleType())
			return "Scale of image panels";
		
		
		return "Sizes";
	}

	/**
	 * @return
	 */
	protected boolean panelScaleType() {
		return type==PANEL_SCALE;
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
		
		allignArray(all, undo, null);
		
		undo.establishFinalState();
		if(selector!=null&&selector.getWorksheet()!=null)
			selector.getWorksheet().getUndoManager().addEdit(undo);
		
	}



	/**Alligns the items in the array*/
	public void allignArray(ArrayList<? extends LocatedObject2D> all, CombinedEdit c, Rectangle2D b) { 
		if(all.size()<2) return;
		if(b!=null)
		b=b.getBounds();
		
		if(panelScaleType()) {
			Double relativeScale=findPanelScale(all);
			if(relativeScale!=null) {
				alignPanelScales(all, relativeScale, c);
			}
			else {
				ShowMessage.showOptionalMessage("Image Panels must be selected to use this option ");
			}
			return;
		}
		if(b==null)
			b =findRectangle(all);		
		
		allignWidthOrHeightArray(all, b, c);
	}
	
	
	
	/**
	 * @param all
	 * @param relativeScale
	 * @param c 
	 */
	private void alignPanelScales(ArrayList<? extends LocatedObject2D> all, Double relativeScale, CombinedEdit c) {
		for(LocatedObject2D a: all) {
			
			if(a instanceof ImagePanelGraphic) {
				 ImagePanelGraphic i=(ImagePanelGraphic) a;
				 c.addEditToList(i.provideDragEdit());
				i.setRelativeScale(relativeScale);
			}
			
		}
	}

	/**finds on image panel and returns its relative scale
	 * returns null if no image panels are present
	 * @param all
	 * @return
	 */
	private Double findPanelScale(ArrayList<? extends LocatedObject2D> all) {
		Double output=null;
		for(LocatedObject2D a: all) {
			
			if(a instanceof ImagePanelGraphic) {
				 ImagePanelGraphic i=(ImagePanelGraphic) a;
				return i.getRelativeScale();
			}
			
		}
		return output;
	}

	/**finds the bounding rectable of one object
	 * @param all
	 * @return
	 */
	private Rectangle2D findRectangle(ArrayList<? extends LocatedObject2D> all) {
		Rectangle2D output=null;
		for(LocatedObject2D a: all) {
			if(a instanceof RectangularGraphic) {
				output=((RectangularGraphic) a).getRectangle();
			}
			if(a instanceof ImagePanelGraphic) {
				 ImagePanelGraphic i=(ImagePanelGraphic) a;
				return i.getBounds2D();
			}
			if(a instanceof ArrowGraphic) {
				ArrowGraphic i=(ArrowGraphic) a;
				
				return findArrowBounds(i);
				
			}
			if(output!=null)
				return output;
		}
		return output;
	}

	/**returns a rectangle in which the two ends of the arrow are at two corners
	 * @param i
	 * @return
	 */
	public Rectangle2D findArrowBounds(ArrowGraphic i) {
		java.awt.geom.Point2D.Double e = i.getLineEndLocation();
		java.awt.geom.Point2D.Double s = i.getLineStartLocation();
		double x = Math.min(e.getX(), s.getX());
		double y = Math.min(e.getY(), s.getY());
		double w=Math.abs(e.getX()-s.getX());
		double h=Math.abs(e.getY()-s.getY());
		return new Rectangle2D.Double(x, y, w, h);
	}


	/**performs the slign operation*/
	public void allignWidthOrHeightArray(ArrayList<? extends LocatedObject2D> all, Rectangle2D b, CombinedEdit c) {
			
			b=b.getBounds();
			
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
						allignSpecial(a, b, c);
					} 
			
		}
		
		
		
		if (selector!=null)
		selector.getWorksheet().getUndoManager().addEdit(c);
	}
	


	
	
	

	/**TODO: create means of aligning the size of arrows
	 * @param a
	 * @param b
	 * @param c
	 */
	private void allignSpecial(LocatedObject2D a, Rectangle2D b, CombinedEdit c) {
		if(a instanceof ArrowGraphic) {
			 IssueLog.log("size align for arrows not yet implemented");
		}
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	private void allignToHeights(LocatedObject2D a, Rectangle2D b, CombinedEdit c) {
		
			double height2 = b.getHeight();
			if(a instanceof RectangularGraphic) {
				RectangularGraphic rectangularGraphic = (RectangularGraphic) a;
				c.addEditToList( rectangularGraphic.provideDragEdit());
				rectangularGraphic.setHeight(height2);;
			}
			if(a instanceof ImagePanelGraphic) {
				 ImagePanelGraphic panel = ( ImagePanelGraphic) a;
				c.addEditToList( panel.provideDragEdit());
				double change = height2/panel.getObjectHeight();
				panel.setRelativeScale(panel.getRelativeScale()*change);
			}
			
			if(a instanceof ArrowGraphic) {
				
				ArrowGraphic panel = ( ArrowGraphic) a;
				c.addEditToList( panel.provideDragEdit());
				panel.setObjectHeight(height2);
			}
		
	}



	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	private void allignWidths(LocatedObject2D a, Rectangle2D b, CombinedEdit c) {
		double width2 = b.getWidth();
		if(a instanceof RectangularGraphic) {
			RectangularGraphic rectangularGraphic = (RectangularGraphic) a;
			c.addEditToList( rectangularGraphic.provideDragEdit());
			rectangularGraphic.setWidth(width2);;
		}
		if(a instanceof ImagePanelGraphic) {
			 ImagePanelGraphic panel = ( ImagePanelGraphic) a;
			c.addEditToList( panel.provideDragEdit());
			double change = width2/panel.getObjectWidth();
			panel.setRelativeScale(panel.getRelativeScale()*change);
		}
		if(a instanceof ArrowGraphic) {
			ArrowGraphic panel = ( ArrowGraphic) a;
			c.addEditToList( panel.provideDragEdit());
			
			panel.setObjectWidth(width2);
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
						} else  if(this.type==HEIGHT) {
							
							output.add(new Rectangle(0, 7, 4,6));
							output.add(new Rectangle(6, 7, 8, 6));
							output.add(new Rectangle(15,7, 4,6));
					} else if (this.type==PANEL_SCALE) {
						output.add(new Rectangle(1,3,16,10));
						output.add(new Rectangle(1,3,8,5));
						
						;
					}else if (this.type==BOTH) {
						output.add(new Rectangle(6,15,8,6));
						output.add(new Rectangle(0,5,8,6));
						output.add(new Rectangle(10,5,8,6));
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
