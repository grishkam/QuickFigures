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
 * Version: 2021.1
 */
package figureOrganizer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.undo.UndoableEdit;

import appContext.ImageDPIHandler;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.Scales;
import messages.ShowMessage;
import undo.CombinedEdit;
import undo.PreprocessChangeUndo;
import undo.UndoScalingAndRotation;

/**performs scaling operations on entire figures*/
public class FigureScaler {
	
	boolean alterSourceImageScale=true;
	
	/**creates a figure scaler
	 * @param linScale determines whether this object alters the scale factor 
	  applied to the original image */
	public  FigureScaler(boolean linScale) {
		this.alterSourceImageScale=linScale;
	}

	/**returns a factor that would bring the figure to slide size*/
	public double getSlideSizeScale(PanelLayoutGraphic item) {
		if (item.getBounds().width>500) return 1;
		double factor=650.00/(item.getBounds().width+item.getBounds().x);
		
		double idealPanelPixelDensity = 300.0;
		double [] factors= new double []{ idealPanelPixelDensity/200, idealPanelPixelDensity/150, idealPanelPixelDensity/100, idealPanelPixelDensity/ImageDPIHandler.getInchDefinition(), idealPanelPixelDensity/50};
		for (int i=0; i<factors.length; i++) {
		    if (factor<factors[i]&&i>0)  {
		    	factor=factors[i-1];
		    	break;}
		}
		
		return factor;
	}
	
	/**scales the figure*/
	public CombinedEdit scaleFigure(PanelLayoutGraphic item, double factor, Point2D about ) {
		GraphicLayer parentLayer = item.getParentLayer();
		
		return scaleLayer(factor, about, parentLayer);
	}

	/**scales the layer*/
	public CombinedEdit scaleLayer(double factor, Point2D about, GraphicLayer parentLayer) {
		CombinedEdit undo = new CombinedEdit();
		
		ArrayList<ImagePanelGraphic> panelsInFigure = getAllPanelGraphics(parentLayer);
		
		ArrayList<ZoomableGraphic> ii = parentLayer.getAllGraphics();

		for(ZoomableGraphic xg: ii) {
			
			if (xg instanceof Scales  ) {
				Scales s=(Scales) xg;
				UndoScalingAndRotation edit = new UndoScalingAndRotation(s);
				
				s.scaleAbout(about, factor);
				edit.establishFinalState();
				undo.addEditToList(edit);
				
				/**undoes the scaling for panels if a redo of the source image scale will be performed*/
				if (( alterSourceImageScale&&(xg instanceof ImagePanelGraphic))&&panelsInFigure.contains(xg)) {
					ImagePanelGraphic image=(ImagePanelGraphic) xg;
					image.setRelativeScale(image.getRelativeScale()/factor);
					edit.establishFinalState();
				}
				
			}
		}
		
		if (alterSourceImageScale) {
			undo.addEditToList(
					scaleDisplays(parentLayer, factor));
			updateDisplays(parentLayer);
			}
		else {
			panelLevelScaleDisplays(parentLayer, factor);
		}
		
		
		return undo;
	}
	
	/**Alters the bilinear scale factor for all the multichannel displays that use
	  this layout
	  */
	CombinedEdit scaleDisplays(GraphicLayer layer, double factor) {
		CombinedEdit undo = new CombinedEdit();
		
		
		if (layer instanceof ImageDisplayLayer) {
			undo.addEditToList(
			scaleDisplay((ImageDisplayLayer) layer, factor));
			//if parent layer is a panel stack display, then only it needs scaling 
		} else 
		
		if (layer instanceof FigureOrganizingLayerPane) {
			for(ImageDisplayLayer disp1: ((FigureOrganizingLayerPane) layer).getMultiChannelDisplays()) {
				undo.addEditToList(
				scaleDisplay(disp1, factor));
			}
		}else {
		
		}
		
		return undo;
		
	}
	
	/**Alters the relative scale factor stored within all the multichannel displays that use
	  this layout
	  */
	CombinedEdit panelLevelScaleDisplays(GraphicLayer layer, double factor) {
		CombinedEdit undo = new CombinedEdit();
		
		
		if (layer instanceof ImageDisplayLayer) {
			undo.addEditToList(
			panelLeveScaleDisplay((ImageDisplayLayer) layer, factor));
			//if parent layer is a panel stack display, then only it needs scaling 
		} else 
		
		if (layer instanceof FigureOrganizingLayerPane) {
			for(ImageDisplayLayer disp1: ((FigureOrganizingLayerPane) layer).getMultiChannelDisplays()) {
				undo.addEditToList(
						panelLeveScaleDisplay(disp1, factor));
			}
		}else {
		}
		
		return undo;
		
	}
	
	/**Alters the relative scale factor stored within the image display layer
	 by a certain factor
	  */
	private UndoableEdit panelLeveScaleDisplay(ImageDisplayLayer layer, double factor) {
		double s = layer.getPanelManager().getPanelLevelScale()*factor;
		 layer.getPanelManager().setPanelLevelScale(s);
		return new CombinedEdit();
	}

	/**refreshes the image display layers*/
	void updateDisplays(GraphicLayer layer) {

		if (layer instanceof ImageDisplayLayer) {
			((ImageDisplayLayer) layer).updatePanels();;
		}
		
		
		
		if (layer instanceof FigureOrganizingLayerPane) {
			for(ImageDisplayLayer disp1: ((FigureOrganizingLayerPane) layer).getMultiChannelDisplays()) {
		
				disp1.updatePanels();
			}
		}
		
	}
	private PreprocessChangeUndo scaleDisplay(ImageDisplayLayer layer, double factor) {
		PreprocessChangeUndo undoer = new PreprocessChangeUndo(layer);//scaleStack( layer.getStack(), factor);
		
		double nScale = layer.getPreprocessScale();
		layer.setPreprocessScale(nScale*factor);
		undoer.establishFinalLocations();
		return undoer;
	}
	
	
	/**Scales several figures at once
	 * @return */
	public CombinedEdit scaleMultipleFigures(ArrayList<PanelLayoutGraphic> layouts, Point2D loc, double factor) {
	
		CombinedEdit undo = new CombinedEdit();
		for(PanelLayoutGraphic ob: layouts) {
			undo.addEditToList(
					scaleFigure(ob, factor, loc)
					);
		}
		
		return undo;
	}
	
	/**returns all the image panels that are managed by the panel managers within the layer*/
	ArrayList<ImagePanelGraphic>  getAllPanelGraphics(GraphicLayer gl) {
		ArrayList<ImagePanelGraphic> items= new 	ArrayList<ImagePanelGraphic>();
		ArrayList<PanelManager> managers = getPanelManagers(gl);
		for(PanelManager man: managers) {
			items.addAll(man.getPanelList().getPanelGraphics());
		}
		
		return items;
	}
	
	/**returns every panel manager present in the layer*/
	public static ArrayList<PanelManager> getPanelManagers(GraphicLayer gl) {
		GraphicLayer layer = gl;//the search layer
		ArrayList<PanelManager> output=new ArrayList<PanelManager>();
		
		/**If the starting layer is a sublayer of the figu*/
		while(layer!=null&& !(layer instanceof MultichannelDisplayLayer) &&!(layer instanceof FigureOrganizingLayerPane)) {
			layer=layer.getParentLayer();
		}
		
		if (layer==null) return output;
		for(ZoomableGraphic item: layer.getObjectsAndSubLayers()) {
			PanelManager pan = getPanelManagerForObject(item);
			if (pan!=null) output.add(pan);
		}
		
		return output;
	}
	
	/**Finds the panel manager that controls the size of the object*/
	public static PanelManager getPanelManagerForObject(Object item) {
		if (item instanceof ImageDisplayLayer) {
			return ((ImageDisplayLayer) item).getPanelManager();
		}
		if (item instanceof PanelGraphicInsetDefiner) {
			return ((PanelGraphicInsetDefiner) item).getPanelManager();
		}
		
		return null;
		
	}
	
	
	/**shows a window to the user with some details about the scaling process*/
	public static void scaleMessages(GraphicLayer l) {
		showScaleMessages(l.getAllGraphics());
	}
	
	/**shows a window to the user with some details about the scaling process*/
	public static void showScaleMessages(ArrayList<?> objects) {
		HashMap<String, Object> allWarnings=new HashMap<String, Object>();
		ArrayList<String> orderedWarnings=new ArrayList<String>();
		for(Object o: objects) {
			if (o instanceof Scales) {
				Object w = ((Scales) o).getScaleWarning();
				if(w!=null && w instanceof String[]) 
					for(String s: (String[]) w) {
						if (!allWarnings.containsKey(s)) {
							allWarnings.put(s, o);
							orderedWarnings.add(s);
						}
						
					}
					
			}
		}
		if(allWarnings.keySet().size()>0)
			ShowMessage.showOptionalMessage("About Scaling", false, orderedWarnings);
		
	}
}
