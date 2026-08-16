import java.util.concurrent.*;



public class FireTask extends RecursiveTask<FireMapParallel.StepResult> {

    private static final int CUTOFF = 50;

    private int start;
    private int end;
    private FireMapParallel map;
    private FireMapParallel.Mode mode;

     public FireTask(FireMapParallel map,FireMapParallel.Mode mode, int start, int end) {

        this.map = map;
        this.mode = mode;
        this.start = start;
        this.end = end;

    }


    /***
     * @param none
     * 
     * DESCRIPTION:
     * -> This method (derived from the RecursiveTask class) is overriden to run and combine threads.
     *    We use the SEQUENCIAL CUTOFF defined above to stop and return the map reigon after end- start is less or equal to the 
     *    cutoff 
     * 
     *    If this is not met, we compute the midpoint of the array and create 2 threads with bounds above and below the midpoint 
     *    respectively, we fork Thread A and compute Thread B, such that Thread B will compute first and then Thread A will 
     *    follow after, with the final step result being computed with the combine method of FireMap.stepResult
     * 
     * 
     */

    @Override
    protected FireMapParallel.StepResult compute() {
     
        if(end - start <= CUTOFF) {
            return map.updateRegion(mode, start, end,1,map.getColumns() -1);
            
        }

        int mid = (start + end)/2;

        FireTask TaskA = new FireTask(map,mode, start, mid);
        FireTask TaskB = new FireTask(map,mode, mid,end);

        TaskA.fork();

        FireMapParallel.StepResult resultB = TaskB.compute();
        FireMapParallel.StepResult resultA = TaskA.join();
        
        return FireMapParallel.StepResult.combine(resultB,resultA);
        

    }

}
