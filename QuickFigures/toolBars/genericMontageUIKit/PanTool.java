package genericMontageUIKit;

import java.awt.Cursor;

import externalToolBar.DragAndDropHandler;

public class PanTool extends BasicToolBit {
	
	{this.normalCursor=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);}
	{createIconSet("icons3/HandToolIcon.jpg","icons3/HandToolIconPressed.jpg","icons3/HandToolIcon.jpg");};
	double dsx=0;
	double dsy=0;
	
	
	
	@Override
	public void mousePressed() {
		dsx=0;
		dsy=0;
	}
	
	@Override
	public void mouseDragged() {
		//dsx+=this.getDragCordinateX()-getClickedCordinateX();
	//	dsy+=this.getDragCordinateY()-this.getClickedCordinateY();
		
		//IssueLog.log("ppints "+dsx+"   ,  "+dsy);
		
		this.getImageDisplayWrapperClick().setCursor(normalCursor);
		
		this.getImageDisplayWrapperClick().scrollPane((int) -getXDisplaceMent(), (int) -getYDisplaceMent());
		//getImageDisplayWrapperClick().setScrollCenter(this.getClickedCordinateX()+dsx, 0);
		//getImageDisplayWrapperClick().updateDisplay();
		//super.setClickPointToDragReleasePoint();
		
	}
	
	@Override
	public void mouseEntered() {
		this.getImageDisplayWrapperClick().setCursor(normalCursor);
		
		
	}
	
	public DragAndDropHandler getDragAndDropHandler() {
		return new MoverDragHandler(this);
	}

}
