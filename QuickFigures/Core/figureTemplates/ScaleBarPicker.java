package figureTemplates;

import java.awt.Color;

import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.ScalededItem;
import utilityClassesForObjects.SnappingPosition;

public class ScaleBarPicker extends GraphicalItemPicker<BarGraphic>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Chose Scale Bar";}
	boolean autoComfortBarSize=true;
	
	
	public ScaleBarPicker(BarGraphic model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	/**returns true if the object is a TextGraphi with the desired snap type*/
	boolean isDesirableItem(Object o) {
		if (!(o instanceof BarGraphic))
		return false;
		
		return true;
	}
	
	@Override
	public void applyProperties(Object item) {
		if (this.getModelItem()==null) return;
		
		if (item instanceof BarGraphic) {
			BarGraphic item2=(BarGraphic) item;
		
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
		if (getModelItem().getSnappingBehaviour()==null) return;
		
		item2.setSnappingBehaviour(getModelItem().getSnappingBehaviour().copy());
		}
		
	}
	
	double getBarLengthStandard(PanelListElement panel) {
		if (panel==null) return this.getModelItem().getLengthInUnits();
		double[] dims = panel.getScaleInfo().convertPixelsToUnits(panel.getDimensions());
		double num = NumberUse.findNearest(dims[0]/3, BarGraphic.reccomendedBarLengths);
		return num;		
	}
	
	public double getBarLengthStandard(ImagePanelGraphic panel) {
		if (panel==null) return this.getModelItem().getLengthInUnits();
		return BarGraphic.getStandardBarLengthFor(panel);		
	}


	
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		float h=(float) (wrap.getWorkingStack().getHeight()*wrap.getPanelManager().getPanelLevelScale());
		
		PanelListElement panel = wrap.getWorkingStack().getPanels().get(0);
		
		
		if (this.getModelItem()!=null) {


			getModelItem().setLengthInUnits(getBarLengthStandard(panel));
			getModelItem().setSnappingBehaviour(SnappingPosition.defaultScaleBar());
			
			setBarDefaultsBasedOnHeight(h, getModelItem(), Color.white);
		}
		
		
	}
	
	public void setBarDefaultsBasedOnHeight(float h, BarGraphic model, Color c) {
		h=(float)NumberUse.findNearest(h/8, new double[] {0,2,4,6,8,10,12,14,16,20, 18,24,28, 32,36,40});
		
		
		model.setBarStroke((float) (h/2));
			model.setFillColor(c);
			
			model.getBarText().setFontSize((int) h);
			model.getBarText().setTextColor(c);
		
	}
}
