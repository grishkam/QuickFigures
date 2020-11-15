package dividedPanels;

import java.awt.Color;
import java.awt.Point;

import dividedPanels.DividedPanelLayout.layoutDividedArea;
import graphicalObjectHandles.SmartHandle;

public class LowerRightHandleForDividedPanelLayout extends SmartHandle {
	private layoutDividedArea area;
	private DividedPanelLayout layout;
	private int hnum;

	public LowerRightHandleForDividedPanelLayout(DividedPanelLayout dpl, layoutDividedArea area, int num) {
		
		super(0, 0);
		hnum = num;
		super.setHandleNumber(num+40000);
		this.area = area;
		this.setHandleColor(Color.blue);
		this.setCordinateLocation(new Point((int)area.getMaxX()-12,  (int)area.getMaxY()-12));
		this.layout=dpl;
	}
	
	@Override
	public void nudgeHandle(double dx, double dy) {

		layout.nudgePanelDimensions(hnum, dx, dy);

	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
