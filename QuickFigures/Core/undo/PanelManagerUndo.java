package undo;

import java.util.ArrayList;

import channelMerging.PanelStackDisplay;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_FigureSpecific.PanelManager;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;

public class PanelManagerUndo extends CompoundEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list;
	private double iPS;
	private double fPS;
	private ArrayList<PanelListElement> iPanels=new ArrayList<PanelListElement>();
	private ArrayList<PanelListElement> fPanels=new ArrayList<PanelListElement>();
	
	public PanelManagerUndo(PanelList list) {
		this.list=list;
		iPS=list.getPanelLevelScale();
		iPanels=new ArrayList<PanelListElement>();
		iPanels.addAll(list.getPanels());
		for(PanelListElement i:iPanels) {
			ListElementUndo edit = new ListElementUndo(i);
			addEditToList(edit);
			if(edit.iBar!=null) {addEditToList(edit.iBar.provideUndoForDialog());}
		}
		addEditToList(new ChannelUseChangeUndo(list.getChannelUseInstructions()));
		
	}
	
	public void establishFinalState() {
		super.establishFinalState();
		fPS=list.getPanelLevelScale();
		fPanels=new ArrayList<PanelListElement>();
		fPanels.addAll(list.getPanels());
		
	}
	public void redo() {
		super.redo();
		list.setPanelLevelScale(fPS);
		list.eliminateAllPanels();
		list.getPanels().addAll(fPanels);
	}
	
	public void undo() {
		super.undo();
		list.setPanelLevelScale(iPS);
		list.eliminateAllPanels();
		list.getPanels().addAll(iPanels);
	}

	
	public static CompoundEdit2 createFor(PanelManager pm) {
		CompoundEdit2 output = new CompoundEdit2();
		output.addEditToList(new PanelManagerUndo(pm.getPanelList()));
		output.addEditToList(new UndoLayerContentChange(pm.getDisplay()));
		output.addEditToList(new UndoLayerContentChange(pm.getLayer()));
		MontageLayoutGraphic layout = pm.getGridLayout();layout.generateCurrentImageWrapper();
		output.addEditToList(new UndoLayoutEdit(layout));
		return output;
	}
	
	public static CompoundEdit2 createFor(PanelStackDisplay pm) {
		CompoundEdit2 output = createFor(pm.getPanelManager());
		output.addEditToList(new PreprocessChangeUndo(pm));
		return output;
	}
	
	public static CompoundEdit2 createForMany(ArrayList<? extends PanelStackDisplay> all) {
		CompoundEdit2 output = new CompoundEdit2();
		for(PanelStackDisplay a:all) {
			output.addEditToList(createFor(a));
		}
		return output;
	}
	
	public class ListElementUndo extends AbstractUndoableEdit2  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PanelListElement iElement;
		private PanelListElement fElement;
		private PanelListElement panel;
		private BarGraphic iBar;
		private BarGraphic fBar;
		
		
		
		public ListElementUndo(PanelListElement p) {
			this.panel=p;
			iElement=p.copy();
			iBar=p.getScaleBar();
			
		}
		public void establishFinalState() {
			fElement=panel.copy();
			fBar=panel.getScaleBar();
		}
		public void redo() {
			fElement.giveObjectsAndSettingsTo(panel);
			if (iElement.getScaleBar()!=fBar)iElement.setScaleBar(fBar);
		}
		
		public void undo() {
			iElement.giveObjectsAndSettingsTo(panel);
			if (iElement.getScaleBar()!=iBar)iElement.setScaleBar(iBar);
				}
	}
	
}
