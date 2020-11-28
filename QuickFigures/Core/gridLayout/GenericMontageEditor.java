package gridLayout;


import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import channelLabels.ChannelLabelTextGraphic;
import genericMontageKit.BasicObjectListHandler;
import genericMontageKit.BasicObjectListHandler.LocatedObjectFilter;
import genericMontageKit.PanelLayout;
import genericMontageKit.PanelContentExtract;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_FigureSpecific.FigureLabelOrganizer.ColumnLabelTextGraphic;
import graphicalObjects_FigureSpecific.FigureLabelOrganizer.RowLabelTextGraphic;
import logging.IssueLog;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.TakesLockedItems;


/**This is a form of montage editor that I created as a general class to be adaptable to other 
 Methods in this class Both alter the layout and move the contents appropriately
 */
public class GenericMontageEditor implements MontageSpaces {
	
	
	private BasicObjectListHandler objecthandler=new BasicObjectListHandler();
	private LocatedObjectFilter[] qualificationsForPanelObject=new LocatedObjectFilter[] {};
	
	public BasicObjectListHandler getObjectHandler() {
		return objecthandler;
	}
	
	
	/**Alters the canvas size of the image while preserving the positions of the objects relative to the pixels of the image*/
	public void resetMontageImageSize(BasicMontageLayout ml,  double xOff, double yOff) {
		if (ml==null||ml.getWrapper()==null) return;
		ml.getWrapper().CanvasResizePixelsOnly( (int)ml.layoutWidth, (int)ml.layoutHeight, (int)xOff, (int) yOff);
		getObjectHandler().shiftAll(ml.getWrapper(), xOff, yOff);
		this.finishEdit(ml);
			}

	/**Extracts the content from each panel of this layout and returns the contents of every
	  panel as an array */
	public ArrayList <PanelContentExtract> cutStack(PanelLayout ml) {
		ArrayList <PanelContentExtract> output=new ArrayList <PanelContentExtract>();
		ml.resetPtsPanels();
		Rectangle2D[] panels = ml.getPanels();
		for (Rectangle2D panel: panels) {output.add(cutPanelContents(ml.getWrapper(), panel) );}
		return output;	
	}
	
	/**Reverses the cutStack Method above. */
	public  void pasteStack(PanelLayout ml, ArrayList <PanelContentExtract> panels) {
		Rectangle2D[] rpanels = ml.getPanels();
		for (int i=0; i<rpanels.length&&i<panels.size(); i++) {
			pastePanelContents(ml.getWrapper(), rpanels[i], panels.get(i));
		}
	}
	
	/**takes the objects in a particular panel and returns them*/
	PanelContentExtract cutPanelContents(ObjectContainer imp, Rectangle2D r) {
		
		PanelContentExtract output = new PanelContentExtract(r);
		
		
		output.setObjectList(getObjectHandler().liftObjectsFromPanelX(imp, r , getQualificationsForPanelObject()));
		
		return output;
	}
	
	/**takes the objects in a particular panel and reverses the cutPanelContent method above*/
	void pastePanelContents(ImageWrapper imp, Rectangle2D r, PanelContentExtract p) {
	
		getObjectHandler().setObjectsIntoPanelX(p.getObjectList(),imp, r);
	}
	
		
	/**Simple methods to set the number of rows. */
	   public void addRows(BasicMontageLayout ml, int rows){
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
	   
	   public void setRowNumber(BasicMontageLayout ml, int rows) {
		   addRows(ml, rows-ml.nRows());
	   }
	   
		
		/**Simple method to set the number of columns. */
	   public void addCols(BasicMontageLayout ml, int cols){  
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
	   public void setColNumber(BasicMontageLayout ml, int cols) {
		   addCols(ml, cols-ml.nColumns());
	   }
	   
	   /**Adds label space to the montage to make it include the Rectangle r*/
	   public void expandSpacesToInclude(BasicMontageLayout ml, Rectangle r) {
		   if (ml==null) return;
		   Rectangle space = ml.getSelectedSpace(1,MontageSpaces.ALL_MONTAGE_SPACE).getBounds();
		  
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
	   public void trimLabelSpacesToFitContents(BasicMontageLayout ml) {
		 
		   trimLabelpacesToIncludeOnly(ml, getRecomendedContentArea(ml));
	   }
	   
	   /**returns the area that this montage takes up, returns the dimensions not the position*/
	   private Rectangle getRecomendedContentArea(BasicMontageLayout ml) {
		     ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(MontageSpaces.ALL_MONTAGE_SPACE));   
		   
		   Rectangle area = stack.get(0).getAreaSpannelByContents();
		   area.x+=ml.specialSpaceWidthLeft;
		   area.y+=ml.specialSpaceWidthTop;
		   pasteStack( ml.makeAltered(MontageSpaces.ALL_MONTAGE_SPACE), stack);
		   return area;
	   }
	   
	   /**Adds or trims label space in the montage*/
	   public void fitLabelSpacesToContents(BasicMontageLayout ml) {
		    Area area =new Area( ml.getBoundry());
		    area.add(new Area(ml.allPanelArea()));
		   
		  //Rectangle area = getRecomendedContentArea(ml);//retrieves panel areas
		  ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(area.getBounds(), ml.getWrapper());
		  Area area2=new Area(new Area(ml.allPanelArea()));
		 	
		  for(LocatedObject2D roi:rois) {
			  expandArea(area2, roi);
			 
		  }
		  
		  expandForLockedItems(area2, ml.findHoldingObject());
		   
		   setLabelpacesToIncludeOnly(ml, area2.getBounds());
	   }

	   /**Enlarges area2 to include the given object*/
	protected void expandArea(Area area2, LocatedObject2D rObject) {
		area2.add(new Area(rObject.getBounds()));
		  expandForLockedItems(area2, rObject);
	}
	 /**Enlarges area2 to include the items attached to the given object*/
	protected void expandForLockedItems(Area area2, LocatedObject2D rObject) {
		if (rObject instanceof TakesLockedItems) {
			  TakesLockedItems roi2=(TakesLockedItems) rObject;
			  for (LocatedObject2D item: roi2.getLockedItems()) {
				  
				 area2.add(new Area(item.getBounds()));
				 	
			  }
			  
		  }
	}
	   
	   /**Adds label space to the layout to make sure it includes the Rectangle r (the bounds of a label).
	     */
	  private void trimLabelpacesToIncludeOnly(BasicMontageLayout ml, Rectangle r) {
		   Rectangle space = ml.getSelectedSpace(1,MontageSpaces.ALL_MONTAGE_SPACE).getBounds();

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
	  private void setLabelpacesToIncludeOnly(BasicMontageLayout ml, Rectangle r) {
		   Rectangle space = ml.getSelectedSpace(1,MontageSpaces.ALL_MONTAGE_SPACE).getBounds();
			  /**currently flawd as does not take into account position the the montage*/
		   	this.addLeftLabelSpace(ml, -(r.x-space.x));
			   this.addTopLabelSpace(ml, -(r.y-space.y));
			   this.addRightLabelSpace(ml, (r.x+r.width-space.x-space.width));
			   this.addBottomLabelSpace(ml, (r.y+r.height-space.y-space.height));
			   ensurePositiveLabelSpace(ml);
	
	   }
	  
	  /**If any label spaces are negative, makes them positive. There are relatively few ways a user can set
	    the spaces to negative values. */
	  private void ensurePositiveLabelSpace(BasicMontageLayout ml) {
		  if (ml.labelSpaceWidthLeft<0)  this.addLeftLabelSpace(ml, Math.abs(ml.labelSpaceWidthLeft));
		  if (ml.labelSpaceWidthRight<0)  this.addRightLabelSpace(ml, Math.abs(ml.labelSpaceWidthRight));
		  if (ml.labelSpaceWidthTop<0)  this.addTopLabelSpace(ml, Math.abs(ml.labelSpaceWidthTop));	  
		  if (ml.labelSpaceWidthBottom<0)  this.addTopLabelSpace(ml, Math.abs(ml.labelSpaceWidthBottom));	  
	  }
	  
	   /**Changes the label space at the top of the layout*/
	   public void addTopLabelSpace(BasicMontageLayout ml, double space) {
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
	   public void setTopLabelSpace(BasicMontageLayout ml, double space) {
		   addTopLabelSpace(ml, space-ml.labelSpaceWidthTop);
	   }
		
	   /**Changes the y location of the layout relative to the 0 point*/
		public void addTopSpecialSpace(BasicMontageLayout ml, double space) {
			  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+TOP_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.layoutHeight+=space;
			  ml.specialSpaceWidthTop+=space;
			  resetMontageImageSize(ml, 0, space);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		 /**Changes the y location of the layout relative to the 0 point*/
		public void setTopSpecialSpace(BasicMontageLayout ml, double space) {
			addTopSpecialSpace(ml, space-ml.specialSpaceWidthTop);
		}
		
		
		 /**Changes the label space at the bottom of the layout*/
		public void addBottomLabelSpace(BasicMontageLayout ml, double space) {
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
		public void setBottomLabelSpace(BasicMontageLayout ml, double space) {
			 addBottomLabelSpace(ml, space-ml.labelSpaceWidthBottom);
		}

		/**Changes the amount of empty space that is expected between the bottom of the layout and
		 * the bottom of the canvas*/
		public void addBottomSpecialSpace(BasicMontageLayout ml, double space) {
			  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+BOTTOM_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.layoutHeight+=space;
			  ml.specialSpaceWidthBottom+=space;
			  resetMontageImageSize(ml, 0, 0);
			  notifyListenersOfCompleteChange(ml, event);
		   }
		/**Changes the amount of empty space that is expected between the bottom of the layout and
		 * the bottom of the canvas*/
		public void setBottomSpecialSpace(BasicMontageLayout ml, double space) {
			addBottomSpecialSpace(ml, space-ml.specialSpaceWidthBottom);
		}
		
		 /**Changes the label space at the left of the layout*/
		public  void addLeftLabelSpace(BasicMontageLayout ml, double space) {	
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
		public  void setLeftLabelSpace(BasicMontageLayout ml, double space) {	
			addLeftLabelSpace(ml, space-ml.labelSpaceWidthLeft);
		}
		
		 /**Changes the x location of the layout relative to the 0 point*/
		public  void addLeftSpecialSpace(BasicMontageLayout ml, double space) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+LEFT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  ml.layoutWidth+=space;
			  ml.specialSpaceWidthLeft+=space;
			  resetMontageImageSize(ml, space, 0);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		
		 /**Changes the x location of the layout relative to the 0 point*/
		public void setLeftSpecialSpace(BasicMontageLayout ml, double space) {
			addLeftSpecialSpace(ml, space-ml.specialSpaceWidthLeft);
		}
		
		 /**Changes the label space at the right of the layout*/
		public void addRightLabelSpace(BasicMontageLayout ml, double space) {	
			if(ml.labelSpaceWidthRight+space<0) {space=-ml.labelSpaceWidthRight;}//TODO test if this works
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.LABEL_SPACE_EDIT+RIGHT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			  ml.labelSpaceWidthRight+=space;
			  if (space>ml.specialSpaceWidthRight) {ml.layoutWidth+=space; resetMontageImageSize(ml, 0, 0);  }
			  else ml.setSpecialRightSpace(ml.specialSpaceWidthRight-space);
			  notifyListenersOfCompleteChange(ml, event);
		   }
		 /**Changes the label space at the right of the layout*/
		public void setRightLabelSpace(BasicMontageLayout ml, double space) {
			addRightLabelSpace(ml, space-ml.labelSpaceWidthRight);
		}
		
		/**Changes the amount of empty space that is expected between the right of the layout and
		 * the right end of the canvas*/
		public void addRightSpecialSpace(BasicMontageLayout ml, double space) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ADITIONAL_SPACE_OR_LOCATION_EDIT+RIGHT_SPACE, space, 0 );
			   notifyListenersOfFutureChange(ml, event);
			   
			  ml.layoutWidth+=space;
			  ml.specialSpaceWidthRight+=space;
			  resetMontageImageSize(ml, 0, 0);
			  
			  notifyListenersOfCompleteChange(ml, event);
		   }
		/**Changes the amount of empty space that is expected between the right of the layout and
		 * the right end of the canvas*/
		public void setRightSpecialSpace(BasicMontageLayout ml, double space) {
			addRightSpecialSpace(ml, space-ml.specialSpaceWidthRight);
		}
		
		
		/**Changes the vertical border between panels
		 *When given a layout, this will add to the vertical border around each panel, copy the rows,
		   and paste then into a resized version of the montage*/
		public void expandBorderY2(BasicMontageLayout ml, double t) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.BORDER_EDIT_V, t, 0 );
			   notifyListenersOfFutureChange(ml, event);
			 
			
			if (ml.BorderWidthBottomTop+t<0) return;
			ArrayList<PanelContentExtract> stack=cutStack(ml.makeAltered(ROWS));
	
			ml.setVerticalBorder(ml.BorderWidthBottomTop+t);
			if (t*ml.nRows()<ml.specialSpaceWidthBottom ) {ml.setSpecialBottomSpace(ml.specialSpaceWidthBottom-t*ml.nRows());}
			else resetMontageImageSize(ml, 0, 0);		
			ml.resetPtsPanels() ;
			pasteStack(ml.makeAltered(ROWS), stack);
			
			finishEdit(ml);
			notifyListenersOfCompleteChange(ml, event);
		}
		/**Changes the vertical border between panels*/
		public void setVerticalBorder(BasicMontageLayout ml, double border) {
			expandBorderY2(ml, border-ml.BorderWidthBottomTop);
		}
		
		
		
		/**Changes the horizontal border between panels
		 * When given a layout, this will add to the horizontal border around each panel, copy the columns,
		   and paste then into a resized version of the montage*/
		public void expandBorderX2(BasicMontageLayout ml, double t) {
			GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.BORDER_EDIT_H, t, 0 );
			   notifyListenersOfFutureChange(ml, event);
			ArrayList<PanelContentExtract> stack=cutStack(ml.makeAltered(COLS));
			
			ml.setHorizontalBorder(ml.BorderWidthLeftRight+t);
			if (t*ml.nColumns()<ml.specialSpaceWidthRight) {ml.setSpecialRightSpace(ml.specialSpaceWidthRight-t*ml.nColumns());}
			else resetMontageImageSize(ml, 0, 0);	
			ml.resetPtsPanels() ;
			
			pasteStack(ml.makeAltered(COLS), stack);
			finishEdit(ml);
			notifyListenersOfCompleteChange(ml, event);
		}
		/**Changes the horizontal border between panels*/
		public void setHorizontalBorder(BasicMontageLayout ml, double border) {
			expandBorderX2(ml, border-ml.BorderWidthLeftRight);
		}
		
		/**called after the end of each layout edit. the array of points and rectangles must
		 be reset to reflect the current layout*/
		void finishEdit(BasicMontageLayout ml) {
			ml.resetPtsPanels() ;
			ml.setMontageProperties();
		}
		
		/**Swaps the number of rows with the number of columns of the layout. does not affect the 
		  items in the label spaces*/
		public  GridLayoutEditEvent invertPanels(BasicMontageLayout ml) {	
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
		public void invertPanelsAndLabels(BasicMontageLayout ml) {
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
		public void repackagePanels(BasicMontageLayout ml, int newrow, int newcol) {
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
		protected void flipSnappings(BasicMontageLayout ml) {
			ArrayList<LocatedObject2D> objects = ml.getWrapper().getLocatedObjects();
			
			ArrayList<AttachmentPosition> bahs = AttachmentPosition.findAllPositions(objects);
			
			for(AttachmentPosition b: bahs) {
				b.flipDiag();
			}
		}


		
		  /**Alters the size alloted to the panel. if the panel's column lacks an individual width
		    this simply alters the width of them all*/
		   public void augmentPanelWidth(BasicMontageLayout ml,  double widthincrease, int colIndex) {
			   
			   if (!ml.columnHasIndividualWidth(colIndex)) {
				   augmentPanelWidthold( ml,  widthincrease); 
				   return;
			   }
			   augmentPanelWidthOfCol( ml,  widthincrease, colIndex) ;
		   }
		   public void setPanelWidth(BasicMontageLayout ml,  double newpwidth, int colindex) {
			   augmentPanelWidth(ml, newpwidth-ml.getPanelWidthOfColumn(colindex),colindex);
		   }
		   
			  /**Alters the size alloted to the panel. if the panel's column lacks and individual width
		    this simply alters the width of them all*/
		   public void augmentPanelWidthOfCol(BasicMontageLayout ml,  double widthincrease, int colIndex) {
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
		   
		   public void setPanelWidthOfColumn(BasicMontageLayout ml, int width, int rowIndex) {
			   augmentPanelWidthOfCol(ml, width-ml.getPanelWidthOfColumn(rowIndex), rowIndex);
		   }
		   
		   /**alters the height of the row at rowIndex. if that row lack an individual height,
		     this alters the standard height for all the panels*/
		   public void augmentPanelHeight(BasicMontageLayout ml,  double increase, int rowIndex) {
			   if (!ml.rowHasIndividualHeight(rowIndex)) {
				   augmentPanelHeightold( ml,  increase); 
				   return;
			   }
			   augmentPanelHeightOfRow( ml,   increase,rowIndex);
		   }
		   public void setPanelHeight(BasicMontageLayout ml,  double newpheight, int rowindex) {
			   augmentPanelHeight(ml,newpheight- ml.getPanelHeightOfRow(rowindex),rowindex);
		   }
		   
		   
		   /**alters the height of the row at rowIndex. if that row lack an individual height,
		     this alters the standard height for all the panels*/
		   public void augmentPanelHeightOfRow(BasicMontageLayout ml,  double increase, int rowIndex) {
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
				
				if (ml.getWrapper()!=null) {
					pasteStack( ml.makeAltered(ROWS), stack);
					ml.setMontageProperties();
					}
				finishEdit(ml);
				notifyListenersOfCompleteChange(ml, event);
				
		
}
		   public void setPanelHeightOfRow(BasicMontageLayout ml, double height, int rowIndex) {
			   augmentPanelHeightOfRow(ml, height-ml.getPanelHeightOfRow(rowIndex), rowIndex);
		   }
		   
			  /**Alters the size alloted to a panel. does this for the general width. not an individual panels width*/
		   public void augmentPanelWidthold(BasicMontageLayout ml,  double widthincrease) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.PANEL_RESIZE_H, widthincrease,  1);
			   notifyListenersOfFutureChange(ml, event);
			   
			   
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
			   
				ml.setStandardPanelWidth(ml.getPanelWidthOfColumn(0)+widthincrease);
				if (widthincrease*ml.nColumns()<ml.specialSpaceWidthRight) {ml.setSpecialRightSpace(-widthincrease*ml.nColumns()+ml.specialSpaceWidthRight);}
				else resetMontageImageSize(ml, 0, 0);
				
				pasteStack(ml.makeAltered(COLS), stack);
				ml.setMontageProperties();
				notifyListenersOfCompleteChange(ml, event);
		   }
		   
		   public void augmentPanelHeightold(BasicMontageLayout ml,  double increase) {
			   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.PANEL_RESIZE_V, increase,  1);
			   notifyListenersOfFutureChange(ml, event);
			   
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
			 
				ml.setStandardPanelHeight(ml.getPanelHeightOfRow(0)+increase);
				if (increase*ml.nRows()<ml.specialSpaceWidthBottom) {ml.setSpecialBottomSpace(-increase*ml.nRows()+ml.specialSpaceWidthBottom);}
				else resetMontageImageSize(ml, 0, 0);
				
				if (ml.getWrapper()!=null) {
					pasteStack( ml.makeAltered(ROWS), stack);
					ml.setMontageProperties();
					}
				
				notifyListenersOfCompleteChange(ml, event);
}
		   
		   public void alterPanelWidthAndHeightToFitContents(BasicMontageLayout ml) {
			   alterPanelWidthsToFitContents(ml);
			   ml.resetPtsPanels();
			   alterPanelHeightsToFitContents(ml);
			   ml.resetPtsPanels();
		   }
		   
		   
		   
		   public void roundUpPanelSizes(BasicMontageLayout ml) {
			   for(int i=1; i<=ml.nRows(); i++) 
				   this.setPanelHeight(ml, Math.ceil(ml.getPanelHeightOfRow(i)), i);
			   
			   for(int i=1; i<=ml.nColumns(); i++) 
				   this.setPanelWidth(ml, Math.ceil(ml.getPanelWidthOfColumn(i)), i);
		   }
		   
		   public void roundUpSpaces(BasicMontageLayout ml) {
			   this.setTopSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthTop));
			   this.setBottomSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthBottom));
			   this.setLeftSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthLeft));
			   this.setRightSpecialSpace(ml, Math.ceil(ml.specialSpaceWidthRight));
			   
			   this.setTopLabelSpace(ml, Math.ceil(ml.labelSpaceWidthTop));
			   this.setBottomLabelSpace(ml, Math.ceil(ml.labelSpaceWidthBottom));
			   this.setLeftLabelSpace(ml, Math.ceil(ml.labelSpaceWidthLeft));
			   this.setRightLabelSpace(ml, Math.ceil(ml.labelSpaceWidthRight));
			   
		   }
		   
		   public void roundUpBorders(BasicMontageLayout ml) {
			   this.setHorizontalBorder(ml, Math.ceil(ml.BorderWidthLeftRight));
			   this.setVerticalBorder(ml, Math.ceil(ml.BorderWidthBottomTop));
		   }
		   
		   public void roundUpAll(BasicMontageLayout ml) {
			   this.roundUpPanelSizes(ml);
			   this.roundUpSpaces(ml);
			   this.roundUpBorders(ml);
		   }
		   
		   public void placePanelsInCorners(BasicMontageLayout ml,ArrayList<LocatedObject2D> objects) {
			   /**puts items in upper-left corner*/
			   ml.resetPtsPanels();
				placeObjectsInUpperLeftCorners2(ml, objects);
				ml.resetPtsPanels();
		   }
		   
		   /**some objects do not enter into the calculations when optimizing panel sizes*/
		   static Class<?>[] nonConsideredClasses=new Class<?>[] {BarGraphic.class, BarGraphic.BarTextGraphic.class, ChannelLabelTextGraphic.class, ColumnLabelTextGraphic.class, RowLabelTextGraphic.class};
		   
		   /**experimental. makes the column fit objects*/
		   public void alterPanelWidthsToFitContents(BasicMontageLayout ml) {
			   
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(MontageSpaces.COLUMN_OF_PANELS));
			   ArrayObjectContainer.ignoredClass=BarGraphic.class;
			   ArrayObjectContainer.ignoredClass2=ChannelLabelTextGraphic.class;
			   ArrayObjectContainer.ignoredClasses=nonConsideredClasses;
			   
			   int[] widths=new int[stack.size()];
			   for(int i=0; i<widths.length; i++) {
				   widths[i]=stack.get(i).getAreaSpannelByContents2().width;
			   }
			   
			   
			   if (ml.allColsSameWidth()&&NumberUse.allSame(widths)) {
				   augmentPanelWidthold(ml,  widths[0]-ml.panelWidth);
			   }else 
			    for(int i=0; i<widths.length; i++) {
				  this.setPanelWidthOfColumn(ml, widths[i],i+1);
				  
			   }
			   ArrayObjectContainer.ignoredClass=null;
			   ArrayObjectContainer.ignoredClass2=null;
			   ArrayObjectContainer.ignoredClasses=null;
			   pasteStack( ml.makeAltered(COLUMN_OF_PANELS), stack);
			  
			  
		   }
		   
		 
	
	
		   	   
		   /**experimental. makes the row to fit objects*/
		   public void alterPanelHeightsToFitContents(BasicMontageLayout ml) {
			
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(MontageSpaces.ROW_OF_PANELS));
			   int[] heights=new int[stack.size()];
			   for(int i=0; i<stack.size(); i++) {
				   heights[i]=stack.get(i).getAreaSpannelByContents2().height;
			   }
			   
			   /**must do the setting before the stack paste. the method called also does a 
			     'cut/paste but strange effects occur when this is done one by one*/
			   if (ml.allRowsSameHeight()&&NumberUse.allSame(heights)) {
				   augmentPanelHeightold(ml,  heights[0]-ml.panelHeight);
			   }else  for(int i=0; i<heights.length; i++) {
				   this.setPanelHeightOfRow(ml, heights[i],i+1);
			   }
			   
			   pasteStack( ml.makeAltered(ROW_OF_PANELS), stack);
			  
			   
		   }
		   
		   /**experimental. makes the column fit objects. buggy*/
		   public void shiftPanelContentsToEdge(BasicMontageLayout ml) {
			   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(MontageSpaces.PANELS));
			   int[] widths=new int[stack.size()];
			   for(int i=0; i<widths.length; i++) {
				   int x = stack.get(i).getAreaSpannelByContents().x;
				   int y = stack.get(i).getAreaSpannelByContents().y;
				   if(x!=0) stack.get(i).nudgeObjects(-x, 0);
				   if(y!=0) stack.get(i).nudgeObjects(0,-y);
			   }
			   pasteStack( ml.makeAltered(PANELS), stack); 
		   }
		   
		  
			 public void trimCanvas(BasicMontageLayout  ml) {
				 if(ml==null) return;
				 	addBottomSpecialSpace(ml, -ml.specialSpaceWidthBottom);
					addTopSpecialSpace(ml,    -ml.specialSpaceWidthTop);
					addLeftSpecialSpace(ml,   -ml.specialSpaceWidthLeft);
					addRightSpecialSpace(ml,  -ml.specialSpaceWidthRight);
					//ml.getWrapper().updateImageDisplay();
			 }
		   
		   public void moveMontageLayout(BasicMontageLayout ml, double mx, double my) {	  
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
		   
		   /**moves the contents of the layout, allows negative positions*/
		   public void moveMontage2(BasicMontageLayout ml, int mx, int my) {	  
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
		   public  void movePanelOffset(BasicMontageLayout ml,  int increasex, int increasey) {
			   ArrayList<PanelContentExtract> stack= cutStack(ml);
			   
			   getObjectHandler().liftPanelObjects(ml, ml.getWrapper());
			
				if (!(Math.abs(ml.xshift+increasex)>ml.BorderWidthLeftRight/2
					||  Math.abs(ml.yshift+increasey)>ml.BorderWidthBottomTop/2	) && ml.type%10==BasicMontageLayout.Center_Of_Border)
				ml.setPoints(ml.xshift+increasex, ml.yshift+increasey);
				if (ml.xshift+increasex>=0 && ml.yshift+increasey>=0 &&ml.type%10==BasicMontageLayout.Corner_Of_Border)ml.setPoints(ml.xshift+increasex, ml.yshift+increasey);
				pasteStack(  ml, stack);
				ml.setMontageProperties();
		   }
		   
		   /**changes one layout to match another with respect to borders and spaces
		      Does not change the panel dimensions */
		   public  void setToModelLayout(BasicMontageLayout ml,  BasicMontageLayout model) {
			   setBordersToModelLayout(ml, model);
			    setLabelSpacesToModelLayout(ml,model);
			    setSpecialSpacesToModelLayout(ml, model);
			     }
		   
		   /**changes one layout to match another with respect to borders and spaces
		      Does not change the panel dimensions */
		   public  void setBordersToModelLayout(BasicMontageLayout ml,  BasicMontageLayout model) {
			   expandBorderX2(ml, model.BorderWidthLeftRight-ml.BorderWidthLeftRight);
			    expandBorderY2(ml, model.BorderWidthBottomTop-ml.BorderWidthBottomTop);
			   
		   }
		   
		   /**changes one layout to match another with respect to label spaces
		      Does not change the panel dimensions */
		   public  void setLabelSpacesToModelLayout(BasicMontageLayout ml,  BasicMontageLayout model) {

			    addTopLabelSpace(ml, model.labelSpaceWidthTop-ml.labelSpaceWidthTop);
			    addBottomLabelSpace(ml, model.labelSpaceWidthBottom-ml.labelSpaceWidthBottom);
			    addLeftLabelSpace(ml, model.labelSpaceWidthLeft-ml.labelSpaceWidthLeft);
			    addRightLabelSpace(ml, model.labelSpaceWidthRight-ml.labelSpaceWidthRight);
			   }
		   
		   /**changes one layout to match another with respect to special spaces
		      Does not change the panel dimensions */
		   public  void setSpecialSpacesToModelLayout(BasicMontageLayout ml,  BasicMontageLayout model) {

			    addBottomSpecialSpace(ml, model.specialSpaceWidthBottom-ml.specialSpaceWidthBottom);
			    addTopSpecialSpace(ml, model.specialSpaceWidthTop-ml.specialSpaceWidthTop);
			    addLeftSpecialSpace(ml, model.specialSpaceWidthLeft-ml.specialSpaceWidthLeft);
			    addRightSpecialSpace(ml, model.specialSpaceWidthRight-ml.specialSpaceWidthRight);
		   }
		   
		   /**alters the standrd sizes for the panels. only alters the default size. if some panels have unique sizes,
		      the unique sizes stay the same*/
		   public void resizePanels(BasicMontageLayout ml, double width, double height) {
			   augmentPanelWidthold(ml,  width-ml.panelWidth);
			   augmentPanelHeightold(ml,  height-ml.panelHeight);
		   }
		   
		   
		   public PanelContentExtract lastCol;	
			public PanelContentExtract lastRow;
			public PanelContentExtract lastPanel;
			
			//public ArrayList<ObjectType> lastObjects;
			   /**Alters the montage getting rid of the column at index colIndex*/
			   public PanelContentExtract removeColumn(BasicMontageLayout ml, int colIndex) {
				  
				   
				   if (colIndex>ml.nColumns()||colIndex<1) return null;
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_REMOVAL, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
				   double widthincrease=ml.xincrementOfColumn(colIndex);
				   ml.columnWidths=BasicMontageLayout.takeoutElement(colIndex-1, ml.columnWidths);
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
			   public PanelContentExtract removeRow(BasicMontageLayout ml, int colIndex) {
				   
				   
				   if (colIndex>ml.nRows()||colIndex<1) return null;
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_REMOVAL, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(ROWS));
				   double increase=ml.yincrementOfRow(colIndex);
				   ml.rowHeights=BasicMontageLayout.takeoutElement(colIndex-1,  ml.rowHeights);
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
			   public void addColumn(BasicMontageLayout ml, int colIndex, PanelContentExtract colContent) {
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.COL_INSERTION, colIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   BasicMontageLayout layout = ml.makeAltered(COLS);
				 ArrayList<PanelContentExtract> stack= cutStack(ml.makeAltered(COLS));
				 double widthnewCol= layout.getPanelWidthOfColumn(colIndex);
				
				 if (colContent==null) {
					  colContent=new PanelContentExtract(new Rectangle(0,0, (int)widthnewCol, (int)ml.getPanelHeightOfRow(1)));
					  colContent.setObjectList(new ArrayList<LocatedObject2D>());
					
				  }
				   
				   int newwidth=colContent.dim().width;
				   
				   ml.columnWidths=BasicMontageLayout.putInElement(colIndex-1, ml.columnWidths);
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
			   public void swapColumn(BasicMontageLayout ml, int index, int index2 ) {
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
			   public void swapRow(BasicMontageLayout ml, int colIndex, int colIndex2 ) {
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
			   public void addRow(BasicMontageLayout ml, int rowIndex, PanelContentExtract colContent) {
				   GridLayoutEditEvent event = new GridLayoutEditEvent(ml, GridLayoutEditEvent.ROW_INSERTION, rowIndex,  1);
				   notifyListenersOfFutureChange(ml, event);
				   
				   BasicMontageLayout layout = ml.makeAltered(ROWS);
				   if (colContent==null) {
					  colContent=new PanelContentExtract(new Rectangle2D.Double(0,0,layout.panelWidth, layout.getPanelHeightOfRow(rowIndex)));
					  colContent.setObjectList(new ArrayList<LocatedObject2D>());
					  
				  }
				   ArrayList<PanelContentExtract> stack= cutStack(layout);
				   int newwidth=colContent.dim().height;
				   
				   ml.rowHeights=BasicMontageLayout.putInElement(rowIndex-1, ml.rowHeights);
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
			
			/**Removes item at index t from the layout*/
			public void deleteInsertPanel(BasicMontageLayout ml, int t, boolean insert, int type) {
				  GridLayoutEditEvent event = new GridLayoutEditEvent(ml, insert?GridLayoutEditEvent.PANEL_INSERTION: GridLayoutEditEvent.PANEL_REMOVAL, t,  1);
				   notifyListenersOfFutureChange(ml, event);
				
				BasicMontageLayout colLayout=ml.makeAltered(type);
				ArrayList<PanelContentExtract> stack=cutStack(colLayout);
				
				try{
				if (!insert){
				PanelContentExtract last=null;
				//lastObjects=null;
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
					//ml.rowHeights=MontageLayout.takeoutElement(t-1, ml.rowHeights);
					}
				if (type==COLS) {
					ml.setNColumns(ml.nColumns() - 1);
					ml.specialSpaceWidthRight+=ml.xincrementOfColumn(ml.nColumns()+1);
					//ml.columnWidths=MontageLayout.takeoutElement(t-1, ml.columnWidths);
					}

				} else {
					PanelContentExtract last; 
					if (type==ROWS)last=lastRow; else
					if (type==COLS)last=lastCol;
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
				ml.setMontageProperties();
				  this.notifyListenersOfCompleteChange(ml, event);
			}
			
			
		
			   /**Given a user selected roi is drawn in one of the panels. this alters the montage to 
			    * contain only the contents of that roi in each panel*/
			   public void cropPanels(BasicMontageLayout ml, Rectangle r){
				   int index=ml.getPanelIndex(r.x, r.y);
				   ml.setRectangles((int)(r.x-ml.getPoint(index).getX()), (int)(r.y-ml.getPoint(index).getY()), r.width, r.height);
				 
				  ArrayList<PanelContentExtract> stack = cutStack(ml);
				
				   resizePanels(ml,  (int)r.getWidth(), (int) r.getHeight());
				   pasteStack(ml,stack);
				
			   }
			   
				   public void swapMontagePanels(ImageWrapper imp, Rectangle2D r1, Rectangle2D r2){
				
					   
					
					//ObjectContainer imp2 = getObjectHandler().getWrapper(imp);
					ArrayList<LocatedObject2D> o1=getObjectHandler().liftObjectsFromPanelX(imp, r1.getBounds());
				
					ArrayList<LocatedObject2D> o2=getObjectHandler().liftObjectsFromPanelX(imp, r2.getBounds());
					
					getObjectHandler().setObjectsIntoPanelX(o2, imp, r1.getBounds());
					getObjectHandler().setObjectsIntoPanelX(o1, imp, r2.getBounds());
				}
				
				public void moveMontagePanels(BasicMontageLayout ml, int index1, int index2, int type) {
				
					
					ml=ml.makeAltered(type%100); ml.resetPtsPanels(0, 0);
					int f=1;
					if (type/100==PAIR/100) f=2;
					if (type/100==TRIAD/100) f=3;
					if (type/100==QUAD/100) f=4;
					if (type/100==PENT/100) f=5;

					
					if (index2>index1) for(int i=index1; i<index2; i+=f) { swapMontagePanels(ml, i, i+f);}
					if (index2<index1) for(int i=index1; i>index2; i-=f) { swapMontagePanels(ml, i, i-f);}
					
				}
				
				public void swapMontagePanels(BasicMontageLayout ml, int index1, int index2){
					 if (ml==null||index1>ml.nPanels()||index2>ml.nPanels()) return;
					
					  GridLayoutEditEvent event = new GridLayoutEditEvent(ml,GridLayoutEditEvent.PANEL_SWAP, index1,  index2);
					   notifyListenersOfFutureChange(ml, event);
					try{swapMontagePanels( ml.getWrapper(),  ml.getPanel(index1),  ml.getPanel(index2));} catch (Exception e) {IssueLog.logT(e);}
					 this.notifyListenersOfCompleteChange(ml, event);	
				}
				
				/**returns a list of the empty panels*/
				boolean[] emptyPanels(BasicMontageLayout ml) {
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
				public int indexOfFirstEmptyPanel(BasicMontageLayout ml, int n) {
					return indexOfFirstEmptyPanel(ml, n, 0);
				}
				/**returns the index of the first sequence of empty panels of length n. First panel 
				  is #1, second 2. Returns 0 if none found*/
				public int indexOfFirstEmptyPanel(BasicMontageLayout ml, int n, int startSearch) {
					boolean [] theempty=emptyPanels(ml);
					
					int lengthEmpty=0;
					
					for(int j=startSearch; j<theempty.length; j++) {
						if (theempty[j]) lengthEmpty+=1; else lengthEmpty=0;
						if (lengthEmpty==n) return 2+j-n;
					}
					
				
					return 0;
					
				}
				
				
				
			
				
					  
					public void notifyListenersOfFutureChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editWillOccur(e);
					}
					public void notifyListenersOfCurrentChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editOccuring(e);
					}
					
					public void notifyListenersOfCompleteChange(GridLayout g, GridLayoutEditEvent e) {
						if (g==null) return;
						g.getListeners().editDone(e);
					}

					public LocatedObjectFilter[] getQualificationsForPanelObject() {
						return qualificationsForPanelObject;
					}

					public void setQualificationsForPanelObject(LocatedObjectFilter... qualificationsForPanelObject) {
						this.qualificationsForPanelObject = qualificationsForPanelObject;
					}
					
					
					
					/**puts each panel object in the upper left corner*/
					public static void placeObjectsInUpperLeftCorners2(BasicMontageLayout gra, ArrayList<LocatedObject2D> objects ) {
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




