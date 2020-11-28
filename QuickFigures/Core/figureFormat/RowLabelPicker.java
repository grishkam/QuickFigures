package figureFormat;

import channelLabels.ChannelLabelTextGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import gridLayout.MontageSpaces;
import logging.IssueLog;
import utilityClasses1.NumberUse;
import utilityClassesForObjects.AttachmentPosition;

public class RowLabelPicker extends GraphicalItemPicker<TextGraphic> implements MontageSpaces {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int[] typicalTypes=new int[] {MontageSpaces.ROW_OF_PANELS, MontageSpaces.COLUMN_OF_PANELS, MontageSpaces.PANELS};
	private int desiredGridSnapType=MontageSpaces.ROW_OF_PANELS;
	
	public RowLabelPicker(TextGraphic model, int desiredSnapType) {
		super(model);
		this.setDesiredGridSnapType(desiredSnapType);
	//	this.setModelItem(model);
	//	
	}
	
	@Override
	public String getOptionName() {
		if (getDesiredGridSnapType()==MontageSpaces.ROW_OF_PANELS) return "Pick Row Label";
		if (getDesiredGridSnapType()==MontageSpaces.COLUMN_OF_PANELS) return "Pick Column Label";
		if (getDesiredGridSnapType()==MontageSpaces.PANELS) return "Pick Panel Label";
		if (getDesiredGridSnapType()==MontageSpaces.ALL_MONTAGE_SPACE) return "Pick Title Label";
		// TODO Auto-generated method stub
		return optionname;
	}
	
	
	public String getTypeName() {
		if (getDesiredGridSnapType()==MontageSpaces.ROW_OF_PANELS) return "Row Labels";
		if (getDesiredGridSnapType()==MontageSpaces.COLUMN_OF_PANELS) return "Column Labels";
		if (getDesiredGridSnapType()==MontageSpaces.PANELS) return "Panel Labels";
		if (getDesiredGridSnapType()==MontageSpaces.ALL_MONTAGE_SPACE) return "Title Label";
		// TODO Auto-generated method stub
		return optionname;
	}
	
	/**returns true if the object is a TextGraphi with the desired snap type*/
	public boolean isDesirableItem(Object o) {
		if (modelItem instanceof BarGraphic.BarTextGraphic) return false;
		if (!(o instanceof TextGraphic))
		return false;
		if (o instanceof ChannelLabelTextGraphic) return false;
		TextGraphic tg=(TextGraphic) o;
		if (tg.getSnapPosition()==null) return false;
		
		if (AttachmentPosition. getGridchoices()[tg.getSnapPosition().getGridLayoutSnapType()]!=getDesiredGridSnapType()) {
		
		return false;
		}
		
		return true;
	}
	
	
	@Override
	public void applyProperties(Object item) {
	
		if (this.getModelItem()==null) return;
		
		if (!this.isDesirableItem(item)) return;
		
		if (item instanceof TextGraphic) {
		TextGraphic item2=(TextGraphic) item;
		item2.copyAttributesFrom(super.getModelItem());
		//IssueLog.log("Will apply model text "+this.getModelItem().getFont().getSize()+ " to "+item2.getFont().getSize());
		if (getModelItem().getSnapPosition()==null) return;
		item2.setSnapPosition(getModelItem().getSnapPosition().copy());
		}
	}
	
	public void setModelItem(Object modelItem) {
		
	if (!this.isDesirableItem(modelItem)) return;
	try{this.modelItem = (TextGraphic)modelItem;} catch (Exception e) {
		e.printStackTrace();
		IssueLog.log("problem. wrong class");
	}
	}

	public int getDesiredGridSnapType() {
		return desiredGridSnapType;
	}

	public void setDesiredGridSnapType(int desiredGridSnapType) {
		this.desiredGridSnapType = desiredGridSnapType;
	}
	
	public boolean isInRowOrColumn() {
		if (getDesiredGridSnapType()==MontageSpaces.ROW_OF_PANELS) return true;
		if (getDesiredGridSnapType()==MontageSpaces.COLUMN_OF_PANELS) return true;
		if (getDesiredGridSnapType()==MontageSpaces.PANELS) return true;
		return false;
	}
	
	public AttachmentPosition getDefaultSnapping() {
		if (getDesiredGridSnapType()==MontageSpaces.ROW_OF_PANELS) return AttachmentPosition .defaultRowLabel() ;
		if (getDesiredGridSnapType()==MontageSpaces.COLUMN_OF_PANELS) return AttachmentPosition.defaultColLabel();
		
		 return AttachmentPosition.defaultPanelLabel();
	}
	
	
	protected void setModelSnappingtoDefault() {
		if (this.getModelItem()!=null)
		getModelItem().setSnapPosition(AttachmentPosition.defaultRowSide());
	}
	
	
	@Override
public void setToStandardFor(MultichannelDisplayLayer wrap) {
		
		/**Calculation to get a comfortable font size for the panel*/
		float h2=(float) (wrap.getPanelList().getHeight()*wrap.getPanelManager().getPanelLevelScale());
		float h=h2/5;
		h=(float)NumberUse.findNearest(h, new double[] {0,2,4,6,8,10,12,14,16,20, 18,24,28, 32,36,40});
		
		
		if (this.getModelItem()!=null) {
			
			getModelItem().setFont(getModelItem().getFont().deriveFont(h));
			setModelSnappingtoDefault();
			
		}
	}
	
	public TextGraphic getModelItem() {
		return modelItem;
	}
	
}
