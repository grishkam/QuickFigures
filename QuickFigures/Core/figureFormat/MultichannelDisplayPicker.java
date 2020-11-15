package figureFormat;

import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import logging.IssueLog;

public class MultichannelDisplayPicker extends
		ItemPicker<MultichannelDisplayLayer> {
	
	public boolean doesPreprocess=true;

	public MultichannelDisplayPicker() {
		this(null);
	}

	public MultichannelDisplayPicker(MultichannelDisplayLayer model) {
		super(model);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	boolean isDesirableItem(Object o) {
		if (o instanceof MultichannelDisplayLayer) {
			return true;
		}
		return false;
	}

	@Override
	public void applyProperties(Object item) {
		
		if (!isDesirableItem(item)) {
			return;
			
		}
		MultichannelDisplayLayer imageMulti=(MultichannelDisplayLayer) item;
		
		
		
		imageMulti.partialCopyTraitsFrom(this.getModelItem(), doesPreprocess);
		try {
			//imageMulti.getPanelManager().getStack().getChannelUseInstructions().clearChannelReorder();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean isProprocessSuitable(MultichannelDisplayLayer imageMulti) {
		double pScale=1;
		if (getModelItem()!=null)
			pScale = getModelItem().getPreprocessScale();
		/**decides whether the preprocess is appropriate or not*/
		boolean b = imageMulti.getMultichanalWrapper().getDimensions().getWidth()*pScale<500;
		return b;
	}

	@Override
	public String getOptionName() {
		return "Pick Multichannel Image";
	}
	
	public void setModelItem(Object modelItem) {
		if (!this.isDesirableItem(modelItem)) return;
		try{super.setModelItem(((MultichannelDisplayLayer)modelItem).copy());} catch (Exception e) {
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}

}
