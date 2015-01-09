/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ThreadPoolExecutor;
import static sort.MergeSort.ANSI_BLUE;
import static sort.MergeSort.ANSI_RED;
import static sort.MergeSort.ANSI_RESET;

/**
 *
 * @author Richard
 */
public class HybridSortTEST {
    private static ThreadPoolExecutor executor;
    private static CompletionService service;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                /*Initialize Local Varibles and Classes.*/
        long start, end;
        final int length = 1000000;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        ParallelMergeSort pms;
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
         /*Create the Random Array.*/
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        /*Initialze Parallel Merge Sort.*/
        pms = new ParallelMergeSort(test, ParallelMergeSort.INSERTION_SORT, 1000);
        freq_start = pms.getFrequency(test);
        
        /*Start Running Merge Sort.*/
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        Comparable[] results = pms.sort();
        end = System.currentTimeMillis();   //End Time
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        
        /*Completed output results*/
        freq_end = pms.getFrequency(results);
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted(results)+ANSI_RESET);
                
        pms.executor.shutdown();    //Shuting Down executor
    }
    
}
