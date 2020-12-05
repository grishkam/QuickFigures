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
package popupMenusForComplexObjects;

import java.util.ArrayList;

import appContext.ImageDPIHandler;
import channelMerging.ImageDisplayLayer;
import genericMontageKit.PanelList;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelManager;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;

public class ScaleFigureDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private PanelManager def;
	private MontageLayoutGraphic layout;
	ArrayList<PanelManager> theArray;
	private PanelGraphicInsetDefiner inset;
/**Dialog for scaling the panels within a layout while still keeping the layout*/
	public ScaleFigureDialog(MontageLayoutGraphic layout, PanelManager s) {
	this.layout=layout;
		this.def=s;
		super.add("scale", new NumberInputPanel("Bilinear Scale", def.getPanelList().getScaleBilinear(), 3));
		double ppi=ImageDPIHandler.getStandardDPI()/def.getPanelLevelScale();
		super.add("PPI", new NumberInputPanel("PPI", ppi, 3));
		
		
	}
	
	
	
	
	public ScaleFigureDialog(MontageLayoutGraphic personalGraphic, PanelManager panelManager, PanelGraphicInsetDefiner inset) {
				this(personalGraphic, panelManager);
				this.inset=inset;
}




	public void setAdditionalPanelManagers(ArrayList<?>ss) {
		theArray = new ArrayList<PanelManager> ();
	
			for(Object s:ss) {
					if (s instanceof PanelManager) {
						theArray.add((PanelManager) s);
					} else
					if (s instanceof ImageDisplayLayer) {
						theArray.add(((ImageDisplayLayer) s).getPanelManager());
					}
			
			
			
			}
	}
	
		@Override
		public void onOK() {
			setStackToDislogItems(def, def.getPanelList());
			
			if (theArray!=null)for(PanelManager a: theArray) {
				
				setStackToDislogItems(a, a.getPanelList());
			}
			
			layout.snapLockedItems();
			layout.generateCurrentImageWrapper();
			layout.getPanelLayout().getEditor().alterPanelWidthAndHeightToFitContents(layout.getPanelLayout());
			layout.updateDisplay();
			
		}
		
		void setStackToDislogItems(PanelManager pm, PanelList stack) {
			
			if (this.inset!=null) inset.setBilinearScale(getNumber("scale"));
			double scalePanel = ImageDPIHandler.getStandardDPI()/this.getNumber("PPI");
			stack.setPixelDensityRatio(scalePanel);
			ArrayList<ImagePanelGraphic> graphi = stack.getPanelGraphics();
			
			pm.updatePanels();
			for(ImagePanelGraphic panel: graphi) {
				panel.setScale(stack.getPixelDensityRatio());
				panel.snapLockedItems();
			}
		}
		
		protected void afterEachItemChange() {
			onOK();
		}
	
}