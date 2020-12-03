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
package keyFrameAnimators;

import graphicalObjects_BasicShapes.RectangularGraphic;

public class RectGraphicKeyFrameAnimator extends ShapeGraphicKeyFrameAnimator {

	RectangularGraphic rect;
	
	public RectGraphicKeyFrameAnimator(RectangularGraphic object) {
		super(object);
		this.shape=object;
		rect=object;
	}
	
	public RectangularGraphic getObject() {
		return rect;
	}
	
	
	protected KeyFrame createkeyFrame(int frame) {
		return new rectKeyFrame(frame);
	}
	
	
	public void setToFrame(int frameNum) {
		super.setToFrame(frameNum);
	
	}
	
	
	protected void onFrameSet(int frameNum) {
		
		
		super.onFrameSet(frameNum);
		if (isKeyFrame(frameNum)!=null) return;
		
		rectKeyFrame before = (rectKeyFrame) this.getKeyFrameBefore(frameNum);
		rectKeyFrame after = (rectKeyFrame) this.getKeyFrameAfter(frameNum);

		if (before==null) return;
		if (after==null) return;
		
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		if (after.animatesResize) {
			double w=interpolate(before.width,after.width, factor);
			double h=interpolate(before.height,after.height, factor);
			
			
			getObject().setWidth(w);
			getObject().setHeight(h);
		}
	}
	
	public class rectKeyFrame extends shapeKeyFrame {

		private double width;
		private double height;
		public boolean animatesResize=true;

		protected rectKeyFrame(int f) {
			super(f);
			
		}
		
		@Override
		void setUp() {
			super.setUp();
			width=getObject().getObjectWidth();
			height=getObject().getObjectHeight();
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			if (animatesResize) {
					getObject().setHeight(height);
					getObject().setWidth(width);
					}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
