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
