package genericMontageLayoutToolKit;
import externalToolBar.IconSet;
import genericMontageKit.*;
import gridLayout.MontageSpaces;
import logging.IssueLog;
import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;

import java.awt.Dimension;
import java.awt.Image;

public class PannelGrabberTool extends GeneralLayoutEditorTool implements 
		MontageSpaces{
	
	public int mode=1;
	{this.removalPermissive=true;}
	
	public PannelGrabberTool(int mode) {
		this.mode=mode;
	}

	
	public void mouseMoved() {
		super.mouseMoved();
		setCursorBasedOnExcerpt();
	} 
	public void  performPressEdit()  {
		
		boolean shift=!shiftDown();
		
		try{
			
		if (mode==1) {
			int index= getCurrentLayout().makeAltered(COLS).getPanelIndex(getClickedCordinateX(), getClickedCordinateY());

			if (!shift)  getEditor().lastCol= getEditor().removeColumn(getCurrentLayout(), index);
			else {		 getEditor().addColumn(getCurrentLayout(), index,  getEditor().lastCol);
						 getEditor().lastCol=null;
						}

		}
		
		if (mode==2) {
			int index= getCurrentLayout().makeAltered(ROWS).getPanelIndex(getClickedCordinateX(), getClickedCordinateY());
		if (!shift)  getEditor().lastRow= getEditor().removeRow(getCurrentLayout(), index);
		else {		 getEditor().addRow(getCurrentLayout(), index,  getEditor().lastRow);
					 getEditor().lastRow=null;
					}
		}
		if (mode==0) {
			 getEditor().deleteInsertPanel(getCurrentLayout(), getCurrentLayout().getPanelIndex(getClickedCordinateX(), getClickedCordinateY()), shift, PANELS); 
		}
		
		
		
		setCursorBasedOnExcerpt();
	//	setMarkerRoi( ALL_OF_THE+mode);
	//	getImageWrapperClick().updateImageDisplay();
		
		} catch (Exception ex) {IssueLog.log("Exception occured when trying to edit monatage ", ex);}
		
		
	}
	@Override
	public int markerType() {
		return mode;
	}
	
	
	public void setCursorBasedOnExcerpt() {
	
		if (getLast()==null){ 
			//getToolCore().setGenericCursor() ;
			
			return;}
		Image last = getLast().getFittedImage(new Dimension(60,60));
	
		setCursorIcon(last);
		

	}
	
	
	
	public panelContentElement getLast() {
		if (mode==1) return  getEditor().lastCol;
		if (mode==2) return  getEditor().lastRow;
		if (mode==0) return getEditor().lastPanel;
		return null;
	}
	
	private String getTextBase() {
		if (mode==1) return  "Column";
		if (mode==2) return  "Row";
		if (mode==0) return "Panel";
		return "";
	}
	
	//public 
	
	/**
	public void setCurrentCursorTolastClicked() {
		setCursorIcon(Toolkit.getDefaultToolkit().createCustomCursor(getCursorIcon(), new Point(0,0), "excerpt"), 0);
		//ImageCanvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon, new Point(0,0), "excerpt") , 0);
	}
	*/
	public void showOptionsDialog() {
		StandardDialog gd=new StandardDialog(getClass().getName().replace("_", " "), true);
		
		String[] option2=new String[] {"Panel Adder (shift to cut)", "Column Adder (shift to remove)", "Row Adder (shift to remove)"};
		gd.add("Adjust", new ComboBoxPanel("Adjust ", option2, mode));
		gd.showDialog();
		
		if (gd.wasOKed()) {	
			mode=gd.getChoiceIndex("Adjust");
				}
	}
	
	static IconSet set1=new IconSet("icons/PannelGrabber.jpg","icons/PannelGrabberPressed.jpg","icons/PannelGrabberRollOver.jpg");
	static IconSet set2=new IconSet("icons/PannelGrabber2.jpg","icons/PannelGrabberPressed2.jpg","icons/PannelGrabber2RollOver.jpg");
	static IconSet set3=new IconSet("icons/PannelGrabber3.jpg","icons/PannelGrabberPressed3.jpg","icons/PannelGrabber3RollOver.jpg");
	
	{createIconSet("icons/PannelGrabber.jpg","icons/PannelGrabberPressed.jpg","icons/PannelGrabberRollOver.jpg",
			"icons/PannelGrabber2.jpg","icons/PannelGrabberPressed2.jpg","icons/PannelGrabber2RollOver.jpg",
			"icons/PannelGrabber3.jpg","icons/PannelGrabberPressed3.jpg","icons/PannelGrabber3RollOver.jpg");}

	@Override
	public IconSet getIconSet() {
		if (mode==1) return set1;
		if (mode==0) return set3;
		if (mode==2) return set2;
		return set1;
	}
	
	public String getToolName() {return "Panel Grabber";}

	
	@Override
	public String getToolTip() {
			
			return "Take "+ getTextBase()+" in and out (hold shift)";
		}


	

}
