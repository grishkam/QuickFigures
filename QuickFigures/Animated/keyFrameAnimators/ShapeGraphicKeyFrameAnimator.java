package keyFrameAnimators;

import java.awt.Color;

import graphicalObjects_BasicShapes.ShapeGraphic;

public class ShapeGraphicKeyFrameAnimator extends BasicGraphicObjectKeyFrameAnimator {

	ShapeGraphic shape;
	
	public ShapeGraphicKeyFrameAnimator(ShapeGraphic object) {
		super(object);
		this.shape=object;
	}
	
	
	
	
	public ShapeGraphic getObject() {
		return shape;
	}
	
	
	protected keyFrame createkeyFrame(int frame) {
		return new shapeKeyFrame(frame);
	}
	
	
	public void setToFrame(int frameNum) {
		super.setToFrame(frameNum);
	
	}
	
	
	protected void onFrameSet(int frameNum) {
		
		
		super.onFrameSet(frameNum);
		if (isKeyFrame(frameNum)!=null) return;
		
		shapeKeyFrame before = (shapeKeyFrame) this.getKeyFrameBefore(frameNum);
		shapeKeyFrame after = (shapeKeyFrame) this.getKeyFrameAfter(frameNum);

		if (before==null) return;
		if (after==null) return;
		
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		
		double r=interpolate(before.fillColor.getRed(),after.fillColor.getRed(), factor);
		double g=interpolate(before.fillColor.getGreen(),after.fillColor.getGreen(), factor);
		double b=interpolate(before.fillColor.getBlue(),after.fillColor.getBlue(), factor);
		double a=interpolate(before.fillColor.getAlpha(),after.fillColor.getAlpha(), factor);
		if (after.animatesFillColorChange) getObject().setFillColor(new Color((int)r,(int)g,(int)b,(int)a));
		
		
		 
		
		double nangle=interpolate(before.angle,after.angle, factor);
		if (after.animatesAngleChange) getObject().setAngle(nangle);
		
		
		 if (after.animatesStrokeChange) {
						r=interpolate(before.strokeColor.getRed(),after.strokeColor.getRed(), factor);
						 g=interpolate(before.strokeColor.getGreen(),after.strokeColor.getGreen(), factor);
						 b=interpolate(before.strokeColor.getBlue(),after.strokeColor.getBlue(), factor);
						 a=interpolate(before.strokeColor.getAlpha(),after.strokeColor.getAlpha(), factor);
						getObject().setStrokeColor(new Color((int)r,(int)g,(int)b,(int)a));
						
						double stroke=interpolate(before.strokeWidth,after.strokeWidth, factor);
						getObject().setStrokeWidth((float)stroke);
						
						float[] dash2 = getObject().getDashes();
						for(int i=0; i<dash2.length&&i<before.dashes.length&&i<after.dashes.length; i++) {
							dash2[i]=(float) interpolate(before.dashes[i],after.dashes[i], factor);
						}
						getObject().setDashes(dash2);
						}
	}
	
	public class shapeKeyFrame extends keyFrame {

		public boolean animatesAngleChange=true;
		public boolean animatesStrokeChange=true;
		public boolean animatesFillColorChange=true;
		
		 Color strokeColor;
		 Color fillColor;
		 double angle;
		 float strokeWidth ;
		 float[] dashes;

		protected shapeKeyFrame(int f) {
			super(f);
			
		}
		
		@Override
		void setUp() {
			super.setUp();
			strokeColor=shape.getStrokeColor();
			fillColor=shape.getFillColor();
			angle=shape.getAngle();
			strokeWidth = shape.getStrokeWidth();
			dashes = shape.getDashes().clone();
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			if (animatesAngleChange)getObject().setAngle(angle);
			if (animatesFillColorChange) getObject().setFillColor(fillColor);
			if (animatesStrokeChange) {
				getObject().setStrokeColor(strokeColor);
				getObject().setStrokeWidth(strokeWidth);
				getObject().setDashes(dashes);
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
