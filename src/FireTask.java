import java.util.concurrent.*;



public class FireTask extends RecursiveTask<FireMapParallel.StepResult> {

    private int start;
    private int end;
    private FireMapParallel map;
    private FireMapParallel.Mode mode;
    private int CUTOFF;


     public FireTask(FireMapParallel map,FireMapParallel.Mode mode, int start, int end, int CUTOFF) {

        this.map = map;
        this.mode = mode;
        this.start = start;
        this.end = end;
        this.CUTOFF = CUTOFF;

    }

    @Override
    protected FireMapParallel.StepResult compute() {
     
        if(end - start <= CUTOFF) {
            return map.updateRegion(mode, start, end,1,map.getColumns() -1);
            
        }

        int mid = (start + end)/2;

        FireTask TaskA = new FireTask(map,mode, start, mid, CUTOFF);
        FireTask TaskB = new FireTask(map,mode, mid,end, CUTOFF);

        TaskA.fork();

        FireMapParallel.StepResult resultB = TaskB.compute();
        FireMapParallel.StepResult resultA = TaskA.join();
        
        return FireMapParallel.StepResult.combine(resultB,resultA);
        

    }

}
