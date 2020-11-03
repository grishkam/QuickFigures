package graphicalObjects_FigureSpecific;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.MultiChannelWrapper;
import fLexibleUIKit.MenuItemMethod;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import objectDialogs.ChannelSelectionDialog;
import undo.AbstractUndoableEdit2;
import undo.CompoundEdit2;
import undo.Edit;
import undo.PreprocessChangeUndo;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoScaling;
import utilityClassesForObjects.RectangleEdges;

/**handles the adding and removing of channel display panels
 */
public class PanelManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private PanelList stack;
	private MultichannelDisplayLayer display;
	
	private transient MultiChannelWrapper multi;
	private int defaultFrameWidth;

	public PanelManager(MultichannelDisplayLayer multichannelImageDisplay, PanelList stack,
			GraphicLayer multichannelImageDisplay2) {
		this.display=multichannelImageDisplay;
		this.setStack(stack);
		this.layer=multichannelImageDisplay2;
		if(layer==null) layer=multichannelImageDisplay;
		setMultiChannelWrapper(multichannelImageDisplay.getMultichanalWrapper());
		
	}
	
	/**Creates a panel Graphic*/
	protected ImagePanelGraphic generatePanelGraphicFor(PanelListElement panel) {
		if (panel==null) return null;
		ImagePanelGraphic panelgraphic = new ImagePanelGraphic();
		panelgraphic.setSourcePanel(panel);
			
			panelgraphic.setScaleInfo(panel.getDisplayScaleInfo());//sets the scale info
			panelgraphic.setScale(getPanelLevelScale());
			panelgraphic.setFrameWidthH(getDefaultFrameWidth());
			//logTime("done setting image and scale");
			panelgraphic.setEmbed(true);
			//logTime("done embedding image and scale");
			panel.setImageDisplayObject(panelgraphic);
			String name=panel.getName();
			if(name==null) name="";
			
			
			try {
				
				String indexname="";
				if (display.getMultichanalWrapper().nFrames()>1)
					indexname+="t:"+panel.originalFrameNum+" "; 
				if (display.getMultichanalWrapper().nSlices()>1)
					indexname+="s:"+panel.originalSliceNum;
				
				if (panel.isTheMerge()&&indexname.length()>1) {
					name="Merge "+indexname;
				}
				if (!panel.isTheMerge()&&"".equals(name)) {
					name="c:"+panel.originalChanNum+" "+indexname;
				}
			} catch (Throwable t) {}
			
			panelgraphic.setName(name);
			
			/**if (this.getParentLayer()!=null ) this.getParentLayer().*/
			layer.add(panelgraphic);
			
			panel.createChanSwapHandles(panelgraphic).setDisplayLayer(display);
			 
			//
			
			return panelgraphic;
	}
	
	public ImagePanelGraphic getImagePanelFor(PanelListElement panel) {
		if (panel==null) return null;
		if (panel.getImageDisplayObject() instanceof ImagePanelGraphic) {
			return (ImagePanelGraphic) panel.getImageDisplayObject();
		}
		return null;
	}
	
	public PanelListElement  getListElementFor(ImagePanelGraphic image) {
		if (image==null) return null;
		if (stack==null) return null;
		for(PanelListElement panel: stack.getPanels()) {
			if (panel.getImageDisplayObject() ==image) {
				return panel;
			}
		}
		return null;
	}
	
	public double getPanelLevelScale() {
		return stack.getPanelLevelScale();
	}
	
	public void setPanelLevelScale(double panelLevelScale) {
		stack.setPanelLevelScale(panelLevelScale);
	}
	

	/**generates the panel graphics and adds then all to the parent layer*/
	@MenuItemMethod(menuActionCommand = "paneloptions2", menuText = "Generate New Panels", subMenuName="Image Panels")
	public void generatePanelGraphics() {
	
		getStack().addAllCandF(display.getMultichanalWrapper());
		
		generatePanelGraphicsFor(getStack());
		
		
	}
	
	/**Creates a panelGraphic for the list elements and adds them
	 to the current layer for this panel Manager. Also returns those items
	 * @return */
	public ArrayList<ImagePanelGraphic> generatePanelGraphicsFor(PanelList p) {
		ArrayList<ImagePanelGraphic> items=new ArrayList<ImagePanelGraphic> ();
			if (p!=null) for(PanelListElement panel: p.getPanels()) {
			items.add(generatePanelGraphicFor(panel));
		}
			return items;
	}
	
	
	public void eliminatePanels() {
		eliminatePanels(getStack());
	}
	
	@MenuItemMethod(menuActionCommand = "panelgone", menuText = "Eliminate Panels", subMenuName="Image Panels")
	public CompoundEdit2 eliminatePanels(PanelList stack) {
		if (stack==null) return null;
		CompoundEdit2 output = new CompoundEdit2();
		ArrayList<ImagePanelGraphic> arr = stack.getPanelGraphics();
		for(ImagePanelGraphic g:arr) {
			output.addEditToList(
			Edit.removeItem(layer,g));
			if(g.getScaleBar()!=null) {
				output.addEditToList(
						Edit.removeItem(g.getScaleBar().getParentLayer(),g.getScaleBar()));
			}
		}
		stack.eliminateAllPanels();
		return null; //undo is incomplete and unfinished
 	}
	
	
	/**removes all objects associated with the panels including imagePanelGraphics,
		channelLabels and scale bars. returns an undo for the action*/
	public CompoundEdit2 removeDisplayObjectsForAll() {
		CompoundEdit2 output = new CompoundEdit2();
		if (stack==null) return  output;
		
		PanelList arr = stack;
		
		for(PanelListElement  g:arr.getPanels()) {
			 output.addEditToList(
					removeDisplayObjectsFor(g)
			);
		}
		return output;
	}
	
	/**removes the display objects for the given panel list element from ther layer.
	  returns a list of what has been removed
	 * @return */
	public CompoundEdit2 removeDisplayObjectsFor(PanelListElement g) {
		CompoundEdit2 itemsTaken=new CompoundEdit2();
		
			itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, g.getPanelGraphic()));
			layer.remove(g.getPanelGraphic()); 
			
		BarGraphic bar = g.getPanelGraphic().getScaleBar();
		if (bar!=null) {
			itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, bar));
			layer.remove(bar); 
			if (layer.getParentLayer()!=null)
				layer.getParentLayer().remove(bar);
			// itemsTaken.add(bar);
		}
		
		itemsTaken.addEditToList(new UndoAbleEditForRemoveItem(layer, g.getChannelLabelDisplay()));
		layer.remove(g.getChannelLabelDisplay());
		
		 
		 return itemsTaken;
	}
	
	private PanelListElement generateSingleChannelPanel(PanelList stack, int channel, int slice, int frame) {
		PanelListElement panel = stack.getOrCreateChannelPanel(getMultiChannelWrapper(),channel, slice, frame);
		
		 if (panel!=null)this.generatePanelGraphicFor(panel);
		 else IssueLog.log("null for requested panel"); 
		return panel;
	}
	

	
	
	
	private PanelListElement generateSingleMergePanel(PanelList stack, int slice, int frame) {
		PanelListElement panel =stack.getOrCreateMergePanel( display.getMultichanalWrapper(), slice, frame);
	
		 if (panel!=null)this.generatePanelGraphicFor(panel);
		 else IssueLog.log("null for requested panel"); 
		 return panel;
	}
	
	@MenuItemMethod(menuActionCommand = "1merge", menuText = "Create 1 Merge Panel", subMenuName="Image Panels")
	public void addSingleMergePanel(PanelList stack) {
		ChannelSelectionDialog dia = new ChannelSelectionDialog(1,1,1,display.getMultichanalWrapper());
		dia.show2DimensionDialog();
		
		generateSingleMergePanel(stack, dia.getSlice(),dia.getFrame());
	}
	
	
	
	@MenuItemMethod(menuActionCommand = "1chan", menuText = "Create New Panel", subMenuName="Image Panels")
	public PanelListElement addSingleChannelPanel(PanelList stack) {
		ChannelSelectionDialog dia = new ChannelSelectionDialog(1,1,1, display.getMultichanalWrapper());
		dia.show3DimensionDialog();
		if (dia.getChannel()==0) { return generateSingleMergePanel(stack, dia.getSlice(),dia.getFrame());} else
		return generateSingleChannelPanel(stack, dia.getChannel(), dia.getSlice(),dia.getFrame());
	}
	
	public int getDefaultFrameWidth() {
		return defaultFrameWidth;
	}



	public void setDefaultFrameWidth(int defaultFrameWidth) {
		this.defaultFrameWidth = defaultFrameWidth;
	}

	public PanelList getStack() {
		return stack;
	}

	public void setStack(PanelList stack) {
		this.stack = stack;
	}

	public MultiChannelWrapper getMultiChannelWrapper() {
		return multi;
	}

	public void setMultiChannelWrapper(MultiChannelWrapper multi) {
		this.multi = multi;
	}
	
	public synchronized void updatePanels() {
		MultiChannelWrapper impw =multi;
		getStack().resetChannelEntriesForAll(impw);
		getStack().updateAllPanelsWithImage(impw);
		
	}
	
	/**updates the panels that include the given channel from the source
	  multichannel stack. This is primarily used after display ranges are changed.
	  Also after cropping, or flipping of the source image. And used 
	  after creation of the image panels.
	 * */
	public synchronized void updatePanelsWithChannel(String realChannelName) {
		getStack().updateAllPanelsWithImage(multi, realChannelName);
	}
	
	public MultichannelDisplayLayer getDisplay() {
		return display;
	}
	
	
	/**Given an array of panels, returns them in an altered order. Ones with 
	 * display panels in the first rectangle go first, second to the second and so on.
	 * All leftover panels are put in the end*/
	public static ArrayList<PanelListElement> getPanelsInLayoutOrder(ArrayList<PanelListElement> input, Rectangle2D[] r) {
		ArrayList<PanelListElement> output = new ArrayList<PanelListElement>();
		
		
		for(int i=0; i<r.length; i++) {
				Rectangle2D rect = r[i];
				
				/**Checks whether each element is inside the rectangle, if so adds it to the list if not already in it*/
				for(PanelListElement panel1: input) {
					if (output.contains(panel1)) continue;
					
					ImagePanelGraphic item = panel1. getPanelGraphic();
					if(item.doesIntersect(rect)) {
						output.add(panel1); 
						continue;}
					if(item.isInside(rect)) {
						output.add(panel1); continue;
						}
				}
				
		}
		
		/**adds all remaining panels that have not yet been added to the output*/
		for(PanelListElement panel1: input) {
			if (output.contains(panel1)) continue;
			
			 {output.add(panel1);}
		}
		
		
		return output;
		
	}
	
	/**When given a single panel list element, finds an empty spot in the layout
	   and puts the panel in that location. If no empty spot exists, it creates one*/
	public void putSingleElementOntoGrid(PanelListElement p, boolean addIfNeeded) {
		BasicMontageLayout layout = getLayout();
		int index = layout.getEditor().indexOfFirstEmptyPanel(layout, 1, 0);
		
		if ((index>layout.nPanels()||index==0)&&addIfNeeded) {
			if (layout.rowmajor)
			layout.getEditor().addCols(layout, 1);
			else 
				layout.getEditor().addRows(layout, 1);
			
		}
		
		if (index>0&&index<=layout.nPanels()) {
			this.getDisplay().getSetter().putDisplayObjectForPanelInRect(p, layout.getPanel(index));
			}
	}
	
	

	public MontageLayoutGraphic getGridLayout(GraphicLayer layer) {
		if (layer==null) return null;
		ArrayList<ZoomableGraphic> arr = layer.getItemArray();
		if (arr==null) return null;
		for(ZoomableGraphic a:arr) {
			if (a==null) continue;
			if (a instanceof MontageLayoutGraphic) {
				MontageLayoutGraphic m=(MontageLayoutGraphic) a;
				
				m.generateStandardImageWrapper();
				return m;
						};
		}
		return null;
	}
	
	public MontageLayoutGraphic getGridLayout() {
		MontageLayoutGraphic layoutGraphic=this.getGridLayout(layer);
		if (layoutGraphic==null &&layer.getParentLayer()!=null) layoutGraphic=this.getGridLayout(layer.getParentLayer());
		if (layoutGraphic==null) return null;
		return layoutGraphic;
	}
	
	public BasicMontageLayout getLayout() {
		MontageLayoutGraphic layoutGraphic=getGridLayout();
		
		if (layoutGraphic==null) return null;

		return layoutGraphic.getPanelLayout();
	}

	/**Updates the Image display canvas*/
	public void updateDisplay() {
		display.getGraphicSetContainer().updateDisplay();
	}

	/**returns that layer that this panel manager adds/removes panels to and from*/
	public GraphicLayer getLayer() {
		return layer;
	}
	
	/**alters the PPI of the figure.
	 * @return */
	public CompoundEdit2 changePPI(double newppi) {
		ImagePanelGraphic panel = getStack().getPanels().get(0).getPanelGraphic();
		double ppi = panel.getQuickfiguresPPI();
		double newPanelScale=panel.getScale()*ppi/newppi;
		double newScale=getDisplay().getPreprocessScale()*newppi/ppi;
		
		CompoundEdit2 output = new CompoundEdit2();
		
		output.addEditToList(
				imposePanelLevelScale(newPanelScale));
		
		output.addEditToList(new PreprocessChangeUndo(getDisplay()));
		getDisplay().setPreprocessScale(newScale);
		
		
		updatePanels();
		output.addEditToList(new PanelManagerUndo2(this));
		
		output.establishFinalState();
		return output;
	}

	protected CompoundEdit2 imposePanelLevelScale(double newPanelScale) {
		CompoundEdit2 output=new CompoundEdit2();
		
		for(PanelListElement panel2: getStack().getPanels()) {
		
			ImagePanelGraphic panelGraphic = panel2.getPanelGraphic();
			
			output.addEditToList(new UndoScaling(panelGraphic));
			
			panelGraphic.setLocationType(RectangleEdges.UPPER_LEFT);
			panelGraphic.setScale(newPanelScale);
		}
		output.addEditToList(new PanelManagerUndo(this));
		this.setPanelLevelScale(newPanelScale);
		output.establishFinalState();
		return output;
	}
	
	class PanelManagerUndo extends AbstractUndoableEdit2 {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		double iScale;
		double fScale;
		private PanelManager pm;
		
		public PanelManagerUndo(PanelManager pm) {
			this.pm=pm;
			iScale=pm.getPanelLevelScale();
		}
		public void undo() {pm.setPanelLevelScale(iScale);}
		public void redo() {pm.setPanelLevelScale(fScale);}
		public void establishFinalState() {
			fScale=pm.getPanelLevelScale();
		}
	}
	
	class PanelManagerUndo2 extends AbstractUndoableEdit2 {
		private static final long serialVersionUID = 1L;
		private PanelManager pm;
		
		public PanelManagerUndo2(PanelManager pm) {
			this.pm=pm;
		}
		public void undo() {pm.updatePanels();}
		public void redo()  {pm.updatePanels();}
		public void establishFinalState() {
		}
	}

}
