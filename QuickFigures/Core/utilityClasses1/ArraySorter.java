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
 * Version: 2021.2
 */
package utilityClasses1;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import locatedObject.Hideable;
import locatedObject.Mortal;
import locatedObject.Selectable;

/**contains a variety of useful methods that work on ArrayLists
  The method names a highly self explanatory */
public class ArraySorter<ItemType> {
	
	private Random randy=null;

	/**swaps the locations of two items*/
	public void swapObjectPositionsInArray(ItemType i1, ItemType i2, ArrayList<ItemType> array) {
		int ind1 = array.indexOf(i1);
		int ind2 = array.indexOf(i2);
		if (ind1<0||ind2<0) return;
		array.set(ind1, i2);
		array.set(ind2, i1);
		
	}
	
	/**swaps the locations of two items*/
	public void swapObjectPositionsInArrayIndex(int ind1,  int ind2, ArrayList<ItemType> array) {
		ItemType i1 = array.get(ind1);
		ItemType i2 = array.get(ind2);
		if (ind1<0||ind2<0) return;
		array.set(ind1, i2);
		array.set(ind2, i1);
		
	}
	
	/**returns a random item from the array*/
	public ItemType getRandom(ArrayList<ItemType> th) {
		return th.get(Math.abs(this.getRandy().nextInt())%th.size());
	}
	
	/**scrambles the array*/
	public void randomizePointPositions(ArrayList<ItemType> th){
		randomizePointPositions(th, th.size());
	}
	
	/**scrambles the array*/
	public void randomizePointPositions(ArrayList<ItemType> th, int ntimes) {
		while (ntimes>0) {
			ItemType p1 = getRandom(th);
			ItemType p2 = getRandom(th);
		swapObjectPositionsInArray(p1, p2, th);
		ntimes--;
		}
	}
	
	public ItemType getFirstNonNull( ArrayList<ItemType> array) {
		for(ItemType item:array) {
			if (item!=null) return item;
		}
		return null;
	}
	
	public ItemType getNthNonNull( ArrayList<ItemType> array, int n) {
		for(ItemType item:array) {
			if (item!=null) {
				if (n==0)return item;else n--;
				}
		}
		return null;
	}
	
	
	/**reorders the items in array 1, so their order matches the newOrder. only applies to objects in 
	  both arrays*/
	public void setOrder(ArrayList<ItemType> array, ArrayList<ItemType> newOrder, ItemSwapper<ItemType> is) {
		ArrayList<Integer> positionsCurrent=new ArrayList<Integer>();
		
		for(ItemType i: newOrder) {
			if (array.contains(i)) {positionsCurrent.add(array.indexOf(i));}
		}
		
		
		int j0=0;
		int j=positionsCurrent.size();	
		while(j0<j ) {
			Integer lowest = takeLowest(positionsCurrent);
			if (is==null)this.swapObjectPositionsInArray(newOrder.get(j0), array.get(lowest.intValue()), array);
			else is.swapItemPositions(newOrder.get(j0), array.get(lowest.intValue()));
			j0++;
		}
		
		
		
	}
	
	/**removes the lowest number in the list and returns it*/
	private Integer takeLowest(ArrayList<Integer> nums) {
		Integer num=nums.get(0);
		for(Integer i: nums) {
			if(i<num) num=i;
		}
		
		nums.remove(num);
		return num;
	}
	
	
	
	/**performs swaps until the position of i1 in the array is at that of i2*/
	public void swapmoveObjectPositionsInArray(ItemType i1, ItemType i2, ArrayList<ItemType> array) {
		int ind1 = array.indexOf(i1);
		int ind2 = array.indexOf(i2);
		if (ind1<0||ind2<0) return;
		if (ind1>ind2) {
			while (ind1>ind2) {
				ItemType i3=array.get(ind1-1);
				while (i3==null) {ind1--; i3=array.get(ind1-1);}
				 swapObjectPositionsInArray(i1, i3, array);
				 ind1--;
				 if (i3==i2) return;
			}
			
		}
		
		else {
			while (ind1<ind2) {
				ItemType i3=array.get(ind1+1);
				while (i3==null) {ind1++; i3=array.get(ind1+1);}
				 swapObjectPositionsInArray(i1, i3, array);
				 ind1++;
				 if (i3==i2) return;
			}
			
		}

		
	}

	/**returns a sublist with only the items of the given classes*/
	public ArrayList<ItemType> getThoseOfClass(ArrayList<ItemType> array, Class<?>... c) {
		ArrayList<ItemType> output = new ArrayList<ItemType>();
		for(ItemType a: array) {
			if (isOfClass(a, c)) output.add(a);
		}
		return output;
	}
	


	/**counts the number of objects in array that are of class c*/
	public static int getNOfClass(ArrayList<?> array, Class<?>... c) {
		int i=0;
		for(Object a: array) {
			if (isOfClass(a, c)) i++;
		}
		return i;
	}
	
	/**replaces item with a list of items
	 * @param array the list being editied
	 * @param a the item being removed*/
	public void replace(ArrayList<ItemType> array, ItemType a,ArrayList<ItemType> b ) {
		int i = array.indexOf(a);
		array.remove(a);
		array.addAll(i, b);
	}
	public void insertAfter(ArrayList<ItemType> array, ItemType a,ArrayList<ItemType> b ) {
		int i = array.indexOf(a);
		array.addAll(i+1, b);
	}
	
	/**returns true if the object is an instance of at least one of the listed classes*/
	public static boolean isOfClass(Object o, Class<?>... classes) {
		for(Class<?> c: classes) {
			if (c.isInstance(o)) return true;
		}
		return false;
	}
	
	public void moveItemForward(	ItemType lin, ArrayList<ItemType> t) {
		int index = t.indexOf(lin);
		int index2=index+1;
		
		while(t.get(index2)==null) {
			index2++;
			if(index2>=t.size())return;
		}
		
		ItemType lin2=t.get(index2);
		if (lin2==null) return;

		swapObjectPositionsInArray(lin, lin2, t);
	}
	
	/**searches for the first non-null item after item lin*/
	public ItemType getItemAfter(ItemType lin, ArrayList<ItemType> t) {
		int index = t.indexOf(lin);
		int index2=index+1;
		while(t.get(index2)==null) {
			index2++;
			if(index2>=t.size())return null;
		}
		
		ItemType lin2=t.get(index2);
		return lin2;
	}
	
	/**searches for the first non-null item before item lin*/
	public ItemType getItemBefore(ItemType lin, ArrayList<ItemType> t) {
		int index = t.indexOf(lin);
		int index2=index-1;
		if(index2<0)return null;
		while(t.get(index2)==null) {
			
			index2--;
			if(index2<0)return null;
		}
		
		ItemType lin2=t.get(index2);
		return lin2;
	}
	
	/**moves the item backward*/
	public void moveItemBackward(	ItemType lin, ArrayList<ItemType> t) {
		int index = t.indexOf(lin);
		int index2=index-1;
		if(index2<0)return;
		while(t.get(index2)==null) {
			
			index2--;
			if(index2<0)return;
		}
		
		ItemType lin2=t.get(index2);
		if (lin2==null) return;
	
		swapObjectPositionsInArray(lin, lin2, t);
	}
	
	public static void removeNonSerialiazble(ArrayList<?> arr) {
		ArrayList<Object> deads = new ArrayList<Object> ();
		for(Object i:arr) {
			if (!(i instanceof Serializable)) deads.add( i);
		}
		for(Object in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes any items of class c from the array*/
	public static void removeThoseOfClass(ArrayList<?> arr, Class<?> c) {
		if (c==null||arr==null) return;
		ArrayList<Object> deads = new ArrayList<Object> ();
		for(Object i:arr) {
			if (c.isInstance(i)) deads.add(i);
		}
		for(Object in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes any items that are not of class c from the array*/
	public static void removeThoseNotOfClass(ArrayList<?> arr, Class<?> c) {
		if (c==null||arr==null) return;
		ArrayList<Object> deads = new ArrayList<Object> ();
		for(Object i:arr) {
			if (!c.isInstance(i)) deads.add(i);
		}
		for(Object in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes any mortal items that are set to 'dead'*/
	public static void removeDeadItems(ArrayList<?> arr) {
		ArrayList<Mortal> deads = new ArrayList<Mortal> ();
		for(Object i:arr) {
			if (i instanceof Mortal && ((Mortal) i).isDead()) deads.add((Mortal) i);
		}
		for(Mortal in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes hidden items from the list*/
	public static void removeHiddenItemsFrom(ArrayList<?> arr) {
		ArrayList<Hideable> deads = new ArrayList<Hideable> ();
		if(arr==null) return;
		for(Object i:arr) {
			if (i instanceof Hideable && ((Hideable) i).isHidden()) deads.add((Hideable) i);
		}
		for(Hideable in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes any selectable items that are not currently selected from the array list*/
	public static void removeNonSelectionItems(ArrayList<?> arr) {
		ArrayList<Selectable> deads = new ArrayList<Selectable> ();
		for(Object i:arr) {
			if (i instanceof Selectable && !((Selectable) i).isSelected()) deads.add((Selectable) i);
		}
		for(Selectable in:deads) {
			arr.remove(in);
		}
	}
	
	/**removes any objects that occur before object object1, within the array*/
	public static void removeItemsBefore(ArrayList<?> arr, Object object1) {
		ArrayList<Object> deads = new ArrayList<Object> ();
		int index1 = arr.indexOf(object1);
		for(Object i:arr) {
			if (arr.indexOf(i)<index1) deads.add( i);
		}
		for(Object in:deads) {
			arr.remove(in);
		}
	}
	
	/**Selects all the items that are not currently select within the array list*/
	public static void selectItems(ArrayList<?> arr) {
		ArrayList<Selectable> deads = new ArrayList<Selectable> ();
		for(Object i:arr) {
			if (i instanceof Selectable && !((Selectable) i).isSelected()) deads.add((Selectable) i);
		}
		for(Selectable in:deads) {
			in.select();
		}
	}
	
	/**Deselects all the items that are selected*/
	public static void deselectItems(ArrayList<?> arr) {
		ArrayList<Selectable> deads = new ArrayList<Selectable> ();
		for(Object i:arr) {
			if (i instanceof Selectable && ((Selectable) i).isSelected()) deads.add((Selectable) i);
		}
		for(Selectable in:deads) {
			in.deselect();
		}
	}


	/**returns a random */
	public synchronized Random getRandy() {
		if (randy==null)
			randy=new  	SecureRandom();
		return randy;
	}

	public void setRandy(Random randy) {
		this.randy = randy;
	}
	

	
	
}
