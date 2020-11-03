package keyFrameAnimators;

import java.awt.geom.Point2D.Double;

import graphicalObjects_BasicShapes.ArrowGraphic;

public class ArrowGraphicKeyFrameAnimator extends ShapeGraphicKeyFrameAnimator {

	ArrowGraphic rect;
	
	public ArrowGraphicKeyFrameAnimator(ArrowGraphic object) {
		super(object);
		this.shape=object;
		rect=object;
	}
	
	public ArrowGraphic getObject() {
		return rect;
	}
	
	
	protected keyFrame createkeyFrame(int frame) {
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
			getObject().setArrowHeadSize(interpolate(before.headSize,after.headSize, factor));
			getObject().setNotchAngle(interpolate(before.notchAngle,after.notchAngle, factor));
			getObject().setArrowTipAngle(interpolate(before.tipAngle,after.tipAngle, factor));
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
			l2=getObject().getOppositeTipEndLocation();
			l1=getObject().getTipLocation();
			
				headSize=getObject().getArrowHeadSize();
				notchAngle=getObject().getNotchAngle();
				tipAngle=getObject().getArrowTipAngle();
			
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			
			if (super.animatesMotion) getObject().setPoints(l1, l2);
			if (animagesArrowEdit) {
				getObject().setArrowHeadSize(headSize);
				getObject().setArrowTipAngle(tipAngle);
				getObject().setNotchAngle(notchAngle);
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
