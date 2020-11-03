package figureTemplates;

import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import utilityClassesForObjects.SnappingPosition;

public class ChannelLabelPicker extends RowLabelPicker {

	public ChannelLabelPicker(TextGraphic model) {
		super(model, 0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns true if the object is a TextGraphi with the desired snap type*/
	public boolean isDesirableItem(Object o) {
		if (!(o instanceof ChannelLabelTextGraphic))
		return false;
		TextGraphic tg=(TextGraphic) o;
		if (tg.getSnappingBehaviour()==null) return false;
		
		
		return true;
	}
	
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		super.setToStandardFor(wrap);
		if (getModelItem()==null) return;
		this.getModelItem().setDimming(1);
	}
	
	protected void setModelSnappingtoDefault() {
		if (getModelItem()!=null)
		getModelItem().setSnappingBehaviour(SnappingPosition.defaultColSide());
	}
	
	public String getOptionName()  {
		return "Pick Channel Label";
	}
	
	@Override
	public void applyProperties(Object item) {
		super.applyProperties(item);
		if (this.getModelItem()==null) return;
		
		if (!this.isDesirableItem(item)) return;
		
		if (item instanceof ChannelLabelTextGraphic) {
			ChannelLabelTextGraphic c=(ChannelLabelTextGraphic) item;
			ChannelLabelTextGraphic cModel=(ChannelLabelTextGraphic) getModelItem();
			c.getChannelLabelproperties().setMergeText(cModel.getChannelLabelproperties().getMergeText());
			c. setParaGraphToChannels();
		}
		
		
	}

}
