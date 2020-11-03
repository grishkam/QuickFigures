package gridLayout;

import java.io.Serializable;

public class GridIndex implements Serializable, GridPanelSwapListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int panelindex=1;
	private int rowindex=1;
	private int colindex=1;
	
	public GridIndex() {}
	
	
	public void upDateRowColFromPanel(GridLayout ml) {
		setRowindex(ml.getRowAtIndex(panelindex));
		setColindex(ml.getColAtIndex(panelindex));
	}
	public void upDatePanelFromRowCol(GridLayout ml) {
		panelindex=ml.getIndexAtPosition( getRowindex(), getColindex());
	}
	
	public void setRowCol(int row, int col) {
		setRowindex(row);
		setColindex(col);
	}
	
	public void setRowCol(int row, int col, GridLayout ml) {
		setRowindex(row);
		setColindex(col);
		upDatePanelFromRowCol(ml);
	}
	public int getPanelindex() {
		return panelindex;
	}
	public void setPanelindex(int panelindex) {
		this.panelindex = panelindex;
	}
	public int getRowindex() {
		return rowindex;
	}
	public void setRowindex(int rowindex) {
		this.rowindex = rowindex;
	}
	public int getColindex() {
		return colindex;
	}
	public void setColindex(int colindex) {
		this.colindex = colindex;
	}
	
	
	
	/**to be called as an update when the column is moved forward*/
	public void onColLocationReset(GridLayout ml, int newcolindex) {
		colindex=newcolindex;
		upDatePanelFromRowCol(ml);
	}
	
	/**to be called as an update when the column is moved forward*/
	public void onRowLocationReset(GridLayout ml, int newcolindex) {
		rowindex=newcolindex;
		upDatePanelFromRowCol(ml);
	}
	
	/**to be called as an update when the column is moved forward*/
	public void onPanelLocationReset(GridLayout ml, int newindex) {
		panelindex=newindex;
		upDateRowColFromPanel(ml) ;
	}
	
	/**called when ncols are inserted at position col in the grid. resets the indexes appropriately*/
	public void onColInsertion(GridLayout ml, int col, int ncols) {
		if(this.getColindex()>col) this. onColLocationReset(ml,this.getColindex()+ncols);
	}
	public void onColRemoval( GridLayout ml, int col, int ncols) {
		if(this.getColindex()>col)  this. onColLocationReset(ml,this.getColindex()-ncols);
	}
	
	/**called when ncols are inserted at position col in the grid. resets the indexes appropriately*/
	public void onRowInsertion( GridLayout ml, int row, int nrows) {
		if(this.getRowindex()>row) this. onRowLocationReset(ml,this.getRowindex()+nrows);
	}
	public void onRowRemoval(GridLayout ml, int row, int nrows) {
		if(this.getRowindex()>row)  this. onRowLocationReset(ml,this.getRowindex()-nrows);
	}
	
	public void dot() {  
	
		
	}
	
	@Override
	public void onSwapCol(GridLayout ml, int row1, int row2) {
		if (this.getColindex()==row1) {
			this.onColLocationReset(ml, row2);
		} else
			if (this.getColindex()==row2) {
				this.onColLocationReset(ml, row1);
			}
		
	}
	@Override
	public void onSwapRow(GridLayout ml, int row1, int row2) {
		if (this.getRowindex()==row1) {
			this.onRowLocationReset(ml, row2);
		} else
			if (this.getRowindex()==row2) {
				this.onRowLocationReset(ml, row1);
			}
	}
	
	@Override
	public void onSwapPanel(GridLayout ml, int index1, int index2) {
		if (this.getPanelindex()==index1) {
			this.onPanelLocationReset(ml, index2);
		} else
			if (this.getPanelindex()==index2) {
				this.onPanelLocationReset(ml, index1);
			}
	}


	@Override
	public void onBorderChange(GridLayout ml, int row1, int row2) {
		// TODO Auto-generated method stub
		
	}
	
	
}
