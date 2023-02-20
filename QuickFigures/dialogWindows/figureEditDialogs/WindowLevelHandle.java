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
 * Date Modified: Feb 20, 2023
 * Version: 2023.1
 */
package figureEditDialogs;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.SmartHandle;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import multiChannelFigureUI.ImagePropertiesButton;
import undo.CombinedEdit;

/**
 A type of handle that can be used instead of a window level dialog
 @see WindowLevelDialog
 */
public class WindowLevelHandle extends SmartHandle {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Value_control {	MIN, MAX, LEVEL, WINDOW}
	
	private ImagePanelGraphic parentPanel;
	private double slidermin;
	private double slidermax;
	private WindowLevelDialog widow;
	MultiChannelImage pressedMultichannel ;
	
	int channelIndex=1;
	private Value_control value_control=Value_control.MIN;
	private WindowLevelHandle sister;
	private ChannelPanelEditingMenu context;
	private CombinedEdit undo;

	public WindowLevelHandle(ImagePanelGraphic c, Value_control value, int channelIndex) {
		this.value_control=value;
		this.channelIndex=channelIndex;
		parentPanel=c;
		ChannelPanelEditingMenu cc = new ChannelPanelEditingMenu(c);
		pressedMultichannel = cc.getPressedMultichannel();
		
		widow = new WindowLevelDialog( channelIndex, pressedMultichannel, null, WindowLevelDialog.MIN_MAX, true);
		super.setHandleNumber(8720000+100*channelIndex+value.ordinal());
		this.setEllipseShape(true);
	}

	public WindowLevelHandle(WindowLevelDialog dialog, ImagePanelGraphic c) {
		parentPanel=c;
		this.widow=dialog;
	}
	
	
	/**location of the handle. this determines where in the figure the handle will actually appear
	   overwritten in many subclasses*/
	public Point2D getCordinateLocation() {
		Rectangle b = parentPanel.getBounds();
		slidermin = b.getX();
		slidermax = b.getMaxX();
	
		double y = b.getMaxY()+32+8*this.channelIndex;
		if(lastDrawnConverter!=null) {
			y = b.getMaxY()+20+(12+8*this.channelIndex)/lastDrawnConverter.getMagnification();
		}
		
		double knobValue = pressedMultichannel.getChannelMin(channelIndex);
		
		if(value_control==Value_control.MAX||	value_control==Value_control.WINDOW)  {
			knobValue = pressedMultichannel.getChannelMax(channelIndex);
		}
		
		if(value_control==Value_control.LEVEL) {
			knobValue =( pressedMultichannel.getChannelMax(channelIndex)+pressedMultichannel.getChannelMin(channelIndex))/2;
		}
		
		
		
		
		double value = (knobValue-minOfSlidingRange())/(maxOfSlidingRange()-minOfSlidingRange());
		
		
		double x = slidermin+value*(slidermax-slidermin);
		if(value_control==Value_control.WINDOW) {
			x+=8;
			y++;
		}
		return new Point2D.Double(x,y);
	}
	
	public Color getHandleColor() {
		if(value_control==Value_control.LEVEL) {
			pressedMultichannel.getChannelColor(channelIndex).darker();
		}
		
		if(value_control==Value_control.WINDOW) {
			return Color.white;
		}
		return pressedMultichannel.getChannelColor(channelIndex);
	}
	
	
	/**Called when a handle is pressed*/
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		ImagePropertiesButton ipb = new ImagePropertiesButton(parentPanel, 0);
		ipb.setSelection(canvasMouseEventWrapper.getSelectionSystem().getSelecteditems());
		
		context=ipb.prepareContext();
		undo=context.getUndoForChannelDisplay();
		if(canvasMouseEventWrapper.clickCount()==2) {
			DisplayRangeChangeListener listenNow = getListplayRangeListener();
			listenNow.minMaxSet(channelIndex, minOfSlidingRange(),  maxOfSlidingRange());
			canvasMouseEventWrapper.addUndo(undo);
		}
	}
	

	/**Subclasses may returns true if the method calls with the handle add an undo to the undomanager.
	  if not, an undo that simply tries to move a handle back to its origin location is used (those are often not complex enough to undo properly)*/
	public boolean handlesOwnUndo() {
		return true;
	}
	
	public static ArrayList<SmartHandle> buildHandlesForImage(ImagePanelGraphic c) {
		
		ChannelPanelEditingMenu cc = new ChannelPanelEditingMenu(c);
		MultiChannelImage pM = cc.getPressedMultichannel();
		
		ArrayList<SmartHandle> output = new ArrayList<SmartHandle> ();
		if(pM==null)
			return output;
		for(ChannelEntry chan : pM.getChannelEntriesInOrder()) {
			WindowLevelHandle max = new WindowLevelHandle(c, WindowLevelHandle.Value_control.MAX, chan.getOriginalChannelIndex());
			
			WindowLevelHandle min = new WindowLevelHandle(c, WindowLevelHandle.Value_control.MIN, chan.getOriginalChannelIndex());
			output.add(min);
			output.add(max);
			max.setSister(min);
			WindowLevelHandle l = new WindowLevelHandle(c, WindowLevelHandle.Value_control.LEVEL, chan.getOriginalChannelIndex());
			output.add(l);
			//l = new WindowLevelHandle(c, WindowLevelHandle.Value_control.WINDOW, chan.getOriginalChannelIndex());
			//output.add(l);
		}
		return output;
	}

	/**
	 * @param min
	 */
	private void setSister(WindowLevelHandle min) {
		this.sister=min;
		setLineConnectionHandle(sister);
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		Point cord = lastDragOrRelMouseEvent.getCoordinatePoint();
		double x = cord.getX();
		double knobPercent = (x-slidermin)/(slidermax-slidermin);
		
		double value = minOfSlidingRange()+(knobPercent)*(maxOfSlidingRange()-minOfSlidingRange());
		boolean shiftDown = lastDragOrRelMouseEvent.shiftDown();
		
		valueSet(value, shiftDown);
		if (undo!=null) {
			lastDragOrRelMouseEvent.addUndo(undo);
			undo=null;
		}
	}

	/**called when this handle is dragged to a particular numerical slider value
	 * @param value
	 * @param shiftDown
	 */
	public void valueSet(double value, boolean shiftDown) {
		
		
		double mmin = pressedMultichannel.getChannelMin(channelIndex);
		double mmax = pressedMultichannel.getChannelMax(channelIndex);
		double newmin=mmin;
		double newmax=mmax;
		
		
		if(value_control==Value_control.LEVEL) {
			double shift = value-(mmax+mmin)/2;
			newmin=pressedMultichannel.getChannelMin(channelIndex)+shift/2;
			newmax= pressedMultichannel.getChannelMax(channelIndex)+shift/2;
		} else  if(value_control==Value_control.WINDOW|| (shiftDown&& (value_control==Value_control.MAX||value_control==Value_control.MIN))) {
			double level= (mmax+mmin)/2;
			double window = Math.abs(value-level)*2;
			newmin=level-window/2;
			newmax=level+window/2;
		} else 	if(value_control==Value_control.MIN) {
			newmin=value;
		} else 	if(value_control==Value_control.MAX) {
			newmax=value;
		}
		
		if(newmin>=newmax) {
			double n = newmax;
			newmax=newmin;
			newmin=n;
		}
		
		DisplayRangeChangeListener listenNow = getListplayRangeListener();
		listenNow.minMaxSet(channelIndex, newmin, newmax);
	}

	/**
	 * @return
	 */
	public DisplayRangeChangeListener getListplayRangeListener() {
		if(context==null)
			context=new ChannelPanelEditingMenu(parentPanel);;
		DisplayRangeChangeListener listenNow=context;
		
		return listenNow;
	}

	/**the number that is the slider position on the left of the image panel
	 * @return
	 */
	public double minOfSlidingRange() {
		return (double)widow.slidermin;
	}

	/**the number that is the slider position on the right of the image panel
	 * @return
	 */
	public double maxOfSlidingRange() {
		return (double)widow.slidermax;
	}
	
	
	
}
