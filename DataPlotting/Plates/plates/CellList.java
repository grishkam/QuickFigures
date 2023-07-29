/**
 * Author: Greg Mazo
 * Date Modified: Jul 19, 2023
 * Copyright (C) 2023 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package plates;

import java.util.ArrayList;
import java.util.Collection;

/**
 
 * 
 */
public class CellList extends ArrayList<PlateCell>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CellList(Collection<? extends PlateCell> cells) {
		this.addAll(cells);
	}

	public boolean hasMatching(PlateCell cell) {
		if(this.findMatching(cell)!=null)
			return true;
		return false;
	}
	
	/**if there is a cell that matches this one in the list*/
	public PlateCell findMatching(PlateCell cell) {
		for(PlateCell a:this) {
			if(a.getAddress().matches(cell.getAddress()))
				return a;
		}
		
		return null;
	}
}
