package gridLayout;

public interface GridLayoutEditListener {

	public void editWillOccur(GridLayoutEditEvent e) ;
	
	public void editOccuring(GridLayoutEditEvent e) ;
	
	public void editDone(GridLayoutEditEvent e);
	
	
}
