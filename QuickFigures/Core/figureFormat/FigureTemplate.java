/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package figureFormat;

import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import genericMontageKit.PanelList;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.LayoutSpaces;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.TakesLockedItems;
import appContext.CurrentAppContext;
import applicationAdapters.ImageWrapper;
import channelLabels.ChannelLabelProperties;
import channelLabels.ChannelLabelTextGraphic;

/**Objects of class figure template alter target figures to match a certain 
  example objects. 
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
	private ScaleBarExamplePicker scaleBarPicker=new ScaleBarExamplePicker(new BarGraphic());
	MultichannelDisplayPicker	mdp=new MultichannelDisplayPicker(); {mdp.setModelItem(new MultichannelDisplayLayer(null));}
	GridLayoutExamplePicker layoutpicker =new GridLayoutExamplePicker(new MontageLayoutGraphic());
	
	
	private GraphicalItemPicker<?>[] pickers=new GraphicalItemPicker[] {layoutpicker,  rowLabelPicker, colLabelPicker,  titleLabelPicker, getPanelLabelPicker(), getChannelLabelPicker(),scaleBarPicker };
	public ItemPicker<?>[] pickersReg=new ItemPicker[] {mdp};
	
	public boolean awaitingReset=false;
	
	
	public FigureTemplate() {
		
	}
	/**creates a new figure template*/
	public FigureTemplate(MultichannelDisplayLayer chan ) {
		this();
		autoGeneratePickersForDisplay(chan);
	}
	
	/**the standard template for new QuickFigure creations.
	  this template will not have any default for row column or panel labels
	private static FigureTemplate createStandardTemplate() {
		FigureTemplate ft = new FigureTemplate();
		ft.panelLabelPicker=null;
		ft.colLabelPicker=null;
		ft.rowLabelPicker=null;
		return ft;
	}*/
	
	/**Applies the format defined by this template to the image
	 * @return */
	public CombinedEdit applyTemplateTo(ImageWrapper theImage) {
		return applyTemplateToLayer(theImage.getGraphicLayerSet());
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
		return undo;
		}
		
			catch (Throwable t) {
			t.printStackTrace();
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
	public void createDefaultLabelsObjectsFromTemplate( GraphicLayer l22, MultichannelDisplayLayer display, MontageLayoutGraphic p) {
		
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
			if (displayob instanceof TakesLockedItems) {
				BarGraphic newbar = new BarGraphic();
				newbar.copyAttributesButNotScale(oldsbar);
				newbar.setAttachmentPosition(oldsbar.getAttachmentPosition());
				p.add(newbar);
				
				TakesLockedItems t= (TakesLockedItems) displayob ;
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
	public void autoGeneratePickersForDisplay(MultichannelDisplayLayer chan ) {
		IssueLog.log("Figure Template Reset, generating standard from multichannel" );
		
		for(GraphicalItemPicker<?> pik: getAllExamplePickers())try  {
			IssueLog.log("Figure Template Reset, generating standard for "+pik.getOptionName());
			pik.setToStandardFor(chan);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	/**returns a list of the example object pickers used in this template*/
	public Iterable<GraphicalItemPicker<?>> getAllExamplePickers() {
		ArrayList<GraphicalItemPicker<?>> outputpickers = new ArrayList<GraphicalItemPicker<?>>();
		for(GraphicalItemPicker<?> pi:pickers ) {
			outputpickers.add(pi);
		}
				return outputpickers;
	}
	
	/**changes the properties of this templates to a version for merge only
	  displays*/
	public void makeMergeOnly() {
		if (mdp==null) IssueLog.log("no example image display (innitial template fail)");
		if (mdp.getModelItem()==null) 
			{
			//if no example image display layer is in the template, creates one
			MultichannelDisplayLayer mid = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromOpenImage();
			if (mid!=null)
			mdp.setModelItem(mid);
			}
		
		
		mdp.getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.ONLY_MERGE_PANELS;
		mdp.getModelItem().getChannelLabelProp().setMergeLabelStyle(ChannelLabelProperties.MULTIPLE_LINES);
	
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
			
			if (z instanceof MontageLayoutGraphic) {
				
				MontageLayoutGraphic m=(MontageLayoutGraphic) z;
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
	public LabelExamplePicker getPanelLabelPicker() {
		return panelLabelPicker;
	}
	public void setPanelLabelPicker(LabelExamplePicker panelLabelPicker) {
		this.panelLabelPicker = panelLabelPicker;
	}
}