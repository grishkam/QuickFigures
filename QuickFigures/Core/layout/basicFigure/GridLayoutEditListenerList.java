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
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package layout.basicFigure;

import java.io.IOException;
import java.util.ArrayList;

import logging.IssueLog;
import utilityClasses1.ArraySorter;

/**A list of grid layout event listeners*/
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
					IssueLog.logT(e2);
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
					IssueLog.logT(e2);
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
					IssueLog.logT(e2);
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
