package distributed;

import java.lang.IndexOutOfBoundsException;
/**
 *
 */
public class Initiator extends Node {

    private Comparable[] arr;
    
    public Initiator(Comparable[] arr)
    {        
        super();
        this.arr = arr;
    }
    
    /**
     * 
     * @param k
     * @return the kth value.
     */
    public Comparable getKthValue(int k) throws IndexOutOfBoundsException
    {
        if(k < 0 || k > (arr.length - 1) )
        {
            throw new IndexOutOfBoundsException();
        }
            
        int indexOfKth = -1;
        
        //Find Kth Value...
        
        return arr[indexOfKth];
    }
    
    public static void main(String[] args)
    {
        int length = 1000;
        int k = 500;
        
        Integer[] sample = sort.output.TestArray.generate(length);
        Initiator node = new Initiator(sample);
        
        System.out.println("The Kth value (where k = "+k+") is: " + node.getKthValue(length));
    }
}
