package geometry_objects.angle;

import java.util.Comparator;

/**
* A LinkedEquivalenceClass provides a means of storing 'equivalent' objects.
* We use one value to be a representative (canonical) element that represents all
* elements in the equivalence class. 
*
* <p>Bugs: None
*
* @author Sean Rowland, Mason Taylor, Alex 
* @date 02/11/2023
*/



public class LinkedEquivalenceClass<T>
{
	protected T _canonical;
	protected Comparator<T> _comparator;
	protected LinkedList<T> _rest;
	
	public LinkedEquivalenceClass(Comparator<T> comparator)
	{
		_canonical = null;
		_comparator = comparator;
		_rest = new LinkedList<T>();
	}
	
	
	/**
	 *	Returns the canonical element for the equivalence class.
	 */
	public T canonical() { return _canonical; }
	
	/**
	 *	Returns true if the equivalence class is empty.
	 */
	public boolean isEmpty() { return _canonical == null; }
	
	/**
	 *	Clears all elements from the equivalence class.
	 */
	public void clear()
	{
		_canonical = null;
		clearNonCanonical();

	}
	
	/**
	 *	Clears all the non-canonical elements from the equivalence class.
	 */
	public void clearNonCanonical() { _rest.clear(); }
	
	/**
	 *	Returns the size of the equivalence class.
	 */
	public int size()
	{
		if(_canonical == null) return 0;
		return 1 + _rest.size();
	}
	
	/**
	 *	Adds an element to the equivalence class if it belongs. 
	 *	Returns whether is was successfully added.
	 */
	public boolean add(T element)
	{
		// If there is no canonical element, then add the element as the canonical.
		if(_canonical == null) 
		{
			_canonical = element;
			return true;
		}
		
		// If the element does not belong, do not add it.
		if(!belongs(element)) return false;
			
		// Otherwise add the element to the equivalence class.
		_rest.addToFront(element);
		return true;
	}
	
	/**
	 *	Returns whether a target element is contained in the equivalence class 
	 */
	public boolean contains(T target)
	{
		// If the class is empty or the element does not belong, then it is not in the class.
		if(isEmpty() || !belongs(target)) return false;
		
		// Check if the element is the canonical element.
		if(_canonical.equals(target)) return true;
		
		// Check if the element is in the rest of the equivalence class.
		return _rest.contains(target);
	}
	
	/**
	 *	Determine whether a given element belongs in the equivalence class by testing only
	 *  whether the given element is equivalent to the canonical element.
	 */
	public boolean belongs(T target){ return _comparator.compare(target,  _canonical) == 0; }
	
	/**
	 *	Remove a target element from the equivalence class if it is contained.
	 */
	public boolean remove(T target)
	{
		if(isEmpty()) return false;
		
		if(_canonical.equals(target)) return removeCanonical();

		return _rest.remove(target);
	}
	
	/**
	 *	Remove the canonical element from the equivalence class, and promote a new one from the class if another exists.
	 */
	public boolean removeCanonical()
	{
		// Check if there is a canonical to remove.
		if(isEmpty()) return false;
		
		
		// If there are still elements in the equivalence class then promote a new canonical.
		if(!isEmpty())
		{	
			T newCanonical = _rest.getFirst();
			remove(newCanonical);
			_canonical = newCanonical;
		}
		
		
		// Otherwise clear the canonical.
		else _canonical = null;
		
		return true;
	}
	
	/**
	 *	Demote the current canonical element to the rest of the equivalence class and add 
	 *	the new element as the canonical element.
	 */
	public boolean demoteAndSetCanonical(T element)
	{
		if(!belongs(element)) return false;
		
		_rest.addToFront(_canonical);
		_canonical = element;
		return true;
	}
	
	
	/**
	 *	Returns a string representation of the equivalence class.
	 */
	public String toString() { return "{" + _canonical + " | " + _rest.toString() + "}" ; }
}
