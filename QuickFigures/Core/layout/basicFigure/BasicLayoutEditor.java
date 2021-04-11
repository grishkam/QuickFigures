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
package layout.basicFigure;


import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.ImageWorkSheet;
import channelLabels.ChannelLabelTextGraphic;
import figureOrganizer.FigureLabelOrganizer.ColumnLabelTextGraphic;
import figureOrganizer.FigureLabelOrganizer.RowLabelTextGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import layout.BasicObjectListHandler;
import layout.PanelContentExtract;
import layout.PanelLayout;
import layout.BasicObjectListHandler.LocatedObjectFilter;
import locatedObject.ArrayObjectContainer;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import utilityClasses1.NumberUse;


/**This is a form of montage editor that I created as a general class to be adaptable to other 
 Methods in this class Both alter the layout and move the contents appropriately
 */
public class BasicLayoutEditor implements LayoutSpaces {
	
	
	
	private BasicObjectListHandler objecthandler=new BasicObjectListHandler();
	private LocatedObjectFilter[] qualificationsForPanelObject=new LocatedObjectFilter[] {};
	
	   /**The last row, column, panel that was extracted by the remove methods*/
	   public PanelContentExtract lastCol;	
		public PanelContentExtract lastRow;
		public PanelContentExtract lastPanel;
		private ImageWorkSheet virtualWorksheet;
	
	public BasicObjectListHandler getObjectHandler() {
		return objecthandler;
	}
	
	
	/**Alters the canvas size of the image while preserving the positions of the objects relative to the pixels of the image*/
	public void resetMontageImageSize(BasicLayout ml,  double xOff, double yOff) {
		if (ml==null||ml.getVirtualWorksheet()==null) return;
		ml.getVirtualWorksheet().worksheetResize( (int)ml.layoutWidth, (int)ml.layoutHeight, (int)xOff, (int) yOff);
		getObjectHandler().shiftAll(ml.getVirtualWorksheet(), xOff, yOff);
		this.finishEdit(ml);
			}

	/**Extracts the content from each panel of this layout and returns the contents of every
	  panel as an array */
	public ArrayList <PanelContentExtract> cutStack(PanelLayout ml) {
		ArrayList <PanelContentExtract> output=new ArrayList <PanelContentExtract>();
		ml.resetPtsPanels();
		Rectangle2D[] panels = ml.getPanels();
		virtualWorksheet = ml.getVirtualWorksheet();
		for (Rectangle2D panel: panels) {
			output.add(cutPanelContents(virtualWorksheet, panel) );}
		
		return output;	
	}
	
	/**Reverses the cutStack Method above. */
	public  void pasteStack(PanelLayout ml, ArrayList <PanelContentExtract> panels) {
		Rectangle2D[] rpanels = ml.getPanels();
		for (int i=0; i<rpanels.length&&i<panels.size(); i++) {
			pastePanelContents(virtualWorksheet, rpanels[i], panels.get(i));
		}
	}
	
	/**takes the objects in a particular panel and returns them*/
	PanelContentExtract cutPanelContents(ObjectContainer imp, Rectangle2D r) {
		
		PanelContentExtract output = new PanelContentExtract(r);
		
		
		output.setObjectList(getObjectHandler().liftObjectsFromPanelX(imp, r , getQualificationsForPanelObject()));
		
		return output;
	}
	
	/**takes the objects in a particular panel and reverses the cutPanelContent method above*/
	void pastePanelContents(ImageWorkSheet imp, Rectangle2D r, PanelContentExtract p) {
	
		getObjectHandler().setObjectsIntoPanelX(p.getObjectList(),imp, r);
	}
	
		
	/**Simple methods to set the number of rows. */
	   public void addRows(BasicLayout ml, int rows){
		   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_ADDITION, ml.nRows()+1, rows );
		   
		   notifyListenersOfFutureChange(ml, event);
		  
		   ml.setNRows(ml.nRows() + rows);
		   if (rows*ml.yincrementOfRow(ml.nRows())<ml.specialSpaceWidthBottom) {ml.specialSpaceWidthBottom-=rows*ml.yincrementOfRow(ml.nRows());}else {
		   ml.layoutHeight+=ml.yincrementOfRow(ml.nRows())*rows;
		  
		   notifyListenersOfCurrentChange(ml, event);
		   
		   resetMontageImageSize(ml, 0, 0);}

		   finishEdit(ml);
		   
		   notifyListenersOfCompleteChange(ml, event);
		   
	   }
	   
	   public void setRowNumber(BasicLayout ml, int rows) {
		   addRows(ml, rows-ml.nRows());
	   }
	   
		
		/**Simple method to set the number of columns. */
	   public void addCols(BasicLayout ml, int cols){  
		   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_ADDITION, ml.nColumns()+1, cols );
		   notifyListenersOfFutureChange(ml, event);
		   ml.setNColumns(ml.nColumns() + cols); 
		   if (cols*ml.xincrementOfColumn( ml.nColumns())<ml.specialSpaceWidthRight) {ml.specialSpaceWidthRight-=cols*ml.xincrementOfColumn( ml.nColumns());}else {
		   ml.layoutWidth+=ml.xincrementOfColumn( ml.nColumns())*cols;
		   notifyListenersOfCurrentChange(ml, event);
		   resetMontageImageSize(ml, 0, 0);}
		   ml.resetPtsPanels();
		  this.finishEdit(ml);
		  notifyListenersOfCompleteChange(ml, event);
	   }
	   /**Simple method to set the number of columns. */
	   public void setColNumber(BasicLayout ml, int cols) {
		   addCols(ml, cols-ml.nColumns());
	   }
	   
	   /**Adds label space to the montage to make it include the Rectangle r*/
	   public void expandSpacesToInclude(BasicLayout ml, Rectangle r) {
		   if (ml==null) return;
		   Rectangle space = ml.getSelectedSpace(1,LayoutSpaces.ALL_MONTAGE_SPACE).getBounds();
		  
		   if (r.x<space.x) {
			   this.addLeftLabelSpace(ml, space.x-r.x);
		   }
		   
		   if (r.y<space.y) {
			   this.addTopLabelSpace(ml, space.y-r.y);
		   }
		   
		   if (r.x+r.width>space.x+space.width) {
			   this.addRightLabelSpace(ml, r.x+r.width-space.x-space.width);
		   }
		   if (r.y+r.height>space.y+space.height) {
			   this.addBottomLabelSpace(ml, r.y+r.height-space.y-space.height);
		   }
		   
		   
	   }
	   
	   /**Trims the unneeded label space in the montage*/
	   public void trimLabelSpacesToFitContents(BasicLayout ml) {
		 
		   trimLabelpacesToIncludeOnly(ml, getRecomendedContentArea(ml));
	   }
	   
	   /**returns the area that this montage takes up, returns the dimensions not the position*/
	   private Rectangle getRecomendedContentArea(BasicLayout ml) {
		     ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(LayoutSpaces.ALL_MONTAGE_SPACE));   
		   
		   Rectangle area = stack.get(0).getAreaSpannelByContents();
		   area.x+=ml.specialSpaceWidthLeft;
		   area.y+=ml.specialSpaceWidthTop;
		   pasteStack( ml.makeAltered(LayoutSpaces.ALL_MONTAGE_SPACE), stack);
		   return area;
	   }
	   
	   /**Adds or trims label space in the montage*/
	   public void fitLabelSpacesToContents(BasicLayout ml) {
		    Area area =new Area( ml.getBoundry());
		    area.add(new Area(ml.allPanelArea()));
		   
		  //Rectangle area = getRecomendedContentArea(ml);//retrieves panel areas
		  ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(area.getBounds(), ml.getVirtualWorksheet());
		  Area area2=new Area(new Area(ml.allPanelArea()));
		 	
		  for(LocatedObject2D roi:rois) {
			  expandArea(area2, roi);
			 
		  }
		  
		  expandForAttachedItems(area2, ml.findHoldingObject());
		   
		   setLabelpacesToIncludeOnly(ml, area2.getBounds());
	   }

	   /**Enlarges area2 to include the given object*/
	protected void expandArea(Area area2, LocatedObject2D rObject) {
		area2.add(new Area(rObject.getBounds()));
		  expandForAttachedItems(area2, rObject);
	}
	 /**Enlarges area2 to include the items attached to the given object*/
	protected void expandForAttachedItems(Area area2, LocatedObject2D rObject) {
		if (rObject instanceof TakesAttachedItems) {
			  TakesAttachedItems roi2=(TakesAttachedItems) rObject;
			  for (LocatedObject2D item: roi2.getLockedItems()) {
				  
				 area2.add(new Area(item.getBounds()));
				 	
			  }
			  
		  }
	}
	   
	   /**Adds label space to the layout to make sure it includes the Rectangle r (the bounds of a label).
	     */
	  private void trimLabelpacesToIncludeOnly(BasicLayout ml, Rectangle r) {
		   Rectangle space = ml.getSelectedSpace(1,LayoutSpaces.ALL_MONTAGE_SPACE).getBounds();

		   if (r.x>space.x) {
			   this.addLeftLabelSpace(ml, -(r.x-space.x));
		   }
		   
		   if (r.y>space.y) {
			   this.addTopLabelSpace(ml, -(r.y-space.y));
		   }
		  
		   if (r.x+r.width<space.x+space.width) {
			   this.addRightLabelSpace(ml, (r.x+r.width-space.x-space.width));
		   }
		  
		   if (r.y+r.height<space.y+space.height) {
			   this.addBottomLabelSpace(ml, (r.y+r.height-space.y-space.height));
		   } /**
		   */
		   
	   }
	  
	  /**Adds or subtracts label space to/from the montage to make fit Rectangle r*/
	  private void setLabelpacesToIncludeOnly(BasicLayout ml, Rectangle r) {
		   Rectangle space = ml.getSelectedSpace(1,LayoutSpaces.ALL_MONTAGE_SPACE).getBounds();
			  /**currently flawd as does not take into account position the the montage*/
		   	this.addLeftLabelSpace(ml, -(r.x-space.x));
			   this.addTopLabelSpace(ml, -(r.y-space.y));
			   this.addRightLabelSpace(ml, (r.x+r.width-space.x-space.width));
			   this.addBottomLabelSpace(ml, (r.y+r.height-space.y-space.height));
			   ensurePositiveLabelSpace(ml);
	
	   }
	  
	  /**If any label spaces are negative, makes them positive. There are relatively few ways a user can set
	    the spaces to negative values. */
	  private void ensurePositiveLabelSpace(BasicLayout ml) {
		  if (ml.labelSpaceWidthLeft<0)  this.addLeftLabelSpace(ml, Math.abs(ml.labelSpaceWidthLeft));
		  if (ml.labelSpaceWidthRight<0)  this.addRightLabelSpace(ml, Math.abs(ml.labelSpaceWidthRight));
		  if (ml.labelSpaceWidthTop<0)  this.addTopLabelSpace(ml, Math.abs(ml.labelSpaceWidthTop));	  
		  if (ml.labelSpaceWidthBottom<0)  this.addTopLabelSpace(ml, Math.abs(ml.labelSpaceWidthBottom));	  
	  }
	  
	   /**Changes the label space at the top of the layout*/
	   public void addTopLabelSpace(BasicLayout ml, double space) {
		   if(ml.labelSpaceWidthTop+space<0) {space=-ml.labelSpaceWidthTop;}
		   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LABEL_SPACE_EDIT+TOP_SPACE, space, 0 );
		   notifyListenersOfFutureChange(ml, event);
			  ml.labelSpaceWidthTop+=space;
			  if (space>ml.specialSpaceWidthTop) {
				  ml.layoutHeight+=space; 
				  resetMontageImageSize(ml, 0, space); 
				  }
			  else ml.setSpecialTopSpace(ml.specialSpaceWidthTop-space);
			 
			  notifyListenersOfCompleteChange(ml, event);
		   }
	   /**Changes the label space at the top of the layout*/
	   public void setTopLabelSpace(BasicLayout ml, double space) {
		   addTopLabelSpace(ml, space-ml.labelSpaceWidthTop);
	   }
		
	   /**Changes the y location of the layout relative to the 0 point*/
		public void addTopSpecialSpace(BasicLayout ml, double space) {
			  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+TOP_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.layoutHeight+=space;
			  ml.specialSpaceWidthTop+=space;
			  resetMontageImageSize(ml, 0, space);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		 /**Changes the y location of the layout relative to the 0 point*/
		public void setTopSpecialSpace(BasicLayout ml, double space) {
			addTopSpecialSpace(ml, space-ml.specialSpaceWidthTop);
		}
		
		/**Sets the base location for the layout.
		 */
		public void setBaseLocation(BasicLayout ml,double lx, double ly) {
			this.setLeftSpecialSpace(ml, ly);
			this.setTopSpecialSpace(ml, ly);
		}
		
		 /**Changes the label space at the bottom of the layout*/
		public void addBottomLabelSpace(BasicLayout ml, double space) {
			if(ml.labelSpaceWidthBottom+space<0) {space=-ml.labelSpaceWidthBottom;}
			  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LABEL_SPACE_EDIT+BOTTOM_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.labelSpaceWidthBottom+=space;
			  if (space>ml.specialSpaceWidthBottom) { 
				  ml.layoutHeight+=space; 
				  resetMontageImageSize(ml, 0, 0); 
				  }
			  else ml.setSpecialBottomSpace(ml.specialSpaceWidthBottom-space);
			  notifyListenersOfCompleteChange(ml, event);
		   }
		 /**Changes the label space at the bottom of the layout*/
		public void setBottomLabelSpace(BasicLayout ml, double space) {
			 addBottomLabelSpace(ml, space-ml.labelSpaceWidthBottom);
		}

		/**Changes the amount of empty space that is expected between the bottom of the layout and
		 * the bottom of the canvas*/
		public void addBottomSpecialSpace(BasicLayout ml, double space) {
			  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+BOTTOM_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.layoutHeight+=space;
			  ml.specialSpaceWidthBottom+=space;
			  resetMontageImageSize(ml, 0, 0);
			  notifyListenersOfCompleteChange(ml, event);
		   }
		/**Changes the amount of empty space that is expected between the bottom of the layout and
		 * the bottom of the canvas*/
		public void setBottomSpecialSpace(BasicLayout ml, double space) {
			addBottomSpecialSpace(ml, space-ml.specialSpaceWidthBottom);
		}
		
		 /**Changes the label space at the left of the layout*/
		public  void addLeftLabelSpace(BasicLayout ml, double space) {	
			if(ml.labelSpaceWidthLeft+space<0) {space=-ml.labelSpaceWidthLeft;}//TODO test if this works
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LABEL_SPACE_EDIT+LEFT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
				
			  ml.labelSpaceWidthLeft+=space;
			  if (space>ml.specialSpaceWidthLeft) { 
				  ml.layoutWidth+=space; 
				  resetMontageImageSize(ml, space, 0);
				  //getObJectHandler().shiftAllRois(ml, space, 0);
				  }
			  else ml.setSpecialLeftSpace(ml.specialSpaceWidthLeft-space);
			  notifyListenersOfCompleteChange(ml, event);
			  
		   }
		 /**Changes the label space at the left of the layout*/
		public  void setLeftLabelSpace(BasicLayout ml, double space) {	
			addLeftLabelSpace(ml, space-ml.labelSpaceWidthLeft);
		}
		
		 /**Changes the x location of the layout relative to the 0 point*/
		public  void addLeftSpecialSpace(BasicLayout ml, double space) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+LEFT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  ml.layoutWidth+=space;
			  ml.specialSpaceWidthLeft+=space;
			  resetMontageImageSize(ml, space, 0);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		
		 /**Changes the x location of the layout relative to the 0 point*/
		public void setLeftSpecialSpace(BasicLayout ml, double space) {
			addLeftSpecialSpace(ml, space-ml.specialSpaceWidthLeft);
		}
		
		 /**Changes the label space at the right of the layout*/
		public void addRightLabelSpace(BasicLayout ml, double space) {	
			if(ml.labelSpaceWidthRight+space<0) {space=-ml.labelSpaceWidthRight;}//TODO test if this works
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LABEL_SPACE_EDIT+RIGHT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.labelSpaceWidthRight+=space;
			  if (space>ml.specialSpaceWidthRight) {ml.layoutWidth+=space; resetMontageImageSize(ml, 0, 0);  }
			  else ml.setSpecialRightSpace(ml.specialSpaceWidthRight-space);
			  notifyListenersOfCompleteChange(ml, event);
		   }
		 /**Changes the label space at the right of the layout*/
		public void setRightLabelSpace(BasicLayout ml, double space) {
			addRightLabelSpace(ml, space-ml.labelSpaceWidthRight);
		}
		
		/**Changes the amount of empty space that is expected between the right of the layout and
		 * the right end of the canvas*/
		public void addRightSpecialSpace(BasicLayout ml, double space) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+RIGHT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  ml.layoutWidth+=space;
			  ml.specialSpaceWidthRight+=space;
			  resetMontageImageSize(ml, 0, 0);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		/**Changes the amount of empty space that is expected between the right of the layout and
		 * the right end of the canvas*/
		public void setRightSpecialSpace(BasicLayout ml, double space) {
			addRightSpecialSpace(ml, space-ml.specialSpaceWidthRight);
		}
		
		
		/**Changes the vertical border between panels
		 *When given a layout, this will add to the vertical border around each panel, copy the rows,
		   and paste then into a resized version of the montage*/
		public void expandBorderY2(BasicLayout ml, double t) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.BORDER_EDIT_V, t, 0 );
			   notifyListenersOfFutureChange(ml, event);
			 
			
			if (ml.theBorderWidthBottomTop+t<0) return;
			ArrayList<PanelContentExtract> stack=cutStack(ml.makeAltered(ROWS));
	
			ml.setVerticalBorder(ml.theBorderWidthBottomTop+t);
			if (t*ml.nRows()<ml.specialSpaceWidthBottom ) {ml.setSpecialBottomSpace(ml.specialSpaceWidthBottom-t*ml.nRows());}
			else resetMontageImageSize(ml, 0, 0);		
			ml.resetPtsPanels() ;
			pasteStack(ml.makeAltered(ROWS), stack);
			
			finishEdit(ml);
			notifyListenersOfCompleteChange(ml, event);
		}
		/**Changes the vertical border between panels*/
		public void setVerticalBorder(BasicLayout ml, double border) {
			double t = border-ml.theBorderWidthBottomTop;
			if(t!=0)
				expandBorderY2(ml, t);
		}
		
		
		
		/**Changes the horizontal border between panels
		 * When given a layout, this will add to the horizontal border around each panel, copy the columns,
		   and paste then into a resized version of the montage*/
		public void expandBorderX2(BasicLayout ml, double t) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.BORDER_EDIT_H, t, 0 );
			   notifyListenersOfFutureChange(ml, event);
			 
			ArrayList<PanelContentExtract> stack=cutStack(ml.makeAltered(COLS));
			
			ml.setHorizontalBorder(ml.theBorderWidthLeftRight+t);
			if (t*ml.nColumns()<ml.specialSpaceWidthRight) {ml.setSpecialRightSpace(ml.specialSpaceWidthRight-t*ml.nColumns());}
			else resetMontageImageSize(ml, 0, 0);	
			ml.resetPtsPanels() ;
			
			pasteStack(ml.makeAltered(COLS), stack);
		
			finishEdit(ml);
			notifyListenersOfCompleteChange(ml, event);
		}
		/**Changes the horizontal border between panels*/
		public void setHorizontalBorder(BasicLayout ml, double border) {
			double t = border-ml.theBorderWidthLeftRight;
			if(t!=0)
			expandBorderX2(ml, t);
		}
		
		/**called after the end of each layout edit. the array of points and rectangles must
		 be reset to reflect the current layout*/
		void finishEdit(BasicLayout ml) {
			ml.resetPtsPanels() ;
			ml.afterEditDone();
		}
		
		/**Swaps the number of rows with the number of columns of the layout. does not affect the 
		  items in the label spaces*/
		public  GridLayoutEditEvent invertPanels(BasicLayout ml) {	
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.INVERSION, 0, 0 );
			   notifyListenersOfFutureChange(ml, event);
			
			ArrayList<PanelContentExtract> stack=cutStack(ml);
			
			int newcol=ml.nRows();
			int newrow=ml.nColumns();
			addCols(ml, newcol-ml.nColumns());
			addRows(ml, newrow-ml.nRows());
			ml.rowmajor=!ml.rowmajor;
			ml.resetPtsPanels();
			pasteStack(ml, stack);
			finishEdit( ml);
			
			notifyListenersOfCompleteChange(ml, event);
			return event;
		}
		
		/**Performs a horizontal/vertical flip of the layout. Edits the items in the label spaces as well*/
		public void invertPanelsAndLabels(BasicLayout ml) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.INVERSION, 0, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  
			   ArrayList<PanelContentExtract> stack=cutStack(ml);
			   ArrayList<PanelContentExtract> collabel = cutStack(  ml.makeAltered(COLS));
			   ArrayList<PanelContentExtract> rowlabel = cutStack(  ml.makeAltered(ROWS));
			   
				
				int newcol=ml.nRows();
				int newrow=ml.nColumns();
				addCols(ml, newcol-ml.nColumns());
				addRows(ml, newrow-ml.nRows());
				ml.rowmajor=!ml.rowmajor;
				ml.resetPtsPanels();
				
				pasteStack(ml, stack);
				pasteStack(ml.makeAltered(ROWS), collabel);
				pasteStack(ml.makeAltered(COLS), rowlabel);
				
				
				finishEdit( ml);
			
				
			flipSnappings(ml);
			
			
			notifyListenersOfCompleteChange(ml, event);
		}
		
		/**If the objects in the layout can be packed into the number of panels given by the new row and new col,
		  changes the number of item*/
		public void repackagePanels(BasicLayout ml, int newrow, int newcol) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.REPACKAGE, 0, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  
			   ArrayList<PanelContentExtract> stack=cutStack(ml);
			  
			   ArrayList<PanelContentExtract> newStack=new ArrayList<PanelContentExtract> ();
			   for(PanelContentExtract extract: stack) {
				   if(extract.hasObjects()) newStack.add(extract);
			   }
				
				if(newrow*newcol>=newStack.size()) {
					addCols(ml, newcol-ml.nColumns());
					addRows(ml, newrow-ml.nRows());
					stack=newStack;
				}
				ml.resetPtsPanels();
				
				pasteStack(ml, stack);
				
				
				finishEdit( ml);
			
			
			notifyListenersOfCompleteChange(ml, event);
		}

		/**Changes the snap position of objects to reflect a row column flip*/
		protected void flipSnappings(BasicLayout ml) {
			ArrayList<LocatedObject2D> objects = ml.getVirtualWorksheet().getLocatedObjects();
			
			ArrayList<AttachmentPosition> bahs = AttachmentPosition.findAllPositions(objects);
			
			for(AttachmentPosition b: bahs) {
				b.flipDiag();
			}
		}


		
		  /**Alters the size alloted to the panel. if the panel's column lacks an individual width
		    this simply alters the width of them all*/
		   public void augmentPanelWidth(BasicLayout ml,  double widthincrease, int colIndex) {
			   
			   if (!ml.columnHasIndividualWidth(colIndex)) {
				   augmentStandardPanelWidth( ml,  widthincrease); 
				   return;
			   }
			   augmentPanelWidthOfCol( ml,  widthincrease, colIndex) ;
		   }
		   public void setPanelWidth(BasicLayout ml,  double newpwidth, int colindex) {
			   augmentPanelWidth(ml, newpwidth-ml.getPanelWidthOfColumn(colindex),colindex);
		   }
		   
			  /**Alters the size alloted to the panel. if the panel's column lacks and individual width
		    this simply alters the width of them all*/
		   public void augmentPanelWidthOfCol(BasicLayout ml,  double widthincrease, int colIndex) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_RESIZE, widthincrease,  colIndex);
			   notifyListenersOfFutureChange(ml, event);
			   
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
			   
			   if (colIndex>0) ml.setColumnWidth(colIndex, ml.getPanelWidthOfColumn(colIndex)+widthincrease);
			  
				//ml.setPanelWidth(ml.getPanelWidthOfColumn(colIndex)+widthincrease);
				if (widthincrease<ml.specialSpaceWidthRight) {ml.setSpecialRightSpace(-widthincrease+ml.specialSpaceWidthRight);}
				else { ml.layoutWidth=ml.layoutWidth+widthincrease; resetMontageImageSize(ml, 0, 0);}
				
				pasteStack(ml.makeAltered(COLS), stack);
				finishEdit(ml);
				notifyListenersOfCompleteChange(ml, event);
		   }
		   
		   public void setPanelWidthOfColumn(BasicLayout ml, double width, int rowIndex) {
			   augmentPanelWidthOfCol(ml, width-ml.getPanelWidthOfColumn(rowIndex), rowIndex);
		   }
		   
		   /**alters the height of the row at rowIndex. if that row lack an individual height,
		     this alters the standard height for all the panels*/
		   public void augmentPanelHeight(BasicLayout ml,  double increase, int rowIndex) {
			   if (!ml.rowHasIndividualHeight(rowIndex)) {
				   augmentStandardPanelHeight( ml,  increase); 
				   return;
			   }
			   augmentPanelHeightOfRow( ml,   increase,rowIndex);
		   }
		   public void setPanelHeight(BasicLayout ml,  double newpheight, int rowindex) {
			   augmentPanelHeight(ml,newpheight- ml.getPanelHeightOfRow(rowindex),rowindex);
		   }
		   
		   
		   /**alters the height of the row at rowIndex. if that row lack an individual height,
		     this alters the standard height for all the panels*/
		   public void augmentPanelHeightOfRow(BasicLayout ml,  double increase, int rowIndex) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_RESIZE, increase,  rowIndex);
			   notifyListenersOfFutureChange(ml, event);
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
			 ml.setRowHeight(rowIndex, ml.getPanelHeightOfRow(rowIndex)+increase);
				//ml.setPanelHeight(ml.getPanelHeightOfRow(rowIndex)+increase);
				
				if (increase<ml.specialSpaceWidthBottom) {
					ml.setSpecialBottomSpace(-increase+ml.specialSpaceWidthBottom);
					}
				else {
					ml.layoutHeight+=increase;
					resetMontageImageSize(ml, 0, 0);
					}
				
				if (ml.getVirtualWorksheet()!=null) {
					pasteStack( ml.makeAltered(ROWS), stack);
					ml.afterEditDone();
					}
				finishEdit(ml);
				notifyListenersOfCompleteChange(ml, event);
				
		
}
		   public void setPanelHeightOfRow(BasicLayout ml, double height, int rowIndex) {
			   augmentPanelHeightOfRow(ml, height-ml.getPanelHeightOfRow(rowIndex), rowIndex);
		   }
		   
			  /**Alters the size alloted to a panel. does this for the general width of the whole layout. not for an individual panels width*/
		   public void augmentStandardPanelWidth(BasicLayout ml,  double widthincrease) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.PANEL_RESIZE_H, widthincrease,  1);
			   notifyListenersOfFutureChange(ml, event);
			   
			   
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
			   
				ml.setStandardPanelWidth(ml.getPanelWidthOfColumn(0)+widthincrease);
				if (widthincrease*ml.nColumns()<ml.specialSpaceWidthRight) {ml.setSpecialRightSpace(-widthincrease*ml.nColumns()+ml.specialSpaceWidthRight);}
				else resetMontageImageSize(ml, 0, 0);
				
				pasteStack(ml.makeAltered(COLS), stack);
				ml.afterEditDone();
				notifyListenersOfCompleteChange(ml, event);
		   }
		   
		   public void augmentStandardPanelHeight(BasicLayout ml,  double increase) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.PANEL_RESIZE_V, increase,  1);
			   notifyListenersOfFutureChange(ml, event);
			   
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
			 
				ml.setStandardPanelHeight(ml.getPanelHeightOfRow(0)+increase);
				if (increase*ml.nRows()<ml.specialSpaceWidthBottom) {ml.setSpecialBottomSpace(-increase*ml.nRows()+ml.specialSpaceWidthBottom);}
				else resetMontageImageSize(ml, 0, 0);
				
				if (ml.getVirtualWorksheet()!=null) {
					pasteStack( ml.makeAltered(ROWS), stack);
					ml.afterEditDone();
					}
				
				notifyListenersOfCompleteChange(ml, event);
}
		   
		   /**Changes the dimensions of the panels such that they fit the objects inside*/
		   public void alterPanelWidthAndHeightToFitContents(BasicLayout ml) {
			   alterPanelWidthsToFitContents(ml);
			   ml.resetPtsPanels();
			   alterPanelHeightsToFitContents(ml);
			   ml.resetPtsPanels();
		   }
		   
		   
		   
		   public void roundUpPanelSizes(BasicLayout ml) {
			   for(int i=1; i<=ml.nRows(); i++) 
				   this.setPanelHeight(ml, Math.ceil(ml.getPanelHeightOfRow(i)), i);
			   
			   for(int i=1; i<=ml.nColumns(); i++) 
				   this.setPanelWidth(ml, Math.ceil(ml.getPanelWidthOfColumn(i)), i);
		   }
		   
		   public void roundUpSpaces(BasicLayout ml) {
			   this.setTopSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthTop));
			   this.setBottomSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthBottom));
			   this.setLeftSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthLeft));
			   this.setRightSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthRight));
			   
			   this.setTopLabelSpace(ml, Math.ceil(ml.labelSpaceWidthTop));
			   this.setBottomLabelSpace(ml, Math.ceil(ml.labelSpaceWidthBottom));
			   this.setLeftLabelSpace(ml, Math.ceil(ml.labelSpaceWidthLeft));
			   this.setRightLabelSpace(ml, Math.ceil(ml.labelSpaceWidthRight));
			   
		   }
		   
		   public void roundUpBorders(BasicLayout ml) {
			   this.setHorizontalBorder(ml, Math.ceil(ml.theBorderWidthLeftRight));
			   this.setVerticalBorder(ml, Math.ceil(ml.theBorderWidthBottomTop));
		   }
		   
		   public void roundUpAll(BasicLayout ml) {
			   this.roundUpPanelSizes(ml);
			   this.roundUpSpaces(ml);
			   this.roundUpBorders(ml);
		   }
		   
		   public void placePanelsInCorners(BasicLayout ml,ArrayList<LocatedObject2D> objects) {
			   /**puts items in upper-left corner*/
			   ml.resetPtsPanels();
				placeObjectsInUpperLeftCorners2(ml, objects);
				ml.resetPtsPanels();
		   }
		   
		   /**some objects do not enter into the calculations when optimizing panel sizes*/
		   static Class<?>[] nonConsideredClasses=new Class<?>[] {BarGraphic.class, BarGraphic.BarTextGraphic.class, ChannelLabelTextGraphic.class, ColumnLabelTextGraphic.class, RowLabelTextGraphic.class};
		   
		   
		   /**makes the column fit objects*/
		   public void alterPanelWidthsToFitContents(BasicLayout ml) {
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(LayoutSpaces.COLUMN_OF_PANELS));
			   ArrayObjectContainer.ignoredClass=BarGraphic.class;
			   ArrayObjectContainer.ignoredClass2=ChannelLabelTextGraphic.class;
			   ArrayObjectContainer.ignoredClasses=nonConsideredClasses;
			   
			   int[] widths=new int[stack.size()];
			   for(int i=0; i<widths.length; i++) {
				   widths[i]=stack.get(i).getAreaSpannelByContents2().width;
			   }
			   
			   
			   if (ml.allColsSameWidth()&&NumberUse.allSame(widths)) {
				   augmentStandardPanelWidth(ml,  widths[0]-ml.panelWidth);
			   }else 
			    for(int i=0; i<widths.length; i++) {
				  this.setPanelWidthOfColumn(ml, widths[i],i+1);
				  
			   }
			   ArrayObjectContainer.ignoredClass=null;
			   ArrayObjectContainer.ignoredClass2=null;
			   ArrayObjectContainer.ignoredClasses=null;
			   pasteStack( ml.makeAltered(COLUMN_OF_PANELS), stack);
			  
			  
		   }
		   
		 
	
	
		   	   
		   /**makes the row to fit objects*/
		   public void alterPanelHeightsToFitContents(BasicLayout ml) {
			
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(LayoutSpaces.ROW_OF_PANELS));
			   ArrayObjectContainer.ignoredClass=BarGraphic.class;
			   ArrayObjectContainer.ignoredClass2=ChannelLabelTextGraphic.class;
			   ArrayObjectContainer.ignoredClasses=nonConsideredClasses;
			   
			   
			   int[] heights=new int[stack.size()];
			   for(int i=0; i<stack.size(); i++) {
				   heights[i]=stack.get(i).getAreaSpannelByContents2().height;
			   }
			   
			   /**must do the setting before the stack paste. the method called also does a 
			     'cut/paste but strange effects occur when this is done one by one*/
			   if (ml.allRowsSameHeight()&&NumberUse.allSame(heights)) {
				   augmentStandardPanelHeight(ml,  heights[0]-ml.panelHeight);
			   }else  for(int i=0; i<heights.length; i++) {
				   this.setPanelHeightOfRow(ml, heights[i],i+1);
			   }
			   
			   ArrayObjectContainer.ignoredClass=null;
			   ArrayObjectContainer.ignoredClass2=null;
			   ArrayObjectContainer.ignoredClasses=null;
			   pasteStack( ml.makeAltered(ROW_OF_PANELS), stack);
			  
			   
		   }
		   
		   /**experimental. makes the column fit objects. buggy*/
		   public void shiftPanelContentsToEdge(BasicLayout ml) {
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(LayoutSpaces.PANELS));
			   int[] widths=new int[stack.size()];
			   for(int i=0; i<widths.length; i++) {
				   int x = stack.get(i).getAreaSpannelByContents().x;
				   int y = stack.get(i).getAreaSpannelByContents().y;
				   if(x!=0) stack.get(i).nudgeObjects(-x, 0);
				   if(y!=0) stack.get(i).nudgeObjects(0,-y);
			   }
			   pasteStack( ml.makeAltered(PANELS), stack); 
		   }
		   
		  
			 public void trimCanvas(BasicLayout  ml) {
				 if(ml==null) return;
				 	addBottomSpecialSpace(ml, -ml.specialSpaceWidthBottom);
					addTopSpecialSpace(ml,    -ml.specialSpaceWidthTop);
					addLeftSpecialSpace(ml,   -ml.specialSpaceWidthLeft);
					addRightSpecialSpace(ml,  -ml.specialSpaceWidthRight);
					//ml.getWrapper().updateImageDisplay();
			 }
		   
			 /**moves the layout and contents of the layout
			  * does not allow negative locations*/
		   public void moveLayout(BasicLayout ml, double mx, double my) {	  
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LOCATION_EDIT, mx,  my);
			   notifyListenersOfFutureChange(ml, event);
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ALL_MONTAGE_SPACE));
			   if (ml.specialSpaceWidthTop+my<0) {addTopSpecialSpace(ml, -my);}
			   if (ml.specialSpaceWidthBottom-my<0) {addBottomSpecialSpace(ml, my);}
			   if (ml.specialSpaceWidthLeft+mx<0) {addLeftSpecialSpace(ml, -mx);}
			   if (ml.specialSpaceWidthRight-mx<0) {addRightSpecialSpace(ml, mx);}
			   resetMontageImageSize(ml, 0, 0);
			   ml.setSpecialTopSpace(ml.specialSpaceWidthTop+my);
			   ml.setSpecialBottomSpace(ml.specialSpaceWidthBottom-my);
			   ml.setSpecialLeftSpace(ml.specialSpaceWidthLeft+mx);
			   ml.setSpecialRightSpace(ml.specialSpaceWidthRight-mx);
			   ml.resetPtsPanels();
			   pasteStack(  ml.makeAltered(ALL_MONTAGE_SPACE), stack);
			   finishEdit(ml);
			   notifyListenersOfCompleteChange(ml, event);
			   }
		   
		   /**moves the layout and contents of the layout, allows negative positions*/
		   public void moveMontage2(BasicLayout ml, int mx, int my) {	  
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LOCATION_EDIT, mx,  my);
			   notifyListenersOfFutureChange(ml, event);
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ALL_MONTAGE_SPACE));
			 
			   ml.setSpecialTopSpace(ml.specialSpaceWidthTop+my);
			   ml.setSpecialBottomSpace(ml.specialSpaceWidthBottom-my);
			   ml.setSpecialLeftSpace(ml.specialSpaceWidthLeft+mx);
			   ml.setSpecialRightSpace(ml.specialSpaceWidthRight-mx);
			   ml.resetPtsPanels();
			   pasteStack(  ml.makeAltered(ALL_MONTAGE_SPACE), stack);
			   finishEdit(ml);
			   notifyListenersOfCompleteChange(ml, event);
			   }
   
		   
		   /**moves the position of the panels within the border of those panels. */
		   public  void movePanelOffset(BasicLayout ml,  int increasex, int increasey) {
			   ArrayList<PanelContentExtract> stack= cutStack(ml);
			   
			   getObjectHandler().liftPanelObjects(ml, ml.getVirtualWorksheet());
			
				if (!(Math.abs(ml.xshift+increasex)>ml.theBorderWidthLeftRight/2
					||  Math.abs(ml.yshift+increasey)>ml.theBorderWidthBottomTop/2	) && ml.type%10==BasicLayout.Center_Of_Border)
				ml.setPoints(ml.xshift+increasex, ml.yshift+increasey);
				if (ml.xshift+increasex>=0 && ml.yshift+increasey>=0 &&ml.type%10==BasicLayout.Corner_Of_Border)ml.setPoints(ml.xshift+increasex, ml.yshift+increasey);
				pasteStack(  ml, stack);
				ml.afterEditDone();
		   }
		   
		   /**changes one layout to match another with respect to borders and spaces
		      Does not change the panel dimensions */
		   public  void setToModelLayout(BasicLayout ml,  BasicLayout model) {
			   setBordersToModelLayout(ml, model);
			    setLabelSpacesToModelLayout(ml,model);
			    setSpecialSpacesToModelLayout(ml, model);
			     }
		   
		   /**changes one layout to match another with respect to borders and spaces
		      Does not change the panel dimensions */
		   public  void setBordersToModelLayout(BasicLayout ml,  BasicLayout model) {
			   expandBorderX2(ml, model.theBorderWidthLeftRight-ml.theBorderWidthLeftRight);
			    expandBorderY2(ml, model.theBorderWidthBottomTop-ml.theBorderWidthBottomTop);
			   
		   }
		   
		   /**changes one layout to match another with respect to label spaces
		      Does not change the panel dimensions */
		   public  void setLabelSpacesToModelLayout(BasicLayout ml,  BasicLayout model) {

			    addTopLabelSpace(ml, model.labelSpaceWidthTop-ml.labelSpaceWidthTop);
			    addBottomLabelSpace(ml, model.labelSpaceWidthBottom-ml.labelSpaceWidthBottom);
			    addLeftLabelSpace(ml, model.labelSpaceWidthLeft-ml.labelSpaceWidthLeft);
			    addRightLabelSpace(ml, model.labelSpaceWidthRight-ml.labelSpaceWidthRight);
			   }
		   
		   /**changes one layout to match another with respect to special spaces
		      Does not change the panel dimensions */
		   public  void setSpecialSpacesToModelLayout(BasicLayout ml,  BasicLayout model) {

			    addBottomSpecialSpace(ml, model.specialSpaceWidthBottom-ml.specialSpaceWidthBottom);
			    addTopSpecialSpace(ml, model.specialSpaceWidthTop-ml.specialSpaceWidthTop);
			    addLeftSpecialSpace(ml, model.specialSpaceWidthLeft-ml.specialSpaceWidthLeft);
			    addRightSpecialSpace(ml, model.specialSpaceWidthRight-ml.specialSpaceWidthRight);
		   }
		   
		   /**alters the standrd sizes for the panels. only alters the default size. if some panels have unique sizes,
		      the unique sizes stay the same*/
		   public void resizePanels(BasicLayout ml, double width, double height) {
			   augmentStandardPanelWidth(ml,  width-ml.panelWidth);
			   augmentStandardPanelHeight(ml,  height-ml.panelHeight);
		   }
		   
		   
		
			
			//public ArrayList<ObjectType> lastObjects;
			   /**Alters the montage getting rid of the column at index colIndex*/
			   public PanelContentExtract removeColumn(BasicLayout ml, int colIndex) {
				  
				   
				   if (colIndex>ml.nColumns()||colIndex<1) return null;
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_REMOVAL, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
				   double widthincrease=ml.xincrementOfColumn(colIndex);
				   ml.columnWidths=BasicLayout.takeoutElement(colIndex-1, ml.columnWidths);
				   {ml.setSpecialRightSpace(widthincrease+ml.specialSpaceWidthRight);}
				   PanelContentExtract output= stack.get(colIndex-1);
				   stack.remove(colIndex-1);
				   ml.setNColumns(ml.nColumns() - 1);
				   pasteStack(ml.makeAltered(COLS), stack);
				   finishEdit(ml);
				   this.notifyListenersOfCompleteChange(ml, event);
				   return output;
			   }
			   /**Alters the montage getting rid of the row at index colIndex*/
			   public PanelContentExtract removeRow(BasicLayout ml, int colIndex) {
				   
				   
				   if (colIndex>ml.nRows()||colIndex<1) return null;
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_REMOVAL, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
				   double increase=ml.yincrementOfRow(colIndex);
				   ml.rowHeights=BasicLayout.takeoutElement(colIndex-1,  ml.rowHeights);
				   {ml.setSpecialBottomSpace(increase+ml.specialSpaceWidthBottom);}
				   PanelContentExtract output= stack.get(colIndex-1);
				   stack.remove(colIndex-1);
				   ml.setNRows(ml.nRows() - 1);
				   pasteStack(ml.makeAltered(ROWS), stack);
				   finishEdit(ml);
				   this.notifyListenersOfCompleteChange(ml, event);
				   return output;
			   }
			   
			   /**Alters the montage adding a column at index colIndex. */
			   public void addColumn(BasicLayout ml, int colIndex, PanelContentExtract colContent) {
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_INSERTION, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   BasicLayout layout = ml.makeAltered(COLS);
				 ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
				 double widthnewCol= layout.getPanelWidthOfColumn(colIndex);
				
				 if (colContent==null) {
					  colContent=new PanelContentExtract(new Rectangle(0,0, (int)widthnewCol, (int)ml.getPanelHeightOfRow(1)));
					  colContent.setObjectList(new ArrayList<LocatedObject2D>());
					
				  }
				   
				   int newwidth=colContent.dim().width;
				   
				   ml.columnWidths=BasicLayout.putInElement(colIndex-1, ml.columnWidths);
				   ml.setColumnWidth(colIndex, newwidth);
				   
				   if (colIndex>stack.size()) stack.add(colContent); else stack.add(colIndex-1, colContent);
				   ml.setNColumns(ml.nColumns()+1);
				   if (ml.xincrementOfColumn( colIndex)<ml.specialSpaceWidthRight) {ml.specialSpaceWidthRight-=ml.xincrementOfColumn(colIndex);}else {
					   ml.layoutWidth+=ml.xincrementOfColumn( colIndex);
					   resetMontageImageSize(ml, 0, 0);
					   }
				  // ml.resetPtsPanels();
				   pasteStack(ml.makeAltered(COLS), stack);
				   finishEdit(ml);

				   this.notifyListenersOfCompleteChange(ml, event);
			   }
			   
			   /**Alters the montage swapping two coumns. */
			   public void swapColumn(BasicLayout ml, int index, int index2 ) {
				   if (ml==null||index>ml.nColumns()||index2>ml.nColumns()) return;
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_SWAP, index,  index2);
				   notifyListenersOfFutureChange(ml, event);
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
				  double w1=ml.getPanelWidthOfColumn(index);
				  double w2=ml.getPanelWidthOfColumn(index2);
				   ml.setColumnWidth(index, w2);
				   ml.setColumnWidth(index2, w1);

				   swapArrayListElements(stack, index-1, index2-1);
				 
				   pasteStack(ml.makeAltered(COLS), stack);
				   finishEdit(ml);
				   this.notifyListenersOfCompleteChange(ml, event);
			   }
			   
			   /**Alters the montage by swapping two rows. */
			   public void swapRow(BasicLayout ml, int colIndex, int colIndex2 ) {
				   //does nothing if the arguments are not valid
				   if (ml==null||colIndex>ml.nRows()||colIndex2>ml.nRows()) return;
				   
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_SWAP, colIndex, colIndex2);
				   notifyListenersOfFutureChange(ml, event);
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
				  double w1=ml.getPanelHeightOfRow(colIndex);
				 double w2=ml.getPanelHeightOfRow(colIndex2);
				   ml.setRowHeight(colIndex, w2);
				   ml.setRowHeight(colIndex2, w1);
				   swapArrayListElements(stack, colIndex-1, colIndex2-1);
				   pasteStack(ml.makeAltered(ROWS), stack);
				   finishEdit(ml);

				   this.notifyListenersOfCompleteChange(ml, event);
			   }
			   
			   public void swapArrayListElements(ArrayList<PanelContentExtract> list, int in1, int in2) {
				   if (in1<0||in2<0) return;
				   if (in1>=list.size()) return;
				   if (in2>=list.size()) return;
				   PanelContentExtract ob1 = list.get(in1);
				  PanelContentExtract ob2 = list.get(in2);
				  list.set(in1, ob2);
				  list.set(in2, ob1);
				   
			   }
			   
			   
			   
			   /**Alters the montage adding a column at index colIndex. */
			   public void addRow(BasicLayout ml, int rowIndex, PanelContentExtract colContent) {
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_INSERTION, rowIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   BasicLayout layout = ml.makeAltered(ROWS);
				   if (colContent==null) {
					  colContent=new PanelContentExtract(new Rectangle2D.Double(0,0,layout.panelWidth, layout.getPanelHeightOfRow(rowIndex)));
					  colContent.setObjectList(new ArrayList<LocatedObject2D>());
					  
				  }
				   ArrayList<PanelContentExtract> stack= cutStack(layout);
				   int newwidth=colContent.dim().height;
				   
				   ml.rowHeights=BasicLayout.putInElement(rowIndex-1, ml.rowHeights);
				   ml.setRowHeight(rowIndex, newwidth);
				   
				   if (rowIndex>stack.size()) stack.add(colContent); else stack.add(rowIndex-1, colContent);
				   ml.setNRows(ml.nRows()+1);
				   if (ml.yincrementOfRow( rowIndex)<ml.specialSpaceWidthBottom) {ml.specialSpaceWidthBottom-=ml.yincrementOfRow(rowIndex);}else {
					   ml.layoutHeight+=ml.yincrementOfRow(rowIndex);
					   resetMontageImageSize(ml, 0, 0);
					   }
				  // ml.resetPtsPanels();
				   pasteStack(ml.makeAltered(ROWS), stack);
				   finishEdit(ml);
				   this.notifyListenersOfCompleteChange(ml, event);
			   }
			
			/**Either removes item at index t from the layout or inserts an empty panel at that location
			 * @param insert true if inserting a panel, false otherwise*/
			public void deleteInsertPanel(BasicLayout ml, int t, boolean insert, int type) {
				  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, insert?GridLayoutEditEvent.PANEL_INSERTION: GridLayoutEditEvent.PANEL_REMOVAL, t,  1);
				   notifyListenersOfFutureChange(ml, event);
				
				BasicLayout colLayout=ml.makeAltered(type);
				ArrayList<PanelContentExtract> stack=cutStack(colLayout);
				
				try{
				if (!insert){
				PanelContentExtract last=null;
				
				if (stack.size()>t-1) {
					last=stack.get(t-1);
					}
				if (type==PANELS)lastPanel=last; 
				if (type==ROWS) {lastRow=last;}
				if (type==COLS){lastCol=last;}
				
				stack.remove(last);
				
				
				if (type==ROWS) {
					ml.setNRows(ml.nRows() - 1);
					ml.specialSpaceWidthBottom+=ml.yincrementOfRow(ml.nRows()+1);
					//ml.rowHeights=takeoutElement(t-1, ml.rowHeights);
					}
				if (type==COLS) {
					ml.setNColumns(ml.nColumns() - 1);
					ml.specialSpaceWidthRight+=ml.xincrementOfColumn(ml.nColumns()+1);
					//ml.columnWidths=takeoutElement(t-1, ml.columnWidths);
					}

				} else {
					PanelContentExtract last; 
					if (type==ROWS)
						 last=lastRow; 
					else if (type==COLS)
						 last=lastCol;
					else last=lastPanel;
					
					PanelContentExtract ipnew=new PanelContentExtract(stack.get(0).dim());
					
					ipnew.setObjectList(new ArrayList<LocatedObject2D>());
					
					if (last!=null && last.dim().equals(ipnew.dim())) {
						ipnew=last; 	
					if (type==PANELS)lastPanel=null; 
					if (type==ROWS)lastRow=null;
					if (type==COLS)lastCol=null; 
					
					}
					//lastObjects=null;
					if (t-1<stack.size()) { 
						stack.add( t-1, ipnew);
					}	else 	{
						stack.add(ipnew); 
						}
					if (type==ROWS)addRows(ml, 1);
					if (type==COLS)addCols(ml, 1);
				}
					} catch (Exception e) {IssueLog.log("problen with delete/insertpanel method", e);}
				colLayout=ml.makeAltered(type);
				pasteStack( colLayout,stack);
				ml.resetPtsPanels(ml.xshift, ml.yshift) ;
				ml.afterEditDone();
				  this.notifyListenersOfCompleteChange(ml, event);
			}
			
			
		
			   /**Given a user selected roi is drawn in one of the panels. this alters the montage to 
			    * contain only the contents of that roi in each panel*/
			   public void cropPanels(BasicLayout ml, Rectangle r){
				   int index=ml.getPanelIndex(r.x, r.y);
				   ml.setRectangles((int)(r.x-ml.getPoint(index).getX()), (int)(r.y-ml.getPoint(index).getY()), r.width, r.height);
				 
				  ArrayList<PanelContentExtract> stack = cutStack(ml);
				
				   resizePanels(ml,  (int)r.getWidth(), (int) r.getHeight());
				   pasteStack(ml,stack);
				
			   }
			   
			   /**swaps the objects within one rectangle with those inside another*/
				   public void swapMontagePanels(ObjectContainer imp, Rectangle2D r1, Rectangle2D r2){
		
					ArrayList<LocatedObject2D> o1=getObjectHandler().liftObjectsFromPanelX(imp, r1.getBounds());
				
					ArrayList<LocatedObject2D> o2=getObjectHandler().liftObjectsFromPanelX(imp, r2.getBounds());
					
					getObjectHandler().setObjectsIntoPanelX(o2, imp, r1.getBounds());
					getObjectHandler().setObjectsIntoPanelX(o1, imp, r2.getBounds());
				}
				
				   /**when given two panel location indices, will move the objects in one index to the next one*/
				public void moveMontagePanels(BasicLayout ml, int index1, int index2, int type) {
				
					
					ml=ml.makeAltered(type%100); ml.resetPtsPanels(0, 0);
					int f=1;
					if (type/GROUP_FACTOR==PAIR/GROUP_FACTOR)  f=2;
					if (type/GROUP_FACTOR==TRIAD/GROUP_FACTOR) f=3;
					if (type/GROUP_FACTOR==QUAD/GROUP_FACTOR)  f=4;
					if (type/GROUP_FACTOR==PENT/GROUP_FACTOR)  f=5;

					
					if (index2>index1) for(int i=index1; i<index2; i+=f) { swapPanels(ml, i, i+f);}
					if (index2<index1) for(int i=index1; i>index2; i-=f) { swapPanels(ml, i, i-f);}
					
				}
				
				public void swapPanels(BasicLayout ml, int index1, int index2){
					 if (ml==null||index1>ml.nPanels()||index2>ml.nPanels()) return;
					
					  GridLayoutEditEvent event = new GridLayoutEditEvent(ml,GridLayoutEditEvent.PANEL_SWAP, index1,  index2);
					   notifyListenersOfFutureChange(ml, event);
					try{swapMontagePanels( ml.getVirtualWorksheet(),  ml.getPanel(index1),  ml.getPanel(index2));} catch (Exception e) {IssueLog.logT(e);}
					 this.notifyListenersOfCompleteChange(ml, event);	
				}
				
				/**returns a list of the empty panels*/
				boolean[] emptyPanels(BasicLayout ml) {
					 ArrayList<PanelContentExtract> stack= cutStack(ml);
					   boolean[] output=new   boolean[stack.size()];
					   for(int i=0;i<stack.size();i++) {
						   output[i]=!stack.get(i).hasObjects();
					   }
					   pasteStack( ml, stack);
					   return output;
				}
				
				/**returns the index of the first sequence of empty panels of length n. First panel 
				  is #1, second 2. Returns 0 if none found*/
				public int indexOfFirstEmptyPanel(BasicLayout ml, int n) {
					return indexOfFirstEmptyPanel(ml, n, 0);
				}
				/**returns the index of the first sequence of empty panels of length n. First panel 
				  is #1, second 2. Returns 0 if none found*/
				public int indexOfFirstEmptyPanel(BasicLayout ml, int n, int startSearch) {
					boolean [] theempty=emptyPanels(ml);
					
					int lengthEmpty=0;
					
					for(int j=startSearch; j<theempty.length; j++) {
						if (theempty[j]) lengthEmpty+=1; else lengthEmpty=0;
						if (lengthEmpty==n) return 2+j-n;
					}
					
				
					return 0;
					
				}
				
					/**Calls the edit will occur method in each listener*/
					private void notifyListenersOfFutureChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editWillOccur(e);
					}
					/**Calls the edit occurring method in each listener*/
					private void notifyListenersOfCurrentChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editOccuring(e);
					}
					/**Calls the edit done method in each listener*/
					private void notifyListenersOfCompleteChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editDone(e);
					}

					/**If there are special criteria for classifying an object as outside a panel, this returns them*/
					public LocatedObjectFilter[] getQualificationsForPanelObject() {
						return qualificationsForPanelObject;
					}
					/**this method is used to set special criteria for classifying an object as outside a panel, */
					public void setQualificationsForPanelObject(LocatedObjectFilter... qualificationsForPanelObject) {
						this.qualificationsForPanelObject = qualificationsForPanelObject;
					}
					
					
					
					/**puts each panel object in the upper left corner*/
					public static void placeObjectsInUpperLeftCorners2(BasicLayout gra, ArrayList<LocatedObject2D> objects ) {
						ArrayList<LocatedObject2D> objects2=new  ArrayList<LocatedObject2D>();
						objects2.addAll(objects);
						
						/**puts each panel in the upper left corner*/
						for(Rectangle2D r: gra.getPanels()) {
							ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(r, new ArrayObjectContainer(objects2));
							LocatedObject2D panelItem =BasicObjectListHandler.identifyPanel(r, items);
							if (panelItem!=null) {
								panelItem.setLocationUpperLeft(r.getX(), r.getY());
							}
							
						}
					}
					
				
		   }




