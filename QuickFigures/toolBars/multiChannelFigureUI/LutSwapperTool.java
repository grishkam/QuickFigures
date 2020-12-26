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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import channelMerging.MultiChannelImage;
import genericMontageLayoutToolKit.GeneralLayoutToolIcon;
import layout.PanelLayout;
import layout.plasticPanels.PlasticPanelLayout;
import menuUtil.SmartPopupJMenu;
import standardDialog.colors.ColorInputEvent;
import standardDialog.colors.ColorInputListener;
import utilityClassesForObjects.RectangleEdges;

/**A tool for changing the channel colors of an image*/
public class LutSwapperTool extends BasicImagePanelTool implements ColorInputListener {
	
	@Override
	public void applyReleaseActionToMultiChannel(MultiChannelImage mw, int chan1, int chan2) {
		if (mw==null) return;
		mw.getChannelSwapper().swapChannelLuts(chan1, chan2);
	}
	
	{
	setIconSet(new  LutIcon(0).generateIconSet());}
	
	@Override
	public void ColorChanged(ColorInputEvent fie) {
		setTheColor(fie.getColor());
	}

	private void setTheColor(Color color) {
		
		for(MultiChannelImage ic: super.getAllWrappers()) {
			int chan=getBestMatchToChannel(ic, getRealNameOfPressedChannel(), getPressChannelOfMultichannel());
			ic.getChannelSwapper().setChannelColor(color, chan);
		}
	
		updateAllDisplays();
		this.getImageClicked().updateDisplay();
	};  
	
	protected void showthePopup(Component source, int x, int y) {
		SmartPopupJMenu pop = new SmartPopupJMenu();
		ChannelColorJMenu men = ChannelColorJMenu.getStandardColorJMenu(this);
		pop.add(men);
		pop.show(source, x, y);
		
		
	}

	
	
	
	@Override
	public String getToolTip() {
			
			return "Change Channel Colors";
		}
	
	

	{this.setIconSet(new  LutIcon(0).generateIconSet());}
	public static class LutIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public LutIcon(int type) {
			super(type);
			super.paintBoundry=false;
			panelColor=new Color[] {Color.blue, Color.green, Color.blue};
			if (type!=NORMAL_ICON_TYPE)
				panelColor=new Color[] {Color.blue, Color.red, Color.blue};
			this.arrowColor=Color.red;
			this.arrowthickNess=3;
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			int xLoc=3;
			int yLoc=3;
			int size=14;
			Rectangle r1 = new Rectangle(xLoc, yLoc, size, size);
			Rectangle r2 = new Rectangle(xLoc+3, yLoc+ 4, size, size);
			
			PlasticPanelLayout layout2 = new PlasticPanelLayout(r1, r2);
			
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
			return panelColor2;
		}
		
		/**
		 
		 */
		@Override
		protected
		GeneralLayoutToolIcon generateAnother(int type) {
			return new LutIcon(type);
		}
		
		public GeneralLayoutToolIcon copy(int type) {
			GeneralLayoutToolIcon another = generateAnother(type);
			return another;
		}
		
		/**draws the given panel of the icon
		 */
		protected void drawPanel(Graphics2D g2d, Rectangle2D p, int count) {
			super.drawPanel(g2d, p, count);
			if (count==1) {
				super.paintArrow(g2d, (int) p.getMaxX(), (int)p.getMinY()-2, 8, RectangleEdges.LOWER_LEFT, 1);
			}
		}
	}
}
