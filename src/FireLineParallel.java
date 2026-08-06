import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;
import java.lang.Thread;



public class FireLineParallel extends RecursiveTask<Integer>{

    private static final int CUTOFF = 50;
    private int i;

    public FireLineParallel(int i) {
        this.i = i;
    }

    public void run() {


    }

    public static void main(String[] args) {



    }

    

    

}
