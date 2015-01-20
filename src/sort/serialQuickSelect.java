package sort;

import java.util.Arrays;
import sort.output.TestArray;

/**
 *
 * @author Matt Chung 
 * @author Richard Coan
 */
public class serialQuickSelect
{
    public static void main(String[] args)
    {        
        Integer[] sample = TestArray.generate(100);
        int left = 0;
        int right = 0;

        int k = 50;    
        int value = 0;

        right = sample.length - 1;

        System.out.println(Arrays.toString(sample));
        value = select(sample, left, right, k - 1);

        System.out.println(k + "-th smallest number is " + value);
        System.out.println(Arrays.toString(sample));
    }

    // swap list[x] and list[y]
    public static Integer[] swap(Integer[] list, int x, int y)
    {
        int temp = list[y];

        list[y] = list[x];
        list[x] = temp;

        return list;
    }

    public static int partition(Integer[] list, int left, int right, int pivotIndex)
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

    public static int select(Integer[] list, int left, int right, int k)
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