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
package appContext;

import java.awt.Color;

/**Interface that keeps track of a toolbar color, not presently used for any critical features
  but kept in the event it is needed to program later features*/
public interface ToolbarColorContext {
	
	public Color getForeGroundColor() ;

	
	public Color getBackGroundColor() ;
}
