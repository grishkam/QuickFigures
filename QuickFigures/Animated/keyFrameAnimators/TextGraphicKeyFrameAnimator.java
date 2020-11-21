package keyFrameAnimators;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import graphicalObjects_BasicShapes.TextGraphic;

public class TextGraphicKeyFrameAnimator extends BasicGraphicObjectKeyFrameAnimator {

	TextGraphic text;
	
	public TextGraphicKeyFrameAnimator(TextGraphic object) {
		super(object);
		this.text=object;
	}
	
	public TextGraphic getObject() {
		return text;
	}
	
	
	protected KeyFrame createkeyFrame(int frame) {
		return new textKeyFrame(frame);
	}
	
	
	public void setToFrame(int frameNum) {
		super.setToFrame(frameNum);
	
	}
	
	
	protected void onFrameSet(int frameNum) {
		
		
		super.onFrameSet(frameNum);
		if (isKeyFrame(frameNum)!=null) return;
		
		textKeyFrame before = (textKeyFrame) this.getKeyFrameBefore(frameNum);
		textKeyFrame after = (textKeyFrame) this.getKeyFrameAfter(frameNum);

		if (before==null) return;
		if (after==null) return;
		
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		
		Color r=interpolate(before.textColor,after.textColor, factor);
		getObject().setTextColor(r);
	
		
		
		double nangle=interpolate(before.angle,after.angle, factor);
		getObject().setAngle(nangle);
		
		float stroke=(float) interpolate(before.fontsize,after.fontsize, factor);
		getObject().setFont(getObject().getFont().deriveFont(stroke));
		
	}
	
	class textKeyFrame extends KeyFrame {

		 Color textColor;
		 
		 double angle;
		 float fontsize ;
		private int fontStyle;
		private String fontFam;

		private Point2D loc;

		protected textKeyFrame(int f) {
			super(f);
			fontsize=getObject().getFont().getSize();
			fontFam=getObject().getFont().getFamily();
			fontStyle=getObject().getFont().getStyle();
			angle=getObject().getAngle();
			textColor=getObject().getTextColor();
			loc = getObject().getLocation();
		}
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			getObject().setTextColor(textColor);
			getObject().setAngle(angle);
			getObject().setFont(new Font(fontFam, fontStyle, (int) fontsize));
			
		}
		
		void setLocation() {getObject().setLocation(loc);}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
