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
 * Version: 2023.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import applicationAdapters.DisplayedImage;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureScaler;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import imageMenu.CanvasAutoResize;
import imageMenu.ZoomFit;
import menuUtil.SmartJMenu;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.InfoDisplayPanel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import utilityClasses1.SizeConstants;

/**a set of menu options that allow the user to scale a figure*/
public class FigureScalerMenu extends SmartJMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic item;;
	

	public FigureScalerMenu(PanelLayoutGraphic c) {
		super("Scale Figure");
		item=c;
		createItems();
	}
	
	public void addEdit(CombinedEdit undo) {
		if(mouseE!=null)
			{
			undo.addEditToList(new CanvasAutoResize(false).performUndoableAction(mouseE.getAsDisplay()));
			}
		addUndo(undo);
		
	}
	
	/**generates the JMenu items for this menu*/
	public void createItems() {

	
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public AbstractUndoableEdit2  performAction() {
				FigureScaler scaler = new FigureScaler(false);
					double factor=scaler.getSlideSizeScale(item, SizeConstants.SLIDE_SIZE);
					if(factor==1)
						return null;
					CombinedEdit undo = scaler.scaleFigure(item, factor, item.getPanelLayout().getReferenceLocation());
					item.updateDisplay();
					
					FigureScaler.scaleMessages(item.getParentLayer());
					DisplayedImage asDisplay = getMemoryOfMouseEvent().getAsDisplay();
					AbstractUndoableEdit2 undo2 = new CanvasAutoResize(CanvasAutoResize.SLIDE_SIZE).performUndoableAction( asDisplay);
					undo.addEditToList(undo2);
					new ZoomFit(ZoomFit.SCREEN_FIT).performActionDisplayedImageWrapper(asDisplay);;
					
					return undo;
			}	
			
	}.createJMenuItem("Make slide size"));
		
		
		
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public AbstractUndoableEdit2  performAction() {
				FigureScaler scaler = new FigureScaler(false);
				CombinedEdit undo = scaler.scaleFigure(item, getScaleFromDialog(), item.getPanelLayout().getReferenceLocation());
				undo.addEditToList(new CanvasAutoResize(false).performUndoableAction(mouseE.getAsDisplay()));
				item.updateDisplay();
				
				FigureScaler.scaleMessages(item.getParentLayer());
				return undo;
				}

			
	}.createJMenuItem("Scale"));
		
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public AbstractUndoableEdit2  performAction() {
				FigureScaler scaler = new FigureScaler(false);
					double factor=scaler.getSlideSizeScale(item,  getMemoryOfMouseEvent().getAsDisplay().getPageSize());
					if(factor==1)
						return null;
					CombinedEdit undo = scaler.scaleFigure(item, factor, item.getPanelLayout().getReferenceLocation());
					item.updateDisplay();
					
					FigureScaler.scaleMessages(item.getParentLayer());
					return undo;
			}	
	}.createJMenuItem("Scale to worksheet size"));
		
	}

	/**creates a menu item that would allow the user to rescale the figures 
	 * @return
	 */
	public JMenuItem createRescaleMenuItem() {
		return new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showFigureRescaleDialog();
				}
	}.createJMenuItem("Scale objects and Reset scale");
	}
	
	
	public  double getScaleFromDialog() {
		return getScaleFromDialog("Scale Layout", null, 2);
	}
	
	/**
	 shows a dialog that allows the user to both reset the scale for the sources images, and scale everything else
	 */
	public void showFigureRescaleDialog() {
		FigureScaler scaler = new FigureScaler(true);
		CombinedEdit undo = scaler.scaleFigure(item, getScaleFromDialog(), item.getPanelLayout().getReferenceLocation());
		item.updateDisplay();
		addEdit(undo);
		FigureScaler.scaleMessages(item.getParentLayer());
	}

	public static double getScaleFromDialog(String name, String note, double factor) {
		StandardDialog sd = new StandardDialog(name, true) ;
		sd.add("scale", new NumberInputPanel("Scale Factor", factor, 4));
		if (note!=null)sd.add("info",new InfoDisplayPanel("If scale is not 1,", note));
		sd.setModal(true);
		sd.showDialog();
		
		factor=sd.getNumber("scale");
		return factor;
	}
	

}
