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
 * Date Modified: Jan 6, 2021
 * Version: 2023.2
 */
package addObjectMenus;

import java.awt.BasicStroke;
import java.awt.Color;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.PathGraphic;
import logging.IssueLog;
import objectCartoon.ShapeRotatingPolygon;

public class CartoonPolygonAdder extends BasicGraphicAdder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int t4 = 3, t5=4,t6=5, t7=6, t8=7, t9=8, t10=9;;
	int vertex=7;
	int length=50;
	int type=0;
	static int t1=0, t2=1,t3=2;
	
	public CartoonPolygonAdder(int type) {
		this.type=type;
		 model=PathGraphic.createPolygon(createShapeMaker(type));
		shape=createShapeMaker(type);
		innitialiseType(shape,type);
	}
	
	void innitialiseType(ShapeRotatingPolygon shape, int type) {
		if (type==t1) {
			
			shape.setMoveCurveInward(0.2);
			shape.setMoveCurveInward(0.2);
			shape.setAngleDistorts(new float[] {(float) -0.03, (float) 0.03});
			shape.setAllIrregularDistances(1.2,1.6,1.9);
		}
		if (type==t2) {
			shape.setMoveCurveInward(2.4, 0.6);
			
			shape.setMidpointCurve(true);
			shape.setAllIrregularDistances(new float[]{(float) 1.4,1,(float) 1.2,1, (float)1.5});
			shape.setMidpointCurve(true);
			shape.setAlternateDistorts(true);
		}
		if (type==t3) {
		shape.setMoveCurveInward(1.45);
		//	shape.setAlternateDistorts(true);
			shape.setnVertex(6);
			
			
			shape.setAllIrregularDistances(new float[]{(float) 0.85,(float)1.2,(float) 1.15,(float) 1.3, (float)1, (float) .45});
			shape.setMidpointCurve(true);//.setAlternateDistorts(true);
			//shape.setMoveCurveInward(2,2.5,2,0);
		}
		
		if (type==t4) {
			shape.setMoveCurveInward(2.5,2,2.5,0);
			shape.setAllIrregularDistances(1,1,.11,.11);
			shape.setnVertex(8);
			shape.setMidpointCurve(true);
			shape.setCurveDistype(1);
		}
		
		if (type==t5) {
			shape.setMoveCurveInward(1.5, 0.8);
			shape.setnVertex(28);
			shape.setMidpointCurve(true);
			shape.setAllIrregularDistances(1.2,1.15,1.5,1,1.2,1.3);
			shape.setMidpointCurve(true);
			shape.setAlternateDistorts(true);
		}
		
		if (type==t6) {
			shape.setMoveCurveInward(1);
			shape.setnVertex(6);
			shape.setMidpointCurve(false);
			shape.setAllIrregularDistances(1);
		
		}
		
		if (type==t7) {
			shape.setMoveCurveInward(-1.4, 2.5,-2, 2);
			shape.setnVertex(24);
			shape.setMidpointCurve(true);
			shape.setAllIrregularDistances(1);
			shape.setCurveDistype(2);
			shape.stretch=2;
			shape.stretchv=0.7;
		}
		
		if (type==t8||t10==type) {
			shape.setStandardDisplacement(10);
			shape.setMoveCurveInward(1, 2);
			shape.setnVertex(40);
			//shape.setMidpointCurve(true);
			shape.setAllIrregularDistances(.1,1.2,.1,1,.1);
			shape.setLimitAngle(Math.PI);
			shape.setPostangle(7*Math.PI/4);
			shape.setRandomizeArray(t10==type);
			
		}
		
		if (type==t9) {
			shape.setMoveCurveInward(1.1);
			shape.setnVertex(24);
			shape.setMidpointCurve(true);
			shape.setAllIrregularDistances(1);
			shape.setCurveDistype(1);
			shape.stretch=2.6;
			shape.stretchv=1.3;
		}
		/**
		if (type==t1) {
			
		}
		
		if (type==t2) {
			
		}
		if (type==t3) {
		
		}*/
	}
	
	
	private PathGraphic model=PathGraphic.createPolygon(createShapeMaker(0));
	public ShapeRotatingPolygon shape=createShapeMaker(0);
	{
		 model.setStrokeColor(Color.blue);
	}
	
	public ShapeRotatingPolygon createShapeMaker(int type) {
		int vertex=this.vertex;
		if (type==t2) vertex=this.vertex*2;
		ShapeRotatingPolygon p = new ShapeRotatingPolygon(0,0, length, vertex);
		if (true) try {
			
			
		
			
			innitialiseType(p, type);
			p.setUpRegularVertexes();
			p.resetPolygonFromVertices();
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		p.setUpRegularVertexes();
		return p;
	}
	
	
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		PathGraphic rg = createCartoon(false, false);
		gc.add(rg);
		//rg.showOptionsDialog();
		rg.updateDisplay();
		shape.showDialog(rg);
		rg.setGraphicSetContainer(super.selector.getWorksheet());
		return rg;
	}
	public PathGraphic createCartoon(boolean dialog, boolean icon) {
		
		double olddis = shape.getStandardDisplacement();
		if (icon) shape.setStandardDisplacement(30);
		if (icon&& this.type==t2)  shape.setStandardDisplacement(15);
		
		shape.setUpRegularVertexes();
		shape.resetPolygonFromVertices();
		
		PathGraphic rg = PathGraphic.createPolygon(shape );
		rg.setStrokeColor(Color.black);
		rg.setUseFilledShapeAsOutline(true);
		rg.setFilled(true);
		rg.setFillColor(Color.red);
		if (type==t2||type==t4) {
			rg.setFillColor(Color.green);
		}
		if (type==t3) {
			rg.setFillColor(Color.blue);
		}
		if (type==t7) {
			rg.setFillColor(Color.red.darker());
		}
		if (type==t8||type==t10) {
			rg.setFillColor(Color.green);
			rg.setStrokeJoin(BasicStroke.JOIN_ROUND);
			rg.setStrokeColor(Color.green);
			rg.setStrokeWidth((float) 0.3);
			rg.setUseFilledShapeAsOutline(false);
			rg.setFilled(false);
		}
		
		if (type==t9) {
			rg.setFillColor(Color.red.brighter());
			
		}
		
		
		rg.setDashes(new float[] {10000000,2});
		
		shape.setStandardDisplacement(olddis);
		
		rg.moveLocation((int)shape.getStandardDisplacement(),(int)shape.getStandardDisplacement());
	if (t6==type) {
		rg.moveLocation(0, -15);
	}
		
		//if (type==t2) rg.moveLocation((int)shape.getStandardDisplacement(),(int)shape.getStandardDisplacement());
		// ymov);
	rg.setSupercurvemode(true);
	return rg;
	}
	
	
	double un=Math.random();

	@Override
	public String getCommand() {
		return "Add poly"+un;
	}

	@Override
	public String getMenuCommand() {
		return "";
	}

	public PathGraphic getModelForIcon() {
		return createCartoon(false, true);
	}

	public void setModel(PathGraphic model) {
		this.model = model;
	}
}
