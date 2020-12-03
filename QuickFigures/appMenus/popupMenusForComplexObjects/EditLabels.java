/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import figureFormat.LabelExamplePicker;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.SwingDialogListener;

public class EditLabels extends JMenuItem implements ActionListener {
	
	
	private int type;
	private LabelExamplePicker picker;
	private MontageLayoutGraphic layout;
	private transient MultiTextGraphicSwingDialog dd;
	private TextGraphic modelTextItem;

	public EditLabels(TextGraphic t) {
		
		 type=t.getAttachmentPosition().getGridSpaceCode();
		 setUpPickerFortype(type);
		 modelTextItem=t;
	}
	
	public EditLabels(int type, MontageLayoutGraphic lay, TextGraphic t) {
		this.type=type;
		
		setUpPickerFortype(type);
		layout=lay;
		this.addActionListener(this);
		 modelTextItem=t;
	}

	public void setUpPickerFortype(int type) {
		picker=new LabelExamplePicker(new ComplexTextGraphic(), type);
		this.setText("Edit All "+picker.getTypeName());
		
	}
	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		 dd = new MultiTextGraphicSwingDialog(getLabels(modelTextItem), true);
		 dd.setUnifyPosition(true);
		dd.addDialogListener(new SwingDialogListener() {
			
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

