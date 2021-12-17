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
 * Version: 2021.2
 */
package objectDialogs;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import applicationAdapters.ImageWorkSheet;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.CurrentSetInformer;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.FigureDisplayWorksheet;
import locatedObject.LocatedObject2D;
import locatedObject.StrokedItem;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.attachmentPosition.AttachmentPositionPanel;
import standardDialog.choices.ItemSelectblePanel;
import standardDialog.graphics.FixedEdgeSelectable;
import standardDialog.graphics.GraphicSampleComponent;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputPanel;
import undo.AbstractUndoableEdit2;
import undo.UndoManagerPlus;

public class GraphicItemOptionsDialog extends StandardDialog {

	/**
	 * 
	 */
	{setHideCancel(false);}
	
	private static final long serialVersionUID = 1L;
	public GraphicSampleComponent previewComponent;
	StrokeInputPanel strokeInput;
	private static ImageWorkSheet currentImage=null;
	private static FigureDisplayWorksheet setContainer;
	private boolean updateAfterEachItemChange=true;
	private static CurrentSetInformer informer=new CurrentFigureSet();
	

	AttachmentPositionPanel snappingPanel=null;
	public AbstractUndoableEdit2 undoableEdit;
	protected transient boolean editAdded=false;
	
	public GraphicItemOptionsDialog() {
	}
	

	
	public void afterEachItemChange() {
		if (updateAfterEachItemChange) try{this.onOK();} catch (Throwable t) {
			IssueLog.logT(t);
			
		}
	}
	
	public void onOK() {
		updateObjectFromDialog() ;
		
		addUndo();
	}



	protected void addUndo() {
		if(undoableEdit!=null&&!editAdded) try {
			undoableEdit.establishFinalState();
			UndoManagerPlus undoManager = new CurrentFigureSet().getCurrentlyActiveDisplay().getUndoManager();
			if (undoManager!=null) {
				undoManager.addEdit(undoableEdit);
				editAdded=true;
			}
		} catch (Throwable t) {}
	}
	
	void updateObjectFromDialog() {
		this.setItemsToDiaog();
		if (previewComponent!=null) {previewComponent.repaint();}
		if (getCurrentImage()!=null) getCurrentImage().updateDisplay();
		else {
			
		}
		if (GraphicItemOptionsDialog.getSetContainer()!=null) GraphicItemOptionsDialog.getSetContainer().updateDisplay();
		informer.updateDisplayCurrent();
	}
	

	@Override
	public void onCancel() {
		updateObjectFromDialog() ;
	}

	
	protected void addOptionsToDialog() {
	}
	protected void setItemsToDiaog() {
		
	}
	
	
	
	public AttachmentPositionPanel addAttachmentPositionToDialog(LocatedObject2D l) {
		
		
		if (l==null||l.getAttachmentPosition()==null) return null;
		
		snappingPanel = new AttachmentPositionPanel(l.getAttachmentPosition());
		snappingPanel.addObjectEditListener(this);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridwidth=4;
		gc.gridheight=2;
		gc.gridx=0;
		gc.anchor=GridBagConstraints.WEST;
		
		gc.gridy=this.gridPositionY;
		gridPositionY+=2;
		this.getOptionDisplayTabs().add("Position", snappingPanel);
		
		return snappingPanel;
	}
	
	public void setObjectSnappingBehaviourToDialog(LocatedObject2D l) {
		if (snappingPanel==null) return;
		l.setAttachmentPosition(snappingPanel.getSnappingBehaviour());
	}
	
	public void addFixedEdgeToDialog(BasicGraphicalObject l) {
		FixedEdgeSelectable f = new FixedEdgeSelectable(l.getLocationType());
		ItemSelectblePanel is = new ItemSelectblePanel("Select Fixed Edge", f);
		add("edge fix", is);
	}
	
	public void setFixedEdgeToDialog(LocatedObject2D l) {
		l.setLocationType(	this.getChoiceIndex("edge fix") );
	}
	
	public void addNameField(BasicGraphicalObject t) {
		StringInputPanel si = new StringInputPanel("Name", t.getName(),10);
		this.add("name", si);
	}
	
	public void setNameFieldToDialog(BasicGraphicalObject t) {
		t.setName(this.getString("name"));
	}
	
public void addStrokePanelToDialog(StrokedItem s) {
	strokeInput=new StrokeInputPanel(s);
	strokeInput.addObjectEditListener(this);
	this.place(strokeInput);
	//strokeInput.setUpFont(strokeInput.getFont().deriveFont((float)10));
}

public void revertAll() {
	super.revertAll();
	if (strokeInput!=null) strokeInput.revert();
}

public void setStrokedItemtoPanel(StrokedItem s) {
	strokeInput.setStrokedItemToPanel(s);
}

public void addInsetToDialog(Insets s) {
	if (s==null) s=new Insets(0,0,0,0);
	//int cod = s.hashCode();
	this.add("Insets Top", new NumberInputPanel("Inset Top",  s.top, true, true, 0,100));
	this.add("Inset Bottom", new NumberInputPanel("Inset Bottom",  s.bottom, true, true, 0,100));
	this.add("Insets Left", new NumberInputPanel("Inset Left",  s.left, true, true, 0,100));
	this.add("Inset Right", new NumberInputPanel("Inset Right",  s.right, true, true, 0,100));
	
}

public  Insets getInsetsPanelFromDialog() {
	 Insets s = new Insets(0,0,0,0);
	s.top=this.getNumberInt("Insets Top");
	s.bottom=this.getNumberInt("Inset Bottom");
	s.left=this.getNumberInt("Insets Left");
	s.right=this.getNumberInt("Inset Right");
	return s;
}





public static ImageWorkSheet getCurrentImage() {
	return currentImage;
}



public static void setCurrentImage(ImageWorkSheet currentImage) {
	GraphicItemOptionsDialog.currentImage = currentImage;
}



public static FigureDisplayWorksheet getSetContainer() {
	return setContainer;
}

public static void updateCurrentDisplay() {
	if (setContainer!=null) setContainer.updateDisplay();
}


public static void setSetContainer(FigureDisplayWorksheet setContainer) {
	GraphicItemOptionsDialog.setContainer = setContainer;
}



public boolean isUpdateAfterEachItemChange() {
	return updateAfterEachItemChange;
}



public void setUpdateAfterEachItemChange(boolean updateAfterEachItemChange) {
	this.updateAfterEachItemChange = updateAfterEachItemChange;
}
	

	
}
