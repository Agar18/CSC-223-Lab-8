package utilities.eq_classes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EquivalenceClasses<T> 
{
	
	protected Comparator<T> _comparator;
	
	protected List<LinkedEquivalenceClass<T>> _classes;
	
	public EquivalenceClasses(Comparator<T> comparator) {
		
		_comparator = comparator;
		
		_classes = new ArrayList<LinkedEquivalenceClass<T>>();
		
	}
	
	// need to make a new list with a new equivalence class to fit it
	
	public boolean add(T element) {
		
		int eqIndex = indexOfClass(element);
		
		if(eqIndex != -1) {
			
			return _classes.get(eqIndex).add(element);
			
		}
		
		LinkedEquivalenceClass<T> list = new LinkedEquivalenceClass<T>(_comparator);
		
		_classes.add(list);
		
		return list.add(element);
		
	}
	
	public boolean contains(T element) {
		
		for(LinkedEquivalenceClass<T> list : _classes) {
			
			if(list.contains(element)) return true;
			
		}
		
		return false;
		
	}
	
	public int size() {
		
		int size = 0;
		
		for(LinkedEquivalenceClass<T> list : _classes) {
			
			size = size + list.size();
			
		}
		
		return size;
		
	}
	
	public int numClasses() {
		
		return _classes.size();
		
	}
	
	protected int indexOfClass(T element) {
		
		int index = 0;
		
		for(; index < _classes.size(); index++) {
			
			if(_classes.get(index).belongs(element)) return index;
			
		}
	
		return -1;
		
	}
	
	public String toString() {
		
		String str = "";
		
		for(LinkedEquivalenceClass<T> list : _classes) {
			
			str = str + ", " + list.toString();
			
		}
		
		return str;
		
	}

}
