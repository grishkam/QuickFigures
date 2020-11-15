package gridLayout;

import java.io.IOException;
import java.util.ArrayList;

import logging.IssueLog;
import utilityClasses1.ArraySorter;

public class GridLayoutEditListenerList extends
		ArrayList<GridLayoutEditListener> implements GridLayoutEditListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void editWillOccur(GridLayoutEditEvent e) {
		for(GridLayoutEditListener l:this) {
			if (l!=null) {
				try {
					{
						l.editWillOccur(e);
					}
				} catch (Throwable e2) {
					IssueLog.log(e2);
				}
			}
		}
		
	}

	@Override
	public void editOccuring(GridLayoutEditEvent e) {
		for(GridLayoutEditListener l:this) {
			if (l!=null) {
				try {
					{
						l.editOccuring(e);
						;
					}
				} catch (Throwable e2) {
					IssueLog.log(e2);
				}
			}
		}
	}

	@Override
	public void editDone(GridLayoutEditEvent e) {
		for(GridLayoutEditListener l:this) {
			if (l!=null) {
				try {
					{
						l.editDone(e);
						;
					}
				} catch (Throwable e2) {
					IssueLog.log(e2);
				}
			}
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {

		ArraySorter.removeDeadItems(this);
		ArraySorter.removeNonSerialiazble(this);
		out.defaultWriteObject();
	}

}
