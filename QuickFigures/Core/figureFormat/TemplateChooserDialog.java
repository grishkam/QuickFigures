package figureFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComboBox;

import graphicalObjects.FigureDisplayContainer;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import standardDialog.ComboBoxPanel;
import standardDialog.GraphicComboBox;
import standardDialog.ItemSelectblePanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.AttachmentPosition;

/**A dialog that allows the user to select which example objects are to be used as models
  for a figure template*/
public class TemplateChooserDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FigureTemplate temp;

	GraphicComboBox rlcb=null;
	
	/**A map storing each graphical combo box used for selecting items to be part of the template*/
	HashMap<String, GraphicComboBox> setOfPanels=new HashMap<String, GraphicComboBox> ();

	/**A map storing each normal combo box used for selecting items to be part of the template*/
	private HashMap<String, JComboBox<?>> setOfPanels2=new HashMap<String, JComboBox<?>>();

	/**Constructor for selecting items for the given figure template. looks in the given layer
	 * for possible example items*/
	public TemplateChooserDialog(FigureTemplate temp, GraphicLayer givenLayer) {
		this.temp=temp;
	
		this.setModal(true);
		for(GraphicalItemPicker<?> pick: temp.getAllExamplePickers()) addGraphicItemPickerPanel(pick.getOptionName(), pick,givenLayer);
		for(ItemPicker<?> pick: temp.pickersReg) addItemPickerPanel(pick.getOptionName(), pick,givenLayer);
		
	}
	
	/**Constructor for selecting items for the given figure template. looks in the given layer
	 figure display container for possible example items*/
	public TemplateChooserDialog(FigureTemplate tp, FigureDisplayContainer oc) {
		this(tp,oc.getAsWrapper().getGraphicLayerSet());
	}

	/**Adds combo boxes to the dialog that that allow the user to choose which objects to use as examples 
	  for the template. Objects will be shown within these combo boxes */
	public void addGraphicItemPickerPanel(String title, GraphicalItemPicker<?> picker,  GraphicLayer source) {
		
		ArrayList<BasicGraphicalObject> possibleExamples = picker.getDesiredItemsAsGraphicals(source);
	
		if (picker.displayGraphicChooser()) {
			rlcb=new GraphicComboBox(possibleExamples);
			setOfPanels.put(picker.getKeyName(), rlcb);
			
			ItemSelectblePanel panel1 = new ItemSelectblePanel(picker.getOptionName(), rlcb);
			
			if (possibleExamples.size()>0) rlcb.setSelectedIndex(1);//if at least one option is available, sets the first one to be the starting option
			
			super.add(title, panel1);
		}
		else {
			addItemPickerPanel(title, picker, source);
		}
	}
	
	/**finds the combo box for the given picker and 
	 * retrieves the selected item from the combo box.
	 * That select item becomes the model for the picker*/
	public void setGraphicalPickerToComboBoxChoice(GraphicalItemPicker<?> picker) {
		String key=picker.getKeyName();
		
		if (picker.displayGraphicChooser()) {
				boolean ss = setOfPanels.containsKey(key);
				if (!ss) return;
				GraphicComboBox combobox1 = setOfPanels.get(key);
				
				picker.setModelItem(combobox1.getSelectedItem());
				}
		else {
			setPickerToComboBoxChoice(picker);
		}
	}
	
	
	/**Adds combo boxes to the dialog that that allow the user to choose which objects to use as examples 
	  for the template. Object names will be shown as strings in the boxes*/
	public void addItemPickerPanel(String title, ItemPicker<?> picker,  GraphicLayer source) {
		ArrayList<?> rowlabelpotentials = picker.getDesiredItemsOnly(source.getAllGraphics());
		if (picker instanceof MultichannelDisplayPicker) {
			rowlabelpotentials = picker.getDesiredItemsOnly(source.getSubLayers());
		}
		
		Vector<Object> vv = new Vector<Object>();
		vv.add(null);
		vv.addAll(rowlabelpotentials);
		JComboBox<Object> rlc = new JComboBox<Object>(vv);
		setOfPanels2.put(picker.getKeyName(), rlc);
		ComboBoxPanel panel1 = new ComboBoxPanel(picker.getOptionName(), rlc);
		if (rowlabelpotentials.size()>0) rlc.setSelectedIndex(1);
		super.add(title, panel1);
	}
	
	/**finds the combo box for the given picker and 
	 * retrieves the selected item from the combo box.
	 * That select item becomes the model for the picker*/
	public void setPickerToComboBoxChoice(ItemPicker<?> picker) {
		String key=picker.getKeyName();
		boolean ss = setOfPanels2.containsKey(key);
		if (!ss) return;
		JComboBox<?> combobox1 = setOfPanels2.get(key);
		picker.setModelItem(combobox1.getSelectedItem());
	}
	
	
	/**when ok is pressed, sets the model items of each picker based on the dialog*/
	protected void onOK() {
		for(GraphicalItemPicker<?> pick: temp.getAllExamplePickers())  {
			setGraphicalPickerToComboBoxChoice(pick);
		}
		for(ItemPicker<?> pick: temp.pickersReg)  {
			setPickerToComboBoxChoice(pick);
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		//FigureTemplate tp=new FigureTemplate();
		ObjectContainer oc1=new GraphicLayerPane("");
		TextGraphic tg4 = new TextGraphic("Hello");
		tg4.setAttachmentPosition(AttachmentPosition.defaultRowSide());
		oc1.addItemToImage(tg4);
		
		tg4 = new TextGraphic("Hi");
		tg4.setAttachmentPosition(AttachmentPosition.defaultRowSide());
		oc1.addItemToImage(tg4);
		oc1.addItemToImage(new MontageLayoutGraphic());
		oc1.addItemToImage(new BarGraphic());
		//new TemplateChooserDialog(tp, oc1).showDialog();;
	}

}
