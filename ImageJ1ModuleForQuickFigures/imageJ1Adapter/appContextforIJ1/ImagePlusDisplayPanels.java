package appContextforIJ1;


import graphicalObjects.*;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import imageDisplayApp.hasSupportingWindow;
import menuUtil.HasUniquePopupMenu;
import utilityClassesForObjects.Mortal;
import utilityClassesForObjects.Named;
import utilityClassesForObjects.ShowsOptionsDialog;


public class ImagePlusDisplayPanels extends MultichannelDisplayLayer implements ZoomableGraphic, hasSupportingWindow, Mortal, ShowsOptionsDialog, KnowsParentLayer, Named, HasUniquePopupMenu /**ImagePlusAccepter,*/, KnowsSetContainer {

	
	
	/**The path, byte array for embedding and transeitn image object*/
	private static final long serialVersionUID = 1L;
	//private transient  ImagePlus containsDisplay;
	{slot=new ImagePlusMultiChannelSlot(this); {slot.addMultichannelUpdateListener(this);}}
	public ImagePlusDisplayPanels() {
		super(new ImagePlusMultiChannelSlot());
		
	}

	
	
	


	


	


	


	


	


}
