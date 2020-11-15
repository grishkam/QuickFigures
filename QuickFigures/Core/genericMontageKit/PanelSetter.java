package genericMontageKit;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import applicationAdapters.ImageWrapper;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.GridLayout;
import gridLayout.RetrievableOption;
import logging.IssueLog;
import utilityClassesForObjects.DrawnGraphic;
import utilityClassesForObjects.DrawnGraphicPicker;
import utilityClassesForObjects.LocatedObject2D;

/**A class that determines the places of panels within a layout and puts then there*/
public class PanelSetter extends RectanglePlacements implements Serializable{
	
	 /**
	 * 
	 */
	public static final int flowInsertion=0, rowSourceInsertion=1,  columnSourceInsertion=2, rectangularInsertion=3;
	
	private static final long serialVersionUID = 1L;

	@RetrievableOption(key = "inserttionLayoutType", label="Insert Image Panel To", choices={"Flow Insertion", "Row Insertion", "Column Insertion", "Rectangle Insertion"})
	 public int insertiontype=0;
	 
	 @RetrievableOption(key = "inserttionPlaceMentType", label="Placement", choices={"Top Left", "Bottom Left", "Top Right", "Bottom Right", "Top Center", "Bottom Center", "Left Center", "Right Center", "Center"})
	 public int insertionplacement=0;
	 
	 final int noEdit=0, firstPanelUniformSize=1;
	 
	 @RetrievableOption(key =  "editMontageToFitPanels", label="Edit Panel Dimensions", choices={"dont", "uniform panel dims"})
	 public  int editMontageToFitPanels=1;
	 
	 @RetrievableOption(key =  "StartIndex", label="Start Index")
	 public  int startPoint=1;
	
	 @RetrievableOption(key =  "stretchToFit", label="Stretch to Fit", choices={"dont", "stretch"})
	 public  int stretchToFit=0;//no longer used
	 
	 @RetrievableOption(key =  "panelPasteMode", label="Paste Panel As", choices={"Pixels", "Image Object"})
	 public 
	 int type=0;
	 
	 @RetrievableOption(key =  "autoMaxMode", label="Maximun for rows or cols", choices={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
	 public 
	 int maxAutoGroup=10;


	 
	
	String key="";
	
	public 	PanelListElement lastSetPanel;

	/**Clears a rectangular area of the image. This can occur by filling the pixels with white or 
	  by deleting the display object
	  */
	public
	void clear(ImageWrapper impw, Rectangle2D r) {
		//ImagePlusWrapper impw = new ImagePlusWrapper(imp);
		if (type==0){
			impw.getPixelWrapper().fill(r, Color.white);
		}
		if (type==1) {
			new BasicObjectListHandler().clearAreaOfDrawnGraphicType(impw, r, new DrawnGraphicPicker(DrawnGraphic.ImagePanel));
		}
		
	}
	

	
	/**this sets up the locations in a montage to place each panel based on the settings*/
	public void mapPanelPlacements(GridLayout ml, PanelList list) {
		int start=startPoint;
		if (insertiontype==flowInsertion) {
			for(PanelListElement p: list.getPanels()) {
				if (p==null) {IssueLog.log("cannot set up the location for a null panel entry", "problem in map panel placements in panel setter class"); continue;}
				
				p.getDisplayGridIndex().setPanelindex(start);
				//p.displayPanelIndex=start;
				p.getDisplayGridIndex().setColindex(ml.getColAtIndex(start));
				p.getDisplayGridIndex().setRowindex(ml.getRowAtIndex(start));
				start++;
			}
			
			
		}
		
		
		if (isRowInsertion()) {
			/**if the panel is from a different source stack, this will insert it in a different location*/
			int row=1; 
			
			
			String lastName=list.getImageName(0);
			for(PanelListElement p: list.getPanels()) {
				if (p==null) {IssueLog.log("cannot set up the location for a null panel entry", "problem in map panel placements in panel setter class"); continue;}
				
				/**moves down a row if the orignal image name is different*/
				if(!p.originalImageName.equals(lastName)||start>maxAutoGroup) {
					start=startPoint;
					row++;
					lastName=p.originalImageName;
					IssueLog.log("moving down one row");
				}
				
				
				p.getDisplayGridIndex().setRowCol(row, start, ml);
				start++;
			}
			
			
		}
		
		if (insertiontype==columnSourceInsertion) {
			int col=1; 
			
			
			String lastName=list.getImageName(0);
			for(PanelListElement p: list.getPanels()) {
				if (p==null) {IssueLog.log("cannot set up the location for a null panel entry", "problem in map panel placements in panel setter class"); continue;}
				
				/**moves over a column if the name does not equal the last one*/
				if(!p.originalImageName.equals(lastName)||start>maxAutoGroup) {
					start=startPoint;
					col++;
					lastName=p.originalImageName;
				}
				
				p.getDisplayGridIndex().setRowCol(start, col, ml);
				start++;
				
			}
			
			
		}
		
		if (insertiontype>=rectangularInsertion) {
			int col=1; 
			int row=start/insertiontype+1;
			
			String lastName=list.getImageName(0);
			for(PanelListElement p: list.getPanels()) {
				if (p==null) {IssueLog.log("cannot set up the location for a null panel entry", "problem in map panel placements in panel setter class"); continue;}
				
				/**moves over a column if the name does not equal the last one*/
				
					
					if(col>rectangularInsertion) {
						col=col-insertiontype;
					}
					row=start/insertiontype+1;
					col=start%insertiontype;
				p.getDisplayGridIndex().setRowCol(row, col, ml);
				
				start++;
			}
			
			
		}
		
		
	}
	
	//public void setToDialog(GenericDialog gd) {IJdialogUse.setAnnotatedFields(gd, this, key);}
/**
	private void insertStackToPanels(BasicMontageLayout ml, PanelList abstractPanelList,
			boolean b, int start) {
		setStack(abstractPanelList, ml, start );
	}*/
	
	/**resets the points on the montage layout then inserts a stack into them*/
	public void replacePanels(BasicMontageLayout ml,
			PanelList workingStack) {
		replacePanels(ml, workingStack,1);
	}
	public void replacePanels(BasicMontageLayout ml,
			PanelList workingStack, int index) {

	   	  ml.resetPtsPanels();
	  	  startPoint=index;
	  	
	  	 mapPanelPlacements(ml, workingStack);
	  	editMontageToFitPanels(ml, workingStack, editMontageToFitPanels);
	  
	 	ml.setMontageProperties();
	}
	
	
	public void editMontageToFitPanels(BasicMontageLayout ml,
			PanelList workingStack, int editMontageToFitPanels) {
			GenericMontageEditor me =new GenericMontageEditor();
			
			/**resizes the panels to fit if it is in flow insertion mode*/
		if (insertiontype==flowInsertion&&(ml.getPanelHeightOfRow(0)!= workingStack.getHeight()|| ml.getPanelWidthOfColumn(0)!= workingStack.getWidth()) &&  firstPanelUniformSize==editMontageToFitPanels){
			me.resizePanels(ml,   workingStack.getWidth(),  workingStack.getHeight());
		}
		
		
		
		for(PanelListElement p: workingStack.getPanels()) { 
			//if (firstPanelUniformSize!=editMontageToFitPanels) break;
			addRowColToFit(ml,p);
			
			if (isRowInsertion()) {
				int row=p.getDisplayGridIndex().getRowindex();
				ml.rowmajor=true;
				if (ml.getPanelHeightOfRow(row)!=p.getHeight()) {
				
					me.augmentPanelHeightOfRow(ml, p.getHeight()-ml.getPanelHeightOfRow(row), row);
				
					//IssueLog.log2("Altering heght of row "+row+" to "+p.getHeight()+" from "+ml.getPanelHeightOfRow(row));
					}
				
			}
			if (isColInsertion()) {
				int col=p.getDisplayGridIndex().getColindex();
				ml.rowmajor=false;
				//addRowColToFit(ml,p);
				if (ml.getPanelWidthOfColumn(col)!=p.getWidth()) {
					
					me.augmentPanelWidthOfCol(ml, p.getWidth()-ml.getPanelWidthOfColumn(col), col);
					}
			}
			
			/**resizes the panels to fit if it is in flow insertion mode*/
			if (insertiontype>=rectangularInsertion&&(ml.getPanelHeightOfRow(0)!= workingStack.getHeight()|| ml.getPanelWidthOfColumn(0)!= workingStack.getWidth()) &&  firstPanelUniformSize==editMontageToFitPanels){
				me.resizePanels(ml,   workingStack.getWidth(),  workingStack.getHeight());
			}
			
			
		}
		
	}
	
	void addRowColToFit(BasicMontageLayout ml, PanelListElement p) {
		GenericMontageEditor editor = new GenericMontageEditor();
		if (p.getDisplayGridIndex().getColindex()>ml.nColumns()) {editor.setColNumber(ml, p.getDisplayGridIndex().getColindex());
		}
		if (p.getDisplayGridIndex().getRowindex()>ml.nRows()){editor.setRowNumber(ml, p.getDisplayGridIndex().getRowindex());
	
		}
	}
	
	public boolean isRowInsertion() {return insertiontype==rowSourceInsertion;}
	public boolean isColInsertion() {return insertiontype==columnSourceInsertion;}
	
	public void setInsertionType(int i) {insertiontype=i;}
	
	/**Takes a panel list with located object display panels. Subsequently places each of the said objects 
	 into the layout*/
	public void layDisplayPanelsOfStackOnLayout(PanelList thestack, BasicMontageLayout layout, boolean resizeLayoutIfNeeded) {
		layDisplayPanelsOfStackOnLayout(thestack, layout, resizeLayoutIfNeeded, resizeLayoutIfNeeded);
	}
	
	/**Takes a panel list with located object display panels. Subsequently places each of the said objects 
	 into the layout*/
	public void layDisplayPanelsOfStackOnLayout(PanelList thestack, BasicMontageLayout layout, boolean resizeLayoutIfNeeded, boolean resizePanels) {
		mapPanelPlacements(layout, thestack);
	
		if (resizeLayoutIfNeeded)
			editMontageToFitPanels(layout, thestack, resizePanels? firstPanelUniformSize: noEdit);
		
		putPanelDisplayObjectsIntoGrid(thestack, layout);
	}
	
	/**Used to place the display objects. */
	public void putDisplayObjectForPanelInRect(PanelListElement p, Rectangle2D pan) {
		LocatedObject2D l=getDisplayObjectForPanel(p);
		if (l!=null&&pan!=null)l.setLocationUpperLeft(pan.getX(), pan.getY());
	}
	
	/**returns the image  used by panel p as an instance of LocatedObject2D*/
	private static LocatedObject2D getDisplayObjectForPanel(PanelListElement p) {
		if (p.getImageDisplayObject() instanceof LocatedObject2D ) {
			LocatedObject2D l=(LocatedObject2D) p.getImageDisplayObject();
			return l;
		}
		return null;
	}

	/**This relocates the panel display objects to locations within the gridlayout*/
	public void putPanelDisplayObjectsIntoGrid(PanelList currentstack, GridLayout layout) {
		for( PanelListElement p: currentstack.getPanels()) {
			int npanel = p.getDisplayGridIndex().getPanelindex();
			Rectangle2D pan = layout.getPanel(npanel );
		
			putDisplayObjectForPanelInRect(p,pan);
			
		}
	}

}
