/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort.output;

import java.util.Random;
/**
 * Generates Test arrays of the Integer type.
 * @author Richard Coan
 */
public class TestArray {
    private static Random rand = new Random();

    /**
     * Generates an array of a given length.
     * @param length the specified size of the array.
     * @return the unsorted testing array.
     */
    public static Integer[] generate(int length) {
        if(length < 1) throw new IllegalArgumentException("Array Length too small.");
        Integer[] test = new Integer[length];
        
         /*Create the Random Array.*/
        for(int i = 0; i < length; i++) {
            test[i] = rand.nextInt();
        }
                
        return test;
    }
}
