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
package animations;

import java.awt.geom.Point2D;
import java.util.HashMap;

import locatedObject.LocatedObject2D;

public class GroupsTranslationAnimation implements Animation {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int nFrames=100;
	
	/**returns the number for frames between each for the two keyframes*/
	int numberFrames() {
		return nFrames;
	}
	
	
	HashMap<LocatedObject2D, Point2D> originalLocal=new HashMap<LocatedObject2D, Point2D>();
	HashMap<LocatedObject2D, Point2D> finalLocal=new HashMap<LocatedObject2D, Point2D>();
	
	public GroupsTranslationAnimation(HashMap<LocatedObject2D, Point2D> o, HashMap<LocatedObject2D, Point2D> f) {
		originalLocal=o;
		finalLocal=f;
	}
	
	/**sets the positions and states of the animation to the given frame number*/
	public void setToFrame(int frameNum) {
		for (LocatedObject2D item:originalLocal.keySet()) {
			double originx= originalLocal.get(item).getX();
			double originy= originalLocal.get(item).getY();
			double finalx= finalLocal.get(item).getX();
			double finaly= finalLocal.get(item).getY();
			double relativeDist= ((double) frameNum)/((double)nFrames);
			
			double x = originx    +(finalx-originx)*relativeDist;
			double y = originy    +(finaly-originy)*relativeDist;
			item.setLocation(x, y);
			
			
		}
		
		
	}
	

}
