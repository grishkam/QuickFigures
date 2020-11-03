package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import figureTemplates.RowLabelPicker;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.SwingDialogListener;

public class EditLabels extends JMenuItem implements ActionListener {
	
	
	private int type;
	private RowLabelPicker picker;
	private MontageLayoutGraphic layout;
	private transient MultiTextGraphicSwingDialog dd;
	private TextGraphic modelTextItem;

	public EditLabels(TextGraphic t) {
		
		 type=t.getSnappingBehaviour().getGridChoiceNumbers();
		 setUpPickerFortype(type);
		 modelTextItem=t;
	}
	
	public EditLabels(int type, MontageLayoutGraphic lay, TextGraphic t) {
		this.type=type;
		
		setUpPickerFortype(type);
		layout=lay;
		this.addActionListener(this);
		 modelTextItem=t;
	}

	public void setUpPickerFortype(int type) {
		picker=new RowLabelPicker(new ComplexTextGraphic(), type);
		this.setText("Edit All "+picker.getTypeName());
		
	}
	
	ArrayList<TextGraphic> getLabels(TextGraphic t) {
		ArrayList<TextGraphic> output=new ArrayList<TextGraphic>();
		ArrayList<?> lockedItems=null;
		if(layout!=null) {
			 lockedItems = layout.getLockedItems();}
		lockedItems=picker.getDesiredItemsAsGraphicals(lockedItems);
		
		if(lockedItems!=null &&lockedItems.size()>0) {
			
			
			for(Object i:lockedItems) {
				if (!(i instanceof TextGraphic)) continue;//ignores non text
				
				TextGraphic text2 = (TextGraphic) i;
				if(picker.isDesirableItem(i))
					output.add(text2);
				}
		}
		else if ( modelTextItem!=null) {
			for(Object i:modelTextItem.getParentLayer().getAllGraphics()) {
				if (!(i instanceof TextGraphic)) continue;//ignores non text
				
				TextGraphic text2 = (TextGraphic) i;
			
				if(picker.isDesirableItem(i))
					output.add(text2);
				}
		}
		
		return output;
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		 dd = new MultiTextGraphicSwingDialog(getLabels(modelTextItem), true);
		 dd.setUnifyPosition(true);
		dd.addDialogListener(new SwingDialogListener() {
			
			@Override
			public void itemChange(DialogItemChangeEvent event) {
				if (layout!=null)
				for(TextGraphic t: dd.getAllEditedItems()) {
				layout.getEditor().expandSpacesToInclude(layout.getPanelLayout(), t.getBounds());
				}
			}});
		
		dd.showDialog();
		
	}
	
}

