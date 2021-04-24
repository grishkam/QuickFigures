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
 * Date Modified: April 24, 2021
 * Version: 2021.1
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
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
import messages.ShowMessage;
import popupMenusForComplexObjects.InsetMenu.ChangeInsetScale.ScaleType;
import sUnsortedDialogs.ScaleFigureDialog;
import standardDialog.StandardDialog;
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
	 codes for each menu action
	 */
	private static final String RECRATE_INSET_LAYOUT = "inset layout",
			 REMOVE = "i6", REMOVE_PANEL_INSETS = "i4",
			CREATE_INSETS = "i3", UPDATE_PANELS = "panelup", TURN_SQUARE_LOCK="SQUARE LOCK",
			CHANGE_SCALE_MODE="change scale mode";
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
		
		
		
		SmartJMenu scaleMenu=new SmartJMenu("Scale");
		SmartJMenu scaleMenuValue=new SmartJMenu("Change Scale to");
		SmartJMenu scaleMenuFit=new SmartJMenu("Change Scale to fit");
		SmartJMenu scaleMenuMode=new SmartJMenu("Change Scale Mode");
		
		scaleMenu.add(scaleMenuValue);
		scaleMenu.add(scaleMenuFit);
		scaleMenu.add(scaleMenuMode);
		
			for(double scaleA: possibleScales)
				scaleMenuValue.add(new ChangeScaleMenuItem(scaleA));
			scaleMenuValue.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.USER_INPUT));
			scaleMenuFit.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_PARENT_WIDTH));
			scaleMenuFit.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_PARENT_HEIGHT));
			scaleMenuFit.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_SIZE_TO_PARENT));
			scaleMenuFit.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_LAYOUT_TO_HEIGHT));
			scaleMenuFit.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.MATCH_LAYOUT_TO_WIDTH));
		
		if(inset.isDoNotScale())	 {
			scaleMenuMode.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.SCALE_IMAGE_MODE));
		} else scaleMenuMode.add(new ChangeScaleMenuItem( ChangeInsetScale.ScaleType.ENLARGE_IMAGE_MODE));

		add(scaleMenu);//panels menu needs upgrade
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

	
	
	/**Shows a scaling dialog for the inset panels*/
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
		
		static enum ScaleType {USER_DEFINED, MATCH_PARENT_WIDTH, MATCH_PARENT_HEIGHT, MATCH_SIZE_TO_PARENT, MATCH_LAYOUT_TO_HEIGHT, MATCH_LAYOUT_TO_WIDTH, USER_INPUT, SCALE_IMAGE_MODE, ENLARGE_IMAGE_MODE;}

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
			
			
			
			PanelGraphicInsetDefiner targetInset = primaryInset;
			
		
			
			output.addEditToList(new UndoLayoutEdit(targetInset.personalLayout));
		
			boolean scaleMode = targetInset.isDoNotScale();
			
			if(type==ScaleType.SCALE_IMAGE_MODE) {
				scaleMode=false; 
				scale=targetInset.getInsetScale();
				
			}
			if(type==ScaleType.ENLARGE_IMAGE_MODE) {
				scaleMode=true; 
				scale=targetInset.getInsetScale();
			}
			
			
			
			if(type==ScaleType.USER_INPUT) {
				scale=StandardDialog.getNumberFromUser("input scale factor", 2);
				if (scale<=1||scale>20) {
					scale=2;
					ShowMessage.showOptionalMessage("invalid input", false, "Please select a value in the 1-20 range");
				}
			}
			
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
					
					scale= primaryInset.getInsetScale()*scaleChange;
					if(scale<1) scale=1;;
					
			}
			
			
			/**makes sure the panels are set to the same panel scale so the dpi ends up the same*/
			double panelScale=sPanel.getRelativeScale();
			panelScale=panelScale*targetInset.getPanelSizeInflation();
			
		
			
			/**changes the scale of the inset that was clicked on */
			output.addEditToList(
					resizeInsetPanels(panelScale, scale, targetInset, scaleMode)
					);
			
			
			
			/**changes the scale of each other inset */
			for(PanelGraphicInsetDefiner inset1: primaryInset.getInsetDefinersThatShareLayout()) {
				
				if(type== ScaleType.MATCH_SIZE_TO_PARENT) {
					output.addEditToList(new UndoInsetDefChange(inset1, true));
					inset1.setWidth(targetInset.getRectangle().getWidth());
					inset1.setHeight(targetInset.getRectangle().getHeight());
				}
				output.addEditToList(
						resizeInsetPanels(panelScale, scale, inset1, scaleMode)
						
				);
				
			}
			
			output.establishFinalState();
			return output;
		}
		
		/**returns a description of the scale change that will be done*/
		public String getName() {
			
			if(type==ScaleType.USER_INPUT) {
				return "Set scale factor";
			}
			if(type==ScaleType.MATCH_PARENT_WIDTH) {
				return "Width of parent panel";
			}
			if(type==ScaleType.MATCH_PARENT_HEIGHT) {
				return "Height of parent panel";
			}
			if(type==  ScaleType.MATCH_SIZE_TO_PARENT) { 
				return "Size of parent panel";
			}
			if(type==ScaleType.MATCH_LAYOUT_TO_HEIGHT) {
				return "Group to height of parent panel";
			}
			if(type==  ScaleType.MATCH_LAYOUT_TO_WIDTH) { 
				return "Group to width of parent panel";
			}
			
			if(type==  ScaleType.ENLARGE_IMAGE_MODE) { 
				return "Do not scale pixels when creating panels";
			}
			
			if(type==  ScaleType.SCALE_IMAGE_MODE) { 
				return "Scale pixels when creating panels";
			}
			
			return ""+scale+" ";
		}

		/**changes the size of the inset panels
		 * @param panelScale
		 * @param targetInset
		 * @param scaleMode 
		 * @return 
		 */
		protected CombinedEdit resizeInsetPanels(double panelScale, double scale, PanelGraphicInsetDefiner targetInset, boolean scaleMode) {
			
			CombinedEdit output = UndoInsetDefChange.createRescale(targetInset);
			targetInset.setDoNotScale(scaleMode);
			for(ImagePanelGraphic p: targetInset.getPanelManager().getPanelList().getPanelGraphics()) {
				p.setRelativeScale(panelScale);
				
			}
			targetInset.setInsetScale(scale);
			targetInset.updateImagePanels();
			DefaultLayoutGraphic layout = targetInset.personalLayout;
			targetInset.updateRelativeScaleOfPanels();
			layout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(layout.getPanelLayout());
			output.establishFinalState();
			return output;
			
		}
		
	}
}
