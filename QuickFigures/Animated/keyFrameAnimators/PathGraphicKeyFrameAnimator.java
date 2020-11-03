package keyFrameAnimators;

import java.awt.geom.Point2D;

import graphicalObjects_BasicShapes.PathGraphic;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class PathGraphicKeyFrameAnimator extends ShapeGraphicKeyFrameAnimator {

	PathGraphic path;
	
	public PathGraphicKeyFrameAnimator(PathGraphic object) {
		super(object);
		this.shape=object;
		path=object;
	}
	
	
	
	public PathGraphic getObject() {
		return path;
	}
	
	
	protected keyFrame createkeyFrame(int frame) {
		return new pathKeyFrame(frame);
	}
	
	
	public void setToFrame(int frameNum) {
		super.setToFrame(frameNum);
	
	}
	
	/**meant to override the superclass method so that one does not execute*/
	protected void interpolateLocation(int frameNum) {}
	
	protected void onFrameSet(int frameNum) {
		
		
		super.onFrameSet(frameNum);
		if (isKeyFrame(frameNum)!=null) return;
		
		pathKeyFrame before = (pathKeyFrame) this.getKeyFrameBefore(frameNum);
		pathKeyFrame after = (pathKeyFrame) this.getKeyFrameAfter(frameNum);

		if (before==null) return;
		if (after==null) return;
		
		before.setObjectToKeyFrame();//needed to make sure the path closing is the same as the previous frame
	
		double factor=spanBetweenKeyFrames(before, after, frameNum);
		
		if (after.animatesMotion)getObject().setLocation(interpolate(before.loc, after.loc, factor));
	
		PathPointList b = before.pathPoints;
		PathPointList a = after.pathPoints;
		PathPointList current = getObject().getPoints();
		
		if (after.animatesPathEdit)
			for(int i=0;i<current.size()&&i<b.size()&&i<a.size(); i++) {
			PathPoint pb = b.get(i);
			PathPoint pa = a.get(i);
			PathPoint pcurrent = current.get(i);
			
			pcurrent.setAnchorPoint(interpolate(pb.getAnchor(), pa.getAnchor(), factor));
			pcurrent.setCurveControl1(interpolate(pb.getCurveControl1(), pa.getCurveControl1(), factor));
			pcurrent.setCurveControl2(interpolate(pb.getCurveControl2(), pa.getCurveControl2(), factor));
			}
		
		
		getObject().updatePathFromPoints();
	}
	
	
	
	public class pathKeyFrame extends shapeKeyFrame {

		 
		private PathPointList pathPoints;
		private Point2D.Double loc;
		public boolean animatesPathEdit=true;

		protected pathKeyFrame(int f) {
			super(f);
		}
		
		@Override
		void setUp() {
			super.setUp();
			pathPoints=getObject().getPoints().copy();
			loc=new Point2D.Double(getObject().getLocation().getX(), getObject().getLocation().getY());
		}
		
		
		void setObjectToKeyFrame() {
			super.setObjectToKeyFrame();
			if (animatesPathEdit)getObject().setPoints(pathPoints.copy());
		}
		
		void setLocation() {
			if (animatesMotion)getObject().setLocation(loc);
			
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
