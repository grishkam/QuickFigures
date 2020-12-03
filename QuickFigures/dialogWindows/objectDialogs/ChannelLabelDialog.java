package objectDialogs;

import java.awt.GridBagConstraints;

import javax.swing.JTabbedPane;

import channelLabels.ChannelLabelTextGraphic;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.SwingDialogListener;

public class ChannelLabelDialog extends ComplexTextGraphicSwingDialog {

	//private ChannelLabelTextGraphic chanLabel;
	boolean doMergeLabelMenu=false;
	private JTabbedPane theTabs;

	
	public ChannelLabelDialog(ChannelLabelTextGraphic t, boolean mergeLabMenu) {
		super(t);
		doMergeLabelMenu=mergeLabMenu;
		super.undoableEdit=t.provideUndoForDialog();
	}
	
	public ChannelLabelDialog(ChannelLabelTextGraphic t) {
		
		super(t);
		
	}
	
	ChannelLabelTextGraphic getChannelLabel() {
		if (super.ct instanceof ChannelLabelTextGraphic) {
			return (ChannelLabelTextGraphic) ct;
		}
		return null;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	StandardDialog createPanelForLines() {
		
		if (getChannelLabel() ==null) {IssueLog.log("null channel label issue");}
		return TextLineDialogForChenLabel.createMultiLineDialog(getChannelLabel() .getChanEntries(), getChannelLabel().getChannelLabelProperties(), createSwingDialogListener());
	}
	protected SwingDialogListener createSwingDialogListener() {
		return new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				getChannelLabel().setParaGraphToChannels();
				GraphicLayer layer = getChannelLabel().getParentLayer();
				if (layer!=null)for(ZoomableGraphic z:layer.getItemArray() ) {
					if (z instanceof ChannelLabelTextGraphic) ((ChannelLabelTextGraphic) z).setParaGraphToChannels();
				}
				onListenerLotification(event);
				CurrentFigureSet.canvasResize();
			}};
	}
	
	protected ChannelLabelPropertiesDialog mergeMenu() {
		ChannelLabelPropertiesDialog dia = new  ChannelLabelPropertiesDialog(getChannelLabel().getChannelLabelProperties());
		dia.addDialogListener(createSwingDialogListener());
		return dia;
		
	}
	
	/**Adds tabs that are useful for channel labels*/
	protected void addLineTabs() {
		if (getChannelLabel() ==null) return;
		try {
			JTabbedPane tabsfull = createPanelForLines().removeOptionsTab();
			JTabbedPane tab = mergeMenu().removeOptionsTab();
			//tabsfull.addObjectEditListener(this);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx=gx;
			c.gridy=gy;
			c.gridheight=4;
			c.gridwidth=6;
			if (this.getChannelLabel().isThisMergeLabel()||doMergeLabelMenu) {
				JTabbedPane p = new JTabbedPane();
				p.addTab("Merge Label Options", tab);
				p.addTab("View Each channels Text", tabsfull);
				this.add(p, c);
				setTheTabs(p);
			}else
			{
				getOptionDisplayTabs().addTab("View Channel Text", tabsfull);
			//	this.add(tabsfull, c);
			setTheTabs(tabsfull);
			}
			
			gy+=4;
			
			
		
		} catch (Throwable t) {
			t.printStackTrace();}
	}

	public JTabbedPane getTheTabs() {
		return theTabs;
	}

	public void setTheTabs(JTabbedPane theTabs) {
		this.theTabs = theTabs;
	}

}
