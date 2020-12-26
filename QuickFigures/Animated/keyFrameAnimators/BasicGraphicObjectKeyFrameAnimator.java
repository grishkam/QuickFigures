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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import animations.Animation;
import animations.BasicKeyFrame;
import animations.KeyFrameAnimation;
import graphicalObjects.BasicGraphicalObject;
import utilityClassesForObjects.Hideable;

/**A simple implementation of animation interfaces that acts as a superclass for other classes*/
public class BasicGraphicObjectKeyFrameAnimator implements Animation, KeyFrameAnimation {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicGraphicalObject object;
	private ArrayList<KeyFrame> frames=new ArrayList<KeyFrame>();
	
	int animatesTranslation=1;


	public BasicGraphicObjectKeyFrameAnimator(BasicGraphicalObject object) {
		this.object=(object);
	}
	
	public void recordKeyFrame(int frame) {
		getFrames().remove(this.isKeyFrame(frame));
		getFrames().add(createkeyFrame(frame));
	}
	
	public void updateKeyFrame(int frame) {
		if (isKeyFrame(frame)==null)recordKeyFrame(frame);
		else isKeyFrame(frame).setUp();
	}
	
	protected KeyFrame createkeyFrame(int frame) {
		return new KeyFrame(frame);
	}
	
	
	double spanBetweenKeyFrames(KeyFrame before, KeyFrame after, int frameNum) {
		double span=after.theFrameNumber-before.theFrameNumber;
		double progressWithinSpan=frameNum-before.theFrameNumber;
		double factor = progressWithinSpan/span;
		
		return factor;
	}
	
	boolean isInterpretableFrame(int frameNum)  {
		
		if (isKeyFrame(frameNum)!=null) return true;
		KeyFrame before = this.getKeyFrameBefore(frameNum);
		KeyFrame after = this.getKeyFrameAfter(frameNum);
		if (before==null) return false;
		if (after==null) return false;
		return true;
	}
	
	@Override
	public void setToFrame(int frameNum) {
		
		if (this.isInterpretableFrame(frameNum))  {
				onFrameSet(frameNum);
				
		
		}
	}
	
	
	
	protected void onFrameSet(int frameNum) {
		if(this.isKeyFrame(frameNum)!=null) {
			isKeyFrame(frameNum).setObjectToKeyFrame();
			return;
		};
		KeyFrame before = getKeyFrameBefore(frameNum);
		if (before!=null)before.setObjectToKeyFrame();
		interpolateLocation(frameNum);
		
		
		
		if(before!=null) getObject().setHidden(before.hidden);
	}
	
	
	protected void interpolateLocation(int frameNum) {
		KeyFrame before = this.getKeyFrameBefore(frameNum);
		KeyFrame after = this.getKeyFrameAfter(frameNum);
		if (before==null) return;
		if (after==null|| !after.animatesMotion) return;
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		
		double nx=interpolate( before.locationULx,after.locationULx, factor);
		double ny=interpolate( before.locationULy,after.locationULy, factor);
		
		 getObject().setLocationUpperLeft(nx, ny);
	}
	
	
	double interpolate(double before, double after, double factor) {
		return before+factor*(after-before);
	}
	
	Point2D.Double interpolate(Point2D.Double before, Point2D.Double after, double factor) {
		return new Point2D.Double( before.x+factor*(after.x-before.x), before.y+factor*(after.y-before.y));
	}
	
	Color interpolate(Color before, Color after, double factor) {
		double r=interpolate(before.getRed(),after.getRed(), factor);
		double g=interpolate(before.getGreen(),after.getGreen(), factor);
		double b=interpolate(before.getBlue(),after.getBlue(), factor);
		double a=interpolate(before.getAlpha(),after.getAlpha(), factor);
		return new Color((int)r,(int)g,(int)b,(int)a);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<KeyFrame> getFrames() {
		return frames;
	}

	

	public BasicGraphicalObject getObject() {
		return object;
	}




	/**simple implementation of the basic key frame interface*/
	public class KeyFrame implements Serializable, BasicKeyFrame{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int theFrameNumber=0;//the frame number of this key frame
		double locationULx;//the location of the object during this key frame
		double locationULy;//the location of the object during this key frame
		boolean hidden=false;//true if the object is hidden during this key frame
		public boolean animatesMotion=true;
		
		protected KeyFrame(int f) {
			theFrameNumber=f;
			setUp();
		}
		
		void setUp() {
			locationULx=getObject().getLocationUpperLeft().getX();
			locationULy=getObject().getLocationUpperLeft().getY();
			if (getObject() instanceof Hideable) {
				hidden=getObject().isHidden();
			}
		}
		
		void setObjectToKeyFrame() {
			setLocation() ;
			getObject().setHidden(hidden);
		}
		
		void setLocation() {
			if (animatesMotion)
			getObject().setLocationUpperLeft(locationULx, locationULy);
		}

		@Override
		public int getFrame() {
			return theFrameNumber;
		}

		@Override
		public void setFrame(int t) {
			theFrameNumber=t;
			
		}
		
	}
	
	/**returns true if there is a key frame at frame 'frame' */
	public KeyFrame isKeyFrame(int frame) {
		for(KeyFrame aFrame:getFrames()) {
			if (aFrame.theFrameNumber==frame)return aFrame;
		} 
		return null;
	}
	
	/**returns the key frame that occurs before the given frame.
	  if there are no key frames before this frame, it returns null */
	public KeyFrame getKeyFrameBefore(int frame) {
		if (getFrames().size()==0) return null;
		
		KeyFrame output=null;
		
		for(KeyFrame aFrame:getFrames()) {
			if(aFrame.theFrameNumber> frame) continue;//ignore if the frame is not before this one
			if (output==null) {output=aFrame; continue;};//what to do if this is the first frame in the iteration that is before the frame argument 
			if (output!=null &&output.theFrameNumber<aFrame.theFrameNumber) output=aFrame;//what is aFrame is closer to the current frame than the previous candidate.
		}
				
		return output;
	}
	
	/**returns the key frame at the given frame index*/
	public KeyFrame getKeyFrameAfter(int frame) {
		
		if (getFrames().size()==0) return null;
		
		KeyFrame output=null;
		
		for(KeyFrame aFrame:getFrames()) {
			if(aFrame.theFrameNumber< frame) continue;//ignore if the frame is before this one
			if (output==null) {output=aFrame; continue;};//what to do if this is the first frame in the iteration that is at the frame argument 
			if (output!=null &&output.theFrameNumber>aFrame.theFrameNumber) output=aFrame;//what is aFrame is closer to the current frame than the previous candidate.
		}
				
		return output;
	}

	/**removes the key frame at time frame*/
	@Override
	public void removeKeyFrame(int frame) {
		getFrames().remove(this.isKeyFrame(frame));
		
	}

	/**returns all of the key frames*/
	@Override
	public ArrayList<BasicKeyFrame> getKeyFrames() {
		ArrayList<BasicKeyFrame> output = new ArrayList<BasicKeyFrame>();
		output.addAll(frames);
		return output;
	}
	

}
