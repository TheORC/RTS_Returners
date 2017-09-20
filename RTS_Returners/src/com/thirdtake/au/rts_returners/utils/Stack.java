package com.thirdtake.au.rts_returners.utils;

import java.lang.reflect.Array;

/**
 * @author Oliver Clarke
 *
 * @param <E>
 * 
 * @ This is class is used to store references to a list of varibales such as ints.
 */
@SuppressWarnings("unchecked")
public class Stack<E> {
	
	private E[] _List;
	private int _Capacity;
	private int _Index = 0;
	
	public Stack(Class<E> c, int capacity){
        final E[] a = (E[]) Array.newInstance(c, capacity);
        this._List = a;
        this._Capacity = capacity;
	}
	
	/**
	 * @param value
	 * @ Add E to the top of the stack.
	 */
	public void Push(E value){
		//Debug.Log("Index: " + _Index + " Length: " + _List.length);
		_List[_Index] = value;
		_Index++;
		
		if(_Index > _Capacity-1)
			_Index = _Capacity-1;
	}
	
	/**
	 * @return E
	 * @ Gets the first item on the stack and removes it.
	 */
	public E Pop(){
		E value = _List[_Index];  //Get the last item in the last
		_List[_Index] = null;     //Remove the item at the end
		_Index--;                           //Reduce the index
		
		return value;
	}
	
	/**
	 * @return Gets the maximum size of the stack.
	 */
	public int Capacity(){ return _Capacity;}
	/**
	 * @return Gets the current size of the stack.
	 */
	public int Length(){ return _Index+1;}

}
