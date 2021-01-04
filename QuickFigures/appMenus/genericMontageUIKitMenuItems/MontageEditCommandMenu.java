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
package genericMontageUIKitMenuItems;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.undo.UndoManager;

import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.IconSet;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GenericMontageEditor;
import layout.basicFigure.LayoutEditorDialogs;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.StandardDialog;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;
import undo.UndoSnappingChange;
import utilityClasses1.ArraySorter;

public class MontageEditCommandMenu extends ArrayList<MenuItem> implements
		ActionListener {

	
	
	private BasicLayout maintLayout;
	private GenericMontageEditor edit=new GenericMontageEditor();
	
	private UndoManager undoManager;

	public MontageEditCommandMenu(BasicLayout l) {
		setMainLayout(l);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JMenuItem colWidth=createMenuItem("Edit Montage Column Unique Widths");
	JMenuItem rowHeight=createMenuItem("Edit Montage Rows Unique Heights");
	JMenuItem rowColDim=createMenuItem("Panel Widths/Heights");
	JMenuItem createUniwueWidthsForEachCol =createMenuItem("Set Unique Column Widths"); 
	JMenuItem createUniwueHeightsForEachRow =createMenuItem("Set Unique Row Heights"); 
	JMenuItem setLayoutToModel=createMenuItem("Set Layout to 'Model Montage1.tif'");
	JMenuItem cropPanels=createMenuItem("Crop Panels");
	JMenuItem showEditorDialog=createMenuItem("Full Layout Edit");
	JMenuItem resizeRowCol=createMenuItem("Resize Rows and Columns to Fit objects");
	JMenuItem trimLabelSpace=createMenuItem("Trim Label Space");
	JMenuItem setLabelSpace=createMenuItem("Optimize Label Space");
	JMenuItem trimCanvas=createMenuItem("Trim Extra Space");
	JMenuItem invertLayout=createMenuItem("Horizontal/Vertical Flip");
	JMenuItem bordersOnly=createMenuItem("Borders");
	
	public ArrayList<JMenuItem> getPanelSizeList() {
		ArrayList<JMenuItem> o = new ArrayList<JMenuItem> ();
		o.add(rowColDim);
		o.addAll(getPanelSizeListReduced());
		
		return o;
	}
	
	public ArrayList<JMenuItem> getPanelSizeListReduced() {
		ArrayList<JMenuItem> o = new ArrayList<JMenuItem> ();
		o.add(resizeRowCol);
		o.add(colWidth);
		o.add(rowHeight);
		o.add(createUniwueHeightsForEachRow);
		o.add(createUniwueWidthsForEachCol);
		
		return o;
	}
	
	public ArrayList<JMenuItem> getGeneralList() {
		ArrayList<JMenuItem> o = new ArrayList<JMenuItem> ();
		o.addAll(getBasicList());

		
		o.addAll(getPanelSizeList());
		return o;
	}
	
	public ArrayList<JMenuItem> getBasicList() {
		ArrayList<JMenuItem> o = new ArrayList<JMenuItem> ();
		o.add(bordersOnly);
		
		
		//o.add(trimLabelSpace);
		
		//o.add(setLayoutToModel);
		o.add(invertLayout);
		o.add(setLabelSpace);
		o.add(showEditorDialog);
		return o;
	}
	
	
	
	
	public JMenu getInclusiveList() {
		ArrayList<JMenuItem> list1 = getBasicList();
		list1.add(2,rowColDim);
		
		JMenu out = getPopupMen("Edit Layout ", list1);
		
		
		ArrayList<JMenuItem> list2 = getPanelSizeListReduced();
		JMenu out2 = getPopupMen("Row/Col Sizes",  list2);
		out.add(out2);
		
		if (useImageIconsFromtools)out.setIcon(new IconSet("icons/PanelSizeAdjusterToolIcon.jpg").getIcon(0));
		
		
		return  out;
	}
	
	boolean useImageIconsFromtools=false;
	
	public JMenu getPanelSizeListJMenu() {
		JMenu out = getPopupMen("Dimensions ", getPanelSizeList());
		if (useImageIconsFromtools)out.setIcon(new IconSet("icons/PanelSizeAdjusterToolIcon.jpg").getIcon(0));
		return  out;
	}
	
	public ArrayList<JMenuItem> getBorderList() {
		ArrayList<JMenuItem> o = new ArrayList<JMenuItem> ();
		o.add(setLayoutToModel);
		o.add(showEditorDialog);
		
		//o.add(cropPanels);
		return o;
	}
	
	public JMenu getGeneralJMenu() {
		return  getPopupMen("General ", getBorderList());
	}

	
	static JMenu getPopupMen(String st, ArrayList<JMenuItem> men) {
		JMenu o=new JMenu(st);
		for(JMenuItem m: men) {
			JMenuItem i=new JMenuItem();
			i.setActionCommand(m.getActionCommand());
			i.setText(m.getText());
			i.setName(m.getName());
			o.add(i);
			if (m.getActionListeners().length>=1) i.addActionListener(m.getActionListeners()[0]);
		}
		return o;
	}
	
	 public void onActionPerformed(Object sour, String st) {
		 
		 CombinedEdit editUndo = new CombinedEdit();
		 UndoLayoutEdit undo1 = new UndoLayoutEdit(getMainLayout());
		 editUndo.addEditToList(undo1);
		 
		 
		if (st==trimCanvas.getActionCommand()) {
			 getEditor().trimCanvas(getMainLayout());
		}
		if (st==colWidth.getActionCommand()) {
			new LayoutEditorDialogs().showUniqueDimensionDialog(getMainLayout(),  getEditor(), 0);
		}
		if (st==rowHeight.getActionCommand()) {new LayoutEditorDialogs().showUniqueDimensionDialog(getMainLayout(),  getEditor(), 1);}
		
		if (st==bordersOnly.getActionCommand()) {new LayoutEditorDialogs().showBorderEditorDialog(new StandardDialog("Edit Borders", true),   getEditor(),getMainLayout());}
		
		
		if (st==resizeRowCol.getActionCommand()) {
			
			/**puts items in upper-left corner*/
			handlePanelSizeFit();
			//getMainLayout().getEditor().alterPanelWidthAndHeightToFitContents(getMainLayout());
			//getMainLayout().getEditor().alterPanelWidthAndHeightToFitContents(getMainLayout());
			
			
		}
		
		if (st==rowColDim.getActionCommand()) {
			new LayoutEditorDialogs().showColumnNumberEditorDialog(getMainLayout().getEditor(), getMainLayout(),1,1);
		}
		
		if (st==createUniwueWidthsForEachCol.getActionCommand()) {
			new LayoutEditorDialogs().showUniqueDimensionDialog(getMainLayout(), 0);
		}
		if (st==createUniwueHeightsForEachRow.getActionCommand()) {
			new LayoutEditorDialogs().showUniqueDimensionDialog(getMainLayout(), 1);
		}
		
		if(st==trimLabelSpace.getActionCommand()) {
			
			getMainLayout().getEditor().trimLabelSpacesToFitContents(getMainLayout());

		}
		if(st==setLabelSpace.getActionCommand()) {
			
			getMainLayout().getEditor().fitLabelSpacesToContents(getMainLayout());

		}
		
		//if (st==cropPanels) { getEditor().cropPanels(getMainLayout(), getSelectionBounds(getMainLayout().getImage()));}
		if (st==showEditorDialog.getActionCommand()) {
			new LayoutEditorDialogs() .showGeneralEditorDialog( getEditor(), getMainLayout());}
		
		
		
		if (st==invertLayout.getActionCommand()) {
			
			 ArrayList<UndoSnappingChange> arraySnapUndo =new ArrayList<UndoSnappingChange>();
				for(LocatedObject2D loc: getMainLayout().getEditedImage().getLocatedObjects()) {
					if (loc.getAttachmentPosition()!=null)
					arraySnapUndo.add(new UndoSnappingChange(loc));
				}
			
				getEditor().invertPanelsAndLabels(getMainLayout());
				
				for(UndoSnappingChange undo0: arraySnapUndo) {
					undo0.establishFinalState();
					editUndo.addEditToList(undo0);
				}
			
		}
		
		undo1.establishFinalLocations();
		addUndo(editUndo);
		
		CurrentFigureSet.updateActiveDisplayGroup();
		getMainLayout().getEditedImage().updateDisplay();
		
		}

	public void handlePanelSizeFit() {
		getMainLayout().getEditor().placePanelsInCorners( getMainLayout(),new ArraySorter<LocatedObject2D>().getThoseOfClass(getMainLayout().getEditedImage().getLocatedObjects(), ImagePanelGraphic.class));

		getMainLayout().getEditor().alterPanelWidthAndHeightToFitContents(getMainLayout());
	}
	 

	 
	private void addUndo(CombinedEdit editUndo) {
		if (this.getUndoManager()!=null) 
			getUndoManager().addEdit(editUndo);
	}

	private GenericMontageEditor getEditor() {
		return edit;
	}



	private JMenuItem createMenuItem(String string) {
		JMenuItem o = new JMenuItem(string);
		o.setActionCommand(string+"Command");
		o.addActionListener(this);
		return o;
	}



	public ArrayList<JMenuItem> getNonMontageSpacePopupMenuItemsForMainLayout() {
		ArrayList<JMenuItem> v = new ArrayList<JMenuItem>();
		v.add(trimCanvas);
		return v;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			onActionPerformed(e.getSource(), e.getActionCommand());
			if (GraphicItemOptionsDialog.getSetContainer()!=null)GraphicItemOptionsDialog.getSetContainer().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
		
	}

	public BasicLayout getMainLayout() {
		return maintLayout;
	}



	public void setMainLayout(BasicLayout maintLayout) {
		this.maintLayout = maintLayout;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

}
