package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelManager;
import objectDialogs.PanelStackDisplayOptions;
import panelGUI.PanelListDisplayGUI;
import standardDialog.StandardDialog;
import undo.CompoundEdit2;
import undo.PanelManagerUndo;

public class PanelMenuForMultiChannel extends MenuForChannelLabelMultiChannel {

	
	private PanelManager panelManager;

	public PanelMenuForMultiChannel(String name, MultichannelDisplayLayer panel,
			PanelList list, PanelManager panMan) {
		super(name, panel, list,  panel.getChannelLabelManager());
		panelManager=panMan;
		if(panMan==null) {panMan=panel.getPanelManager();}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void generateLabelMenuItems() {
		
		recreateChannelUseMenuItem();
		showPanelListItem() ;
		addChangePPIMenuItem();
		
		
		JMenu expert=new JMenu("Expert Options");
		createImagePanelMenuItem(expert);
		create1ChannelPanelMenuItem(expert);
		createEliminatePanelMenuItem(expert) ;
	//	create1MergePanelMenuItem();
		recreatePanelsMenuItem(expert);
		this.add(expert);
		
		
	}

	public void addChangePPIMenuItem() {
		add(new ObjectAction<PanelManager>(panelManager) {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImagePanelGraphic panel = panelManager.getPanelList().getPanels().get(0).getPanelGraphic();
			double ppi = panel.getQuickfiguresPPI();
			double newppi=StandardDialog.getNumberFromUser("Input PPI ", ppi);
			addUndo(
					panelManager.changePPI(newppi)
					);
			}
			
		}.createJMenuItem("Change Panel PPI (Scale if needed)"));
	}
	
	SmartMenuItem2 createImagePanelMenuItem(JMenu  thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Image Panels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				stack.addAllCandF(display.getMultichanalWrapper());
				panelManager.generatePanelGraphicsFor(stack);
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createEliminatePanelMenuItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Eliminate Image Panels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction(){
				undo=PanelManagerUndo.createFor(panelManager);
				panelManager.eliminatePanels(stack);
				panelManager.updateDisplay();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	
	SmartMenuItem2 create1MergePanelMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Create 1 Merge Panel") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				panelManager.addSingleMergePanel(stack);
				panelManager.updateDisplay();
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 create1ChannelPanelMenuItem(JMenu t) {
		SmartMenuItem2 out=new SmartMenuItem2("Create New Image Panel") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(panelManager);
				PanelListElement panelp = panelManager.addSingleChannelPanel(stack);
				panelManager.putSingleElementOntoGrid(panelp, true);
				panelManager.updateDisplay();
			}
			
		};
		t.add(out);
		return out;
	}
	
	
	SmartMenuItem2 recreateChannelUseMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Channel Use") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				new PanelStackDisplayOptions(display, stack, panelManager,false).showDialog();;
				
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 recreatePanelsMenuItem(JMenu j) {
		SmartMenuItem2 out=new SmartMenuItem2("Separately Recreate Panels") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.showOptionsThenRegeneratePanelGraphics();
			}
			
		};
		j.add(out);
		return out;
	}
	
	SmartMenuItem2 showPanelListItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Advanced Channel and Frame Use") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				PanelListDisplayGUI distpla = new PanelListDisplayGUI( panelManager, man);
				
				distpla.setVisible(true);
			}
			
		};
		this.add(out);
		return out;
	}
	
}
