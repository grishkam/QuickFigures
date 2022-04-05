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
 * Date Created: Mar 26, 2022
 * Date Modified: Mar 27, 2022
 * Version: 2022.0
 */
package plates;

import java.util.ArrayList;

/**
 
 * 
 */
public enum PlateOrientation {
	
	STANDARD(1,0), FLIP(0,-1);
	
	public int xFlow=1;
	public int yFlow=0;
	
	PlateOrientation(int dx, int dy) {
		this.xFlow=dx;
		this.yFlow=dy;
	}
	
	
	

}
