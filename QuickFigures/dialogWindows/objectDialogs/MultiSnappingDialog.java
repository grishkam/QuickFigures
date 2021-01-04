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
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import standardDialog.attachmentPosition.SnappingPanel;
import standardDialog.booleans.BooleanInputPanel;

public class MultiSnappingDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<LocatedObject2D> array=new ArrayList<LocatedObject2D>();

	private LocatedObject2D object;

	private boolean copySnap=true;
	
	
	public MultiSnappingDialog(boolean b) {
		copySnap=b;
	}

	public void setGraphics(ArrayList<?> zs) {
		array=new ArrayList<LocatedObject2D>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		for(LocatedObject2D ob1: array) {
			
			if (ob1.getAttachmentPosition()!=null) object=ob1; 
		}
		
		SnappingPanel panel = this.addSnappingBehviourToDialog(object);
			this.getOptionDisplayTabs().remove(mainPanel);
			BooleanInputPanel booleanPanel = new BooleanInputPanel("Keep Relative Positions Same", !copySnap);
			add("create unique", booleanPanel);
			booleanPanel.placeItems(panel,0, 6);
			
			
	}
	
	public void addGraphicsToArray(ArrayList<LocatedObject2D> array, ArrayList<?> zs) {
		if (zs!=null)
		for(Object z:zs) {
			if (z instanceof LocatedObject2D) {array.add((LocatedObject2D) z);}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	public boolean isEmpty() {
		if(array.size()==0 &&object==null) return true;
		return false;
	}

	protected void setItemsToDiaog() {
		copySnap=!this.getBoolean("create unique");
		
		this.setObjectSnappingBehaviourToDialog(object);
		
		for(LocatedObject2D s: array) {
				if (s.getAttachmentPosition()!=null) 
					{
					AttachmentPosition newSnap = object.getAttachmentPosition().copy();
					if(!copySnap) newSnap = object.getAttachmentPosition();
					s.setAttachmentPosition(newSnap);
					};
		}
}

}
