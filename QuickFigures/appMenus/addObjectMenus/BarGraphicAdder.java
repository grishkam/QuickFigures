package addObjectMenus;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import standardDialog.GraphicDisplayComponent;
import utilityClassesForObjects.ScaleInfo;

public class BarGraphicAdder extends BasicGraphicAdder {
	
	BarGraphic modelbar = new BarGraphic(); {modelbar.moveLocation(-4, 0);}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		BarGraphic ag = getModelBar().copy();
		addLockedItemToSelectedImage(ag);
		gc.add(ag);
		
		
		return ag;
	}
	


	@Override
	public String getCommand() {
	
		return "bar";
	}

	@Override
	public String getMenuCommand() {
	
		return "Scale Bar To Selected Image";
	}
	
	public BarGraphic getModelBar() {
		return modelbar;
	}
	
	public BarGraphic getModelForIcon() {
		BarGraphic out = getModelBar().copy();
		out.setStrokeColor(Color.black);
		out.setFillColor(Color.black);
		out.getBarText().setTextColor(Color.black);
		out.setScaleInfo(new ScaleInfo("units",.10,.10));
		return out;
	}
	public Icon getIcon() {
		 BarGraphic m = getModelForIcon();
		if (m==null)return null;
		GraphicDisplayComponent out = new GraphicDisplayComponent(m, .5);
		m.setLocation(0,16);
		out.setCurrentItemInsets(new Insets(5,5,1,5));
		 return out;
	}
}