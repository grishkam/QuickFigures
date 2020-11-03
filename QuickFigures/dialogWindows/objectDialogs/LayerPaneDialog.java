package objectDialogs;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicGroup.GroupedLayerPane;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import standardDialog.BooleanInputPanel;
import standardDialog.StringInputPanel;

public class LayerPaneDialog extends GraphicItemOptionsDialog  {

	boolean showKey=false;
	
	GraphicLayerPane layer;
	public LayerPaneDialog(GraphicLayerPane layer) {
		
		//super(layer.getName()+" Dialog");
		this.layer=layer;
		addOptionsToDialog() ;
		
		this.showDialog();
		
		
	}
	@Override
	public void addOptionsToDialog() {
		this.add("name", new StringInputPanel("Layer Name ", layer.getName(), 30));
		if (showKey) this.add("key", new StringInputPanel("Layer Key ", ""+layer.getKey(), 30));
		this.add("desc", new StringInputPanel("Description  ", ""+layer.getDescription(), 30));
		if(layer instanceof GraphicGroup.GroupedLayerPane) {
			add("selGroup", new BooleanInputPanel("Select in Group", GraphicGroup.treatGroupsLikeLayers));
			add("unGroup", new BooleanInputPanel("Ungroup", false));
			
		}
	}
	
	@Override
	public void setItemsToDiaog() {
		layer.setName(this.getString("name"));
		if (showKey)layer.setKey(this.getString("key"));
		layer.setDescription(this.getString("desc"));
		if(layer instanceof GraphicGroup.GroupedLayerPane) {
			GraphicGroup.treatGroupsLikeLayers=this.getBoolean("selGroup");
			if (getBoolean("unGroup")) {
				GraphicGroup.GroupedLayerPane g=(GroupedLayerPane) layer;
				g.getTheGroup().ungroup();
				this.setVisible(false);
			}
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
