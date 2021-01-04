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
package standardDialog;

import java.awt.Container;
import java.awt.Insets;

public interface OnGridLayout {

	
	public void placeItems(Container jp, int x0, int y0) ;
	public int gridHeight();
	public int gridWidth();
	public static Insets lastInsets=new Insets(2,2,2,10);
	public static Insets firstInsets=new Insets(2,10,2,2);
	public static Insets middleInsets=new Insets(2,2,2,2);
	
}
