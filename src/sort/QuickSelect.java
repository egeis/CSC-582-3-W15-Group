// Question 2
// Write a parallel Java program to find the k-th smallest number in a non-ordered array of numbers. 
// Again you are not allowed to sort the array first.

public class QuickSelect
{
	// swap list[x] and list[y]
	public static int[] swap(int[] list, int x, int y)
	{
		int temp = list[y];
		
		list[y] = list[x];
		list[x] = temp;
		
		return list;
	}
	
	public static int partition(int[] list, int left, int right, int pivotValue)
	{
		int storeIndex = left;
		
		for(int i = left; i <= right; i++)
		{
			if (list[i] < pivotValue)
			{
				list = swap(list, storeIndex, i);
				storeIndex++;
			}
		}
		
		// return index of first value greater than pivot value
		return storeIndex;
	}
	
	public static int seqPartition(int[] list, int left, int right, int pivotIndex)
	{
		int pivotValue = list[pivotIndex];
		
		list = swap(list, pivotIndex, right);
		
		int storeIndex = left;
		
		for(int i = left; i < right; i++)
		{
			if (list[i] < pivotValue)
			{
				list = swap(list, storeIndex, i);
				storeIndex++;
			}
		}
		
		list = swap(list, right, storeIndex);
		
		return storeIndex;
	}
	
	public static int select(int[] list, int left, int right, int k)
	{
		int pivotIndex = 0;
		
		if (left == right)
			return list[left];
				
		pivotIndex = (int) (left + Math.floor(Math.random() * (right - left + 1))); 
		pivotIndex = seqPartition(list, left, right, pivotIndex);
		
		if (k == pivotIndex)
			return list[k];
		
		else if (k < pivotIndex)
			return select(list, left, pivotIndex - 1, k);
		
		else
			return select(list, pivotIndex + 1, right, k);
	}
}
