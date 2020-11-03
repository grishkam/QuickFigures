package genericMontageKit;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import applicationAdapters.ImageWrapper;
import applicationAdapters.PixelWrapper;
import fieldReaderWritter.RetrievableOption;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.GridLayout;
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
	 
	 @RetrievableOption(key =  "panelPasteMode", label="Paste Panel As", choices={"Pixels", "Image Overlay"})
	 public 
	 int type=0;
	 
	 @RetrievableOption(key =  "autoMaxMode", label="Maximun for rows or cols", choices={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})
	 public 
	 int maxAutoGroup=10;


	 
	
	String key="";
	
	public 	PanelListElement lastSetPanel;
	
	transient private GenericMontageEditor me=new GenericMontageEditor();

	
	/**pastes the image. If this is done by altering the pixels of the data then
	  it returns null. If an object is created to display the pasted image, then this returns
	  that object. */
	public Object paste(ImageWrapper imp2, PixelWrapper ipw, int x, int y) {
		//ImageWrapper imp2 = new ImagePlusWrapper(imp);
		//PixelWrapper<ImageProcessor> ipw=new ProcessorWrapper(image);
		if (type==0)me.paste(imp2, ipw, x, y);
		if (type==1) {
			String name=lastSetPanel.getChannelEntry(0).getLabel();
			LocatedObject2D i1 = imp2.getDefaultObjectCreator().createImageObject(name, ipw, x, y);
			imp2.addRoiToImageBack(i1);
			return i1;
		}
		return null;
	}
	
	public void pasteIntoPixels(PixelWrapper recipient, PixelWrapper image) {
		
	}
	
	
/**clear and paste may be overwritten by subclasses*/
	
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
			new BasicOverlayHandler().clearAreaOfDrawnGraphicType(impw, r, new DrawnGraphicPicker(DrawnGraphic.ImagePanel));
		}
		
	}
	
	/**
	public void showDialog() {
	
		
		GenericDialog gd = new GenericDialog("Panel Insertion Options") ;
		IJdialogUse.createDialogItemsFromAnnotatedFields(gd, this, key);
		//if (dl!=null) gd.addDialogListener(dl);
		gd.showDialog();
		
		if (gd.wasOKed()) {
			setToDialog(gd);
		}
		
	}
	*/
	
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
				//p.displayGridIndex.colindex=start;
				//p.displayRowIndex=row;
				//p.displayPanelIndex=ml.getIndexAtPosition( p.displayRowIndex, p.displayColIndex);
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

	private void insertStackToPanels(BasicMontageLayout ml, PanelList abstractPanelList,
			boolean b, int start) {
		setStack(abstractPanelList, ml, start );
	}
	
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
	  	setStack2( workingStack,ml,startPoint);
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
	//	IssueLog.log2("adding columns for panel");
		}
		if (p.getDisplayGridIndex().getRowindex()>ml.nRows()){editor.setRowNumber(ml, p.getDisplayGridIndex().getRowindex());
		//IssueLog.log2("adding rows for panel");
		}
	}
	
	public boolean isRowInsertion() {return insertiontype==rowSourceInsertion;}
	public boolean isColInsertion() {return insertiontype==columnSourceInsertion;}
	
	public void setInsertionType(int i) {insertiontype=i;}

	/**without updating the points of the montage layout this pastes a set of images into them */
	public void setStack(PanelList stack, BasicMontageLayout ml, int i) {
		for (int j=0; j<stack.getPanels().size()&&i+j<=ml.nPanels(); j++) {setPanel(ml.getWrapper(), stack.getPanels().get(j),  ml.getPanel(i+j));}
	}
	
	/**without updating the points of the montage layout this pastes a set of images into them */
	public void setStack2(PanelList stack, BasicMontageLayout ml, int i) {
		for (int j=0; j<stack.getPanels().size()&&i+j<=ml.nPanels(); j++) {setPanel(ml.getWrapper(), stack.getPanels().get(j),  ml.getPanel(stack.getPanels().get(j).getDisplayGridIndex().getPanelindex()));}
	}
	


	
	public void setPanel(ImageWrapper impw, PanelListElement image, Rectangle2D panel) {	
		//ImageWrapper impw =createWrapper(imp);
		
		removePanelImage(impw, image, panel);
		lastSetPanel=image;
		Point bounds = putRelativeToCorner(new Rectangle(0,0,image.getWidth(), image.getHeight()), panel,insertionplacement, 0,0 );
		
		if (stretchToFit==1) {image.fit(panel.getWidth(), panel.getHeight());}//this one should no longer be used
		
		Object o=paste(impw, image.getImageWrapped(), bounds.x, bounds.y);
		if (image.getImageDisplayObject() instanceof LocatedObject2D) {
			LocatedObject2D loc=(LocatedObject2D) image.getImageDisplayObject() ;
			loc.setLocationUpperLeft(bounds.x, bounds.y);
		}
		image.setImageDisplayObject(o);
	}
	
	public void removePanelImages(PanelList panels, BasicMontageLayout layout) {
		if (panels==null||layout.getWrapper()==null) return;
		for(PanelListElement panel: panels.getPanels()) try{clear(layout.getWrapper(), layout.getPanel(panel.getDisplayGridIndex().getPanelindex()));} catch (Throwable t) {IssueLog.log(t);}
	}
	
	public void removePanelImage(ImageWrapper imp, PanelListElement image, Rectangle2D panel) {
		clear(imp, panel);
	}
	

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

	public int getInsertionType() {
		return insertiontype;
	}
}
