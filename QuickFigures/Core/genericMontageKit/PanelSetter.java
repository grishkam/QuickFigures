package genericMontageKit;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.GridLayout;
import gridLayout.RetrievableOption;
import logging.IssueLog;
import utilityClassesForObjects.LocatedObject2D;

/**A class that determines the places of panels within a layout and puts then there.
 * */
public class PanelSetter extends RectanglePlacements implements Serializable{
	
	 /**
	 * 
	 */
	/**the possible options for how this one inserts panels. only the default type is used  but the others
	  are still present in the event that I may use them to accommodate new features*/
	public static final int DEFAULT_INSERTION=0, EACH_IMAGE_TO_ROW=1,  EACH_IMAGE_TO_COLUMNS=2, AROUND_RECTANGLE=3;
	
	private static final long serialVersionUID = 1L;

	@RetrievableOption(key = "inserttionLayoutType", label="Insert Image Panel To", choices={"Flow Insertion", "Row Insertion", "Column Insertion", "Rectangle Insertion"})
	 public int insertiontype=DEFAULT_INSERTION;
	 
	 @RetrievableOption(key = "inserttionPlaceMentType", label="Placement", choices={"Top Left", "Bottom Left", "Top Right", "Bottom Right", "Top Center", "Bottom Center", "Left Center", "Right Center", "Center"})
	 public int insertionplacement=0;
	 
	 final int DO_NOT_RESIZE_LAYOUT_PANELS=0, RESIZE_LAYOUT_FIT_FIRST_PANEL=1;
	 
	 @RetrievableOption(key =  "editMontageToFitPanels", label="Edit Panel Dimensions", choices={"dont", "uniform panel dims"})
	 public int editMontageToFitPanels=RESIZE_LAYOUT_FIT_FIRST_PANEL;
	 
	 @RetrievableOption(key =  "StartIndex", label="Start Index")
	 public  int startPoint=1;//setter will put the panel in index 1
	

	 @RetrievableOption(key =  "autoMaxMode", label="Maximun for rows or cols", choices={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
	private  
	 int maxAutoGroup=10;


	public 	PanelListElement lastSetPanel;

	
	/**this sets up the locations in a montage to place each panel based on the settings*/
	public void mapPanelPlacements(GridLayout ml, PanelList list) {
		int start=startPoint;
		if (insertiontype==DEFAULT_INSERTION) {
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
		
		if (insertiontype==EACH_IMAGE_TO_COLUMNS) {
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
		
		if (insertiontype>=AROUND_RECTANGLE) {
			int col=1; 
			int row=start/insertiontype+1;
			
			String lastName=list.getImageName(0);
			for(PanelListElement p: list.getPanels()) {
				if (p==null) {IssueLog.log("cannot set up the location for a null panel entry", "problem in map panel placements in panel setter class"); continue;}
				
				/**moves over a column if the name does not equal the last one*/
				
					
					if(col>AROUND_RECTANGLE) {
						col=col-insertiontype;
					}
					row=start/insertiontype+1;
					col=start%insertiontype;
				p.getDisplayGridIndex().setRowCol(row, col, ml);
				
				start++;
			}
			
			
		}
		
		
	}
	

	
	/**resets the points on the layout then inserts a list of panels into them*/
	public void replacePanels(BasicMontageLayout ml,
			PanelList workingList) {
		replacePanels(ml, workingList,1);
	}
	/**resets the points on the layout then inserts a list of panels into them starting from the layout location at the given index*/
	public void replacePanels(BasicMontageLayout ml,
			PanelList workingLists, int index) {

	   	  ml.resetPtsPanels();
	  	  startPoint=index;
	  	
	  	 mapPanelPlacements(ml, workingLists);
	  	editLayoutToFitPanels(ml, workingLists, editMontageToFitPanels);
	  
	 	ml.setMontageProperties();
	}
	
	/**edits the layout so that it fits the given panel list*/
	public void editLayoutToFitPanels(BasicMontageLayout ml,
			PanelList workingStack, int editMontageToFitPanels) {
			GenericMontageEditor me =new GenericMontageEditor();
			
			/**resizes the panels to fit if it is in flow insertion mode*/
		if (insertiontype==DEFAULT_INSERTION&&(ml.getPanelHeightOfRow(0)!= workingStack.getHeight()|| ml.getPanelWidthOfColumn(0)!= workingStack.getWidth()) &&  RESIZE_LAYOUT_FIT_FIRST_PANEL==editMontageToFitPanels){
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
				
					}
				
			}
			if (isColInsertion()) {
				int col=p.getDisplayGridIndex().getColindex();
				ml.rowmajor=false;
				if (ml.getPanelWidthOfColumn(col)!=p.getWidth()) {
					
					me.augmentPanelWidthOfCol(ml, p.getWidth()-ml.getPanelWidthOfColumn(col), col);
					}
			}
			
			/**resizes the panels to fit if it is in flow insertion mode*/
			if (insertiontype>=AROUND_RECTANGLE&&(ml.getPanelHeightOfRow(0)!= workingStack.getHeight()|| ml.getPanelWidthOfColumn(0)!= workingStack.getWidth()) &&  RESIZE_LAYOUT_FIT_FIRST_PANEL==editMontageToFitPanels){
				me.resizePanels(ml,   workingStack.getWidth(),  workingStack.getHeight());
			}
			
			
		}
		
	}
	
	/**when given a panel list element with a grid index (row r, column c) mapped,
	   adds rows and columns to make sure the layout contains a place with the given index*/
	void addRowColToFit(BasicMontageLayout ml, PanelListElement p) {
		GenericMontageEditor editor = new GenericMontageEditor();
		if (p.getDisplayGridIndex().getColindex()>ml.nColumns()) {editor.setColNumber(ml, p.getDisplayGridIndex().getColindex());
		}
		if (p.getDisplayGridIndex().getRowindex()>ml.nRows()){editor.setRowNumber(ml, p.getDisplayGridIndex().getRowindex());
	
		}
	}
	
	public boolean isRowInsertion() {return insertiontype==EACH_IMAGE_TO_ROW;}
	public boolean isColInsertion() {return insertiontype==EACH_IMAGE_TO_COLUMNS;}
	
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
			editLayoutToFitPanels(layout, thestack, resizePanels? RESIZE_LAYOUT_FIT_FIRST_PANEL: DO_NOT_RESIZE_LAYOUT_PANELS);
		
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
