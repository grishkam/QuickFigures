package gridLayout;

import genericMontageKit.PanelLayout;

/**A layout that is a grid. Positions are references with a number index as well as a row-columns number
   The methods below convert between the two*/
public interface GridLayout extends PanelLayout{
	public int getIndexAtPosition( int row,int column);
	public int getRowAtIndex(int i);
	public int getColAtIndex(int i);
	public int nRows();
	public int nColumns();
	
	public GridLayoutEditListenerList getListeners();
}