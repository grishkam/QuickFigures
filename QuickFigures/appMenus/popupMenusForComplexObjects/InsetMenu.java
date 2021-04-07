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
 * Date Modified: April 7, 2021
 * Version: 2021.1
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import figureOrganizer.insetPanels.InsetLayoutDialog;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import popupMenusForComplexObjects.InsetMenu.ChangeInsetScale.ScaleType;
import sUnsortedDialogs.ScaleFigureDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import undo.CombinedEdit;
import undo.UndoInsetDefChange;
import undo.UndoLayoutEdit;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;

/**A popup menu for inset definers
 * TODO: add options for scale changes to menu
 * @see PanelGraphicInsetDefiner
 * */
public class InsetMenu extends SmartPopupJMenu implements ActionListener,
		PopupMenuSupplier {
	
	/**
	 * 
	 */
	private static final String RECRATE_INSET_LAYOUT = "inset layout",
			 REMOVE = "i6", REMOVE_PANEL_INSETS = "i4",
			CREATE_INSETS = "i3", UPDATE_PANELS = "panelup", TURN_SQUARE_LOCK="SQUARE LOCK";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelGraphicInsetDefiner inset;
	
	private double[] possibleScales=new double[] {2, 2.5, 3, 4, 5,6,8};
	
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
	
		createMenuItem("Redo Inset Layout", RECRATE_INSET_LAYOUT);
		
		SmartJMenu panels=new SmartJMenu("Expert Options");
		createMenuItem("Update Panels", UPDATE_PANELS, panels);
		createMenuItem("Remove inset panels", REMOVE_PANEL_INSETS, panels);
		createMenuItem("Create new inset panels", CREATE_INSETS, panels);
		
		createMenuItem("Square Lock "+(inset.isSquareLock()? "Off": "On"), TURN_SQUARE_LOCK);
		
		SmartJMenu rescale=new SmartJMenu("Change Scale");
			for(double scaleA: possibleScales)
				rescale.add(new ChangeScaleMenuItem(scaleA));
			rescale.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_PARENT_WIDTH));
			rescale.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_PARENT_HEIGHT));
			rescale.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_SIZE_TO_PARENT));
			rescale.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_LAYOUT_TO_HEIGHT));
			rescale.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_LAYOUT_TO_WIDTH));
		
		add(rescale);//panels menu needs upgrade
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
		if (c.equals(REMOVE_PANEL_INSETS)) {
			inset.removePanels();
			}
		if (c.equals(REMOVE)) {
			inset.removePanels();
			inset.getParentLayer().remove(inset);
			}
		
	
		
	if (c.equals(TURN_SQUARE_LOCK)) {
				
		
				inset.setSquareLock(!inset.isSquareLock());
				if (inset.isSquareLock()) {
					inset.setWidth(inset.getObjectHeight());
					inset.afterHandleMove(RectangleEdges.LOWER_RIGHT, null, null);
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

	
	/**old version of dialog that was designed to scale the inset panels, decided that it was not
	  helpful enough and a rewrite is called for
	private void showFigureScalerDialog() {
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
	}*/
	
	/**Sbows a scaling dialog for the inset panels*/
	public void showScaleDialog() {
		new ScaleFigureDialog(inset.personalLayout, inset.getPanelManager(), inset).showDialog();
		
		
	}
	
	
	
	
	/**A menu item for changing the scale*/
	public class ChangeScaleMenuItem extends BasicSmartMenuItem implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ChangeInsetScale changer;
		
		public ChangeScaleMenuItem(double scale) {
			this.setText(""+scale);
			this.addActionListener(this);
			changer=new ChangeInsetScale(inset, scale);
			this.setText(changer.getName());
		}
		
		
		public ChangeScaleMenuItem( ScaleType form) {
			
			this.addActionListener(this);
			changer=new ChangeInsetScale(inset,  form);
			this.setText(changer.getName());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				me.getUndoManager().addEdit(changer.rescale());
				me.getAsDisplay().updateDisplay();
				me.getAsDisplay().updateDisplay();
			} catch (Exception e1) {
				IssueLog.logT(e1);
			}
		}
		
	}
	
	/**a class that changes the scale level of the insets*/
	public static class ChangeInsetScale {
		
		static enum ScaleType {USER_DEFINED, MATCH_PARENT_WIDTH, MATCH_PARENT_HEIGHT, MATCH_SIZE_TO_PARENT, MATCH_LAYOUT_TO_HEIGHT, MATCH_LAYOUT_TO_WIDTH;}

		private PanelGraphicInsetDefiner primaryInset;
		private double scale=2;
		private ImagePanelGraphic sPanel;
		private ScaleType type=ScaleType.USER_DEFINED;

		/**
		 * @param inset
		 * @param b
		 */
		public ChangeInsetScale(PanelGraphicInsetDefiner inset, double b) {
			this.primaryInset=inset;
			this.scale=b;
			this.type=ScaleType.USER_DEFINED;
		}
		
		
		public ChangeInsetScale(PanelGraphicInsetDefiner inset,  ScaleType form) {
			this.primaryInset=inset;
			this.scale=1;
			this.type=form;
		}

		/**
		changes the scale
		 * @return 
		 */
		public CombinedEdit rescale() {
			CombinedEdit output = new CombinedEdit();
			sPanel=primaryInset.getSourcePanel();
			primaryInset.getPanelManager().getPanelList().setPanelFixedEdges();
			
			/**makes sure the panels are set to the same panel scale so the dpi ends up the same*/
			double panelScale=sPanel.getScale();
			
			PanelGraphicInsetDefiner targetInset = primaryInset;
			
			output.addEditToList(new UndoLayoutEdit(targetInset.personalLayout));
		
			
			if(type==ScaleType.MATCH_PARENT_WIDTH||type== ScaleType.MATCH_SIZE_TO_PARENT) {
				scale=sPanel.getObjectWidth()/targetInset.getRectangle().getWidth();
			}
			if(type==ScaleType.MATCH_PARENT_HEIGHT) {
				scale=sPanel.getObjectHeight()/targetInset.getRectangle().getHeight();
			}
			
			/**changes the inset to match the aspect ratio of the parent*/
			if(type== ScaleType.MATCH_SIZE_TO_PARENT) {
				output.addEditToList(new UndoInsetDefChange(targetInset, true));
				double ratio = ((double)sPanel.getObjectHeight())/sPanel.getObjectWidth();
				double newH = targetInset.getRectangle().getWidth()*ratio;
				targetInset.setHeight(newH);
			}
			
			/**calcuates the scale that if used will result in the laout reaching from one end of the parent panel to the other*/
			if(type==ScaleType.MATCH_LAYOUT_TO_HEIGHT||type==ScaleType.MATCH_LAYOUT_TO_WIDTH) {
				BasicLayout l = primaryInset.personalLayout.getPanelLayout();
				double scaleChange=1;
					if(type==ScaleType.MATCH_LAYOUT_TO_HEIGHT) {
						double constantBorderSpace = (l.nRows()-1)*l.theBorderWidthBottomTop;
						Rectangle2D b = l.getSelectedSpace(LayoutSpaces.ALL_OF_THE+LayoutSpaces.PANELS).getBounds2D();
						scaleChange = (sPanel.getObjectHeight()-constantBorderSpace)/(b.getHeight()-constantBorderSpace);
		
					}
					if(type==  ScaleType.MATCH_LAYOUT_TO_WIDTH) { 
						double constantBorderSpace = (l.nColumns()-1)*l.theBorderWidthLeftRight;
						Rectangle2D b = l.getSelectedSpace(LayoutSpaces.ALL_OF_THE+LayoutSpaces.PANELS).getBounds2D();
						scaleChange = (sPanel.getObjectWidth()-constantBorderSpace)/(b.getWidth()-constantBorderSpace);
		
					}
					
					scale= primaryInset.getBilinearScale()*scaleChange;
					if(scale<1) scale=1;;
					
			}
			
			
			/**changes the scale of the inset that was clicked on */
			output.addEditToList(
					resizeInsetPanels(panelScale, scale, targetInset)
					);
			
			/**changes the scale of each other inset */
			for(PanelGraphicInsetDefiner inset1: primaryInset.getInsetDefinersThatShareLayout()) {
				if(type== ScaleType.MATCH_SIZE_TO_PARENT) {
					output.addEditToList(new UndoInsetDefChange(inset1, true));
					inset1.setWidth(targetInset.getRectangle().getWidth());
					inset1.setHeight(targetInset.getRectangle().getHeight());
				}
				output.addEditToList(
						resizeInsetPanels(panelScale, scale, inset1)
				);
				
			}
			
			output.establishFinalState();
			return output;
		}
		
		/**returns a description of the scale change that will be done*/
		public String getName() {
			if(type==ScaleType.MATCH_PARENT_WIDTH) {
				return "To width of parent panel";
			}
			if(type==ScaleType.MATCH_PARENT_HEIGHT) {
				return "To height of parent panel";
			}
			if(type==  ScaleType.MATCH_SIZE_TO_PARENT) { 
				return "To size of parent panel";
			}
			if(type==ScaleType.MATCH_LAYOUT_TO_HEIGHT) {
				return "Group to height of parent panel";
			}
			if(type==  ScaleType.MATCH_LAYOUT_TO_WIDTH) { 
				return "Group to width of parent panel";
			}
			
			return ""+scale+" fold";
		}

		/**changes the size of the inset panels
		 * @param panelScale
		 * @param targetInset
		 * @return 
		 */
		protected CombinedEdit resizeInsetPanels(double panelScale, double scale, PanelGraphicInsetDefiner targetInset) {
			
			CombinedEdit output = UndoInsetDefChange.createRescale(targetInset);
			
			for(ImagePanelGraphic p: targetInset.getPanelManager().getPanelList().getPanelGraphics()) {
				p.setRelativeScale(panelScale);
				
			}
			targetInset.setBilinearScale(scale);
			targetInset.updateImagePanels();
			DefaultLayoutGraphic layout = targetInset.personalLayout;
			layout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(layout.getPanelLayout());
			output.establishFinalState();
			return output;
			
		}
		
	}
}
