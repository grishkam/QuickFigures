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
 * Version: 2022.1
 */
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.attachmentPosition.AttachmentPositionPanel;
import standardDialog.booleans.BooleanInputPanel;

/**A dialog that allows the user to change an attachment positon
 * by dragging rectangles
 * @see AttachmentPosition
 * @see LocatedObject2D*/
public class MultiAttachmentPositionDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<LocatedObject2D> array=new ArrayList<LocatedObject2D>();

	private LocatedObject2D primaryObject;

	/** set to true if each item will use a different attachment position objects
	 if false, other changes to attachment positions will affect all objects even after the dialog is closed*/
	private boolean useDistinctAttachmentPositions=true;
	
	/**cretes the dialog
	 * @param distinct determines if each item will use a different attachment position objects*/
	public MultiAttachmentPositionDialog(boolean distinct) {
		useDistinctAttachmentPositions=distinct;
	}

	/**Sets the list of objects used by this dialog*/
	public void setGraphics(ArrayList<?> zs) {
		array=new ArrayList<LocatedObject2D>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		for(LocatedObject2D ob1: array) {
			if (ob1.getAttachmentPosition()!=null) primaryObject=ob1; 
		}
		
		AttachmentPositionPanel panel = this.addAttachmentPositionToDialog(primaryObject);
			this.getOptionDisplayTabs().remove(mainPanel);
			BooleanInputPanel booleanPanel = new BooleanInputPanel("Keep Relative Positions Same", !useDistinctAttachmentPositions);
			add("create unique", booleanPanel);
			booleanPanel.placeItems(panel,0, 6);
			
			
	}
	
	/**Adds objects from an array of unknown type to an array of located objects*/
	private void addGraphicsToArray(ArrayList<LocatedObject2D> array, ArrayList<?> zs) {
		if (zs!=null)
		for(Object z:zs) {
			if (z instanceof LocatedObject2D) {array.add((LocatedObject2D) z);}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	/**returns true if no objects are available*/
	public boolean isEmpty() {
		if(array.size()==0 &&primaryObject==null) return true;
		return false;
	}

	protected void setItemsToDiaog() {
		useDistinctAttachmentPositions=!this.getBoolean("create unique");
		
		this.setObjectSnappingBehaviourToDialog(primaryObject);
		
		for(LocatedObject2D s: array) {
				if (s.getAttachmentPosition()!=null) 
					{
					AttachmentPosition newSnap = primaryObject.getAttachmentPosition().copy();
					if(!useDistinctAttachmentPositions) newSnap = primaryObject.getAttachmentPosition();
					s.setAttachmentPosition(newSnap);
					};
		}
}

	
	public void showDialog() {
		 if(array.size()==0) {
			 ShowMessage.showOptionalMessage("No items compatible with this dialog are selected");
			 return;
		 }
		super.showDialog();
			  
		  }
}
