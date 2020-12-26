/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package multiChannelFigureUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.MultiChannelImage;
import genericMontageLayoutToolKit.GeneralLayoutToolIcon;
import layout.PanelLayout;
import layout.plasticPanels.PlasticPanelLayout;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiTextGraphicSwingDialog;
import popupMenusForComplexObjects.MenuForChannelLabelMultiChannel;

public class BasicChannelLabelTool extends BasicImagePanelTool implements ActionListener{
	
	
	static String renameCommand="Rename";
	static String renameCommand2="RelabelChannels";
	static String alterAll="Alter labels Channels";
	


				
		/**If called after a mouse press on a multichannel. Does not get called when a popup menu is triggered*/
		protected void afterMousePress(MultiChannelImage mw, int chan1) {
					
					
		ChannelLabelManager lm=getPressedChannelLabelManager();
		
		
		if (stackSlicePressed==null) return; 
		
		if (stackSlicePressed.isTheMerge() && !shiftDown()) {
			lm.showChannelLabelPropDialog();
			
		} else lm.nameChannels(stackSlicePressed.getChannelEntries());
		
	}
	
	
	
	
	
	public JPopupMenu createJPopup() {
		
		MenuForChannelLabelMultiChannel menu = new MenuForChannelLabelMultiChannel("All Channel Labels", presseddisplay, this.getPressedPanelManager().getPanelList(), this.getPressedChannelLabelManager());
		JPopupMenu output = new SmartPopupJMenu();
		
		addChanlabelsMenuItems(output);
		
		 output.add(menu);
		return output;
	}





	protected void addChanlabelsMenuItems(Container output) {
		addButtonToMenu(output, "Alter Label Text", renameCommand2);
		
		addButtonToMenu(output, "Edit All Channel Labels", alterAll);
		
		addButtonToMenu(output, "Rename Image Channel/Stack Slice", renameCommand);
	}
	
	
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw) {
		
		applyReleaseActionToMultiChannel(mw, 1,1);
	}
	
	protected void showthePopup(Component source, int x, int y) {
		createJPopup().show(source, x, y);
		
	}
	
	@Override
	public String getToolTip() {
	
			return "Change Channel Labels and Slice Labels";
		}
	
	


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(renameCommand)) {
		
		new StackSliceNamingDialog().showNamingDialog(stackSlicePressed.originalIndices, this.presseddisplay.getMultiChannelImage());
		//presseddisplay.updatePanels();
		
		}
		
		if (arg0.getActionCommand().equals(renameCommand2)) {
			
			ChannelLabelManager lm=getPressedChannelLabelManager();
			lm.nameChannels(stackSlicePressed.getChannelEntries());
		
				
			
			}
		
		
if (arg0.getActionCommand().equals(alterAll)) {
			
			ArrayList<ChannelLabelTextGraphic> labels = this.getPressedPanelManager().getPanelList().getChannelLabels();
			MultiTextGraphicSwingDialog mt = new MultiTextGraphicSwingDialog( labels, true);
			mt.showDialog();
			
			}
	}
	
	
	{this.setIconSet(new  ChanelLabelIcon(0).generateIconSet());}
	public static class ChanelLabelIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public ChanelLabelIcon(int type) {
			super(type);
			super.paintBoundry=false;
			super.panelColor=new Color[] {Color.red, Color.green, Color.blue};
			
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			int xLoc=3;
			int yLoc=3;
			int size=11;
			Rectangle r1 = new Rectangle(xLoc, yLoc, size, size);
			Rectangle r2 = new Rectangle(xLoc+3, yLoc+ 4, size, size);
			Rectangle r3 = new Rectangle(xLoc+6, yLoc+8, size, size);
			
			PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2, r3);
			
			return layout2;
		}
		
		/**
		 alters the color for the stroke of the panels
		 */
		protected Color derivePanelStrokeColor(Color panelColor2) {
			return panelColor2.darker().darker();
		}
		
		/**given the base color of a panel, returns the fill color used to give the panel a light tint
		 * @param panelColor2
		 * @return
		 */
		protected Color deriveFillColor(Color panelColor2) {
			return Color.lightGray;
		}
		
		/**draws the given panel of the icon with some text
		 * @param g2d
		 * @param p
		 * @param count
		 */
		protected void drawPanel(Graphics2D g2d, Rectangle2D p, int count) {
			super.drawPanel(g2d, p, count);
			String str = "B";
			if(type==ROLLOVER_ICON_TYPE)
				str="C";
			if (count!=2) 
				str="A";
			
			drawLabelOnPanel(g2d, p, str);
		}

		
		
		/**
		 * @param type
		 * @return
		 */
		@Override
		protected
		GeneralLayoutToolIcon generateAnother(int type) {
			return new ChanelLabelIcon(type);
		}
		
		public GeneralLayoutToolIcon copy(int type) {
			GeneralLayoutToolIcon another = generateAnother(type);
			return another;
		}
	}
	
}
