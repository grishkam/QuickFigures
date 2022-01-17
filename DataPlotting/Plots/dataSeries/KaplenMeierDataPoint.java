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
 * Version: 2022.0
 */
package dataSeries;

import java.io.Serializable;

/**a Data point for a kaplen-meier plot*/
public class KaplenMeierDataPoint implements Serializable, DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int Event_Occured_Status=0, Censored_Status=1;
	
	private double serialTime=0;
	int status=Event_Occured_Status;
	
	public KaplenMeierDataPoint(double time, boolean censor) {
		serialTime=(time);
		this.status=  censor?  Censored_Status: Event_Occured_Status;
	}

	public boolean isCensored() {
		return status==Censored_Status;
	}

	public double getSerialTime() {
		return serialTime;
	}

	@Override
	public double getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getValue() {
		return serialTime;
	}

	@Override
	public boolean isExcluded() {
		return false;
	}

	@Override
	public String getValueString() {
		// TODO Auto-generated method stub
		return this.getValue()+"";
	}

	

	
}
