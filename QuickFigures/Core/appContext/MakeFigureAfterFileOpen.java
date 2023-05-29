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
 * Date Created: Mar 23, 2022
 * Date Modified:Dec 3, 2022
 * Version: 2023.2
 */
package appContext;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;

import addObjectMenus.FigureAdder;
import channelMerging.ImageDisplayLayer;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.ImageWindowAndDisplaySet;
import layout.basicFigure.BasicLayout;
import locatedObject.Selectable;
import logging.IssueLog;
import messages.ShowMessage;
import objectDialogs.CroppingDialog;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**
 Contains all the methods to make a figure at a drag/drop location on a worksheet or add to an existing figure.
 Also regosnises a file name.
 */
public class MakeFigureAfterFileOpen implements PendingFileOpenActions  {

	 PreProcessInformation preprocessForNewFigure=null;
	FigureAdder figureAdder = new FigureAdder(true);
	private Point2D location2;
	private GraphicLayer layer;
	/**The actions that are done by this object are added to this undo*/
	CombinedEdit undo;
	
	/**this object waits for a specific file to be opened*/
	File expectedFile;
	
	/**Will add the file to teh worksheet window listed here. if that one is closed*/
	ImageWindowAndDisplaySet window;
	/**whether this action is already consumed*/
	boolean finished=false;
	protected long time;
	public ExistingFigure theExistingFigure=new ExistingFigure();
	
	/**
	 * 
	This enclosed type carries properties that may be shared by multiple make figure at drop locations
		 */
	public static class ExistingFigure{

		
		int startIndex;
		FigureOrganizingLayerPane figure;
		BasicLayout thelayout;

		/**
		 * @param figure
		 * @param ml
		 * @param startIndex
		 */
		public ExistingFigure(FigureOrganizingLayerPane figure, BasicLayout ml, int startIndex) {
			setStoredFigure(figure, ml, startIndex);
		}

		/**
		 * @param figure
		 * @param ml
		 * @param startIndex
		 */
		public void setStoredFigure(FigureOrganizingLayerPane figure, BasicLayout ml, int startIndex) {
			this.startIndex=startIndex;
			this.figure=figure;
			 this.thelayout=ml;
		}
		
		/**
		 * 
		 */
		public ExistingFigure() {
			// TODO Auto-generated constructor stub
		}

		public boolean present() {return figure!=null;}

		/**
		 * 
		 */

	}
	
	/**
	Tells the user if his file is to big
	 */
	protected void showFileSizeWarning(MultichannelDisplayLayer item) {
		IssueLog.log("Checking for size warning");
		int size = item.getSlot().getEstimatedSizeOriginal();
		if (size>16*1500*1000*4*2) {
			ShowMessage.showOptionalMessage("That is a large file!", false, "You added a very large file that takes a lot of memory",
					"The original is kept in case you want to change the scale or crop later", 
					"No figure needs to be this large",
					"Please use smaller version of the image in the future ");
		}
		item.attemptSizeManageMentDialog(false);
	}
	
	/**selects an item. if that  item is a layer, selects everything inside the layer*/
	public static void select(Object z) {
		if (z instanceof Selectable) ((Selectable) z).select();
		if (z instanceof GraphicLayer) {
			
			for(ZoomableGraphic l: ((GraphicLayer) z).getAllGraphics()) {
				select(l);
			};
		}
	}

	
	/**returns true if this is still in use*/
	public boolean isActive() {
		
		if(finished) {
		
			return false;
			}
		if(window!=null&&!window.getTheWindow().isVisible()) {
			
			return false;
			}
		
		/**does not wait more than a minute*/
		if(System.currentTimeMillis()-time>1000*60)			
			return false;
		
		return true;
	}

	@Override
	public boolean isTargetFile(String path) {
		if(path!=null) {
			boolean  output=path.contentEquals(expectedFile.getAbsolutePath())||path.contains(expectedFile.getAbsolutePath());
			if(output==false) {
				IssueLog.log("files do not match "+path);
				IssueLog.log("files do not match "+expectedFile.getAbsolutePath());
			} else {
				IssueLog.log("file names match "+path);
			}
			return output;
		}
		return false;
	}

	@Override
	public void performActionOnImageDisplayLayer(MultichannelDisplayLayer zero) {
		
		addNewImage(zero);
		zero.closeWindow(false);
		this.finished=true;
		
	}
	/**
	 * @param imageAndDisplaySet
	 * @param layer
	 * @param undo
	 * @param location2
	 * @param f 
	 * @param figurecontext 
	 */
	public MakeFigureAfterFileOpen(ImageWindowAndDisplaySet imageAndDisplaySet, GraphicLayer layer, CombinedEdit undo,
			Point2D location2, File f, ExistingFigure figurecontext) {
		this.theExistingFigure=figurecontext;
		this.undo=undo;
		window=imageAndDisplaySet;
		this.layer=layer;
		this.location2=location2;
		this.expectedFile=f;
		time=System.currentTimeMillis();
	}
	
	public MakeFigureAfterFileOpen(FigureOrganizingLayerPane fpane, CombinedEdit undo, File f) {
		this.theExistingFigure.setStoredFigure(fpane, fpane.getLayout(), -1);
		this.undo=undo;
		
		this.layer= fpane;
		this.location2=new Point();
		this.expectedFile=f;
		time=System.currentTimeMillis();
	}

	
	protected void addNewImage(MultichannelDisplayLayer zero) {
		createNewFigureToDropLocation(zero);
		finished=true;

	}
	
	/**Creates a figure at the drop location. user may stop this action by hitting cancel in the dialog
	 * @param item
	 * @param imageAndDisplaySet
	 * @param layer
	 * @param undo
	 * @param location2
	 */
	public void createNewFigureToDropLocation(MultichannelDisplayLayer item) {
		
		if(this.theExistingFigure.present()) {
			addNewImage(item, theExistingFigure);
			return;
		}
		
		
		CroppingDialog.lastUserCancel=false;
		FigureOrganizingLayerPane aa=null;
		
		try {
			aa = figureAdder.addNewlyOpenedDisplayLayer(item, layer, preprocessForNewFigure);
			aa.getMontageLayoutGraphic().moveLayoutAndContents(location2.getX(), location2.getY());
			this.showFileSizeWarning((MultichannelDisplayLayer) aa.getPrincipalMultiChannel());
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		if(CroppingDialog.lastUserCancel)
				{
			CroppingDialog.lastUserCancel=false;
			new UndoAddItem(layer, aa).undo();
				return;
				}
		
		window.updateDisplay();
		aa.fixLabelSpaces();
		
		undo.addEditToList(new UndoAddItem(layer, aa));
		
		
		theExistingFigure.setStoredFigure(aa, aa.getLayout(), theExistingFigure.startIndex);
	}
	
	/**what to do if drop target is a figure that already exists rather than empty space
	 * @param item
	 */
	public void addNewImage(MultichannelDisplayLayer item, ExistingFigure existingFigure) {
				if (existingFigure.startIndex>0&&existingFigure.figure.getPrincipalMultiChannel()!=null)
				{
				ImageDisplayLayer principalMultiChannel = existingFigure.figure.getPrincipalMultiChannel();
				
				int numberOfEmptyNeeded = principalMultiChannel.getPanelManager().getPanelList().getChannelUseInstructions().estimageNPanels(item.getMultiChannelImage());
				existingFigure.startIndex=existingFigure.figure.getMontageLayout().getEditor().indexOfFirstEmptyPanel(existingFigure.thelayout, numberOfEmptyNeeded, existingFigure.startIndex-1);
				
				}
		
			
				
			undo.addEditToList(existingFigure. figure.nextMultiChannel(item,existingFigure.startIndex));
			
			for(ZoomableGraphic g:item.getAllGraphics()) {
				select(g);
			}
			existingFigure.figure.getMontageLayoutGraphic().select();
			existingFigure.figure.getMontageLayoutGraphic().generateCurrentImageWrapper();
			if (existingFigure.figure.hasItem(item)) {
				showFileSizeWarning(item);
			}
			
			finished=true;
	}

	/**if the image display layer is null (which can occur if the image is not yet open/opening in another thread)
	 * then this class will add itself to the pening action list.
	 * Otherwise, it just performs the action.
	 * @param item
	 */
	public void complteOrPostcomeAction(MultichannelDisplayLayer item) {
		if(item==null) {
			PendingFileOpenActions.pendingList.add(  this);
		} else
			createNewFigureToDropLocation(item);
		
	}

}
