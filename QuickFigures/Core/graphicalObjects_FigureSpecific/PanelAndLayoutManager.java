package graphicalObjects_FigureSpecific;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import channelLabels.ChannelLabelManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import objectDialogs.PanelStackDisplayOptions;
import popupMenusForComplexObjects.MenuForChannelLabelMultiChannel;
import popupMenusForComplexObjects.PanelMenuForMultiChannel;

/**handles menus and dialogs
 */
class PanelAndLayoutManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChannelLabelManager labels;
	private MontageLayoutGraphic layout;
	private PanelManager panelManager;
	private GraphicLayer layer;
	
	
	

	PanelAndLayoutManager(ChannelLabelManager labelsMan, PanelManager stack,
			GraphicLayer dumpingLayer, MontageLayoutGraphic layout) {
		this.labels=labelsMan;
			this.layout=layout;
			this.panelManager=stack;
			this.layer=dumpingLayer;
		
	}
	
	
	void showChannelUseOption() {
		new PanelStackDisplayOptions(panelManager.getDisplay(),panelManager.getStack(), panelManager, false);
		
	}
	
	
	
	ArrayList<JMenuItem> getPopupItems() {
		ArrayList<JMenuItem> output=new ArrayList<JMenuItem>();
		output.add(new PanelMenuForMultiChannel("Image Panels",  panelManager.getDisplay(), panelManager.getStack(),panelManager));
		output.add(new MenuForChannelLabelMultiChannel("Channel Labels", panelManager.getDisplay(),  panelManager.getStack(), labels));
		
		return output;
	}

	

}
