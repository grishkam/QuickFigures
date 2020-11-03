package standardDialog;

import java.awt.Container;
import java.awt.Insets;

public interface OnGridLayout {

	
	public void placeItems(Container jp, int x0, int y0) ;
	public int gridHeight();
	public int gridWidth();
	public static Insets lastInsets=new Insets(2,2,2,10);
	public static Insets firstInsets=new Insets(2,10,2,2);
	public static Insets middleInsets=new Insets(2,2,2,2);
	
}
