package keyFrameAnimators;

import java.awt.geom.Point2D.Double;

import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.ArrowGraphic.ArrowHead;

/**Animator for animating changes to arrows*/
public class ArrowGraphicKeyFrameAnimator extends ShapeGraphicKeyFrameAnimator {

	ArrowGraphic rect;
	private ArrowHead head;
	
	public ArrowGraphicKeyFrameAnimator(ArrowGraphic object) {
		super(object);
		this.shape=object;
		rect=object;
		head=getObject().getHead();
	}
	
	public ArrowGraphic getObject() {
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
		
		
		Double l1=interpolate(before.l1,after.l1, factor);
		Double l2=interpolate(before.l2,after.l2, factor);
		if (after.animagesArrowEdit) {
			head.setArrowHeadSize(interpolate(before.headSize,after.headSize, factor));
			head.setNotchAngle(interpolate(before.notchAngle,after.notchAngle, factor));
			head.setArrowTipAngle(interpolate(before.tipAngle,after.tipAngle, factor));
			}
		if (after.animatesMotion) getObject().setPoints(l1, l2);
	}
	
	class rectKeyFrame extends shapeKeyFrame {

		 


		
		Double l2;
		Double l1;
		double headSize;
		double notchAngle;
		private double tipAngle;
		public boolean animagesArrowEdit=true;

		protected rectKeyFrame(int f) {
			super(f);
			
		}
		
		@Override
		void setUp() {
			super.setUp();
			
			l2=getObject().getLineStartLocation();
			l1=getObject().getLineEndLocation();
			
				headSize=head.getArrowHeadSize();
				notchAngle=head.getNotchAngle();
				tipAngle=head.getArrowTipAngle();
			
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			
			if (super.animatesMotion) getObject().setPoints(l1, l2);
			if (animagesArrowEdit) {
				head.setArrowHeadSize(headSize);
				head.setArrowTipAngle(tipAngle);
				head.setNotchAngle(notchAngle);
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
