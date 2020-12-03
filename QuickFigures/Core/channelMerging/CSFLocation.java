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
package channelMerging;

import java.io.Serializable;

public class CSFLocation implements Serializable {
	/**
	 * 
	 */
	
	public static final int MERGE_SELECTED=0, NONE_SELECTED=-1;
	
	private static final long serialVersionUID = 1L;
	/**the channel frame and slice position specified. channel 0 represents merged images
	  negative numbers indicate the place is not specified (which can mean all or none depending on context)*/
	public int frame=1;
	public int slice=1;
	public int channel=0;//0 refers to the merged images. 
	int[] otherDims=null;
	
	public CSFLocation() {}
	public CSFLocation(int c, int s, int f) {
		this.frame=f;
		this.channel=c;
		this.slice=s;
	}
	
	public static CSFLocation frameLocation(int t) {
		CSFLocation out = new CSFLocation();
		out.channel=-1;
		out.frame=t;
		out.slice=-1;
		return out;
	}
	
	public static CSFLocation sliceLocation(int t) {
		CSFLocation out = new CSFLocation();
		out.channel=-1;
		out.frame=-1;
		out.slice=t;
		return out;
	}
	
	public boolean isFrameLocation() {return frame>0;}
	public boolean isSliceLocation() {return slice>0;}
	public CSFLocation duplicate() {return new CSFLocation(channel, slice, frame);}
	public boolean changesT(CSFLocation c) {return (c.frame!=frame);}
	public boolean changesZ(CSFLocation c) {return (c.slice!=slice);}
}
