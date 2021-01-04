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
package undo;

import java.awt.Color;
import java.util.ArrayList;

import channelMerging.MultiChannelImage;
import multiChannelFigureUI.DisplayRangeChangeListener;

public class ChannelDisplayUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DISPLAY_RANGE_TYPE=0, COLOR_TYPE=1, ALL_TYPE=2;
	int currentType=ALL_TYPE;
	double[] iMin;
	double[] iMax;
	Color[] iColor;
	
	double[] fMin;
	double[] fMax;
	Color[] fColor;

	private MultiChannelImage mw;

	private DisplayRangeChangeListener lis;
	
	public ChannelDisplayUndo(MultiChannelImage mw, DisplayRangeChangeListener lis, int type) {
		currentType=type;
		this.mw=mw;
		this.lis=lis;
		establishInnitialState();
	}

	protected void establishInnitialState() {
		int nC=mw.nChannels();
		iMin=new double[nC];
		iMax=new double[nC];
		iColor=new Color[nC];
		for(int i=0; i<nC; i++) {
			iMin[i]=mw.getChannelMin(i+1);
			iMax[i]=mw.getChannelMax(i+1);
			iColor[i]= mw.getChannelColor(i+1);
		}
	};
	boolean editsColors() {
		if(currentType==COLOR_TYPE) return true;
		if(currentType==ALL_TYPE) return true;
		
		return false;
	}
	
	public void establishFinalState() {
		int nC=mw.nChannels();
		fMin=new double[nC];
		fMax=new double[nC];
		fColor=new Color[nC];
		for(int i=0; i<nC; i++) {
			fMin[i]=mw.getChannelMin(i+1);
			fMax[i]=mw.getChannelMax(i+1);
			fColor[i]= mw.getChannelColor(i+1);
		}
	}
	
	public void editNow(double[] mins, double[] max) {
		int nC=mw.nChannels();
		for(int i=0; i<nC; i++) {
			mw.setChannelMin(i+1, mins[i]);
			mw.setChannelMax(i+1, max[i]);
		}
		mw.updateDisplay();
		lis.updateAllDisplaysWithRealChannel(null);
	}
	public void editNow(Color[] ca) {
		int nC=mw.nChannels();
		for(int i=0; i<nC; i++) {
			mw.getChannelSwapper().setChannelColor(ca[i], i+1);
		}
		mw.updateDisplay();
		lis.updateAllDisplaysWithRealChannel(null);
	}
	
	public void undo() {
		editNow(iMin, iMax);
		if (editsColors())editNow(iColor);
		
	}
	public void redo() {
		editNow(fMin, fMax);
		if (editsColors()) editNow(fColor);
	}
	
	public static CombinedEdit createMany(ArrayList<MultiChannelImage> mws, DisplayRangeChangeListener  extras, int type) {
		CombinedEdit ce2=new CombinedEdit();
		for(MultiChannelImage mw: mws) {
			ce2.addEditToList(new ChannelDisplayUndo(mw, extras, type));
		}
		
		return ce2;
		
	}
	public static CombinedEdit createMany(ArrayList<MultiChannelImage> mws, DisplayRangeChangeListener  extras) { 
		return createMany(mws, extras, DISPLAY_RANGE_TYPE);
	}
		
}
