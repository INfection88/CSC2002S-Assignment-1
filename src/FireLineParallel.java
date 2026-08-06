import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;
import java.lang.Thread;
import java.util.Arrays;
import java.util.ArrayList;



public class FireLineParallel extends RecursiveAction{

    private static final int CUTOFF = 50;
    
    private int start;
    private int end;
    private FireMap ParallelMap;


    public FireLineParallel(FireMap ParallelMap,int start, int end) {
        this.ParallelMap = ParallelMap;
        this.start = start;
        this.end = end;
    }

    @Override
    public void compute() {

        if (end - start <= CUTOFF) {
            return;
        }

        int split = (start + end)/2;

        FireLineParallel ThreadA = new FireLineParallel(ParallelMap, start, split);
        FireLineParallel ThreadB = new FireLineParallel(ParallelMap,split, end);

        ForkJoinPool pool = new ForkJoinPool(8);
        FireMap CreateMap = new FireMap(ParallelMap,0,ParallelMap.getRows());

        pool.invoke(CreateMap);
        ThreadA.fork();

        ThreadB.join();

        



    }

    public static void main(String[] args) {
         if (args.length < 5 || args.length > 11 || (args.length > 8 && args.length < 11)) {
            printUsage();
            System.exit(1);
        }


    }

        private static int parsePositiveInteger(String value, String name) {
        int result = Integer.parseInt(value);
        if (result <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero.");
        }
        return result;
    }

    private static int parseNonNegativeInteger(String value, String name) {
        int result = Integer.parseInt(value);
        if (result < 0) {
            throw new IllegalArgumentException(
                    name + " must be zero or greater.");
        }
        return result;
    }

    private static double parsePositiveDouble(String value, String name) {
        double result = Double.parseDouble(value);
        if (!Double.isFinite(result) || result <= 0.0) {
            throw new IllegalArgumentException(
                    name + " must be a finite value greater than zero.");
        }
        return result;
    }


    
    private static void printUsage() {
        System.err.println(
                "Usage: java FirelineSerial <rows> <columns> <seed> "
                + "<diffusion|wildfire> <output-prefix> "
                + "[max-steps] [tolerance] [mixed|grass] "
                + "[ignition-top-row ignition-left-column patch-size]");
        System.err.println("Examples:");
        System.err.println(
                "  java FirelineSerial 300 300 42 wildfire "
                + "output/fireline");
        System.err.println(
                "  java FirelineSerial 2000 2000 17 wildfire "
                + "output/benchmark 50000 0.05 grass 20 20 9");
    }

    

    

}
