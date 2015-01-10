// Question 1
// Write a sequential Java program to find the k-th smallest number in a non-ordered array 
// of numbers.

// Using quickselect algorithm
public class SeqQuickSelect
{
	public static void main(String[] args)
	{
		int[] sample = {3, 7, 6, 8, 2, 4, 1, 5, 10, 9, 15, 14, 12, 13, 11};
		int left = 0;
		int right = 0;
		
		// since k is an index of the array, k-th smallest number would actually be (k+1)-th smallest number
		// for example, if k is 2, the program will actually print the 3rd smallest number in the array
		int k = 1;    
		int kValue = 0;
		
		right = sample.length - 1;
		
		kValue = select(sample, left, right, k - 1);
		
		System.out.println(k + "-th smallest number is " + kValue);
		printArray(sample);
	}
	
	public static void printArray(int[] list)
	{
		System.out.print("Final array: ");
		
		for (int i = 0; i < list.length - 1; i++)
			System.out.print(list[i] + ", ");
		
		System.out.print(list[list.length - 1]);
	}
	
	// swap list[x] and list[y]
	public static int[] swap(int[] list, int x, int y)
	{
		int temp = list[y];
		
		list[y] = list[x];
		list[x] = temp;
		
		return list;
	}
	
	public static int partition(int[] list, int left, int right, int pivotIndex)
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
		pivotIndex = partition(list, left, right, pivotIndex);
		
		if (k == pivotIndex)
			return list[k];
		
		else if (k < pivotIndex)
			return select(list, left, pivotIndex - 1, k);
		
		else
			return select(list, pivotIndex + 1, right, k);
	}
}
