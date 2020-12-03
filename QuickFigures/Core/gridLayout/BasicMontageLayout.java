package gridLayout;


import infoStorage.BasicMetaDataHandler;
import infoStorage.MetaInfoWrapper;
import logging.IssueLog;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import applicationAdapters.ImageWrapper;
import genericMontageKit.PanelLayout;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;

/**This is the class that keeps the layout of panels. 
   The locations of panels are arranged like a grid
   with rows and columns. Columns, rows and panels are
   indexed from 1 to the total number.
   */
public class BasicMontageLayout implements LayoutSpaces,GridLayout, Serializable, PanelLayout{

	  private transient BasicMetaDataHandler IJMetaDatause=null;
		public  BasicMetaDataHandler IJMetaDatause() {
			if (IJMetaDatause==null) IJMetaDatause=new BasicMetaDataHandler();;
			return IJMetaDatause;
		}
	
	private static final long serialVersionUID = 1L;
	public double layoutWidth, layoutHeight;
	
	/**whether the panels of this montage are rows.*/
	boolean rowlayout=false;
	/**whether the panels of this montage are columns.*/
	boolean collayout=false;
	
	/**is this layout forced to have uniform panels dims despite the width and height arrays*/
	boolean useUniformPanelDimensions=false;
	
	/**the number of columns in the montage*/
	@RetrievableOption(key="xMontage", label="Columns ")
	public int colMontage=1;
	/**the number of rows in a montage*/
	@RetrievableOption(key="yMontage", label="Rows ")
	public int rowMontage=1;
	

	public static final int sequential=0, rowcolumn=1;
	/**this field is an experimental part of my code and not permanent*/
	int panelInsertion=sequential;
	
	
	public static final int Center_Of_Border=0, Corner_Of_Border=1, SPREAD=2;//several varieties of montage layout with slightly different 
	
	/**the general width of a panel*/
	@RetrievableOption(key="wMontage", label="Standard Panel Width")
	public double panelWidth;
	/**the general height of a panel*/
	@RetrievableOption(key="hMontage", label="Standard Panel Height")
	public double panelHeight;

	/**whether the panel indexes are arrayed in row-major order*/
	@RetrievableOption(key="RowMajor", label="Row Major Order")
	public
	boolean rowmajor=true;
	
	/**whether then panel indexes are in normal first to last order*/
	@RetrievableOption(key="InsertionOrder", label="Insertion Order")
	public boolean firsttoLast=true;
	/**The amount of spacing after a panel in the y-axis. ends up representing 
	   the space between panels*/
	@RetrievableOption(key="yBorder", label="Vertical Border Width")
	public
	double BorderWidthBottomTop=2;
	/**The amount of spacing after a panel in the x-axis. ends up representing the
	  space between panels*/
	@RetrievableOption(key="xBorder",label="Horizontal Border Width")
	public
	double BorderWidthLeftRight=2;
	
	/**How much space above the columns are alloted to putting labels.
	  this is part of the montage as well. */
	@RetrievableOption(key="yClear", label="Top Label Space")
	public double labelSpaceWidthTop=10; 
	/**Same as above, just the left space*/
	@RetrievableOption(key="xClear",  label="Left Label Space")
	public
	double labelSpaceWidthLeft=10; 
	/**Same as above, just the bottom space*/
	@RetrievableOption(key="y2Clear",  label="Bottom Label Space")
	public double labelSpaceWidthBottom; 
	/**Same as above just the right space*/
	@RetrievableOption(key="x2Clear",  label="Right Label Space")
	public double labelSpaceWidthRight;
	
	/**Refers to how much canvas space is above the montage. 
	   It is best thought of as the x,y position of the montage
	   within a larger image cordinate system.
	   */
	@RetrievableOption(key="specialSpaceWidthTop", label="Top Non-Montage Space")
	public double specialSpaceWidthTop;
	
	/**Refers to how much canvas space is to the left of the montage.
	   Details similar to above
	 */
	@RetrievableOption(key="specialSpaceWidthLeft", label="Left Non-Montage Space")
	public double specialSpaceWidthLeft;
	
	@RetrievableOption(key="specialSpaceWidthBot", label="Bottom Non-Montage Space")
	public double specialSpaceWidthBottom; 
	@RetrievableOption(key="specialSpaceWidthRight", label="Right Non-Montage Space")
	public double specialSpaceWidthRight;
	
	
	/**the position of the image relative to the upper left hand corner of the panel in the x axis. should normally be 0.
	    */
	@RetrievableOption(key="xshift",  label="x shift")
	public double xshift;
	/**the position of the image relative to the upper left hand corner of the panel in the y axis. should normally be 0.
	   */
	@RetrievableOption(key="yshift", label="y shift")
	public double yshift;
	
	/**The "type" of the montage. currently only one is implmented*/
	@RetrievableOption(key="LayoutType")
	public int type=Corner_Of_Border;
	
	/**the widths of panels in each column. If this is null, then each panel will have
	  individual widths. Refer to methods.*/
	@RetrievableOption(key="GMColumnWidths", label="Column width")
	public double[] columnWidths=new double[] {};
	/**the heights of panels in each row. */
	@RetrievableOption(key="GMRowHeights", label="Row height")
	public double[] rowHeights=new double[] {};
	
	protected transient ImageWrapper wrapper;

	
	
	/**scales the montage*/
	public void scale(double factor) {
		for(int i=0;i<rowHeights.length; i++) {
			rowHeights[i]= rowHeights[i]*factor;
		}
		for(int i=0;i<columnWidths.length; i++) {
			
			columnWidths[i]= columnWidths[i]*factor;
			
		}
		
		panelWidth= panelWidth*factor;
		panelHeight= panelHeight*factor;
		
		
		xshift= xshift*factor;
		yshift= yshift*factor;
		specialSpaceWidthBottom= specialSpaceWidthBottom*factor;
		specialSpaceWidthTop= specialSpaceWidthTop*factor;
		specialSpaceWidthLeft= specialSpaceWidthLeft*factor;
		specialSpaceWidthRight= specialSpaceWidthRight*factor;
		
		labelSpaceWidthTop= labelSpaceWidthTop*factor;
		labelSpaceWidthBottom= labelSpaceWidthBottom*factor;
		labelSpaceWidthLeft= labelSpaceWidthLeft*factor;
		labelSpaceWidthRight= labelSpaceWidthRight*factor;
		
		BorderWidthBottomTop= BorderWidthBottomTop*factor;
		BorderWidthLeftRight= BorderWidthLeftRight*factor;
		
		
		
		layoutWidth= layoutWidth*factor;
		layoutHeight= layoutHeight*factor;
		this.resetPtsPanels();
	}
	
	/**when given a length and and int[], this returns an array of
	length that has as many of the same values as the int[] provided.
	   */
	static double[] expandSize(int length, double[] orign) {
		double[] output=new double[length];
		for (int i=0; i<length&&i<orign.length; i++) {
			output[i]=orign[i];
		}
		return output;
	}
	/**when given an index and an int[], this returns an array of
	that has the integer at at index (0 based) place.
	   */
	static double[] takeoutElement(int place, double[] orign) {
		if (place>=orign.length||place<0) return orign;
		double[] output=new double[orign.length-1];
		for (int i=0; i<orign.length; i++) {
			if (i<place) output[i]=orign[i];
			if (i>place) output[i-1]=orign[i];
		}
		return output;
	}
	/**when given an index and an int[], this returns an array of
	that has the integer at at index (0 based) place.
	   */
	static double[] putInElement(int place, double[] orign) {
		double[] output=new double[orign.length+1];
		for (int i=0; i<orign.length; i++) {
			if (i<place) output[i]=orign[i];
			if (i>place) output[i]=orign[i-1];
		}
		return output;
	}
	
	/**returns true if the integer is valid for a panel width or height
	   false otherwise.*/
	boolean isValidPanelDimension(int i) {
		if (i<=0) return false;
		return true;
	}
	
	 /**The array of grid points. */
	Point2D[] pts= new Point2D[] {};

	/**The array of panels. */
	Rectangle2D[] panels=new Rectangle2D[] {};
	
	
	/**sets the total dimensions of the montage based on panel dimensions, label spaces and borders.
	   This was originally called before creating an image. 
	   this ensures that the fields of the layout are consistent.*/
	public void computeSizes() {
		layoutWidth=SumXincrement(nColumns()) +labelSpaceWidthRight+labelSpaceWidthLeft+specialSpaceWidthLeft+specialSpaceWidthRight;
		layoutHeight=SumYincrement(nRows())+labelSpaceWidthTop+specialSpaceWidthTop+specialSpaceWidthBottom+labelSpaceWidthBottom;
	}
	
	/**returns the array of panel rectangles. does not re-innitialize the rectangles 
	   unless the array is null or empty*/
	public Rectangle2D[] getPanels() {
		if (panels==null||panels.length==0) setPanelRectangles();
		return panels;
	}
	
	 /**This methods innitializes the point array (based top left corner of the panel).*/
 	void setPoints(){
 		setPoints(xshift, yshift);
 	}

	   /**this resets the Point array and rectangles resets the 
    points and rectangles with positions shifted. */
 public void resetPtsPanels(double xshift, double yshift) {
	   setPoints(xshift, yshift);
	   setPanelRectangles();
 }
 public void resetPtsPanels() {
	   resetPtsPanels(0,0); 
 }
	
	/**returns a rectangle representing the panel at the index*/
	public Rectangle2D getPanel(int index) {
		if (panels==null||panels.length==0||index>=panels.length) setPanelRectangles();
		if (index<=nPanels() &&index>=panels.length)resetPtsPanels();
		if (index>panels.length) {
		//	IssueLog.log("was asked to retreive a panel outside the range of the panel list in montagelayout", ("asked for "+index+" while had only panel "+panels.length), "will return the last panel");	
			return panels[panels.length-1];
			}
		//if(index<0) return null;//not a valid index TODO:rewrite so not needed
		return panels[index-1];
	}

	/**return the array of points representing the upper left corners of each panel*/
	public Point2D[] getPoints() {
		if (pts==null||pts.length==0) {setPoints();}
		return pts;
	}
	
	/**returns the point representing the upper left hand corner of the panel at index*/
	public Point2D getPoint(int index) {
		if (pts==null||pts.length==0) {setPoints();}
		return pts[index-1];
	}
	

	 /**Sets the space above the grid of panels that is also part of the column.
	    also alters the point array that stores the upper left hand corners but
	    does not alter the stored rectangles.
	    */
	 public void setTopSpace(double t) {
		 double told=labelSpaceWidthTop;
		 labelSpaceWidthTop=t;
		 layoutHeight+=t-told;
		 movePoints(0, t-told);
	 }
	 /**see method above*/
	 public void setBottomSpace(double t) {
		 double told=labelSpaceWidthBottom;
		 labelSpaceWidthBottom=t;
		 layoutHeight+=t-told;
		 
	 }
	 /**see method above*/
	 public void setLeftSpace(double t) {
		 double told=labelSpaceWidthLeft;
		 labelSpaceWidthLeft=t;
		 layoutWidth+=labelSpaceWidthLeft-told;
		 movePoints(t-told, 0);
	 }
	 
	 /**see method above*/
	 public void setRightSpace(double t) {
		 double told=labelSpaceWidthRight;
		 labelSpaceWidthRight=t;
		 layoutWidth+=t-told;
	 }
	 
	 /**sets the distance between panels in the x direction*/
	 public void setHorizontalBorder(double t) {
		 if (t<0) { return;}
		 double told=BorderWidthLeftRight;
		 BorderWidthLeftRight=t;
		 layoutWidth+=(t-told)*nColumns();
	 }
	 
	 /**sets the distance between panels in the y direction*/
	 public void setVerticalBorder(double t) {
		 if (t<0) { return;}
		 double told=BorderWidthBottomTop;
		 BorderWidthBottomTop=t;
		 layoutHeight+=(t-told)*nRows();
	 }
	 
		
		/**moves all points in the point array by displacement x,y
		   */
		Point2D[] movePoints(double x, double y) { 
			return pts=getmovedPoints(pts,x,y);
		}
		
		public static Point2D[] getmovedPoints(Point2D[] pts, double x, double y) {  
			if (x==0&&y==0) return pts;
			for (int i=0; i<pts.length; i++) pts[i]=new Point2D.Double((pts[i].getX()+x), (pts[i].getY()+y));
			return pts;
		}
		
		/**Initializes the rectangle array containing the panels. Panels are equally spaced rectangles
		   positioned in a grid.
		   Panels may have either uniform height or a height depending on the row number.
		   Panels may have either uniform width or a width depending on the column number
		 */
		public void setPanelRectangles() {
			if (pts==null||pts.length==0||pts.length<nPanels()) setPoints();
			panels=new Rectangle2D[pts.length];
			for (int i=0; i<pts.length; i++) {
				panels[i]=createRectFor(i+1, pts[i]) ;
				//old call preserved
				//panels[i]=new Rectangle((int)(pts[i].getX()), (int)(pts[i].getY()), getPanelWidth(i+1), getPanelHeight(i+1));
			}
		}
		
		/**creates a rectangle of the appropriate dimensions for panel i. places it at point pt*/
		public Rectangle2D createRectFor(int i, Point2D pt) {
			return new Rectangle2D.Double((pt.getX()), (pt.getY()), getPanelWidth(i), getPanelHeight(i));
		}
		
	//public int getPanelWidth() {return panelWidth;}
	//public int getPanelHeight() {return panelHeight;}
		
		 /**This setter method sets up the spaces that will be reserved for the row and column labels.*/
		 public void setLabelSpaces(int labelSpaceWidthTop, int labelSpaceWidthBottom, int labelSpaceWidthLeft, int labelSpaceWidthRight) {
			 setTopSpace(labelSpaceWidthTop);
			 setBottomSpace(labelSpaceWidthBottom);
			 setLeftSpace(labelSpaceWidthLeft);
			 setRightSpace(labelSpaceWidthRight) ;
		 };
		 
		 /**This method sets the space in the motnage that is not part of a row or column 
		    and is treated like an outside image*/
		 public void setSpecialTopSpace(double t) {
			 double told=specialSpaceWidthTop;
			 specialSpaceWidthTop=t;
			 layoutHeight+=t-told;
			 movePoints(0, t-told);
		 }
		 public void setSpecialLeftSpace(double t) {
			 double told=specialSpaceWidthLeft;
			 specialSpaceWidthLeft= t;
			 layoutWidth+=t-told;
			 movePoints(t-told, 0);
		 }
		 
		 public void setSpecialBottomSpace(double t) {
			 double told=specialSpaceWidthBottom;
			 specialSpaceWidthBottom=t;
			 layoutHeight+=t-told;
		 }
		 public void setSpecialRightSpace(double t) {
			 double told=specialSpaceWidthRight;
			 specialSpaceWidthRight=t;
			 layoutWidth+=t-told;
		 }
		 
		 public void setAdditionalSpaces(int topspace, int bottomspace, int leftspace, int rightspace) {
			 setSpecialTopSpace(topspace); 
			 setSpecialBottomSpace(bottomspace); 
			 setSpecialLeftSpace(leftspace); 
			 setSpecialLeftSpace(rightspace);  
		 }
		 
		  /**when given a montage layout argument, this sets all field in this layout to match the
	     argument.*/
	   public void setToMatch(BasicMontageLayout ml) {
		  // setImage(ml.getImage());
		   matchLayoutSettings(ml);
	  		
	   }
	   
	   public void matchLayoutSettings(BasicMontageLayout ml) {
		   pts= ml.pts;        panels=ml.panels;
	  		layoutWidth=ml.layoutWidth;      layoutHeight=ml.layoutHeight;
	  		setNColumns(ml.nColumns());          setNRows(ml.nRows());
	  		specialSpaceWidthTop=ml.specialSpaceWidthTop; 
	  		specialSpaceWidthLeft=ml.specialSpaceWidthLeft;
	  		specialSpaceWidthBottom=ml.specialSpaceWidthBottom;
	  		specialSpaceWidthRight=ml.specialSpaceWidthRight;
	  		labelSpaceWidthTop=ml.labelSpaceWidthTop;             labelSpaceWidthLeft=ml.labelSpaceWidthLeft;
	  		labelSpaceWidthBottom=ml.labelSpaceWidthBottom;       labelSpaceWidthRight=ml.labelSpaceWidthRight;
	  		panelWidth=ml.panelWidth;        panelHeight=ml.panelHeight;
	  		//xincrement=ml.xincrement(1);        yincrement=ml.yincrement(1);
	  		//frameBorderWidth=ml.frameBorderWidth;
	  		BorderWidthBottomTop=ml.BorderWidthBottomTop;   BorderWidthLeftRight=ml.BorderWidthLeftRight;
	  		rowmajor=ml.rowmajor;
	  		xshift=ml.xshift; yshift=ml.yshift; panelInsertion=ml.panelInsertion;
	  		firsttoLast=ml.firsttoLast;
	  		if(ml.columnWidths!=null)
	  		columnWidths=ml.columnWidths.clone();
	  		if(ml.rowHeights!=null)
	  		rowHeights=ml.rowHeights.clone();
	  		
	  		ml.resetPtsPanels();
	  		
	   }
	   

		/**when given a rectangle sets the layout such that the standard panels dimensions
		  correspond to the dimensions of the rectangle, and the labels spaces correspond to the
		  position of the rectangle (x,y)*/
		public void setLayoutBasedOnRect(Rectangle r) {
			panelWidth=r.width; panelHeight=r.height;
			labelSpaceWidthLeft=r.x; labelSpaceWidthTop=r.y;
			layoutWidth=r.x+r.width;
			layoutHeight=r.y+r.height;
		}

		/**makes uniformly sized rectangles for the panels*/
		public void setRectangles(int x, int y, int width, int height) {
			panels=getMontagePositionRectangles(x,y, width,height);
		}
		
		/**returns an array of rectangles with the given, parameters psotioned relative to the
		   upper left hand corner of the panels (panel position). */
		public Rectangle2D[] getMontagePositionRectangles(double x, double y, double width, double height) {
			Rectangle2D[] panels=new Rectangle2D[pts.length];
			for (int i=0; i<pts.length; i++) {
				panels[i]=new Rectangle2D.Double((pts[i].getX()+x), (pts[i].getY()+y), width, height);
			}
			return panels;
		}
		
		
		/**sets the standard panel width*/
		 public void setStandardPanelWidth(double t) {
			// if (panelWidth<=0) {IssueLog.log("invalid panel width at "+panelWidth);}
			 double told=panelWidth;
			 panelWidth=t;
			// xincrement+=(t-told);
			 layoutWidth+=(panelWidth-told)*nColumns();
			 resetPtsPanels();
			 if (panelWidth<=0) {IssueLog.log("Error bizare panel width after resetof ponts and panels "+panelWidth);}
		 }
		 
		 /**sets the standard panel height*/
		 public void setStandardPanelHeight(double t) {
			 double told=panelHeight;
			 panelHeight=t;
			 //yincrement+=t-told;
			 layoutHeight+=(panelHeight-told)*nRows();
			 resetPtsPanels(xshift, yshift);
		 }
		 /**A convenience method that calls both the above methods*/
		 public void setPanelSizes(int width, int height) {
			 setStandardPanelWidth(width); setStandardPanelHeight(height);
		 }
		 
		 
			/**when given an index of a panel, this returns the column and row number*/
			public int[] getGridCordAtIndex(int i) {
				int column=1;
				int row =1;
				if (rowmajor) {
					while (i>nColumns()) {row++; i-=nColumns();}
					return new int[] {i, row};
					}
				else {
					while (i>nRows()) {column++; i-=nRows();}
					return new int[] {column, i};
					}
			//	return new int[column, row];
			}
			
			/**when given the index of a panel, returns the column index.*/
			public int getColAtIndex(int i) {
				return getGridCordAtIndex(i)[0];
				
			}
			
			/**when given the index of a panel, returns the row index.*/
			public int getRowAtIndex(int i) {
				return getGridCordAtIndex(i)[1];
			}

			/**when given a column and row number, this returns the panel index*/
			public int getIndexAtPosition( int row, int column) {
				int i;
				if (rowmajor) {i=column+(row-1)*(nColumns()); }
				else {i=row+(column-1)*(nRows()); }
				if (!firsttoLast) return panels.length+1-i;
				return i;
			}
			
			/**When given a pair of x,y cordinates, returns the position in the montage of the cordinates 
			 in terms of rows and columns. [column, row]*/
			public int[] getPanelPosition(double x, double y) {
				int column=1;
				int row=1;
				while (xincrementOfColumn(column)+labelSpaceWidthLeft+specialSpaceWidthLeft-1<x) {x-=xincrementOfColumn(column); column++;}//these loops are meant to put the Roi into the upper left panel of the montage
				while (yincrementOfRow(row)+labelSpaceWidthTop+specialSpaceWidthTop-1<y) {y-=yincrementOfRow(row); row++;}
				//IssueLog.log("found location to be row "+row +" and col "+column);
				return new int[]{column, row};
			}
			
			
			
			/**Given a point, returns the number of the panel that contain it.
			  range: 1-nrows*ncolumns inclusive*/
			public int getPanelIndex(double x, double y) {
				int[] position=getPanelPosition( x, y);
				return getIndexAtPosition( position[1], position[0]);
				
				}
			
			
			/**returns the distance between a point in coloumn index, and the same position in
			   the next column. This is used to set up the positions of the points and panels
			   that can be retrieved by the getPoints() and getPanels() methods.*/
			double xincrementOfColumn(int index) {
				return getPanelWidthOfColumn(index)+rightBorderOfColumn(index);
			}
			/**returns the distance between a point in row index, and the same position in
			   the next row. This is used to set up the positions of the points and panels
			   that can be retrieved by the  and getPanels() methods*/
			double yincrementOfRow(int index) {
				return getPanelHeightOfRow(index)+bottomBorderOfRow(index) ;
			}
			
			/**returns the panel width in col number index of the montage.
			   this class was initially designed with uniform widths between 
			   different columns so bugs may exist in forms with differently sized 
			   panels */
			public double getPanelWidthOfColumn(int index) {
				if (columnHasIndividualWidth(index)) return columnWidths[index-1];
				return panelWidth;
			};
			
			/**returns the panel height in row number index of the montage
			   this class was initially designed with uniform widths heights
			   different rows so bugs may exist in forms with differently sized 
			   panels */
			public double getPanelHeightOfRow(int index) {
				if (rowHasIndividualHeight(index)) return rowHeights[index-1];
				return  panelHeight;
			};	
			
			
			/**returns the panel width in panel number index of the montage. 
			   */
			public double getPanelWidth(int index) {
				int colIndex=getGridCordAtIndex(index)[0];
				return getPanelWidthOfColumn(colIndex);
			};

			/**returns the panel height in panel number index of the montage*/
			public double getPanelHeight(int index) {
				int colIndex=getGridCordAtIndex(index)[1];
				return getPanelHeightOfRow(colIndex);
			};	


			
			/**return the distance between the right edge of a panel in column index
			  and the left edge of the next panel. In other words the spacing between panels*/
			double rightBorderOfColumn(int index) {
				return BorderWidthLeftRight;
			}

			
			/**returns the distance between the bottom edge of a panel in row index, 
			  and the top edge of the next panel*/
			double bottomBorderOfRow(int index) {
				return BorderWidthBottomTop;
			}
			
			/**adds up all the 2 increments up to and including the given column index*/
			double SumYincrement(int index) {
				double sum=0;
				for (int i=1; i<=index; i++) {sum+=yincrementOfRow(i) ;}
				return sum;
			}
			/**adds up all the 2 increments up to and including the given column index*/
			double SumXincrement(int index) {
				double sum=0;
				for (int i=1; i<=index; i++) {sum+=xincrementOfColumn(i) ;}
				return sum;
				
			}

			 public void setCols(int t) {
				 int told=nColumns();
				 setNColumns(t);
				 layoutWidth+=(t-told)*xincrementOfColumn(nColumns());
			 }
			 
			 public void setRows(int t) {
				 int told=nRows();
				 setNRows(t);
				 layoutHeight+=(t-told)*yincrementOfRow(nRows());
			 }
			 
			 	 /**This methods sets the point array to x,y displacements from the top left corner of the panel.
			 	    */
				public void setPoints(double x, double y) {
					xshift=x; yshift=y;
					Point2D[] p=new Point2D[nColumns()*nRows()];
				
					double x2;//=x+BorderWidthLeftRight/2+labelSpaceWidthLeft+specialSpaceWidthLeft;
					double y2;//=y+BorderWidthBottomTop/2+labelSpaceWidthTop+specialSpaceWidthTop;
					
						x2=x+labelSpaceWidthLeft+specialSpaceWidthLeft;
						y2=y+labelSpaceWidthTop+specialSpaceWidthTop;
					
					
					if (rowmajor){
					for (int i=0; i<nColumns()*nRows(); i++) {
						int col=i%nColumns();//current column, 0 based
						int row=i/nColumns();//current row, 0 based
						 
						double nx = x2+SumXincrement(col);
						double ny = y2+SumYincrement(row);
						p[i]=new Point2D.Double(nx, ny);
					}}
					
					else {
						for (int i=0; i<nColumns()*nRows(); i++) {
							int row=i%nRows();//current row, 0 based
							int col=i/nRows();//current column, 0 based
							double nx = x2+SumXincrement(col);
							double ny = y2+SumYincrement(row);
							p[i]=new Point2D.Double(nx, ny);
						}}
					
					pts=p;
					if (!firsttoLast) {
						Point2D[] p2=new Point2D[p.length] ;
						for(int i=0; i<p.length; i++) {
							p2[p.length-1-i]=p[i];
						}
						pts=p2;
					}
					
				}
	
	public String layoutKey="";
	private GridLayoutEditListenerList listeners;
	public String getKey() {
		return layoutKey;
	}
	
	/**sets unique column widths from as many columns as numbers provided*/
	public void setIndividualColumnWidths(int... width) {
		for(int i=0; i<width.length; i++) {
			setColumnWidth(i+1, width[i]); 
		}
		//columnWidths=width;
	}
	/**sets unique row heights for the rows provided*/
	public void setIndividualRowHegihts(int... height) {
		for(int i=0; i<height.length; i++) {
			setRowHeight(i+1, height[i]); 
		}
	}

	
	/**sets up a unique width for column nubmer col*/
	void setColumnWidth(int col, double width) {
		if (columnWidths==null ||columnWidths.length==0) columnWidths=new double[nColumns()];
		if (col>columnWidths.length&&nColumns()>=col) {
			columnWidths=expandSize(nColumns(), columnWidths);
		}
		if (col<1) return;
		if (col>columnWidths.length) return;
		if (width==panelWidth) width=0;
		if (width==getPanelWidthOfColumn(col)) return;
		if (nColumns()==1&&width>0) {
			panelWidth=width;
			width=0;
		}
		
		columnWidths[col-1]=width;
		
		if(allsame(columnWidths, width)&&width>0) {
			panelWidth=width;
			for(int i=0; i<columnWidths.length; i++) {columnWidths[i]=0;}
		}
	}
	
	private boolean allsame(double[] items, double width) {
		for(double i: items) {
			if(i!=width) return false;
		}
		return true;
	}
	
	/**sets the Width of the column that corresponds to that panels columns*/ 
	public void setPanelWidth(int panel, double width) {
		setColumnWidth(getColAtIndex(panel), width);
	}
	/**sets the Height of the Row that corresponds to that panels columns*/ 
	public void setPanelHeight(int panel, double height) {
		setRowHeight(getRowAtIndex(panel), height);
	}

	
	
	/**sets up a unique height for row number row*/
	void setRowHeight(int row, double height) {
		if (rowHeights==null ||rowHeights.length==0) rowHeights=new double[nRows()];
		if (row>rowHeights.length&&nRows()>=row) {
			rowHeights=expandSize(nRows(), rowHeights);
		}
		if (row<1) return;
		if (row>rowHeights.length) return;
		if (height==panelHeight) height=0;
		if (height==getPanelHeightOfRow(row)) return;
		if (nRows()==1&&height>0) {
			panelHeight=height;
			height=0;
		}
		rowHeights[row-1]=height;
		
		/**if it turns out all the individual heights are identical*/
		if(allsame(rowHeights, height)&&height>0) {
			panelHeight=height;
			for(int i=0; i<rowHeights.length; i++) {rowHeights[i]=0;}
		}
	}
	
	
	public boolean allRowsSameHeight() {
		for(int i=0; i<this.nRows(); i++) {
			if (this.rowHasIndividualHeight(i)) return false;
		}
		return true;
	}
	
	public boolean allColsSameWidth() {
		for(int i=0; i<this.nColumns(); i++) {
			if (this.columnHasIndividualWidth(i)) return false;
		}
		return true;
	}
		
	/**returns true if the column has a unique individual panel width.
	   false, if it has the general panel width. if the width is set to
	   0, this should also return false.*/
	public boolean columnHasIndividualWidth(int colIndex) {
		if (columnWidths==null) return false;
		if (colIndex>columnWidths.length) return false;
		if (colIndex<1) return false;
		if (columnWidths[colIndex-1]==0) return false;
		return !useUniformPanelDimensions;
	}
	

	
	/**returns true if the row has an individual height for its panels.
	  false if it has the general panel height. If the height is set to
	  0, this should also return false; */
	public boolean rowHasIndividualHeight(int rowIndex) {
		if(rowHeights==null) return false;
		if (rowIndex>rowHeights.length) return false;
		if (rowIndex<1) return false;
		if (rowHeights[rowIndex-1]==0) return false;
		return !useUniformPanelDimensions;
	}
	

	 
	 /**the generic constructor for a layout. */
	public BasicMontageLayout(){}
	
	/**constructs a layout with the given dimensions*/
	 	public BasicMontageLayout(int cols, int rows, int panelWidth, int panelHeight, int BorderWidthBottomTop, int BorderWidthLeftRight , boolean rowmajor) {
	 		this.rowmajor=rowmajor;
	 		this.setNColumns(cols);
	 		this.setNRows(rows);
	 		this.panelWidth=panelWidth;
	 		this.panelHeight=panelHeight;
	 		this.labelSpaceWidthLeft=0;
	 		this. labelSpaceWidthBottom=0;
	 		this.labelSpaceWidthRight=0;
	 		this.labelSpaceWidthTop=0;
	 		this.BorderWidthBottomTop=BorderWidthBottomTop;
	 		this.BorderWidthLeftRight=BorderWidthLeftRight;
	 		computeSizes();
	 		setPoints(xshift, yshift);
	 		setPanelRectangles();
	 }
	
	 	public BasicMontageLayout(MetaInfoWrapper mdw) {
	 		setToMetaData(mdw);
	 	}
	 	
	 	private void setToMetaData(MetaInfoWrapper impmw) {
			new BasicMetaDataHandler().loadAnnotatedFields(impmw, this, this.getKey());
	 }

			/**Given a point in the montage, returns the point's position relative to the origen of its panel.*/
			public Point2D convertToPanelCordinatePosition(Point2D p1) {
				int i=getPanelIndex(p1.getX(), p1.getY());
				Point2D p2 = pts[i-1];
				return new Point2D.Double((p1.getX()-p2.getX()), (p1.getY()-p2.getY()));
			}
			
			/**returns the panel index of each panel in row number row*/
			public int[] getRowIndexes(int row) {
				int[] output = new int[nColumns()];
				for(int j=1; j<=nColumns(); j++) {
					int index=getIndexAtPosition( row, j);	
					output[j-1]=index;
				}
				return output;
			}
			
			/**returns the panel indes of each panel in columns number col*/
			public int[] getColIndexes(int col) {
				int[] output = new int[nRows()];
				for(int j=1; j<=nRows(); j++) {
					int index=getIndexAtPosition( j, col);
					output[j-1]=index;
				}
				return output;
			}
			
		   /**
		   private AbstractMontageLayout<ImageType> makeColumnLayout() {
			   AbstractMontageLayout<ImageType> ml2 = duplicate();
			   ml2.convertToColumnLayout();
			   return ml2;
		   }*/
		   
		   /**transforms the columns into panels of this layout*/
		   private void convertToColumnLayout() {
			   /**not sure if this calculated correctlu*/
			   double newheight=SumYincrement(nRows())+labelSpaceWidthBottom+labelSpaceWidthTop-bottomBorderOfRow(nRows());//-BorderWidthBottomTop;			  
			   panelHeight=newheight; 
			   
			   colapseVertical();
			   resetPtsPanels();
		   }
		   
		   /**sets all vertical data to 0 resulting in a montage with 1 row only*/
		   private void colapseVertical() {
			   setNRows(1);
			   labelSpaceWidthTop=0;
			   labelSpaceWidthBottom=0;
			   BorderWidthBottomTop=0;
			   rowHeights=null;
			   resetPtsPanels(xshift, 0);
		   }
		 
		   
		   /**transforms the columns into panels of this layout*/
		   void convertToRowLayout() {
			  double newWidth = SumXincrement(nColumns())+labelSpaceWidthRight+labelSpaceWidthLeft-rightBorderOfColumn(nColumns());
			panelWidth=newWidth;
			  colapseHorizontal();
			  resetPtsPanels();
		   }
		   
		   
		   /**sets all horizontal data to 0 resulting in a montage with 1 col only*/
		   void colapseHorizontal() {
			   setNColumns(1);
			   columnWidths=null;
			   labelSpaceWidthLeft=0;
			   labelSpaceWidthRight=0;
			   BorderWidthLeftRight=0;
			  
			   resetPtsPanels(0, yshift);
		   }
		   
	
		   
		   /**returns a version of this layout in which that space that
		     was part of the horizontal borders in this layout becomes
		     part of the panel width. does not take into accound columns 
		     with unique dimensions so will be wrong for some layouts
		      */
		    void converToHorizontalBorderLess(){
			   panelWidth+=rightBorderOfColumn(0);
			   BorderWidthLeftRight=0;
			   resetPtsPanels(0, yshift); 
		   }
		   
		   
		   
		   /**returns a version of this layout in which that space that
		     was part of the vertical borders in this layout becomes
		     part of the panel height. 
		      */
		    void convertToVerticalBorderLess(){
			   panelHeight+=bottomBorderOfRow(0);
			   BorderWidthBottomTop=0;
			   resetPtsPanels(xshift, 0);
		   }
		   
		   
		    
		  /**creates a layout in which that span of every panel in the column is
	      consolidated to a singel panel*/
		  private void converttoPanelColumnLayout() {
			  panelHeight=SumYincrement(nRows())- bottomBorderOfRow(nRows());
			  rowHeights=null;
			  labelSpaceWidthBottom+=  bottomBorderOfRow(nRows());
			  setNRows(1);
			  BorderWidthBottomTop=0;
			  resetPtsPanels(xshift, 0);
		  }
		  

		   
		   private void convertoPanelRowLayout() {
			   panelWidth=SumXincrement(nColumns())- rightBorderOfColumn(nColumns());
			   columnWidths=null;
			   labelSpaceWidthRight+=  rightBorderOfColumn(nColumns());
			   setNColumns(1);
			   BorderWidthLeftRight=0;
			    resetPtsPanels(0, yshift);
		   }
		   
		   
			/**returns the panel at a given point*/
			public Rectangle2D getContaingPanel( int x, int y) {
				setPanelRectangles();
				int i=getPanelIndex(x, y);
				return panels[i-1];
			}
			
			 /**Given a montage layout. Returns and Roi corresponding to the space above the panel.
			    */
			 public Rectangle2D getTopSpace(int i) {
				 Rectangle2D panel=getPanel(i);
				 double space=getSpaceAbovePanel(i);
				 return new Rectangle2D.Double(panel.getX(),  panel.getY()-space, panel.getWidth(), space); 

			 }
			 
			 public double getSpaceAbovePanel(int index) {
					int xMontage=getGridCordAtIndex(index) [1];
					if (xMontage==1) return labelSpaceWidthTop; else
					 return bottomBorderOfRow(xMontage-1);
			 }
			 
			 public Rectangle2D getLeftSpace(int i) {
				 Rectangle2D panel=getPanel(i);
				 double space=getSpaceLeftOfPanel(i);
				 return new Rectangle2D.Double(panel.getX()-space, panel.getY(), space, panel.getHeight());

			 }
			 
			 public double getSpaceLeftOfPanel(int index) {
					int xMontage=getGridCordAtIndex(index) [0];
					if (xMontage==1) return labelSpaceWidthLeft; else
					 return rightBorderOfColumn(xMontage-1);
			 }
			 
			 
			 
			 public Rectangle2D getRightSpace(int i) {
				 Rectangle2D panel=getPanel(i);
				 double space=getSpaceRightOfPanel(i);
				 return new Rectangle2D.Double(panel.getX()+panel.getWidth(), panel.getY(), space, panel.getHeight());

				 }
			 
			 public double getSpaceRightOfPanel(int index) {
					int xMontage=getGridCordAtIndex(index) [0];
					if (xMontage==nColumns()) return labelSpaceWidthRight; else
					 return rightBorderOfColumn(xMontage);
			 }
			 
			 
			 /**Given a montage layout. Returns and Roi corresponding to the space above the panel.
			    */
			 public Rectangle2D getBottomSpace(int i) {
				 Rectangle2D panel=getPanel(i);
				 double space=getSpaceBelowPanel(i);
				 return new Rectangle2D.Double(panel.getX(), panel.getY()+panel.getHeight(), panel.getWidth(), space); 

			 }
			 
			 public double getSpaceBelowPanel(int index) {
					int xMontage=getGridCordAtIndex(index) [1];
					if (xMontage==nRows()) return labelSpaceWidthBottom; else
					 return bottomBorderOfRow(xMontage-1);
			 }

			 
			   public BasicMontageLayout makeAltered(int type) {
				   BasicMontageLayout out=duplicate();
				   out.convertAltered(type);
				   return out;
				 
			   }
			   
			  public BasicMontageLayout duplicate() {
				BasicMontageLayout out = new BasicMontageLayout();
				out.matchLayoutSettings(this);
				out.setWrapper(getWrapper());
				return out;
			}
			  
			void convertAltered(int type) {
				  switch(type) {
				  case COLS:convertToColumnLayout(); return;
				  case ROWS:convertToRowLayout();return;
				  case PANELS_WITH_BORDER: convertBorderLess();return;
				  case EXTENDED_COL: {convertBorderLess();convertToColumnLayout();return;}
				  case EXTENDED_ROW: {convertBorderLess();convertToRowLayout();return;}
				  case COLUMN_OF_PANELS: {converttoPanelColumnLayout();return;}
				  case ROW_OF_PANELS: {convertoPanelRowLayout();return;}
				  case EXTENDED_LEFT_SPACE: {convertToVerticalBorderLess();return;}
				  case EXTENDED_RIGHT_SPACE: {convertToVerticalBorderLess();return;}
				  case EXTENDED_BOTTOM_SPACE: {converToHorizontalBorderLess();return;}
				  case EXTENDED_TOP_SPACE: {converToHorizontalBorderLess();return;}
				  case BLOCK_OF_PANELS: {convertToColumnLayout();convertoPanelRowLayout();return;}
				  case ALL_MONTAGE_SPACE: { convertToColumnLayout();convertToRowLayout();convertBorderLess();return;}
				  }
	  
			  }
			  
			  void convertBorderLess() {
				  converToHorizontalBorderLess();
				  convertToVerticalBorderLess();
			  }
			   
			  /**returns a shape of the given type that would be at position x,y in the montage.
			   * see motnage spaces for possible arguments*/
			   public Shape getSelectedSpace(double x, double y, int type) {
				 int i= makeAltered(type).getPanelIndex(x,y);
				 return getSelectedSpace(i, type);
			   }
			   
			   /**returns the shape that would be at index i in the montage. See MontageSpaces
			    * for type values that can be used as arguments*/
			   public Shape getSelectedSpace(int i, int type) {
				   BasicMontageLayout ml=makeAltered(type);
				   ml.setPanelRectangles();
				   
				   if (type==MONTAGE) {return ml.getDifferenceSpace(i, ALL_OF_THE+PANELS, ALL_MONTAGE_SPACE);}
				   if (type==BORDER) return ml.getDifferenceSpace(i, PANELS, PANELS_WITH_BORDER);
				   if (type==TOP_SPACE)  return ml.getTopSpace(i);
				   if (type==LEFT_SPACE)  return ml.getLeftSpace(i);
				   if (type==RIGHT_SPACE)  return ml.getRightSpace(i);
				   if (type==BOTTOM_SPACE) return ml.getBottomSpace(i);
				   if (type==EXTENDED_TOP_SPACE)  return ml.getTopSpace(i);
				   if (type==EXTENDED_LEFT_SPACE)  return ml.getLeftSpace(i);
				   if (type==EXTENDED_RIGHT_SPACE)  return ml.getRightSpace(i);
				   if (type==EXTENDED_BOTTOM_SPACE)  return ml.getBottomSpace(i);
				   if (type==NON_MONTAGE_SPACE)  return ml.getDifferenceSpace(i, ALL_MONTAGE_SPACE, ENTIRE_IMAGE); 
				   if (type==PANEL_WITH_TOP_SPACE) return ml.getSumSpace(i,  new int[] {PANELS, TOP_SPACE}, true);
				   if (type==PANEL_WITH_LEFT_SPACE)  return ml.getSumSpace(i,  new int[] {PANELS, LEFT_SPACE}, true);
				   if (type==PANEL_WITH_BOTTOM_SPACE)  return ml.getSumSpace(i,  new int[] {PANELS, BOTTOM_SPACE}, true);
				   if (type==PANEL_WITH_RIGHT_SPACE)  return ml.getSumSpace(i,  new int[] {PANELS, RIGHT_SPACE}, true);
				   if (type==PANEL_WITH_SPACES) return ml.getSumSpace(i,  new int[] {EXTENDED_TOP_SPACE, EXTENDED_BOTTOM_SPACE, EXTENDED_LEFT_SPACE, EXTENDED_RIGHT_SPACE}, true);
				   if (type==ALL_SPACES) return ml.getDifferenceSpace(i, PANELS, PANEL_WITH_SPACES);
				   if (type==TOP_3rd) return new Rectangle2D.Double(ml.panels[i-1].getX(), ml.panels[i-1].getY(), ml.panels[i-1].getWidth(), (ml.panels[i-1].getHeight()/3));
				   if (type==BOTTOM_3rd) return new Rectangle2D.Double(ml.panels[i-1].getX(), (ml.panels[i-1].getY()+(ml.panels[i-1].getHeight()*2/3)), ml.panels[i-1].getWidth(), (ml.panels[i-1].getHeight()/3));
				   if (type==LEFT_3rd) return new Rectangle2D.Double(ml.panels[i-1].getX(), ml.panels[i-1].getY(), ml.panels[i-1].getWidth()/3, (ml.panels[i-1].getHeight()));
				   if (type==RIGHT_3rd) return new Rectangle2D.Double(ml.panels[i-1].getX()+ ml.panels[i-1].getWidth()*2/3, ml.panels[i-1].getY(), ml.panels[i-1].getWidth()/3, (ml.panels[i-1].getHeight()));
				   
				   if (type==NON_MONTAGE_TOP) return new Rectangle2D.Double(0,0, ml.layoutWidth, ml.specialSpaceWidthTop); 
				   if (type==NON_MONTAGE_LEFT)  return new Rectangle2D.Double(0,0, ml.specialSpaceWidthLeft, ml.layoutHeight); 
				   if (type==NON_MONTAGE_BOT)  return new Rectangle2D.Double(0,ml.layoutHeight-ml.specialSpaceWidthBottom, ml.layoutWidth, ml.specialSpaceWidthBottom); 
				   if (type==NON_MONTAGE_RIGHT) return new Rectangle2D.Double(ml.layoutWidth-ml.specialSpaceWidthRight,0, ml.specialSpaceWidthRight,ml.layoutHeight); 
				   if (type==ENTIRE_IMAGE) return new Rectangle2D.Double(0,0, ml.layoutWidth, ml.layoutHeight);
				   
				   
				   if (type==ALL_BORDERS) return ml.getSelectedSpace(i, ALL_OF_THE+BORDER) ;
				   if (type==LABEL_ALLOTED_TOP)  return ml.getDifferenceSpace(i,  THIS_COLS+PANELS_WITH_BORDER, THIS_COLS+TOP_SPACE);
				   if (type==LABEL_ALLOTED_BOT)  return ml.getDifferenceSpace(i, THIS_COLS+PANELS_WITH_BORDER, THIS_COLS+BOTTOM_SPACE);
				   if (type==LABEL_ALLOTED_LEFT)  return ml.getDifferenceSpace(i,  THIS_ROWS+PANELS_WITH_BORDER, THIS_ROWS+LEFT_SPACE);
				   if (type==LABEL_ALLOTED_RIGHT) return ml.getDifferenceSpace(i,  THIS_ROWS+PANELS_WITH_BORDER, THIS_ROWS+RIGHT_SPACE);
				   if (type==HORIZONTAL_SPACES) return  ml.getSumSpace(i, new int[] {LEFT_SPACE, RIGHT_SPACE}, false);
				   if (type==VERTICAL_SPACES)  return ml.getSumSpace(i,  new int[] {TOP_SPACE, BOTTOM_SPACE}, false);
				   
				   if (type==HORIZONTAL_BORDER) return ml.getOverlapSpace(i, HORIZONTAL_SPACES, BORDER);
				   if (type==VERTICAL_BORDER)  return ml.getOverlapSpace(i, VERTICAL_SPACES, BORDER);
				   
				  int[] rowcol= ml.getGridCordAtIndex(i);
				  if (type>100) ml=ml.makeAltered(type%100);
				  if (type/100==ALL_OF_THE/100) {return ml.getAllPanelsSpace( type%ALL_OF_THE);}
				   if (type/100==THIS_COLS/100) {return ml.getAllPanelsInColSpace(rowcol[0], type%THIS_COLS);}
				   if (type/100==THIS_ROWS/100) {return ml.getAllPanelsInRowSpace(rowcol[1], type%THIS_ROWS);}
				   if (type/100==PAIR/100) { ml=ml.makeAltered(type%PAIR); return ml.getAllPanelsInIndexRangeSpace(i, i+1, type%PAIR);}
				   if (type/100==TRIAD/100) { ml=ml.makeAltered(type%TRIAD); return ml.getAllPanelsInIndexRangeSpace(i, i+2,  type%TRIAD);}
				   if (type/100==QUAD/100) { ml=ml.makeAltered(type%QUAD); return ml.getAllPanelsInIndexRangeSpace(i, i+3, type%QUAD);}
				   if (type/100==PENT/100) { ml=ml.makeAltered(type%PENT);return ml.getAllPanelsInIndexRangeSpace(i, i+4, type%QUAD);}
				   //return getAllPanelsInIndexRangeSpace(i, i+1, type);
				   ml.setPanelRectangles();
				   try{return ml.getPanel(i);} catch (Exception e) {return new Rectangle(0,0,0,0);}
				 //  return null;
			   }
			   
			  Shape getDifferenceSpace(int i, int type1, int type2) {
					 Area r1=new Area(getSelectedSpace(i, type1));
					 Area r2=new Area(getSelectedSpace(i, type2));
					 r2.subtract(r1);
					 return r2;
				}
			  
			  Shape getSumSpace(int i, int[] types, boolean bound) {
				  Area r1=new Area(getSelectedSpace(i, types[0]));
				  for (int j=1; j<types.length; j++)	r1.add(new Area(getSelectedSpace(i, types[j])));
				  if (bound) return r1.getBounds();
					 return r1;
				}
			  Shape getOverlapSpace(int i, int type1, int type2) {
					 Area r1=new Area(getSelectedSpace(i, type1));
					 Area r2=new Area(getSelectedSpace(i, type2));
					 r2.intersect(r1);
					 return r2;
				}
			  Shape getAllPanelsSpace(int type) {
				  Area r1=new Area(getSelectedSpace(1, type));
				  int panels=nRows()*nColumns();
				  if (panels==1) return r1;
				 
				  for (int i=2; i<=panels; i++) {
					  r1.add(new Area(getSelectedSpace(i, type)));
					  }
				  return r1;
			  }
			  
			  Shape getAllPanelsInRowSpace(int row, int type) {
				  int[] ind=getRowIndexes(row);
				  Area r1=new Area(getSelectedSpace(ind[0], type));
				  for (int i=1; i<ind.length; i++) {r1.add(new Area(getSelectedSpace(ind[i], type)));}
				  return r1;
			  }
			  Shape getAllPanelsInIndexRangeSpace(int start, int end, int type) {
				  Area r1=new Area(getSelectedSpace(start, type));
				  for (int i=start+1; i<=end; i++) {r1.add(new Area(getSelectedSpace(i, type)));}
				  return r1;
			  }
			  
			  Shape getAllPanelsInColSpace(int col, int type) {
				  int[] ind=getColIndexes(col);
				  Area r1=new Area(getSelectedSpace(ind[0], type));
				  for (int i=1; i<ind.length; i++) {r1.add(new Area(getSelectedSpace(ind[i], type)));}
				  return r1;
			  }
			  
				public Shape rangeRoi( int x, int y, int x2, int y2, int type) {
					if (x2<x) {int t=x; x=x2;x2=t;}
					if (y2<y) {int t=y; y=y2;y2=t;}
				     Area output=new Area(getSelectedSpace( x, y, type));
					 output.add(new Area(getSelectedSpace(  x2, y2, type)));
					 int[] ori=getPanelPosition(x, y);
					 int[] ori2=getPanelPosition(x2, y2);
			         
					   for(int i=ori[0]; i<=ori2[0]; i++) {
						   for(int j=ori[1]; j<=ori2[1];  j++) {	
							   BasicMontageLayout ml2 = duplicate();
							   ml2.setPoints(xshift, yshift);
							   int k=getIndexAtPosition(j,i)-1;//the index at the xy position
							   if (k>=ml2.pts.length)k=ml2.pts.length-1;//prevents any out of bounds exceptions
							   Point2D p=ml2.pts[ k];  	   
							   output.add(
									  new Area( getSelectedSpace(p.getX(),  p.getY(), type))
									   );
						   }
					   }
					   Area output2=output;
					   return output2;
					 
				}
				 
				/**returns the union of mutiple types of shapes of index i.
				  see montage spaces for possible arguments.*/
				public Area getComboSpace( int i, int... types) {	 
					 Area r1=new Area(getSelectedSpace( i, types[0]));
					 Area output=r1;
					 for (int j=1; j<types.length; j++) {
						 output.add(new Area(getSelectedSpace( i, types[j])));
						// output= output.or(r2);
					 }
					 return output;
				}
			  
				/**returns the total number of panels.*/
			  public int nPanels() {return nRows()*nColumns();}
			  
			  /**a string summary of the montage dimensions*/
			  String reportMontageDims() {
				  return "Layout has "+nColumns()+" columns and "+nRows()+" rows";
			  }
			@Override
			public int nRows() {
				// TODO Auto-generated method stub
				return rowMontage;
			}
			@Override
			public int nColumns() {
				// TODO Auto-generated method stub
				return colMontage;
			}
			public void setNRows(int rowMontage) {
				this.rowMontage = rowMontage;
			}
			public void setNColumns(int colMontage) {
				this.colMontage = colMontage;
			}
			
			public Rectangle2D getNearestPanel(Point2D p) {
				return getNearestPanel(p.getX(), p.getY());
			}
			
			public Rectangle2D getNearestPanel(double x, double y) {
				double shortest=Double.MAX_VALUE;
				Rectangle2D closest=null;
				for(int i=0; i<this.nPanels(); i++) {
					Rectangle2D p=this.getPanel(i+1);
					if (p.contains(x, y)) return p;
					double dist=(new Point2D.Double(p.getCenterX(), p.getCenterY())).distance(x, y);
				if (dist<shortest) {
					closest=p;
					shortest=dist;
				}
				}
				return closest;
			}
			
			public int getNearestPanelIndex(Point2D p) {
				return getNearestPanelIndex(p.getX(), p.getY());
			}
			
			public int getNearestPanelIndex(double x, double y) {
				double shortest=Double.MAX_VALUE;
				Integer closest=1;
				for(int i=0; i<this.nPanels(); i++) {
					Rectangle2D p=this.getPanel(i+1);
					if (p.contains(x, y)) return i+1;
					double dist=(new Point2D.Double(p.getCenterX(), p.getCenterY())).distance(x, y);
				if (dist<shortest) {
					closest=i+1;
					shortest=dist;
				}
				}
				return closest;
			}
			
			
			public ImageWrapper getWrapper() {
				return wrapper;
			}
			public void setWrapper(ImageWrapper wrapper) {
				this.wrapper = wrapper;
			}
			
			/**Searches the list of layout listeneras to determine if one
			   is the layout graphic for this item*/
			public PanelLayoutGraphic findHoldingObject() {
				//ArrayList<LocatedObject2D> obs = getWrapper().getLocatedObjects();
				for(GridLayoutEditListener o: getListeners()) {
					if (o instanceof PanelLayoutGraphic) {
						PanelLayoutGraphic p=(PanelLayoutGraphic) o;
						if (this==p.getPanelLayout())
							{
							return p;
							}
					}
				}
				return null;
			}
			
			/**does nothing but used save montage properties in some subclasses*/
			public void setMontageProperties() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void move(double x, double y) {
				
				this.setSpecialTopSpace( (y+specialSpaceWidthTop));
				this.setSpecialLeftSpace( (x+specialSpaceWidthLeft));
				this.resetPtsPanels();
			}
			@Override
			public Point2D getReferenceLocation() {
				return new Point2D.Double(specialSpaceWidthLeft,specialSpaceWidthTop);
			}
			@Override
			public Shape allPanelArea() {
				return this.getSelectedSpace(1, ALL_OF_THE+PANELS);
			}
			@Override
			public Shape getBoundry() {
				// TODO Auto-generated method stub
				return this.getSelectedSpace(1, ALL_MONTAGE_SPACE);
			}
			
			
			@Override
			public double getStandardPanelWidth() {
				// TODO Auto-generated method stub
				return panelWidth;
			}
			@Override
			public double getStandardPanelHeight() {
				// TODO Auto-generated method stub
				return panelHeight;
			}
			
			/**Called when the user drags a panel*/
			@Override
			public void nudgePanel(int panelnum, double dx, double dy) {
				int col=this.getColAtIndex(panelnum);
				int row=this.getRowAtIndex(panelnum);
				if (col>1) {
						dx/=(col-1);
						double newBorder = this.BorderWidthLeftRight+dx;
						if(newBorder<1) newBorder=1;
						
						this.setHorizontalBorder(newBorder);
				}
				
				if (row>1) {
							dy/=(row-1);	
							double newVBorder = BorderWidthBottomTop+dy;
							if(newVBorder<1) newVBorder=1;
							this.setVerticalBorder(newVBorder);
				}
				else  {
					//not nudged for first panel. something else is done
					
				}
			}
			
			public boolean doPanelsUseUniqueWidths() {
				for(int i=1; i<this.nPanels(); i++) {
					if(this.doesPanelUseUniqueWidth(i))
						return true;
				}
				return false;
			}
			
			public boolean doPanelsUseUniqueHeights() {
				for(int i=1; i<this.nPanels(); i++) {
					if(this.doesPanelUseUniqueHeight(i))
						return true;
				}
				return false;
			}
			
			@Override
			public boolean doesPanelUseUniqueWidth(int panel) {
				int col=this.getColAtIndex(panel);
				return this.columnHasIndividualWidth(col);
			//	return false;
			}
			@Override
			public boolean doesPanelUseUniqueHeight(int panel) {
				int row=this.getRowAtIndex(panel);
				return this.rowHasIndividualHeight(row);
			}
			@Override
			public void nudgePanelDimensions(int panelnum, double wi, double hi) {
				int col=this.getColAtIndex(panelnum);
				int row=this.getRowAtIndex(panelnum);
				
				double w;
				if (doesPanelUseUniqueWidth(panelnum)) w = this.getPanel(panelnum).getWidth()+wi;
				else w=this.getPanel(panelnum).getWidth()+wi/col;
				
				double h;

				if (doesPanelUseUniqueWidth(panelnum)) h	= this.getPanel(panelnum).getHeight()+hi;
				else h= this.getPanel(panelnum).getHeight()+hi/row;
				
				if (doesPanelUseUniqueWidth(panelnum))  setPanelWidth(panelnum, w);
				else {setStandardPanelWidth((w));}
				if (doesPanelUseUniqueHeight(panelnum)) setPanelHeight(panelnum, h);
				else {setStandardPanelHeight((h));}
				
			}
			
			public String report() {
				String out="Rows "+this.nRows()+'\n';
				out+="Cols "+this.nColumns()+'\n';
				return out;
			}
			@Override
			public GridLayoutEditListenerList getListeners() {
				if (listeners==null) listeners=new  GridLayoutEditListenerList();
				return listeners;
			}
			
	
			
			public GenericMontageEditor getEditor() { 
		    	return new GenericMontageEditor(); 
		    	};
			
			 
}
