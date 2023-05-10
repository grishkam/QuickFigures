/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2023.2
 */
package standardDialog;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTabbedPane;

import logging.IssueLog;

/**A tab for editing an object */
public class ObjectInputTabPane extends JTabbedPane{
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
		for(ObjectEditListener l:lis) try {
			l.objectEdited(oee);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	/**reorders the tabs within the tabbed pane*/
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
			IssueLog.logT(t);
		}
	}
}
