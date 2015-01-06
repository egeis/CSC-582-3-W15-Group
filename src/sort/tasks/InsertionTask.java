/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort.tasks;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 *
 * @author egeis
 */
public class InsertionTask extends Base implements Callable<Comparable[]> {
    private Comparable[] source;
            
    /**
     * Initialize the Task with an comparable type Array or Sub-Array.
     * @param src The Source array.
     */
    public InsertionTask(Comparable[] src) {
        this.source = src;
    }
    
    @Override
    public Comparable[] call() throws Exception {
        for (int i = 0; i < source.length; i++) {
            for (int j = i; j > 0; j--) {
                if (source[j-1].compareTo(source[j]) > 0) {
                    Comparable swap = source[j-1];
                    source[j-1] = source[j];
                    source[j] = swap;
                }
                else break;
            }
        }
        
        return source;
    }

    // read in a sequence of words from standard input and print
    // them out in sorted order
    public static void main(String[] args) {
        final int length = 10;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        InsertionTask it = new InsertionTask(test); 
        freq_start = it.getFrequency(test);
        System.out.println("Running with length: " + length);
        
        try{
            test = (Integer[]) it.call();
        } catch(Exception e) {
            //Do Nothing
        }
        
        freq_end = it.getFrequency(test);
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+it.isSorted(test)+ANSI_RESET);
    }
}