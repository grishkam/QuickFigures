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
 * Version: 2021.1
 */
package standardDialog;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;

import logging.IssueLog;

/**A superclass for multiple panels that edit an object and notify object edit listeners */
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
		for(ObjectEditListener l:lis) try {
			l.objectEdited(oee);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
}
