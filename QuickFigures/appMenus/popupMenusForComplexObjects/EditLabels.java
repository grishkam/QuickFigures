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
 * Version: 2022.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import figureFormat.LabelExamplePicker;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.LayoutSpaces;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;

/**A menu item that displays a dialog for a group of labels*/
public class EditLabels extends BasicSmartMenuItem  {
	
	
	private int type;
	private LabelExamplePicker picker;
	private DefaultLayoutGraphic layout;
	private transient MultiTextGraphicSwingDialog dd;
	private TextGraphic modelTextItem;

	/**creates an edit labels that targets all labels with the same grid attachment type as the given
	 * text */
	EditLabels(TextGraphic t) {
		 type=t.getAttachmentPosition().getGridSpaceCode();
		 setUpPickerFortype(type);
		 modelTextItem=t;
		 
		 IssueLog.log("TODO, confirm that this version of edit labels works");
	}
	
	/**Crates an edit labels item that targets a given type of label on the layout given
	 * @param type the type of label
	 * @param lay the layout
	 * @param t the text object*/
	public EditLabels(int type, DefaultLayoutGraphic lay, TextGraphic t) {
		this.type=type;
		
		setUpPickerFortype(type);
		layout=lay;
		this.addActionListener(this);
		 modelTextItem=t;
	}

	/**setup up the label picker for the label*/
	public void setUpPickerFortype(int type) {
		picker=new LabelExamplePicker(new ComplexTextGraphic(), type);
		this.setText("Edit All "+picker.getTypeName());
		if (type==LayoutSpaces.ROWS) {}
		
	}
	
	/**returns all the labels that are compatible with this edit labels action*/
	ArrayList<TextGraphic> getLabels(TextGraphic t) {
		ArrayList<TextGraphic> output=new ArrayList<TextGraphic>();
		ArrayList<?> lockedItems=null;
		if(layout!=null) {
			 lockedItems = layout.getLockedItems();}
		lockedItems=picker.getDesiredItemsAsGraphicals(lockedItems);
		
		if(lockedItems!=null &&lockedItems.size()>0) {
			
			
			for(Object i:lockedItems) {
				if (!(i instanceof TextGraphic)) continue;//ignores non text
				
				TextGraphic text2 = (TextGraphic) i;
				if(picker.isDesirableItem(i))
					output.add(text2);
				}
		}
		else if ( modelTextItem!=null) {
			for(Object i:modelTextItem.getParentLayer().getAllGraphics()) {
				if (!(i instanceof TextGraphic)) continue;//ignores non text
				
				TextGraphic text2 = (TextGraphic) i;
			
				if(picker.isDesirableItem(i))
					output.add(text2);
				}
		}
		
		return output;
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**creates a dialog for multiple text items and shows it*/
	@Override
	public void actionPerformed(ActionEvent e) {
		 dd = new MultiTextGraphicSwingDialog(getLabels(modelTextItem), true);
		 dd.setUnifyPosition(true);
		dd.addDialogListener(new StandardDialogListener() {
			
			@Override
			public void itemChange(DialogItemChangeEvent event) {
				if (layout!=null)
				for(TextGraphic t: dd.getAllEditedItems()) {
				layout.getEditor().expandSpacesToInclude(layout.getPanelLayout(), t.getBounds());
				}
			}});
		
		dd.showDialog();
		
	}
	

	
}

