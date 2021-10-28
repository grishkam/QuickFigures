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
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package layout.dividerLayout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import handles.SmartHandle;
import layout.dividerLayout.DividedPanelLayout.LayoutDivider;
import menuUtil.SmartPopupJMenu;
import standardDialog.numbers.NumberInputPanel;

/**A handle used for moving the dividers of a layout*/
public class DividerHandle extends SmartHandle  implements ActionListener {

	public LayoutDivider divider;
	private DividedPanelLayoutGraphic layoutG;
	
	int x,y;
	private int width;
	private int height;
	Rectangle2D shapeOfdivider;
	private DividerHandle(Rectangle2D r, Color c, DividedPanelLayoutGraphic layourGraphic) {
		
		this.x=(int) r.getX();
		this.y=(int) r.getY();
		this.width=(int) r.getWidth();
		this.height=(int) r.getHeight();
		this.setHandleColor(c);
		this.layoutG=layourGraphic;
	}

	public int getHandleNumber() {
		return layoutG.smartl.indexOf(this);
	}
	
	private DividerHandle(Rectangle2D.Double rect, Color pink, CordinateConverter cords, DividedPanelLayoutGraphic layourGraphic) {
		this(cords.getAffineTransform().createTransformedShape(rect).getBounds(), pink, layourGraphic);
		
		if(this.width<6)this.width=6;
		if(this.height<6)this.height=6;
	}


	public DividerHandle(LayoutDivider div, Color orange, CordinateConverter cords,DividedPanelLayoutGraphic layourGraphic) {
		this(div.rect, orange, cords,layourGraphic);
		this.divider=div;
		shapeOfdivider=div.rect;
	}
	
	 


	public void draw(Graphics2D graphics, CordinateConverter cords) { 
	
		super.drawHandleShape(graphics, new Rectangle2D.Double(x,y,width, height));
	}
	
	
	public JPopupMenu getJPopup() {
		SmartPopupJMenu out = new SmartPopupJMenu();
		JMenuItem mi = new JMenuItem("Remove Divider");
		mi.addActionListener(this);
		out.add(mi);
		return out;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		divider.parent.removeDivider(divider);
		
		layoutG.getPanelLayout().resetPtsPanels();
		layoutG.updateDisplay();
		
	}
	
public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
	
		if(canvasMouseEventWrapper.clickCount()==2) {
			double pos = NumberInputPanel.getNumber("Divider Position", divider.getPosition(), 1, true, null);
			divider.setPosition(pos);
			layoutG.getPanelLayout().resetPtsPanels();
			layoutG.updateDisplay();
		}
		
	}
	
}
