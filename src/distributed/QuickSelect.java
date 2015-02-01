package distributed;

public class QuickSelect
{
	// swap list[x] and list[y]
	public static Comparable[] swap(Comparable[] list, int x, int y)
	{
		Comparable temp = list[y];
		
		list[y] = list[x];
		list[x] = temp;
		
		return list;
	}
	
	public static int partition(Comparable[] list, int left, int right, Comparable pivotValue)
	{
		int storeIndex = left;
		
		for(int i = left; i <= right; i++)
		{
                        if (list[i].compareTo(pivotValue) < 0)
			//if (list[i] < pivotValue)
			{
				list = swap(list, storeIndex, i);
				storeIndex++;
			}
		}
		
		// return index of first value greater than pivot value
		return storeIndex;
	}
}
