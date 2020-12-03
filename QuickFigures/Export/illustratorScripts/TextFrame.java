/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package illustratorScripts;


import java.awt.Rectangle;
import java.awt.geom.Point2D;

import logging.IssueLog;

public class TextFrame extends IllustratorObjectRef {
	
	CharAttributesRef charat=null;
	PathItemRef path=null;
	
	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String createItem(IllustratorObjectRef layer) {
		set=true;
		String output="";
		output+='\n'+"var "+refname+" ="+layer.refname+".textFrames.add();";
		addScript(output);
		return output;
	}
	
	
	
	public String createAreaItem(IllustratorObjectRef layer, Rectangle r, String contents) {
		
		double x=getGenerator().x0+((int) r.getX()*getGenerator().scale);
		double y=(r.getY()*getGenerator().scale);
		if (getGenerator().invertvertical) y=invertY(y);
		y+=getGenerator().y0;
		double width=(r.getWidth()*getGenerator().scale);
		double height=(r.getHeight()*getGenerator().scale);
		if (width==0&&height==0) {IssueLog.log("invalide height and width for illustrator item");}
		
		String output="var rectRef ="+ layer.refname+".pathItems.rectangle("+y+","+x+ "," +width +","+height+");"; 
		output+='\n'+getAssignment()+ layer.refname+".textFrames.areaText(rectRef);";
		output+='\n' +refname+".contents = '"+contents+"';";
		addScript(output);
		setContents(contents) ;
		return output;
	}
	

	
	public PathItemRef getPath() {
		return path;
	}
	
	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String createLinePathItem(IllustratorObjectRef layer, Point2D[] point2ds) {
		path=new PathItemRef();
		path.createItem(layer);
		path.setPointsOnPath(point2ds, false);
		set=true;
		String output="";
		output+='\n'+"var "+refname+" ="+layer.refname+".textFrames.pathText("+path.refname+");";
		addScript(output);
		return output;
	}
	
	public String createCharAttributesRef() {
		charat= new CharAttributesRef();
		String output=charat.getAssignment()+refname+".textRange.characterAttributes;";
		addScript(output);
		return output;
	}
	public CharAttributesRef getCharAttributesRef() {
		return charat;
	}
	
	

	public String setContents(String contents) {
		String output="";
		output+=refname+".textRange.contents= '"+contents+"';";
		addScript(output);
		return output;
	}

	public String setContents2(String contents) {
		String output="";
		output+=refname+".contents= '"+contents+"';";
		addScript(output);
		return output;
	}



	public void createLinePathItem(ArtLayerRef aref, Point2D baseLineStart,
			Point2D baseLineEnd) {
		this.createLinePathItem(aref, new Point2D[] {
				new Point2D.Double(
						baseLineStart.getX(),
						baseLineStart.getY()
						), 
				new Point2D.Double(
					baseLineEnd.getX(), 
					baseLineEnd.getY()
						)
		}
		);
	}

}
