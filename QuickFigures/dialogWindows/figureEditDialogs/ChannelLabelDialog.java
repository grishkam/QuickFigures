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
 * Version: 2022.2
 */
package figureEditDialogs;

import java.awt.GridBagConstraints;

import javax.swing.JTabbedPane;

import channelLabels.ChannelLabelTextGraphic;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.CanvasOptions;
import logging.IssueLog;
import objectDialogs.ComplexTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;

/**A special text item dialog with additional features for channel labels*/
public class ChannelLabelDialog extends ComplexTextGraphicSwingDialog {

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
		return TextLineDialogForChannelLabel.createMultiLineDialog(getChannelLabel() .getChanEntries(), getChannelLabel().getChannelLabelProperties(), createSwingDialogListener());
	}
	protected StandardDialogListener createSwingDialogListener() {
		return new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				getChannelLabel().setParaGraphToChannels();
				GraphicLayer layer = getChannelLabel().getParentLayer();
				if (layer!=null)for(ZoomableGraphic z:layer.getItemArray() ) {
					if (z instanceof ChannelLabelTextGraphic) ((ChannelLabelTextGraphic) z).setParaGraphToChannels();
				}
				onListenerLotification(event);
				if (CanvasOptions.current.resizeCanvasAfterEdit)CurrentFigureSet.canvasResize();
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
			c.gridy=gridPositionY;
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
			
			gridPositionY+=4;
			
			
		
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
