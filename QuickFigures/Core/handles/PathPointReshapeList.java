/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 5, 2021
 * Version: 2021.2
 */
package handles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.PathGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.LocationChangeListenerList;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import locatedObject.Scales;
import locatedObject.ScalesFully;
import undo.PathEditUndo;

/**A handle list that allows the user to scale a group of points within a path by dragging the handles*/
public class PathPointReshapeList extends ReshapeHandleList {

	private PathGraphic path;
	private PathEditUndo undo;

	public PathPointReshapeList( int hNumber, PathGraphic path) {
		super(0, hNumber, getSelectedPoints(path));
		this.reshapeHandleColor=new Color(128, 0, 128);
		this.fixedpointHandleColor=reshapeHandleColor;
		hideCenterHandle=false;
		this.path=path;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean isHiddenCenterHandle() {
		return false;
	}

	public void onHandlePress() {
		undo=new PathEditUndo(path);
	}
	
	public void editover(CanvasMouseEvent w) {
		super.editover(w);
		path.updatePathFromPoints();
		if(undo!=null) {
			w.addUndo(undo);
		}
	}
	
	public static PathPointProxy[] getSelectedPoints(PathGraphic path) {
		PathPointList all = path.getPoints().getSelectedPointsOnly();
		PathPointProxy[] output = new PathPointProxy[all.size()];
		for(int i=0; i<all.size(); i++)
			output[i]=new PathPointProxy(all.get(i), path);
		return output;
	}
	
	
	
	/**a marker that is drawn to represent the locations of the path points*/
	static class PathPointProxy implements LocatedObject2D, ScalesFully, Scales, ZoomableGraphic {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PathGraphic path;
		private PathPoint point;
		

		public PathPointProxy(PathPoint point, PathGraphic path) {
			this.path=path;
			this.point=point;
		}
		
		@Override
		public void kill() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isDead() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isHidden() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setHidden(boolean b) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void select() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deselect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isSelected() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean makePrimarySelectedItem(boolean isFirst) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void rotateAbout(Point2D clickedCord, double distanceFromCenterOfRotationtoAngle) {
			Point2D pointCenter=path.convertPointToInternalCrdinates(clickedCord);
			AffineTransform at = AffineTransform.getRotateInstance(distanceFromCenterOfRotationtoAngle, pointCenter.getX(), pointCenter.getY());
			point.applyAffine(at);
			//path.updatePathFromPoints();
		}

		@Override
		public void scaleAbout(Point2D p, double mag) {
			p=path.convertPointToInternalCrdinates(p);
			
			//Point2D p2 = this.getLocation();
			AffineTransform af = new AffineTransform();
			af.translate(p.getX(), p.getY());
			af.scale(mag, mag);
			af.translate(-p.getX(), -p.getY());
			//p2=scaleAbout(p2, p,mag,mag);
			
			point.applyAffine(af);
			//this.setLocation(p2);
			//path.updatePathFromPoints();
			
		}

		@Override
		public Object getScaleWarning() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void scaleAbout(Point2D p, double magx, double magy) {
			p=path.convertPointToInternalCrdinates(p);

			AffineTransform af = new AffineTransform();
			af.translate(p.getX(), p.getY());
			af.scale(magx, magy);
			af.translate(-p.getX(), -p.getY());
			point.applyAffine(af);
			//path.updatePathFromPoints();
		}

		@Override
		public Point2D getLocationUpperLeft() {
			// TODO Auto-generated method stub
			return getProxyLocation();
		}

		@Override
		public void setLocationUpperLeft(double x, double y) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocationUpperLeft(Point2D p) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int isUserLocked() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public LocatedObject2D copy() {
			return new PathPointProxy(point.copy(), path);
		}

		@Override
		public boolean doesIntersect(Rectangle2D rect) {
			// TODO Auto-generated method stub
			return rect.contains(getProxyLocation());
		}

		@Override
		public boolean isInside(Rectangle2D rect) {
			// TODO Auto-generated method stub
			return rect.contains(getProxyLocation());
		}

		protected Double getProxyLocation() {
			return path.convertPointToExternalCrdinates(point.getAnchor());
		}

		@Override
		public Rectangle getExtendedBounds() {
			// TODO Auto-generated method stub
			return new Rectangle((int)getProxyLocation().getX()-1, (int)getProxyLocation().getY()-1, 1,1);
		}

		@Override
		public Object dropObject(Object ob, int x, int y) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Point2D getLocation() {
			// TODO Auto-generated method stub
			return getProxyLocation();
		}

		@Override
		public void setLocation(double x, double y) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocation(Point2D p) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void moveLocation(double xmov, double ymov) {
			point.move(xmov, ymov);
			//path.updatePathFromPoints();
		}

		@Override
		public Shape getOutline() {
			// TODO Auto-generated method stub
			return  getBounds();
		}

		@Override
		public Rectangle getBounds() {
			// TODO Auto-generated method stub
						int proxysize = 9;
						return new Rectangle((int)getProxyLocation().getX()-proxysize, (int)getProxyLocation().getY()-proxysize, proxysize*2,proxysize*2);
				}

		@Override
		public void addLocationChangeListener(LocationChangeListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeLocationChangeListener(LocationChangeListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeAllLocationChangeListeners() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public LocationChangeListenerList getListenerList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AttachmentPosition getAttachmentPosition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setAttachmentPosition(AttachmentPosition snap) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocationType(int n) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getLocationType() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public GraphicLayer getParentLayer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setParentLayer(GraphicLayer parent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			SmartHandle dd = new SmartHandle();
			dd.setCordinateLocation(getProxyLocation());
			dd.setHandleColor(Color.magenta);
			dd.handlesize=4;
			dd.draw(graphics, cords);
		}

		@Override
		public HashMap<String, Object> getTagHashMap() {
			// TODO Auto-generated method stub
			return new HashMap<String, Object>();
		}
		
	}
}
