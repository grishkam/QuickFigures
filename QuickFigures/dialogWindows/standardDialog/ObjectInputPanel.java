package standardDialog;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

public class ObjectInputPanel extends JPanel{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
ArrayList<ObjectEditListener> lis=new ArrayList<ObjectEditListener>();
	
	public void addObjectEditListener(ObjectEditListener o) {
		lis.add(o);
	}
	
	public void addObjectEditListeners(Collection<ObjectEditListener> o) {
		lis.addAll(o);
	}
	
	public void removeObjectEditListener(ObjectEditListener o) {
		lis.remove(o);
	}
	
	public void notifyListeners(ObjectEditEvent oee) {
		for(ObjectEditListener l:lis) {
			l.objectEdited(oee);
		}
	}
}
