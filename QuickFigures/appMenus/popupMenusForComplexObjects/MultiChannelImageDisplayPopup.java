package popupMenusForComplexObjects;


import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import fLexibleUIKit.ObjectAction;
import genericMontageKit.PanelList;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import multiChannelFigureUI.ChannelSwapperToolBit2;
import ultilInputOutput.FileChoiceUtil;

public class MultiChannelImageDisplayPopup extends SmartPopupJMenu implements
		PopupMenuSupplier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list;
	private MultichannelDisplayLayer panel;
	private ImagePanelGraphic clickedPanel;

	public MultiChannelImageDisplayPopup(MultichannelDisplayLayer panel, PanelList list, ImagePanelGraphic img) {
		this.list=list;
		this.panel=panel;
			this.clickedPanel=img;	
		addMenus(this, panel, list);
		
	}

	public void addMenus(SmartPopupJMenu thi, MultichannelDisplayLayer panel, PanelList list) {
		thi.add(new ImageMenuForMultiChannel("Source Image", panel, list) );
		thi.add(new PanelMenuForMultiChannel("Image Panels", panel, list, panel.getPanelManager()));
		thi.add(new MenuForChannelLabelMultiChannel("Channel Labels", panel, list, panel.getChannelLabelManager()));
		addChannelMenu(thi);
	}
	
	public JMenu[] addMenus(JMenu thi) {
		JMenu[] output=new JMenu[3];
		output[0]=new ImageMenuForMultiChannel("Source Image", panel, list) ;
		thi.add(output[0]);
		output[1]=new PanelMenuForMultiChannel("Image Panels", panel, list, panel.getPanelManager());
		thi.add(output[1]);
		addChannelLabelMenu(thi);
		
		addChannelMenu(thi);
		addRemoveImage(thi);
		return output;
		
	}

	/**creates a menu item to remove the image*/
	protected void addRemoveImage(JMenu thi) {
		ObjectAction<MultichannelDisplayLayer> act = new ObjectAction<MultichannelDisplayLayer>(panel) {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.getParentLayer() instanceof FigureOrganizingLayerPane) {
					FigureOrganizingLayerPane f=(FigureOrganizingLayerPane) panel.getParentLayer() ;
					if(f.getAllSourceStacks().size()==1) {
						FileChoiceUtil.yesOrNo("Figure must contain at least one image. Will not remove last image. Understood?");
						return;
					}
				}
				boolean b=FileChoiceUtil.yesOrNo("Are you sure you want to remove this image from figure?");
				
				if (b)panel.getParentLayer().remove(panel);
				
			}};
		thi.add(act.createJMenuItem("Remove From Figure"));
	}

	public void addChannelMenu(Container thi) {
		JMenu oneMore = new SmartJMenu("Channels");
		ChannelSwapperToolBit2 b = new ChannelSwapperToolBit2(panel, clickedPanel);
		b.workOn=0;
		b.addChannelRelevantMenuItems(oneMore, true);
		thi.add(oneMore);
	}

	public void addChannelLabelMenu(JMenu thi) {
		thi.add(createChanLabelMenu());
	}

	public MenuForChannelLabelMultiChannel createChanLabelMenu() {
		return new MenuForChannelLabelMultiChannel("Channel Labels", panel, list, panel.getChannelLabelManager());
	}
	
	@Override
	public JPopupMenu getJPopup() {
		return this;
	}



}
