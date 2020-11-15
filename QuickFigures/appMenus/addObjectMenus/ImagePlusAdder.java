package addObjectMenus;


import appContext.CurrentAppContext;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;

public class ImagePlusAdder extends BasicGraphicAdder{

	boolean layonGrid=false;
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		// TODO Auto-generated method stub
		;
		MultichannelDisplayLayer display = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(false, null);
		if (!gc.canAccept(display)) return null;
		display.setLaygeneratedPanelsOnGrid(layonGrid);
		display.getSlot().setImageDialog();
		gc.add(display);
		return display;
	}

	
	@Override
	public String getCommand() {
		return "Add Multichannel Image";
	}

	@Override
	public String getMenuCommand() {
		return "Add Multichannel Image";
	}

}
