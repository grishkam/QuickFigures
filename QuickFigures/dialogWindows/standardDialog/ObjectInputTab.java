package standardDialog;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTabbedPane;

import logging.IssueLog;

public class ObjectInputTab extends JTabbedPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
protected ArrayList<ObjectEditListener> lis=new ArrayList<ObjectEditListener>();
	
	public void addObjectEditListener(ObjectEditListener o) {
		lis.add(o);
	}
	
	public void addObjectEditListeners(Collection<ObjectEditListener> o) {
		for(ObjectEditListener l:o) {
			addObjectEditListener(l);
		}
	}
	
	public void removeObjectEditListener(ObjectEditListener o) {
		lis.remove(o);
	}
	
	public void notifyListeners(ObjectEditEvent oee) {
		for(ObjectEditListener l:lis) {
			l.objectEdited(oee);
		}
	}
	
	public void moveTabForward(int tabindex) {
		
		if (tabindex==this.getTabCount()-1) return;
	
		try{
		Component c1 = this.getComponentAt(tabindex);
		Component c2 = this.getComponentAt(tabindex+1);
		this.remove(c1);
		this.remove(c2);
		this.setTabComponentAt(tabindex, c2);
		this.setTabComponentAt(tabindex+1, c1);
		
		} catch (Throwable t) {
			IssueLog.log(t);
		}
	}
}
