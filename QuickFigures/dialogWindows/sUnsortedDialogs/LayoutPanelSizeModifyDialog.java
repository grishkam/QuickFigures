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
 * Version: 2021.1
 */
package sUnsortedDialogs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import layout.PanelLayout;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.numbers.NumberInputPanel;

/**A dialog for changing the size of layout panels*/
public class LayoutPanelSizeModifyDialog extends GraphicItemOptionsDialog implements MouseListener{

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic graphic;
	private PanelLayout layout;
	int panelnum=1;
	int type=1;
	
	
	{this.addMouseListener(this);}
	public LayoutPanelSizeModifyDialog(PanelLayoutGraphic g, PanelLayout p) {
		graphic=g;
		layout=p;
	}

	public PanelLayoutGraphic getLayoutGraphic() {
		return graphic;
	}
	public PanelLayout getPanelLayout() {
		return layout;
	}
	
	public void showPaneldimDialog(int panelnum) {
		this.panelnum=panelnum;
		double width=this.getPanelLayout().getPanel(panelnum).getWidth();
		double height= this.getPanelLayout().getPanel(panelnum).getHeight();
		this.add("pwidth", new NumberInputPanel("Panel Width", width));
		this.add("pheight", new NumberInputPanel("Panel Height", height));
		
		this.add("swidth", new NumberInputPanel("Standard Panel Width", getPanelLayout().getStandardPanelWidth()));
		this.add("sheight", new NumberInputPanel("Standard Panel Height", getPanelLayout().getStandardPanelHeight()));
		this.add("sw", new BooleanInputPanel("Use Standard Panel Width", this.getPanelLayout().getStandardPanelWidth()==width ));
		this.add("sh", new BooleanInputPanel("Use Standard Panel Height", this.getPanelLayout().getStandardPanelHeight()==height ));
		
		this.showDialog();
	}
	
	protected void setItemsToDiaog() {
		this.getLayoutGraphic().mapPanelLocationsOfLockedItems();
		if (type==1) {
			this.getPanelLayout().setPanelWidth(panelnum, this.getNumberInt("pwidth"));
			this.getPanelLayout().setPanelHeight(panelnum,this.getNumberInt("pheight"));
			boolean sw=this.getBoolean("sw");
			boolean sh=this.getBoolean("sh");
			this.getPanelLayout().setStandardPanelWidth(this.getNumberInt("swidth"));
			this.getPanelLayout().setStandardPanelHeight(this.getNumberInt("sheight"));
			
			if (sw) {
				getPanelLayout().setPanelWidth(panelnum,getPanelLayout().getStandardPanelWidth());
			}
			if (sh) {
				getPanelLayout().setPanelHeight(panelnum,getPanelLayout().getStandardPanelHeight());
			}
			this.getPanelLayout().resetPtsPanels();
		}
		this.getLayoutGraphic().snapLockedItems();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	if (graphic!=null) graphic.select();
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
