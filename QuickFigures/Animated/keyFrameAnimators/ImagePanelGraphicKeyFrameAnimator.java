package keyFrameAnimators;

import java.awt.Color;

import graphicalObjects.ImagePanelGraphic;

public class ImagePanelGraphicKeyFrameAnimator extends BasicGraphicObjectKeyFrameAnimator {

	ImagePanelGraphic image;
	
	public ImagePanelGraphicKeyFrameAnimator(ImagePanelGraphic object) {
		super(object);
		this.image=object;
	}
	
	public ImagePanelGraphic getObject() {
		return image;
	}
	
	
	protected KeyFrame createkeyFrame(int frame) {
		return new imageKeyFrame(frame);
	}
	
	
	public void setToFrame(int frameNum) {
		super.setToFrame(frameNum);
	
	}
	
	
	protected void onFrameSet(int frameNum) {
		
		
		super.onFrameSet(frameNum);
		if (isKeyFrame(frameNum)!=null) return;
		
		imageKeyFrame before = (imageKeyFrame) this.getKeyFrameBefore(frameNum);
		imageKeyFrame after = (imageKeyFrame) this.getKeyFrameAfter(frameNum);

		if (before==null) return;
		if (after==null) return;
		
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		
	
		
		
		
		double nangle=interpolate(before.angle,after.angle, factor);
		if (after.animagesAngleChange)getObject().setAngle(nangle);
		
		double scale=interpolate(before.scale,after.scale, factor);
		if (after.animagesSizeChange)getObject().setScale(scale);
		
		if (after.animatesFrameChange)  {
				nangle=interpolate(before.frameWidthH,after.frameWidthH, factor);
				getObject().setFrameWidthH(nangle);
				
				nangle=interpolate(before.frameWidthV,after.frameWidthV, factor);
				getObject().setFrameWidthV(nangle);
				
				 double r = interpolate(before.frameColor.getRed(),after.frameColor.getRed(), factor);
				 double g = interpolate(before.frameColor.getGreen(),after.frameColor.getGreen(), factor);
				 double b = interpolate(before.frameColor.getBlue(),after.frameColor.getBlue(), factor);
				 double a = interpolate(before.frameColor.getAlpha(),after.frameColor.getAlpha(), factor);
					 getObject().setFrameColor(new Color((int)r,(int)g,(int)b,(int)a));
		}
	}
	
	class imageKeyFrame extends KeyFrame {

		 Color frameColor;
		 double angle;
		private double frameWidthH;
		private double frameWidthV;
		private double scale;
		
		public boolean animatesFrameChange=false;
		public boolean animagesAngleChange=true;
		public boolean animagesSizeChange=true;

		protected imageKeyFrame(int f) {
			super(f);
		}
		
		@Override
		void setUp() {
			super.setUp();
			frameColor=image.getFrameColor();
			frameWidthH=image.getFrameWidthH();
			frameWidthV=image.getFrameWidthV();
			scale = image.getScale();
			angle=image.getAngle();
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			
			if (animagesAngleChange) getObject().setAngle(angle);
			if (animatesFrameChange) {
				getObject().setFrameWidthH(frameWidthH);
				getObject().setFrameWidthV(frameWidthV);
				getObject().setFrameColor(frameColor);
			}
			
			if (animagesSizeChange) getObject().setScale(scale);
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
