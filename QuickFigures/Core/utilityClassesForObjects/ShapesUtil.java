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
package utilityClassesForObjects;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class ShapesUtil {

	public ShapesUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static Rectangle addInsetsToRectangle(Rectangle r, Insets insets) {
		if (insets==null) return r;
		r.x-=insets.left;
		r.y=r.y-insets.top;
		r.width+=insets.left+insets.right;
		r.height+=insets.top+insets.bottom;
		return r;
	}
	public static Rectangle2D addInsetsToRectangle(Rectangle2D.Double r, Insets insets) {
		if (insets==null) return r;
		r.x-=insets.left;
		r.y-=insets.top;
		r.width+=insets.left+insets.right;
		r.height+=insets.top+insets.bottom;
		return r;
	}

}
