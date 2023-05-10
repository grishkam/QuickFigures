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
 * Version: 2023.2
 */
package journalCriteria;

import java.util.ArrayList;

import javax.swing.Icon;

import appContext.ImageDPIHandler;
import figureOrganizer.PanelManager;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import iconGraphicalObjects.IconUtil;
import logging.IssueLog;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import standardDialog.numbers.NumberInputPanel;
import undo.CombinedEdit;

/**A user options to change the final pixel density of selected panels*/
public class PPIOption extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getMenuCommand() {
		return "Set pixel Density for image panels";
	}




	@Override
	public String getMenuPath() {
		return null;
	}


	@Override
	public void run() {
		ArrayList<ZoomableGraphic> array1 = this.getAllArray();
		
		ArrayList<PanelManager> panels=new ArrayList<PanelManager>();
		
		for(ZoomableGraphic z: array1) {
			if(z instanceof ImagePanelGraphic) try {
				ChannelPanelEditingMenu z2 = new ChannelPanelEditingMenu((ImagePanelGraphic) z);
				PanelManager pm = z2.getPressedPanelManager();
				if(pm!=null&&!panels.contains(pm)) panels.add(pm);
			} catch (Throwable t) {}
			
		}
		
		if (panels.size()<1) {
			IssueLog.showMessage("Select an image panel first");
			return;}
		
		
		
		String st="Set Pixel Density";
		double starting=ImageDPIHandler.idealPanelPixelDesity();
		CombinedEdit undo = showPPIChangeDialog(panels, st, starting);
		super.getUndoManager().addEdit(undo);
	}



	/**shows a dialog to the user*/
	protected CombinedEdit showPPIChangeDialog(ArrayList<PanelManager> panels, String st, double starting) {
		CombinedEdit output = new CombinedEdit();
		StandardDialog sd = new StandardDialog(st);
		sd.setModal(true);
		sd.setWindowCentered(true);
		
		sd.add(st, new NumberInputPanel(st, starting, 4));
		
		sd.showDialog();
		
		if(!sd.wasOKed()) {
			return null;
		}
		double n = sd.getNumber(st);
		for(PanelManager p: panels) {
			output.addEditToList(
					p.changePPI(n)
					)	;
			
		}
		return output;
	}


	
	public Icon getIcon() {
		return new GraphicDisplayComponent(IconUtil.createAllIcon("ppi")  );
	}
	
	

}
