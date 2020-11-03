package standardDialog;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class GriddedPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gx=1;
	private int gy=1;
	private int gxmax=1;
	private int gymax=1;
	boolean moveDown=true;
	
	public GriddedPanel() {
		super();
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
	}
	

	public void place(OnGridLayout st) {
	
		st.placeItems(this, gx, gy);
		if (gxmax<st.gridWidth())gxmax=st.gridWidth();
		
		if (moveDown)gy+=st.gridHeight();
				else gx+=st.gridWidth();
		
		
	}
	
	public void moveGrid(int x, int y) {
		gy+=y;
		gx+=x;
		if (gx>gxmax) gxmax=gx;
		if (gy>gymax) gymax=gy;
	}

}
