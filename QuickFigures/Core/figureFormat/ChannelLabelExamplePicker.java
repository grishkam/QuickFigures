package figureFormat;

import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import undo.AbstractUndoableEdit2;
import undo.ChannelLabelPropertiesUndo;
import undo.CombinedEdit;
import utilityClassesForObjects.AttachmentPosition;
import utilityClassesForObjects.ColorDimmer;

/**A subclass of label picker that selects only channel labels*/
public class ChannelLabelExamplePicker extends LabelExamplePicker {

	public ChannelLabelExamplePicker(TextGraphic model) {
		super(model, 0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns true if the object is a channel label with an attachment position*/
	public boolean isDesirableItem(Object o) {
		if (!(o instanceof ChannelLabelTextGraphic))
			return false;
		TextGraphic tg=(TextGraphic) o;
		if (tg.getAttachmentPosition()==null) return false;
		
		return true;
	}
	
	/**when given a display layer. modifies the example label to make it more appropriate 
	  for the display layer*/
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		super.setToStandardFor(wrap);
		if (getModelItem()==null) return;
		this.getModelItem().setDimming(ColorDimmer.NORMAL_DIM);
	}
	
	/**Sets the attachment position for the label to the default*/
	protected void setModelAttachmentPositionDefault() {
		if (getModelItem()!=null)
		getModelItem().setAttachmentPosition(AttachmentPosition.defaultColSide());
	}
	
	public String getOptionName()  {
		return "Pick Channel Label";
	}
	
	/**if the target object is a channel label.
	  alters the object's properties to match the model (the example label)*/
	@Override
	public AbstractUndoableEdit2 applyProperties(Object item) {
		
		if (this.getModelItem()==null) return null;
		
		if (!this.isDesirableItem(item)) return null;
		
		if (item instanceof ChannelLabelTextGraphic) {
			
			ChannelLabelTextGraphic c=(ChannelLabelTextGraphic) item;
			ChannelLabelTextGraphic cModel=(ChannelLabelTextGraphic) getModelItem();
			
			CombinedEdit undo = new CombinedEdit();
			undo.addEditToList(super.applyProperties(item));
			undo.addEditToList(c.provideUndoForDialog());
			undo.addEditToList(new ChannelLabelPropertiesUndo(c.getChannelLabelProperties()));
			c.getChannelLabelProperties().setMergeText(cModel.getChannelLabelProperties().getMergeText());
			c. setParaGraphToChannels();
			
			return undo;
		}
		return null;
		
	}

}
