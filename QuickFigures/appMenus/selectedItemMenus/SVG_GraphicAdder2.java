package selectedItemMenus;

import java.io.File;

import addObjectMenus.BasicGraphicAdder;
import basicMenusForApp.SVGOpener;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.GraphicSet;
import ultilInputOutput.FileChoiceUtil;

public class SVG_GraphicAdder2 extends BasicGraphicAdder {
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		File f=FileChoiceUtil.getOpenFile();
		
		return addFromFile(f,gc);
	}
	
	public ZoomableGraphic addFromFile(File f, GraphicLayer gc) {
		GraphicSet ss = SVGOpener.readFromFile(f);
		GraphicLayer ob = ss.getGraphicLayerSet();
		
	
		gc.add(ob);
		
		
		if (selector!=null&&selector.getGraphicDisplayContainer()!=null)selector.getGraphicDisplayContainer().onItemLoad(ob);
		return ob;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "open svg";
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "Saved SVG Graphics";
	}
}
