package gridLayout;

/**Listens for a panel swap event*/
public interface GridPanelSwapListener {
	public void onSwapCol(GridLayout ml, int row1, int row2);
	public void onSwapRow(GridLayout ml, int row1, int row2);
	
	public void onColInsertion(GridLayout ml, int col, int ncols);
	public void onColRemoval(GridLayout ml, int col, int ncols);
	public void onRowInsertion( GridLayout ml, int row, int nrows);
	public void onRowRemoval(GridLayout ml, int row1, int row2);
	
	public void onBorderChange(GridLayout ml, int row1, int row2) ;
	
	void onSwapPanel(GridLayout ml, int row1, int row2);
	
}
