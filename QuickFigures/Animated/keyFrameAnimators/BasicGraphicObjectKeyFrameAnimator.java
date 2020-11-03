package keyFrameAnimators;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import animations.Animation;
import animations.BasicKeyFrame;
import animations.KeyFrameAnimation;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import utilityClassesForObjects.Hideable;

public class BasicGraphicObjectKeyFrameAnimator implements Animation, KeyFrameAnimation {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicGraphicalObject object;
	private ArrayList<keyFrame> frames=new ArrayList<keyFrame>();
	
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
	
	protected keyFrame createkeyFrame(int frame) {
		return new keyFrame(frame);
	}
	
	
	double spanBetweenKeyFrames(keyFrame before, keyFrame after, int frameNum) {
		double span=after.theFrameNumber-before.theFrameNumber;
		double progressWithinSpan=frameNum-before.theFrameNumber;
		double factor = progressWithinSpan/span;
		
		return factor;
	}
	
	boolean isInterpretableFrame(int frameNum)  {
		
		if (isKeyFrame(frameNum)!=null) return true;
		keyFrame before = this.getKeyFrameBefore(frameNum);
		keyFrame after = this.getKeyFrameAfter(frameNum);
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
		keyFrame before = getKeyFrameBefore(frameNum);
		if (before!=null)before.setObjectToKeyFrame();
		interpolateLocation(frameNum);
		
		
		
		if(before!=null) getObject().setHidden(before.hidden);
	}
	
	
	protected void interpolateLocation(int frameNum) {
		keyFrame before = this.getKeyFrameBefore(frameNum);
		keyFrame after = this.getKeyFrameAfter(frameNum);
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
	
	public ArrayList<keyFrame> getFrames() {
		return frames;
	}

	

	public BasicGraphicalObject getObject() {
		return object;
	}





	public class keyFrame implements Serializable, BasicKeyFrame{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int theFrameNumber=0;
		double locationULx;
		double locationULy;
		boolean hidden=false;
		public boolean animatesMotion=true;
		
		protected keyFrame(int f) {
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
			// TODO Auto-generated method stub
			return theFrameNumber;
		}

		@Override
		public void setFrame(int t) {
			theFrameNumber=t;
			
		}
		
	}
	
	/**returns true if frame 'frame' is a keyFrame*/
	public keyFrame isKeyFrame(int frame) {
		for(keyFrame aFrame:getFrames()) {
			if (aFrame.theFrameNumber==frame)return aFrame;
		} 
		return null;
	}
	
	
	public keyFrame getKeyFrameBefore(int frame) {
		if (getFrames().size()==0) return null;
		
		keyFrame output=null;
		
		for(keyFrame aFrame:getFrames()) {
			if(aFrame.theFrameNumber> frame) continue;//ignore if the frame is not before this one
			if (output==null) {output=aFrame; continue;};//what to do if this is the first frame in the iteration that is before the frame argument 
			if (output!=null &&output.theFrameNumber<aFrame.theFrameNumber) output=aFrame;//what is aFrame is closer to the current frame than the previous candidate.
		}
				
		return output;
	}
	
	public keyFrame getKeyFrameAfter(int frame) {
if (getFrames().size()==0) return null;
		
		keyFrame output=null;
		
		for(keyFrame aFrame:getFrames()) {
			if(aFrame.theFrameNumber< frame) continue;//ignore if the frame is before this one
			if (output==null) {output=aFrame; continue;};//what to do if this is the first frame in the iteration that is before the frame argument 
			if (output!=null &&output.theFrameNumber>aFrame.theFrameNumber) output=aFrame;//what is aFrame is closer to the current frame than the previous candidate.
		}
				
		return output;
	}

	@Override
	public void removeKeyFrame(int frame) {
		getFrames().remove(this.isKeyFrame(frame));
		
	}

	@Override
	public ArrayList<BasicKeyFrame> getKeyFrames() {
		ArrayList<BasicKeyFrame> output = new ArrayList<BasicKeyFrame>();
		output.addAll(frames);
		return output;
	}
	

}
