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
package figureFormat;

import java.awt.Color;

import javax.swing.undo.AbstractUndoableEdit;

import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import graphicalObjects_SpecialObjects.BarGraphic;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.ScalededItem;
import utilityClassesForObjects.AttachmentPosition;

/**An implementation of graphical item picker for scale bars*/
public class ScaleBarExamplePicker extends GraphicalItemPicker<BarGraphic>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Chose Scale Bar";}
	boolean autoComfortBarSize=true;
	
	/**creates a scale bar picker with the given example scale bar*/
	public ScaleBarExamplePicker(BarGraphic model) {
		super(model);
	}

	/**returns true if the object is a scale bar*/
	boolean isDesirableItem(Object o) {
		if (!(o instanceof BarGraphic))
		return false;
		return true;
	}
	
	/**if the targeted item is a scale bar, alters its appearance to match the example scale bar.
	 * Does nothing unless there is an example scale bar to act as a model*/
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		if (this.getModelItem()==null) return null;
		
		if (item instanceof BarGraphic) {
			BarGraphic item2=(BarGraphic) item;
			AbstractUndoableEdit undo = item2.provideUndoForDialog();
		if (autoComfortBarSize) {
			ScalededItem scaleProvider = item2.getScaleProvider();
			if (scaleProvider!=null)
				{
			double num = NumberUse.findNearest(scaleProvider.getDimensionsInUnits()[0]/3, BarGraphic.reccomendedBarLengths);
			item2.setLengthInUnits(num);
				}
		}
	
		item2.copyAttributesButNotScale(getModelItem());
		item2.copyColorsFrom(getModelItem());
		if (getModelItem().getAttachmentPosition()!=null) 
				item2.setAttachmentPosition(getModelItem().getAttachmentPosition().copy());
		return undo;
		}
		return null;
	}
	



	/**When given a multichannel display, changes the model item
	 * to be more appropriate for the size of the panels within the display
	 * if there are no panels, does nothing*/
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		if (wrap.getPanelList().getSize()<1) return;
		float h=(float) (wrap.getPanelList().getHeight()*wrap.getPanelManager().getPanelLevelScale());
		
		PanelListElement panel = wrap.getPanelList().getPanels().get(0);
		
		
		if (this.getModelItem()!=null) {


			getModelItem().setLengthInUnits(getBarLengthStandard(panel));
			getModelItem().setAttachmentPosition(AttachmentPosition.defaultScaleBar());
			
			setBarDefaultsBasedOnHeight(h, getModelItem(), Color.white);
		}
		
		
	}
	
	/**returns a scale bar length that is appropriate for the given panel*/
	double getBarLengthStandard(PanelListElement panel) {
		if (panel==null) return this.getModelItem().getLengthInUnits();
		double[] dims = panel.getScaleInfo().convertPixelsToUnits(panel.getDimensions());
		double num = NumberUse.findNearest(dims[0]/3, BarGraphic.reccomendedBarLengths);
		return num;		
	}
	
	
	/**Based on the height h, alters the scale bar thickness to be a fraction of the height given.
	 * Sets the color of the scale bar as well*/
	public static void setBarDefaultsBasedOnHeight(float h, BarGraphic model, Color c) {
		h=(float)NumberUse.findNearest(h/8, new double[] {0,2,4,6,8,10,12,14,16,20, 18,24,28, 32,36,40});
		
		
		model.setBarStroke((float) (h/2));
			model.setFillColor(c);
			
			model.getBarText().setFontSize((int) h);
			model.getBarText().setTextColor(c);
		
	}
}
