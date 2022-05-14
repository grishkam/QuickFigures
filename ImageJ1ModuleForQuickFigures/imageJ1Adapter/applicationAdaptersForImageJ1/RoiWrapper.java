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
 * Date Modified: Jan 4, 2021
 * Version: 2022.1
 */
package applicationAdaptersForImageJ1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.gui.*;
import ij.io.Opener;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.IllustratorObjectRef;
import illustratorScripts.PathItemRef;
import illustratorScripts.PlacedItemRef;
import illustratorScripts.TextFrame;
import locatedObject.AttachmentPosition;
import locatedObject.DefaultPaintProvider;
import locatedObject.Fillable;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.LocationChangeListenerList;
import locatedObject.Named;
import locatedObject.PaintProvider;
import locatedObject.StrokedItem;
import logging.IssueLog;
import textObjectProperties.HasText;
/**An implementation of LocatedObject2D that allows many QuickFigures classes
 to act on ImageJ Rois. not extensively used anymore. The methods in this class
 might be useful to future programmers but not currently important for the package*/
public class RoiWrapper implements LocatedObject2D, HasText, IllustratorObjectConvertable, StrokedItem, Fillable, Named{
	Roi roi;
	
	
	public RoiWrapper(Roi roi) {
		this.roi=roi;
	}
	
	
	 public boolean doesIntersect(Rectangle2D rect) {
		   return roi.getPolygon().intersects(rect);
	   }
	   public  boolean isInside(Rectangle2D rect) {
		   return rect.contains(getObject().getPolygon().getBounds());
	   }
	 
		public void takeFromImage( Object imp) {
			takeRoiFromImage(getObject() , (ImagePlus)imp);
		}
		public void addToImage( Object imp) {
			addRoiToImage(getObject() ,(ImagePlus)imp);
			
		}
		
		/**Adds an object to the back of the stack in the image*/
		public
		void addToImageBack(Object imp) {
			if (imp instanceof ImagePlus)
			addRoiToImageBack(getObject() , (ImagePlus)imp);
		}
		/**moves the roi by a given displacement*/	
		public void moveLocation(double xmov, double ymov) {
			moveRoiLocation(getObject() , (int)xmov, (int)ymov);
		}

		
		public void setWrappedObject(Roi roi) {
			this.roi=roi;
		}

		@Override
		public float getStrokeWidth() {
			return (float) getObject() .getStrokeWidth();
		}

		
		public Shape getShape() {
		if (getObject()  instanceof ShapeRoi ) return ((ShapeRoi)getObject() ).getShape();
		return getObject() .getPolygon();
		}

		@Override
		public Rectangle getBounds() {
			return getObject() .getBounds();
			
		}

		@Override
		public Polygon getOutline() {
			if (getObject()==null) return null;
			return getObject().getPolygon();
		}
		
		public RoiWrapper copy() {
			return new RoiWrapper((Roi) getObject() .clone());
		}

		@Override
		public Point getLocation() {
				return getRoiLocation(roi);
		}

		@Override
		public void setLocation(double x, double y) {
			getObject() .setLocation((int)x, (int)y);
		}

		
		public Roi getObject() {
			if (roi==null) {
				return null;
			}
		return roi;
		}

		@Override
		public void setStrokeColor(Color c) {
			roi.setStrokeColor(c);
		}

		@Override
		public Color getStrokeColor() {
			return roi.getStrokeColor();
		}

		@Override
		public void setFillColor(Color c) {
			roi.setFillColor(c);
			
		}

		@Override
		public Color getFillColor() {
			return roi.getFillColor();
		}

		@Override
		public void setTextColor(Color c) {
			roi.setStrokeColor(c);
		}

		@Override
		public Color getTextColor() {
			return roi.getStrokeColor();
		}


	
		

		
		@Override
		public String getName() {
			return roi.getName();
		}







		public Point getPoint(int ind) {
			if (roi instanceof ij.gui.Line ||roi instanceof ij.gui.Arrow) {
				ij.gui.Line lin=(ij.gui.Line) roi;
				if (ind==0) return new Point(lin.x1, lin.y1);
				if (ind==1) return new Point(lin.x2, lin.y2);
			}
		
				int[] xs=roi.getPolygon().xpoints;
				int[] ys=roi.getPolygon().ypoints;
				if (ind<xs.length) return new Point(xs[ind], ys[ind]);
			
			// TODO Auto-generated method stub
			return null;
		}


	

		@Override
		public void setName(String name) {
			roi.setName(name);
		}


		@Override
		public String getText() {
			if (roi instanceof TextRoi) return ((TextRoi)roi).getText();
			return null;
		}


		@Override
		public void setText(String st) {
		
		}


		@Override
		public Font getFont() {
			if (roi instanceof TextRoi) return ((TextRoi)roi).getCurrentFont();
			return null;
		}


		@Override
		public void setFont(Font font) {
			if (roi instanceof TextRoi) ((TextRoi)roi).setCurrentFont(font);
		}


		@Override
		public Stroke getStroke() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public Point getLocationUpperLeft() {
			return this.getLocation();
		}


		@Override
		public void setLocationUpperLeft(double x, double y) {
			this.setLocation(x, y);
			
		}
		
		@Override
		public void setLocationUpperLeft(Point2D p) {
			this.setLocation(p.getX(), p.getY());
			
		}


		@Override
		public Object dropObject(Object ob, int x, int y) {
			if (ob instanceof Color) this.setStrokeColor((Color) ob);
			return null;
		}


		@Override
		public boolean isFilled() {
				return roi.getFillColor()==null;
			
		}


		@Override
		public void setFilled(boolean fill) {
			if (fill) {
				
			}
			else roi.setFillColor(null);
			
		}


		@Override
		public void setStrokeWidth(float width) {
			roi.setStrokeWidth(width);
			
		}


		@Override
		public float[] getDashes() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public void setDashes(float[] fl) {
			
			
		}


		@Override
		public void setStroke(BasicStroke stroke) {
			this.setStrokeWidth(stroke.getLineWidth());
			
		}
		
		public int isUserLocked() {
			return 0;
		}


		@Override
		public Rectangle getExtendedBounds() {
			return getBounds();
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
		public void kill() {
			
			
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
		public int getStrokeJoin() {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public int getStrokeCap() {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public void setStrokeJoin(int selectedIndex) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void setStrokeCap(int size) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public boolean isDead() {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public LocationChangeListenerList getListenerList() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public void setMiterLimit(double miter) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public double getMiterLimit() {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public PaintProvider getFillPaintProvider() {
			// TODO Auto-generated method stub
			return new DefaultPaintProvider(getFillColor());
		}


		@Override
		public void setFillPaintProvider(PaintProvider p) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void setLocation(Point2D p) {
			this.setLocation(p.getX(), p.getY());
		}
		
		public static Point getRoiLocation(Roi roi) {
			if (roi==null) return null;
			int roix= (int) roi.getBounds().getX();
			int roiy= (int) roi.getBounds().getY();
			if (roi instanceof Arrow) {
				  ij.gui.Line lin= (ij.gui.Line) roi;    
			      int newx, newy;
			      if (lin.x2>lin.x1) newx=lin.x1; else newx=lin.x2;
			      if (lin.y2>lin.y1) newy=lin.y1; else newy=lin.y2;
			      roix=newx;
			      roiy=newy;
			} 
			return new Point(roix, roiy);
		}
		
		public static ArrayList<Roi> getObjects(ImagePlus imp) {
			if (imp==null||imp.getOverlay()==null) {
				return new ArrayList<Roi>() ;
				}
			Roi[] rois=imp.getOverlay().toArray();
			ArrayList<Roi> output=new ArrayList<Roi>();
			for(Roi roi: rois) output.add(roi);
			return output;
		}

		public static void moveRoiLocation(Roi roi, int xmov, int ymov) {
			if (roi==null) return;
			
			Number x=roi.getBounds().getX()+xmov;
			Number y=roi.getBounds().getY()+ymov;
			if (roi instanceof ij.gui.Line) {
				  ij.gui.Line lin= (ij.gui.Line) roi;    
			      int newx, newy;
			      if (lin.x2>lin.x1) newx=lin.x1; else newx=lin.x2;
			      if (lin.y2>lin.y1) newy=lin.y1; else newy=lin.y2;
			      x=newx+xmov;
			      y=newy+ymov;
			} 
			roi.setLocation(x.intValue(), y.intValue());
			
		}

		
		public static Roi getClickedRoi(ImagePlus imp, int x, int y) {
			if (imp==null) return null;
					Roi roi1;
					ArrayList<Roi> rois=getObjects(imp);
					for (int i=rois.size()-1; i>=0;  i-- ) {
						Roi roi= rois.get(i);
						if (roi==null) continue;
						Polygon p=roi.getPolygon();
						if (roi instanceof ij.gui.Line && !(roi instanceof ij.gui.Arrow)) {
							ij.gui.Line lin=(ij.gui.Line) roi;
							int w=2;  if (roi.getStrokeWidth()>4) {w=(int)roi.getStrokeWidth()/2;}
							p=new Polygon(new int [] {lin.x1, lin.x2, lin.x1, lin.x2}, new int[] {lin.y1-w, lin.y2-w, lin.y1+w, lin.y2+w}, 4);
						}
						if (p.contains(x, y)) {
							roi1=roi;
							return roi1;
						}
				}
					return null;
				

			}
			
		public static ArrayList<Roi> unwrap(ArrayList<LocatedObject2D> array) {
			ArrayList<Roi> output=new ArrayList<Roi> ();
			for(LocatedObject2D wrap: array) {
				
				if (wrap instanceof RoiWrapper ) {
					Object o=((RoiWrapper)wrap).getObject();
					if (o instanceof Roi)
					output.add((Roi)o);
				}
				
			}
			return output;
		}
		
		/**Adds an object to the back of the stack in the image*/
		public static void addRoiToImageBack(Roi roi, ImagePlus imp) {
			if (imp==null||roi==null) return;
			{
			Overlay o2=new Overlay();
			o2.add(roi);
			Overlay o=imp.getOverlay();
			
			if (o!=null) {
				for (Roi roi2: o.toArray()) {o2.add(roi2);}
				}
			imp.setOverlay(o2);
			}
		}
		

		public static void takeRoiFromImage(Roi roi, ImagePlus imp) {
			
					Overlay o=imp.getOverlay(); 
					if (o==null) {imp.setOverlay(new Overlay()); o=imp.getOverlay();}
					o.remove(roi);
		}
		
		
		public static void addRoiToImage(Roi roi, ImagePlus imp) {
			if (imp==null||roi==null) return;
				Overlay o=imp.getOverlay();
				if (o==null) imp.setOverlay(new Overlay());
			if (o!=null) o.add(roi);
			
		}
	
		@Override
		public Object toIllustrator(ArtLayerRef aref) {
			// TODO Auto-generated method stub
			return null;
		}
		

		public static PolygonRoi wandPolygon(Roi roi) {
			ImagePlus ip=new ImagePlus("name", roi.getMask());
			Wand w=new Wand(ip.getProcessor() ); w. 	autoOutline(ip.getWidth()/2, ip.getHeight()/2) ; 
			ArrayList<Integer> newx=new 	ArrayList<Integer>();
			ArrayList<Integer> newy=new 	ArrayList<Integer>();
			
			for (int lastpoints=0; lastpoints<w.xpoints.length; lastpoints++) try{
				
			if 	(w.xpoints[lastpoints]==0 &&w.ypoints[lastpoints]==0 ) {
				w.xpoints[lastpoints]=w.xpoints[lastpoints-1];
				w.ypoints[lastpoints]=w.ypoints[lastpoints-1];
			} else {
				newx.add(w.xpoints[lastpoints]);
				newy.add(w.ypoints[lastpoints]);
			}
				
			} catch (Throwable t) {t.printStackTrace();}
			int[] newx2=new int[newx.size()]; for(int i=0; i<newx.size(); i++) {newx2[i]=newx.get(i);}
			int[] newy2=new int[newy.size()];for(int i=0; i<newy.size(); i++) {newy2[i]=newy.get(i);}
			
			PolygonRoi roi2= new PolygonRoi(newx2, newy2, newx2.length, Roi.POLYGON);// if (roi2==null) IJ.log("Problem drawing arrow");
			roi2.setFillColor(roi.getStrokeColor()); roi2.setLocation((int)roi.getBounds().getX(), (int)roi.getBounds().getY());
			return roi2;
		}
		
		public static void SendRoiToIllutrator(IllustratorObjectRef layer, Roi roi) {
			if (roi==null||layer==null) return;
			if (roi instanceof ij.gui.ImageRoi)  {
				ImageRoi image=(ImageRoi) roi;
				PlacedItemRef p=new PlacedItemRef();
				p.createItem(layer);
				p.prepareImageForJavaScript((new Opener().deserialize(image.getSerializedImage())).getProcessor().createImage(), "Image ", (int)roi.getBounds().getX(), (int)roi.getBounds().getY(), false);
				p.embed();
				return;
				//p.prepareImageForJavaScript(, "ImageRoi "+newlayerName, (int) roi.getBounds().getX(), (int) roi.getBounds().getY(), link);
			}
			if (roi instanceof ij.gui.Arrow){
				IssueLog.log("cant draw arrow");
			//if (drawPolygonArrow)	 {roi= wandPolygon(roi) ;}
			}
			
			PathItemRef p=new PathItemRef();
			if (roi instanceof TextRoi) {
				TextRoi t=(TextRoi) roi;
				TextFrame tf = new TextFrame();
				String contents =t.getText().split(""+'\n')[0];
				
				Rectangle tr=t.getBounds();
				
				
				tr.y+=t.getCurrentFont().getSize()/4;
				tr.width*=1.05;
				tf.createAreaItem(layer, tr, contents);
		
				
				tf.createCharAttributesRef();
				tf.getCharAttributesRef().setFillColor(t.getStrokeColor());
				tf.getCharAttributesRef().setfont(t.getCurrentFont());
				return;
				
			} else if (roi.getType()==Roi.RECTANGLE) {
				p.createRectangle(layer, roi.getBounds());
			} else 
			if (roi.getType()==Roi.OVAL) {
				p.createElipse(layer, roi.getBounds());
				
			} else
				if (roi instanceof ij.gui.Line) {
					p.createItem(layer);
					Point[] pt=new Point[2];
					ij.gui.Line lin=(ij.gui.Line) roi;
					pt[0]=new Point(lin.x1, lin.y1);
					pt[1]=new Point(lin.x2, lin.y2);
					p.setPointsOnPath(pt, false);
				} else 
					
			{
			p.createItem(layer);
			p.setPointsOnPath(roi.getPolygon(), roi.getType()==Roi.POLYGON);}
			p.setFillColor(roi.getFillColor());
			p.setStrokeColor(roi.getStrokeColor());
			p.setFilled(roi.getFillColor()!=null);
			p.setStoke((int)roi.getStrokeWidth());
		}
		
		
		public static void sendOverLayToIlls(ArtLayerRef aref, Overlay o) {
			if (o!=null&&aref!=null) {
				
				for (Roi roi: o.toArray()) try {
					SendRoiToIllutrator( aref, roi);
					} catch (Throwable t) {IssueLog.logT(t);}
			}
		}
		@Override
		public boolean isHidden() {
			return false;
		}
		@Override
		public void setHidden(boolean b) {
			
			
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
		public boolean isFillable() {
			// TODO Auto-generated method stub
			return true;
		}


		@Override
		public HashMap<String, Object> getTagHashMap() {
			// TODO Auto-generated method stub
			return new HashMap<String, Object>();
		}
		
		
		

}
