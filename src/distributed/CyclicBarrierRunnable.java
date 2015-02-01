package distributed;

public class CyclicBarrierRunnable implements Runnable
{
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ParallelQuickSelect.go.set(1);
	}
}
