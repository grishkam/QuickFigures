package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import channelLabels.ChannelLabelManager;
import genericMontageKit.PanelList;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

public class MenuForChannelLabelMultiChannel extends JMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MultichannelDisplayLayer display;
	protected PanelList stack;
	protected ChannelLabelManager man;
	
	

	public MenuForChannelLabelMultiChannel(String name, MultichannelDisplayLayer  panel, PanelList list, ChannelLabelManager man) {
		this.setText(name);
		this.setName(name);
		display=panel;
		stack=list;
		this.man=man;
		generateLabelMenuItems();
	}
	
	public void generateLabelMenuItems() {
		createMergeMenuItem() ;
	
		createAllLabelMenuItem();
		JMenu add=new JMenu("Add/Replace");
		JMenu rem=new JMenu("Remove");
		create1ChannelLabelItem(add);
		create1MergeLabelItem(add) ;
		createGenerateChannelLabelItem(add);
		createGenerateChannelLabelItem2(add);
	;
		createEliminateChannelLabelItem(rem) ;
		
		
		//createCopySavedChannelLabelItem();
		JMenu expert = new JMenu("Expert Options");
		createResetNameItem(expert);
		createNameChannelLabelItem(expert);
		expert.add(add);
		expert.add(rem);
		this.add(expert);
	}
	
	public SmartMenuItem2 createAllLabelMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Edit All Channel Labels") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				man.completeMenu();
			}
			
		};
		this.add(out);
		return out;
	}
	
	
	
	SmartMenuItem2 createMergeMenuItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Merge Label Menu") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				
				man.showChannelLabelPropDialog();
				
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 create1MergeLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Add 1 Merge Label") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				man.addSingleMergeLabel();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 create1ChannelLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Add 1 Channel Label") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				man.addSingleChannelLabel();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createEliminateChannelLabelItem(JMenu j) {
		SmartMenuItem2 out=new SmartMenuItem2("Eliminate Channel Labels") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onAction(){
			undo=man.eliminateChanLabels();
			}
		};
		j.add(out);
		return out;
	}
	
	SmartMenuItem2 createCopySavedChannelLabelItem() {
		SmartMenuItem2 out=new SmartMenuItem2("Load Label Properties From Saved") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.setLabalPropertiesToSaved();
			}
			
		};
		this.add(out);
		return out;
	}
	
	SmartMenuItem2 createGenerateChannelLabelItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Channel Labels") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				man.generateChannelLabels();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createGenerateChannelLabelItem2(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Generate New Channel Labels (first slice only)") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				undo=PanelManagerUndo.createFor(display);
				man.generateChannelLabels2();
			}
			
		};
		thi.add(out);
		return out;
	}
	
	SmartMenuItem2 createNameChannelLabelItem(JMenu th) {
		SmartMenuItem2 out=new SmartMenuItem2("See Channel Labels") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				man.nameChannels();
			}
			
		};
		th.add(out);
		return out;
	}
	
	SmartMenuItem2 createResetNameItem(JMenu thi) {
		SmartMenuItem2 out=new SmartMenuItem2("Reset Channel Names") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onAction() {
				display.getMultichanalWrapper().renameBasedOnRealChannelName();;
				display.updatePanelsAndLabelsFromSource();
			}
			
		};
		thi.add(out);
		return out;
	}
	

	 protected abstract class SmartMenuItem2 extends JMenuItem implements ActionListener {
		 
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected CombinedEdit undo;

		public SmartMenuItem2(String name) {
			 super(name);
			 this.addActionListener(this);
			 
		 }
		public void addUndo(AbstractUndoableEdit2 e) {
			new CurrentFigureSet().addUndo(e);
		}
		
		public void actionPerformed(ActionEvent e) {
			this.onAction();
			if(undo==null) return;
			undo.establishFinalState();
			addUndo(undo);
		}
		abstract void onAction() ;
		 
	 }

}
