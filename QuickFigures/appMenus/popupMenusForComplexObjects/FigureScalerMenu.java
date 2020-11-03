package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_FigureSpecific.FigureScaler;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import imageMenu.CanvasAutoResize;
import menuUtil.SmartJMenu;
import standardDialog.InfoDisplayPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import undo.CompoundEdit2;

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
	
	public void addEdit(CompoundEdit2 undo) {
		if(mouseE!=null)
			{
			undo.addEditToList(new CanvasAutoResize().performUndoableAction(mouseE.getAsDisplay()));
			}
		addUndo(undo);
		
	}
	
	public void createItems() {
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FigureScaler scaler = new FigureScaler(true);
				CompoundEdit2 undo = scaler.scaleFigure(item, getScaleFromDialog(), item.getPanelLayout().getReferenceLocation());
				item.updateDisplay();
				addEdit(undo);
				scaler.scaleWarnings(item.getParentLayer());
				}
	}.createJMenuItem("Scale (Same PPI, Bilinear Interpolation if needed)"));
		
		//JMenu jj = new JMenu("Advanced Scaling");
		
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FigureScaler scaler = new FigureScaler(false);
					double factor=scaler.getSlideSizeScale(item);
					
					CompoundEdit2 undo = scaler.scaleFigure(item, factor, item.getPanelLayout().getReferenceLocation());
					item.updateDisplay();
					addEdit(undo);
					scaler.scaleWarnings(item.getParentLayer());
			}	
	}.createJMenuItem("Scale to Slide Size (PPI changes, No Interpolation)"));
		
		
		
		add(new ObjectAction<PanelLayoutGraphic>(item) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FigureScaler scaler = new FigureScaler(false);
				CompoundEdit2 undo = scaler.scaleFigure(item, getScaleFromDialog(), item.getPanelLayout().getReferenceLocation());
				undo.addEditToList(new CanvasAutoResize().performUndoableAction(mouseE.getAsDisplay()));
				item.updateDisplay();
				addEdit(undo);
				scaler.scaleWarnings(item.getParentLayer());
				}

			
	}.createJMenuItem("Scale (PPI changes, No Interpolation)"));
		
		//this.add(jj);
		
		
	}
	public  double getScaleFromDialog() {
		return getScaleFromDialog("Scale Layout", null, 2);
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
