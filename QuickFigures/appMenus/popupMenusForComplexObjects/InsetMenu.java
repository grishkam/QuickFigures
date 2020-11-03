package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import graphicalObjects_FigureSpecific.InsetDefiner;
import graphicalObjects_FigureSpecific.InsetLayoutDialog;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import standardDialog.DialogItemChangeEvent;
import standardDialog.SwingDialogListener;
import menuUtil.PopupMenuSupplier;

public class InsetMenu extends SmartPopupJMenu implements ActionListener,
		PopupMenuSupplier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InsetDefiner inset;
	private ScaleFigureDialog ss;
	
	void createMenuItem(String st, String ac) {
		JMenuItem g=new JMenuItem(st);
		g.setActionCommand(ac);
		g.addActionListener(this);
		this.add(g);
	}

	public InsetMenu(InsetDefiner inset) {
		this.inset=inset;
		createMenuItem("Update Panels", "panelup");
	//	createMenuItem("Create Primary RGB mode Inset", "i1");
	//	createMenuItem("Create Additional RGB mode Insets", "i2");
		createMenuItem("Create MultiChannel Display Insets", "i3");
		createMenuItem("Remove Insets", "i4");
		createMenuItem("Remove", "i6");
		createMenuItem("Scale Options", "i5");
		createMenuItem("Redo Inset Layout", "inset layout");
		//createMenuItem("Update Panels", "panelup");
	}

	@Override
	public JPopupMenu getJPopup() {
		// TODO Auto-generated method stub
		return this;
	}

	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String c=arg0.getActionCommand();
		if (c.equals("panelup")) {inset.updateImagePanels();;}
		if (c.equals("i1")) {inset.createImageInsetDisplay();}
		if (c.equals("i2")) {inset.createChannelInsets();}
		if (c.equals("i3")) {
			inset.createMultiChannelInsets();
			}
		if (c.equals("i4")) {
			inset.removePanels();
			}
		if (c.equals("i6")) {
			inset.removePanels();
			inset.getParentLayer().remove(inset);
			}
		
		if (c.equals("i5")) {
			
			if(inset.personalGraphic!=null)
			{
				showFigureScalerMenu();
			}
			}
		
		if (c.equals("inset layout")) {
			if (inset instanceof PanelGraphicInsetDef ) {
				 PanelGraphicInsetDef pgInset=(PanelGraphicInsetDef) inset;
				 pgInset.previosInsetLayout.practicalSize=true;
				InsetLayoutDialog dialog = new InsetLayoutDialog(pgInset.previosInsetLayout);
				dialog.setTargetInset(pgInset);
				dialog.showDialog();
				
			}
		
			}
		
		inset.updateDisplay();
	}

	public void showFigureScalerMenu() {
		ss=new ScaleFigureDialog(inset.personalGraphic, inset.getPanelManager());
		IssueLog.log("Scale starts as "+inset.getBilinearScale());
		ss.addDialogListener(new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				if (inset instanceof PanelGraphicInsetDef) {
					PanelGraphicInsetDef inset2=(PanelGraphicInsetDef) inset;
				IssueLog.log("Scale is "+inset.getBilinearScale());
				double scale = ss.getNumber("scale");
				inset.setBilinearScale(scale);
				inset.updateImagePanels();
				MontageLayoutGraphic layout = inset.personalGraphic;
				layout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(layout.getPanelLayout());
				}
			//	inset.setBilinearScale(scale);
			}});
		ss.showDialog();
	}
	
	public void showScaleDialog() {
		new ScaleFigureDialog(inset.personalGraphic, inset.getPanelManager(), inset).showDialog();
		
		
	}
}
