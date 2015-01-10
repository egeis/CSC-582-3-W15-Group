// Question 2
// Write a parallel Java program to find the k-th smallest number in a non-ordered array of numbers. 
// Again you are not allowed to sort the array first.

public class CyclicBarrierRunnable implements Runnable
{
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ParQuickSelect.go.set(1);
	}
}
