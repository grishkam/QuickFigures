package gridLayout;

import java.util.ArrayList;

public class GridIndexList extends ArrayList<GridPanelSwapListener> implements GridPanelSwapListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	/**called when ncols are inserted at position col in the grid. resets the indexes appropriately*/
	public void onColInsertion( GridLayout ml, int col, int ncols) {
		for(GridPanelSwapListener index:this) {index.onColInsertion( ml, col, ncols);}
	}
	public void onColRemoval(GridLayout ml, int col, int ncols) {
		for(GridPanelSwapListener index:this) {index.onColRemoval( ml,col, ncols);}
	}
	
	/**called when ncols are inserted at position col in the grid. resets the indexes appropriately*/
	public void onRowInsertion(GridLayout ml, int row, int nrows) {
		for(GridPanelSwapListener index:this) {index.onRowInsertion( ml, row, nrows);}
	}
	public void onRowRemoval(GridLayout ml, int row, int nrows) {
		for(GridPanelSwapListener index:this) {index.onRowRemoval(ml,row, nrows);}
	}
	
	//public void removeIndexesOfCol(int col) {}
	//public void removeIndexesOfRow(int row) {}
	@Override
	public void onSwapCol(GridLayout ml, int row1, int row2) {
		for(GridPanelSwapListener index:this) {index.onSwapCol(ml, row1, row2);;}
		
	}
	@Override
	public void onSwapRow(GridLayout ml, int row1, int row2) {
		for(GridPanelSwapListener index:this) {index.onSwapRow(ml, row1, row2);;}
		
	}
	@Override
	public void onSwapPanel(GridLayout ml, int row1, int row2) {
		for(GridPanelSwapListener index:this) {index.onSwapPanel(ml, row1, row2);}
	}
	
	@Override
	public void onBorderChange(GridLayout ml, int row1, int row2) {
		for(GridPanelSwapListener index:this) {index.onBorderChange(ml, row1, row2);}
		
	}
	
	/**returns the grid index object for the panel index*/
	public GridIndex getPanelIndex(int index) {
		for(GridPanelSwapListener g: this) {
			if (g instanceof GridIndex&&((GridIndex) g).getPanelindex()==index) return (GridIndex) g;
		}
		return null;
	}
	
	public boolean hasPanelIndex(int index) {
		return  getPanelIndex(index)!=null;
	}
	
	/**returns the first panel index that does not have a grid index object associated with it*/
	public int getFirstEmptyIndex() {
		int i=1;
		while(i<100000) {
			if (!hasPanelIndex(i)) return i;
			i++;
		}
		return -1;
	}
	
	
	
	
	}


