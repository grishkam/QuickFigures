package plasticPanels;

import java.awt.Rectangle;

public class PlasticPanel extends Rectangle {

	public PlasticPanel(int i, int j, double standardPanelWidth,
			double standardPanelHeight) {
		super(i,j, (int)standardPanelWidth, (int)standardPanelHeight);
	}
	
	 public PlasticPanel(Rectangle r) {
		super(r);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	


}
