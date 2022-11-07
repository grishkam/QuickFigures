/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2022.2
 */
package figureFormat;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.FigureType;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;
import appContext.CurrentAppContext;
import applicationAdapters.ImageWorkSheet;
import channelLabels.ChannelLabelProperties;
import channelLabels.MergeLabelStyle;
import channelLabels.ChannelLabelTextGraphic;

/** Figure template alter target figures to match a certain 
  example objects stored in the figure template. 
  */
public class FigureTemplate implements LayoutSpaces, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The classes below select and store example objects that define the properties of the template*/
	private LabelExamplePicker rowLabelPicker=new LabelExamplePicker(new TextGraphic() , ROW_OF_PANELS);
	private LabelExamplePicker colLabelPicker=new LabelExamplePicker(new TextGraphic() , COLUMN_OF_PANELS);
	private LabelExamplePicker titleLabelPicker=new LabelExamplePicker(new TextGraphic() , LayoutSpaces.ALL_MONTAGE_SPACE);
	private LabelExamplePicker panelLabelPicker=new LabelExamplePicker(new TextGraphic() , LayoutSpaces.PANELS);
	
	private LabelExamplePicker channelLabelPicker=new ChannelLabelExamplePicker(new ChannelLabelTextGraphic(new ChannelLabelProperties()));
	private ScaleBarExamplePicker scaleBarPicker=new ScaleBarExamplePicker(new BarGraphic(Color.white));
	MultichannelDisplayPicker	mdp=new MultichannelDisplayPicker(); {mdp.setModelItem(new MultichannelDisplayLayer(null));}
	GridLayoutExamplePicker layoutpicker =new GridLayoutExamplePicker(new DefaultLayoutGraphic());
	
	
	private GraphicalItemPicker<?>[] pickers=new GraphicalItemPicker[] {layoutpicker,  rowLabelPicker, colLabelPicker,  titleLabelPicker, getPanelLabelPicker(), getChannelLabelPicker(),scaleBarPicker };
	public ItemPicker<?>[] pickersReg=new ItemPicker[] {mdp};
	
	public boolean awaitingReset=false;

	private ArrayList<GraphicalItemPicker<?>> activePickerList;
	public FigureType suggestedType=null;
	
	public FigureTemplate() {
		
	}
	/**creates a new figure template*/
	public FigureTemplate(MultichannelDisplayLayer chan ) {
		this();
		autoGeneratePickersForDisplay(chan);
	}
	
	
	/**Applies the format defined by this template to the image
	 * @return */
	public CombinedEdit applyTemplateTo(ImageWorkSheet theImage) {
		return applyTemplateToLayer(theImage.getTopLevelLayer());
	}
	
	/**Applies the format defined by this template to the layer and its sublayers
	 * @return */
	public CombinedEdit applyTemplateToLayer(GraphicLayer theLayer) {
		
		try{
		ArrayList<GraphicLayer> l = new ArrayList<GraphicLayer>();
		l.add(theLayer);
		l.addAll(theLayer.getSubLayers());
		CombinedEdit undo = new CombinedEdit();
		undo.addEditToList(
				this.applyTemplateToList(theLayer.getAllGraphics()));
		undo.addEditToList(
				applyTemplateToList(l));
		
		FigureOrganizingLayerPane.updateAllPanelsFromSource(theLayer);//so that the color mode changes take effect
		return undo;
		}
		
			catch (Throwable t) {
				IssueLog.logT(t);
			return null;
		}
	}
	
	/**Applies the format defined by this template to the objects in the list 
	 * @return */
	public CombinedEdit applyTemplateToList(ArrayList<?> list) {
		CombinedEdit undo = new CombinedEdit();
		for(GraphicalItemPicker<?> pik:getAllExamplePickers()) {
			undo.addEditToList(
					pik.applyPropertiesToList(list));
		}
		for(ItemPicker<?> pik:pickersReg) {
			undo.addEditToList(
					pik.applyPropertiesToList(list));
			
		}
		return undo;
	}
	
	/**Returns a string summarizing the what types of items are used by this template*/
	public String summarizeContent() {
		String st="";
		for(GraphicalItemPicker<?> pik:getAllExamplePickers()) {
			st+="picker "+pik.getClass().getName()+'\n';
		}
		for(ItemPicker<?> pik:pickersReg) {
			st+="picker "+pik.getClass().getName()+'\n';
		}
		return st;
	}
	

	/**Returns a list of objects that store the example labels*/
	public ArrayList<ItemPicker<?>> getStartupLabelPickerList() {
		ArrayList<ItemPicker<?>> output=new ArrayList<ItemPicker<?>>();
			output.add(getRowLabelPicker());
			output.add( getColLabelPicker());
			output.add(getTitleLabelPicker());
			output.add(getPanelLabelPicker());
		return output;
	}
	

	public LabelExamplePicker getRowLabelPicker() {
		return rowLabelPicker;
	}
	public void setRowLabelPicker(LabelExamplePicker rowLabelPicker) {
		this.rowLabelPicker = rowLabelPicker;
	}
	public LabelExamplePicker getColLabelPicker() {
		return colLabelPicker;
	}
	public void setColLabelPicker(LabelExamplePicker colLabelPicker) {
		this.colLabelPicker = colLabelPicker;
	}
	public LabelExamplePicker getTitleLabelPicker() {
		return titleLabelPicker;
	}
	public void setTitleLabelPicker(LabelExamplePicker titleLabelPicker) {
		this.titleLabelPicker = titleLabelPicker;
	}
	public ScaleBarExamplePicker getScaleBar() {
		return scaleBarPicker;
	}
	public void setScaleBar(ScaleBarExamplePicker scaleBar) {
		this.scaleBarPicker = scaleBar;
	}
	public LabelExamplePicker getChannelLabelPicker() {
		return channelLabelPicker;
	}
	public void setChannelLabelPicker(LabelExamplePicker channelLabelPicker) {
		this.channelLabelPicker = channelLabelPicker;
	}
	
	
	/**creates a set of starter labels and scale bar from the list of model objects
	  Does not create channel labels as those are created by another method
	  */
	public void createDefaultLabelsObjectsFromTemplate( GraphicLayer l22, MultichannelDisplayLayer display, DefaultLayoutGraphic p) {
		
		if (this.awaitingReset) {
			autoGeneratePickersForDisplay(display);
		}
	
		if (l22==null) return;
		applyTemplateToLayer(l22);
	
		/**iterates through the pikers, letting them each add their model
		  to the layer. Locks them all to the Layout*/
		for(ItemPicker<?> item : getStartupLabelPickerList()) {
			if (item==null||item.getModelItem()==null||item instanceof ChannelLabelExamplePicker) continue;
			
			if (item.getModelItem() instanceof BasicGraphicalObject&&item.getModelItem() !=null) {
				 
				/**The model item is copied to produce the new item*/
				BasicGraphicalObject b=(BasicGraphicalObject) item.getModelItem();
				 LocatedObject2D cop = b.copy();
				 l22.add((ZoomableGraphic)cop);
				 p.addLockedItem(cop);
				 item.applyProperties(cop);
				 p.snapLockedItems();
				
				
				 /**If the item is a row, panel or column picker, this alters the montage label space
				    to include its area. Since the label starts out locked to the layout, the space is
				    not essensial to keep it in place*/
				 if (item instanceof LabelExamplePicker) {
						LabelExamplePicker rowLabp=(LabelExamplePicker) item;
						if (rowLabp.isInRowOrColumnOrPanel()) {
							
							p.generateCurrentImageWrapper();
							p.getEditor().expandSpacesToInclude(p.getPanelLayout(), cop.getBounds());
						}
					}
				 
				 
				 
			}
			
			
			
		};
		
		
		
		applyTemplateToLayer(l22);
		
	}
	
	/**If there is a model scale bar selected,
	 * this method will add a similar scale bar to 
	 * the merge panel of the principal graphic layer*/
	public void createScaleBarOffTemplate(FigureOrganizingLayerPane p) {
		ImageDisplayLayer display=p.getPrincipalMultiChannel();
		
		if (this.awaitingReset) {
			autoGeneratePickersForDisplay((MultichannelDisplayLayer) display);
		}
		
		BarGraphic oldsbar = getScaleBar().getModelItem();
		
		if (oldsbar!=null) {
			PanelList stack = display.getPanelList();
			int i =stack.getSize();
			if (i<=0) {IssueLog.log("Cannot create scale bar from template without a panel to put it");return;}
			Object displayob = stack.getPanels().get(i-1).getImageDisplayObject();
			if (displayob instanceof TakesAttachedItems) {
				BarGraphic newbar = new BarGraphic(Color.white);
				newbar.copyAttributesButNotScale(oldsbar);
				newbar.setAttachmentPosition(oldsbar.getAttachmentPosition());
				p.add(newbar);
				
				TakesAttachedItems t= (TakesAttachedItems) displayob ;
				t.addLockedItem(newbar);
				getScaleBar().applyProperties(newbar);
				
				/**optimize the bar length. */
				if (displayob instanceof LocatedObject2D) {
					LocatedObject2D panel = (LocatedObject2D)displayob;
					BarGraphic.optimizeBarLengths(newbar, panel);
					
				}
				
				
			}
		
			
			}
	}
	
	/**Adds a multichannel display to a a figure organizing layer and applies this template to the newly created items*/
	public void addDisplayToFigure(FigureOrganizingLayerPane currentFigureOrganizer, MultichannelDisplayLayer display ) {
		if (awaitingReset) {
			autoGeneratePickersForDisplay(display);
		}
		/**must apply before creating the layout so the minimum number of columns can be created*/
		applyTemplateToLayer(display);
		currentFigureOrganizer.addNovelMultiChannel(display, -1);
		/**must also apply after creating the layout and objects*/
		applyTemplateToLayer(currentFigureOrganizer);
	}
	

	/**creates new example picker objects that are suitable for the given display layer*/
	public void autoGeneratePickersForDisplay(MultichannelDisplayLayer multichannelDisplayLayer ) {
		
		try {
			/**if the image is small, this creates a picker with suitbale scale*/
			this.getMultiChannelPicker().setScaleAppropriateFor(multichannelDisplayLayer);
			if (getMultiChannelPicker().forceScale!=null)
				getMultiChannelPicker().applyProperties(multichannelDisplayLayer);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
		for(GraphicalItemPicker<?> pik: getAllExamplePickers())try  {
			
			pik.setTheSizeFor(multichannelDisplayLayer);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
	}
	
	/**returns a list of the example object pickers used in this template*/
	public ArrayList<GraphicalItemPicker<?>> getAllExamplePickers() {
		if(activePickerList !=null)
			return activePickerList ;
		activePickerList = new ArrayList<GraphicalItemPicker<?>>();
		for(GraphicalItemPicker<?> pi:pickers ) {
			activePickerList.add(pi);
		}
				return activePickerList;
	}
	
	/**changes the properties of this templates to a version for merge only
	  displays*/
	public void makeMergeOnly() {
		if (mdp==null) IssueLog.log("no example image display ( template fail)");
		ensureModelMultiChannel();
		
		
		mdp.getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.ONLY_MERGE_PANELS;
		mdp.getModelItem().getChannelLabelProp().setMergeLabelStyle(MergeLabelStyle.MULTIPLE_LINES);
	
	}

	
	/**changes the properties of this templates to a version for merge only
	  displays*/
	public void makeSplitChannel() {
		if (mdp==null) IssueLog.log("no example image display ( template fail)");
		ensureModelMultiChannel();
		
		/**if the current format is merge only, changes it to merge last*/
		boolean change = ChannelUseInstructions.ONLY_MERGE_PANELS==mdp.getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing;
		if(change) {
			mdp.getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.MERGE_LAST;
		}
		
	}
	
	/**
	 if the model multichannel for the default template is null, this creates one from 
	 the currently open image
	 */
	private void ensureModelMultiChannel() {
		if (mdp.getModelItem()==null) 
			{
			//if no example image display layer is in the template, creates one
			MultichannelDisplayLayer mid = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromOpenImage();
			if (mid.getMultiChannelImage()==null) {
				
			}
			if (mid!=null)
				mdp.setModelItem(mid);
			}
	}
	
	
	/**expands the label spaces of layouts in the layer to fit their contents 
	 * @return */
	public CombinedEdit fixupLabelSpaces(GraphicLayer graphicLayer) {
		ArrayList<ZoomableGraphic> items = graphicLayer.getAllGraphics();
		
		return fixupLabelSpaces(items);
		
	}
	/**expands the label spaces of layouts to fit their contents 
	 * @return */
	public CombinedEdit fixupLabelSpaces( ArrayList<ZoomableGraphic> items) {
		CombinedEdit undo = new CombinedEdit();
		for (ZoomableGraphic z: items) {
			
			if (z instanceof DefaultLayoutGraphic) {
				
				DefaultLayoutGraphic m=(DefaultLayoutGraphic) z;
				UndoLayoutEdit lEdit = new UndoLayoutEdit(m);
				m.snapLockedItems();
				m.updateDisplay();
				//selector.getGraphicDisplayContainer().updateDisplay();
				m.getEditor().fitLabelSpacesToContents(m.getPanelLayout());
				lEdit.establishFinalState();
				undo.addEditToList(lEdit);
			}
			if (z instanceof GraphicLayer) {
				undo.addEditToList(
						fixupLabelSpaces( ((GraphicLayer) z).getAllGraphics()));
			}
		}
		return undo;
	}
	
	/**Returns the picker for multichannel display layers*/
	public MultichannelDisplayPicker getMultiChannelPicker() {
		return mdp;
	}
	
	/**returns the label picker for panel labels*/
	public LabelExamplePicker getPanelLabelPicker() {
		return panelLabelPicker;
	}
	/**sets the label picker for panel labels*/
	public void setPanelLabelPicker(LabelExamplePicker panelLabelPicker) {
		this.panelLabelPicker = panelLabelPicker;
	}
	
	public GridLayoutExamplePicker getLayoutChooser() {return layoutpicker;}
	
	
	/**
	 * @param figure
	 */
	public void setToFigure(FigureOrganizingLayerPane figure) {
		ImageDisplayLayer m = figure.getPrincipalMultiChannel();
		this.getMultiChannelPicker().setModelItem(m);;
		this.getLayoutChooser().setModelItem(figure.getMontageLayoutGraphic());
		ArrayList<ChannelLabelTextGraphic> allLabels = m.getChannelLabelManager().getAllLabels();
		if (allLabels.size()>0)this.getChannelLabelPicker().setModelItem(allLabels.get(0));
		
		for(ZoomableGraphic all:figure.getAllGraphics())
			if (all instanceof BarGraphic )this.getScaleBar().setModelItem(all);
		
	}
	
	/**alters the variety of pickers available to match either a blot figure 
	 * @param graphicLayerSet
	 */
	public void setBasedOnAvailableFigureTypes(GraphicLayer graphicLayerSet) {
		ArrayList<FigureType> availableTypes = listAvailabletypes(graphicLayerSet);
		
		/**Some figure types tend to have frames around their images*/
		if(hasType(availableTypes, FigureType.WESTERN_BLOT, FigureType.ELECTRON_MICROSCOPY, FigureType.H_AND_E)) {
			this.getAllExamplePickers().add(new ImageFrameExamplePicker(null));
		}
		
		/**A slighly different combination of pickers is used for blots*/
		if(hasType(availableTypes, FigureType.WESTERN_BLOT)) {
			this.getAllExamplePickers().add(new BandMarkExamplePicker(null));
		}
		
		/**If no scale bar is needed for the figure types*/
		if(!hasType(availableTypes, FigureType.FLUORESCENT_CELLS, FigureType.ELECTRON_MICROSCOPY, FigureType.H_AND_E)) {
			this.getAllExamplePickers().remove(this.getScaleBar());
		}
		
		/**If no channel label is needed for the figure types*/
		if(!hasType(availableTypes, FigureType.FLUORESCENT_CELLS)) {
			this.getAllExamplePickers().remove(this.channelLabelPicker);
		}
		
		if(availableTypes.size()==1) {
			this.suggestedType=availableTypes.get(0);
		}
	}
	
	/**
	 * @param graphicLayerSet
	 * @return
	 */
	public static ArrayList<FigureType> listAvailabletypes(GraphicLayer graphicLayerSet) {
		ArrayList<FigureType> availableTypes=new ArrayList<FigureType>();
		addFigureTypeFor(graphicLayerSet, availableTypes);
		for(GraphicLayer l: graphicLayerSet.getSubLayers()) {
			addFigureTypeFor(l, availableTypes);
		}
		return availableTypes;
	}
	
	/***/
	public static FigureType getSuggestedFigureTypeFor(GraphicLayer l) {
		FigureType output=null;
		ArrayList<FigureType> types = listAvailabletypes(l);
		if(types.size()==1)
			return types.get(0);
		return output;
	}
	
	/**checks if the interable contains the object
	 * @param availableTypes is the available objects list that may or may not contain w2
	 * @param w2 is a list of objects that one is looking for
	 * @return
	 */
	private boolean hasType(Iterable<?> availableTypes, Object... w2) {
		for(Object w: w2) {
			for(Object o:  availableTypes) {
				if(o==w)
					return true;
			}
		}
		return false;
	}
	
	/**finds the figure type of the layer and adds it to the list.
	 * does not add null or items already on the list
	 * @param graphicLayerSet
	 * @param availableTypes
	 */
	private static void addFigureTypeFor(GraphicLayer l, ArrayList<FigureType> availableTypes) {
		if(l instanceof FigureOrganizingLayerPane) {
			 FigureType figureType = ((FigureOrganizingLayerPane) l).getFigureType();
			 if(availableTypes.contains(figureType)&&figureType!=null)
				return;
			availableTypes.add(figureType);
		}
		
	}
	
}
