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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import graphicalObjects_FigureSpecific.InsetLayoutDialog;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;

public class InsetMenu extends SmartPopupJMenu implements ActionListener,
		PopupMenuSupplier {
	
	/**
	 * 
	 */
	private static final String RECRATE_INSET_LAYOUT = "inset layout",
			SCALE_OPTIONS = "i5",  REMOVE = "i6", REMOVE_INSETS = "i4",
			CREATE_INSETS = "i3", UPDATE_PANELS = "panelup";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelGraphicInsetDefiner inset;
	private ScaleFigureDialog ss;
	
	/**creates a menu item with this action listener*/
	void createMenuItem(String st, String ac) {
		 createMenuItem(st, ac, null);
	}
	void createMenuItem(String st, String ac, JMenu submenu) {
		JMenuItem g=new BasicSmartMenuItem(st);
		
		g.setActionCommand(ac);
		g.addActionListener(this);
		if(submenu==null)
			this.add(g);
		else submenu.add(g);
	}

	public InsetMenu(PanelGraphicInsetDefiner inset) {
		this.inset=inset;
		
		
		createMenuItem("Remove", REMOVE);
		//createMenuItem("Scale Options", SCALE_OPTIONS);
		createMenuItem("Redo Inset Layout", RECRATE_INSET_LAYOUT);
		
		SmartJMenu panels=new SmartJMenu("Expert Options");
		createMenuItem("Update Panels", UPDATE_PANELS, panels);
		createMenuItem("Remove inset panels", REMOVE_INSETS, panels);
		createMenuItem("Create new inset panels", CREATE_INSETS, panels);
		//this.add(panels);
	}

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}

	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String c=arg0.getActionCommand();
		if (c.equals(UPDATE_PANELS)) {inset.updateImagePanels();;}
		if (c.equals(CREATE_INSETS)) {
			inset.createMultiChannelInsets();
			}
		if (c.equals(REMOVE_INSETS)) {
			inset.removePanels();
			}
		if (c.equals(REMOVE)) {
			inset.removePanels();
			inset.getParentLayer().remove(inset);
			}
		
		if (c.equals(SCALE_OPTIONS)) {
			
			if(inset.personalLayout!=null)
			{
				showFigureScalerDialog();
			}
			}
		
		if (c.equals(RECRATE_INSET_LAYOUT)) {
			if (inset instanceof PanelGraphicInsetDefiner ) {
				 PanelGraphicInsetDefiner pgInset=(PanelGraphicInsetDefiner) inset;
				 pgInset.previosInsetLayout.practicalSize=true;
				InsetLayoutDialog dialog = new InsetLayoutDialog(pgInset.previosInsetLayout);
				dialog.setTargetInset(pgInset);
				dialog.showDialog();
				
			}
		
			}
		
		inset.updateDisplay();
	}

	public void showFigureScalerDialog() {
		ss=new ScaleFigureDialog(inset.personalLayout, inset.getPanelManager());
		IssueLog.log("Scale starts as "+inset.getBilinearScale());
		ss.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				
				
				double scale = ss.getNumber("scale");
				inset.setBilinearScale(scale);
				inset.updateImagePanels();
				DefaultLayoutGraphic layout = inset.personalLayout;
				layout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(layout.getPanelLayout());
				
			
			}});
		ss.showDialog();
	}
	
	public void showScaleDialog() {
		new ScaleFigureDialog(inset.personalLayout, inset.getPanelManager(), inset).showDialog();
		
		
	}
}
