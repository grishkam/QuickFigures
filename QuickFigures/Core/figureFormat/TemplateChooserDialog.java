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
import utilityClassesForObjects.SnappingPosition;

public class TemplateChooserDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FigureTemplate temp;
	//private ObjectContainer oc;
	
	GraphicComboBox rlcb=null;
	
	HashMap<String, GraphicComboBox> setOfPanels=new HashMap<String, GraphicComboBox> ();
	//private JComboBox rlcb2;
	private HashMap<String, JComboBox> setOfPanels2=new HashMap<String, JComboBox>();

	public TemplateChooserDialog(FigureTemplate temp, GraphicLayer asWrapper) {
		this.temp=temp;
	//	this.oc=oc;
		this.setModal(true);
		/**ArrayList<TextGraphic> rowlabelpotentials = temp.getRowLabelPicker().getDesiredObjects(oc);
		rlcb=new GraphicComboBox(rowlabelpotentials);
		ItemSelectblePanel panel1 = new ItemSelectblePanel("Row Label Template", rlcb);*/
		for(GraphicalItemPicker<?> pick: temp.getPickers()) addGraphicItemPickerPanel(pick.getOptionName(), pick,asWrapper);
		for(ItemPicker<?> pick: temp.pickersReg) addItemPickerPanel(pick.getOptionName(), pick,asWrapper);
		
	}
	
	public TemplateChooserDialog(FigureTemplate tp, FigureDisplayContainer oc) {
		this(tp,oc.getAsWrapper().getGraphicLayerSet());
	}

	public void addGraphicItemPickerPanel(String title, GraphicalItemPicker<?> picker,  GraphicLayer source) {
		
		ArrayList<BasicGraphicalObject> rowlabelpotentials = picker.getDesiredItemsAsGraphicals(source);
	
		if (picker.displayGraphicChooser()) {
		rlcb=new GraphicComboBox(rowlabelpotentials);
		setOfPanels.put(picker.getKeyName(), rlcb);
		
		ItemSelectblePanel panel1 = new ItemSelectblePanel(picker.getOptionName(), rlcb);
		if (rowlabelpotentials.size()>0) rlcb.setSelectedIndex(1);
		super.add(title, panel1);
		}
		
		else {
			addItemPickerPanel(title, picker, source);
		}
	}
	
	public void setGraphicalPickerToComboBoxChoice(GraphicalItemPicker<?> picker) {
		String key=picker.getKeyName();
		
		if (picker.displayGraphicChooser()) {
		boolean ss = setOfPanels.containsKey(key);
		if (!ss) return;
		GraphicComboBox combobox1 = setOfPanels.get(key);
		
		
		picker.setModelItem(combobox1.getSelectedItem());}
		else {
			setPickerToComboBoxChoice(picker);
		}
	}
	
	
	
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
	
	public void setPickerToComboBoxChoice(ItemPicker<?> picker) {
		String key=picker.getKeyName();
		boolean ss = setOfPanels2.containsKey(key);
		if (!ss) return;
		JComboBox combobox1 = setOfPanels2.get(key);
		picker.setModelItem(combobox1.getSelectedItem());
	}
	
	
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		for(GraphicalItemPicker<?> pick: temp.getPickers())  {
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
		tg4.setSnapPosition(SnappingPosition.defaultRowSide());
		oc1.addItemToImage(tg4);
		
		tg4 = new TextGraphic("Hi");
		tg4.setSnapPosition(SnappingPosition.defaultRowSide());
		oc1.addItemToImage(tg4);
		oc1.addItemToImage(new MontageLayoutGraphic());
		oc1.addItemToImage(new BarGraphic());
		//new TemplateChooserDialog(tp, oc1).showDialog();;
	}

}
