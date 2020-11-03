package objectDialogs;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import applicationAdapters.ImageWrapper;
import graphicActionToombar.CurrentSetInformer;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects.GraphicSetDisplayContainer;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import logging.IssueLog;
import standardDialog.FixedEdgeSelectable;
import standardDialog.GraphicSampleComponent;
import standardDialog.ItemSelectblePanel;
import standardDialog.NumberInputPanel;
import standardDialog.SnappingPanel;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;
import undo.AbstractUndoableEdit2;
import undo.CompoundEdit2;
import undo.UndoManagerPlus;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.StrokedItem;

public class GraphicItemOptionsDialog extends StandardDialog {

	/**
	 * 
	 */
	{setHideCancel(false);}
	
	private static final long serialVersionUID = 1L;
	public GraphicSampleComponent sam;
	StrokeInputPanel strokeInput;
	private static ImageWrapper currentImage=null;
	private static GraphicSetDisplayContainer setContainer;
	private boolean updateAfterEachItemChange=true;
	private static CurrentSetInformer informer=new CurrentSetInformerBasic();
	

	SnappingPanel snappingPanel=null;
	public AbstractUndoableEdit2 undoableEdit;
	protected transient boolean editAdded=false;
	
	public GraphicItemOptionsDialog() {
	}
	

	
	public void afterEachItemChange() {
		if (updateAfterEachItemChange) try{this.onOK();} catch (Throwable t) {
			IssueLog.log(t);
			
		}
	}
	
	public void onOK() {
		updateObjectFromDialog() ;
		
		addUndo();
	}



	protected void addUndo() {
		if(undoableEdit!=null&&!editAdded) try {
			undoableEdit.establishFinalState();
			UndoManagerPlus undoManager = new CurrentSetInformerBasic().getCurrentlyActiveDisplay().getUndoManager();
			if (undoManager!=null) {
				undoManager.addEdit(undoableEdit);
				editAdded=true;
			}
		} catch (Throwable t) {}
	}
	
	void updateObjectFromDialog() {
		this.setItemsToDiaog();
		if (sam!=null) {sam.repaint();}
		if (getCurrentImage()!=null) getCurrentImage().updateDisplay();
		else {
			
		}
		if (GraphicItemOptionsDialog.getSetContainer()!=null) GraphicItemOptionsDialog.getSetContainer().updateDisplay();
		informer.updateDisplayCurrent();
	}
	

	@Override
	public void onCancel() {
		updateObjectFromDialog() ;
	}

	
	protected void addOptionsToDialog() {
	}
	protected void setItemsToDiaog() {
		
	}
	
	
	
	public SnappingPanel addSnappingBehviourToDialog(LocatedObject2D l) {
		
		
		if (l==null||l.getSnappingBehaviour()==null) return null;
		//GriddedPanel newtabContent = new GriddedPanel();
		
		snappingPanel = new SnappingPanel(l.getSnappingBehaviour());
		snappingPanel.addObjectEditListener(this);
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridwidth=4;
		gc.gridheight=2;
		gc.gridx=0;
		gc.anchor=GridBagConstraints.WEST;
		
		gc.gridy=this.gy;
		gy+=2;
		this.getOptionDisplayTabs().add("Position", snappingPanel);
		//SnapBox f = new SnapBox();
		//f.setSnappingBehaviour(l.getSnappingBehaviour().copy());
		//ItemSelectblePanel is = new ItemSelectblePanel("Select Location", f);
		//add("snap", is);
		return snappingPanel;
	}
	
	public void setObjectSnappingBehaviourToDialog(LocatedObject2D l) {
		if (snappingPanel==null) return;
		l.setSnappingBehaviour(snappingPanel.getSnappingBehaviour());
	}
	
	public void addFixedEdgeToDialog(BasicGraphicalObject l) {
		FixedEdgeSelectable f = new FixedEdgeSelectable(l.getLocationType());
		ItemSelectblePanel is = new ItemSelectblePanel("Select Fixed Edge", f);
		add("edge fix", is);
	}
	
	public void setFixedEdgeToDialog(LocatedObject2D l) {
		l.setLocationType(	this.getChoiceIndex("edge fix") );
	}
	
	public void addNameField(BasicGraphicalObject t) {
		StringInputPanel si = new StringInputPanel("Name", t.getName(),10);
		this.add("name", si);
	}
	
	public void setNameFieldToDialog(BasicGraphicalObject t) {
		t.setName(this.getString("name"));
	}
	
public void addStrokePanelToDialog(StrokedItem s) {
	strokeInput=new StrokeInputPanel(s);
	strokeInput.addObjectEditListener(this);
	this.place(strokeInput);
	//strokeInput.setUpFont(strokeInput.getFont().deriveFont((float)10));
}

public void revertAll() {
	super.revertAll();
	if (strokeInput!=null) strokeInput.revert();
}

public void setStrokedItemtoPanel(StrokedItem s) {
	strokeInput.setStrokedItemToPanel(s);
}

public void addInsetToDialog(Insets s) {
	if (s==null) s=new Insets(0,0,0,0);
	//int cod = s.hashCode();
	this.add("Insets Top", new NumberInputPanel("Inset Top",  s.top, true, true, 0,100));
	this.add("Inset Bottom", new NumberInputPanel("Inset Bottom",  s.bottom, true, true, 0,100));
	this.add("Insets Left", new NumberInputPanel("Inset Left",  s.left, true, true, 0,100));
	this.add("Inset Right", new NumberInputPanel("Inset Right",  s.right, true, true, 0,100));
	
}

public  Insets getInsetsPanelFromDialog() {
	 Insets s = new Insets(0,0,0,0);
	s.top=this.getNumberInt("Insets Top");
	s.bottom=this.getNumberInt("Inset Bottom");
	s.left=this.getNumberInt("Insets Left");
	s.right=this.getNumberInt("Inset Right");
	return s;
}





public static ImageWrapper getCurrentImage() {
	return currentImage;
}



public static void setCurrentImage(ImageWrapper currentImage) {
	GraphicItemOptionsDialog.currentImage = currentImage;
}



public static GraphicSetDisplayContainer getSetContainer() {
	return setContainer;
}

public static void updateCurrentDisplay() {
	if (setContainer!=null) setContainer.updateDisplay();
}


public static void setSetContainer(GraphicSetDisplayContainer setContainer) {
	GraphicItemOptionsDialog.setContainer = setContainer;
}



public boolean isUpdateAfterEachItemChange() {
	return updateAfterEachItemChange;
}



public void setUpdateAfterEachItemChange(boolean updateAfterEachItemChange) {
	this.updateAfterEachItemChange = updateAfterEachItemChange;
}
	

	
}
