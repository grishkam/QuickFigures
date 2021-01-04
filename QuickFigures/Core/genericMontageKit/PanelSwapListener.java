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
package genericMontageKit;

import layout.basicFigure.BasicLayout;

/**Listens for a panel swap event*/
public interface PanelSwapListener {
	void onSwapCol(BasicLayout ml, int row1, int row2);
	void onSwapRow(BasicLayout ml, int row1, int row2);
	void onSwapPanel(BasicLayout ml, int row1, int row2);
}
