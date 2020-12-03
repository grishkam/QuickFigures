package figureFormat;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

/**A subclass of item picker for multichannel display layers*/
public class MultichannelDisplayPicker extends
		ItemPicker<MultichannelDisplayLayer> {
	
	/**set to true if the scale factor from the model should also be used, false otherwises*/
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

	/**if the object given is a multichannel display layer, changes its settings to match the model*/
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		
		if (!isDesirableItem(item)) {
			return null;
			
		}
		try {
		MultichannelDisplayLayer imageMulti=(MultichannelDisplayLayer) item;
		CombinedEdit undo = PanelManagerUndo.createFor(imageMulti);
		imageMulti.partialCopyTraitsFrom(this.getModelItem(), doesPreprocess);
		return undo;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**returns true if the scale used by the model available is reasonable for the target image */
	public boolean isProprocessSuitable(MultichannelDisplayLayer imageMulti) {
		double pScale=1;
		if (getModelItem()!=null)
			pScale = getModelItem().getPreprocessScale();
		/**decides whether the preprocess is appropriate or not*/
		boolean b = imageMulti.getMultiChannelImage().getDimensions().getWidth()*pScale<500;
		return b;
	}

	@Override
	public String getOptionName() {
		return "Pick Multichannel Image";
	}
	
	/**sets the multi channel display layer that is to be used as a model item*/
	public void setModelItem(Object modelItem) {
		if (!this.isDesirableItem(modelItem)) return;
		try{super.setModelItem(((MultichannelDisplayLayer)modelItem).copy());} catch (Exception e) {
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}

}
