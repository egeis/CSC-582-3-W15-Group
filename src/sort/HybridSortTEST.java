package sort;

import java.util.Map;
import sort.output.AnsiColor;
import sort.output.TestArray;

/**
 *
 * @author Richard
 */
public class HybridSortTEST {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*Initialize Local Varibles and Classes.*/
        long start, end;
        final int length = 1000000;
        ParallelMergeSort pms;
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
        Integer[] test = TestArray.generate(length);  
        
        /*Initialze Parallel Merge Sort.*/
        pms = new ParallelMergeSort(test, ParallelMergeSort.INSERTION_SORT, 1000);
        freq_start = pms.getFrequency(test);
        
        /*Start Running Merge Sort.*/
        System.out.print(AnsiColor.BLUE+"Starting Sort..."+AnsiColor.RESET);
        start = System.currentTimeMillis(); //Start Time
        Comparable[] results = pms.sort();
        end = System.currentTimeMillis();   //End Time
        System.out.println(AnsiColor.BLUE+"Done!"+AnsiColor.RESET);
        
        /*Completed output results*/
        freq_end = pms.getFrequency(results);
        System.out.println("[Completed] The algorithm took: " + AnsiColor.RED + (end - start) + AnsiColor.RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+AnsiColor.RED+(freq_start.equals(freq_end))+AnsiColor.RESET);
        System.out.println("[Completed] Is Sorted? "+AnsiColor.RED+pms.isSorted(results)+AnsiColor.RESET);
    }
    
}
