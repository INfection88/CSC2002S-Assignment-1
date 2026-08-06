import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;
import java.lang.Thread;



public class FireLineParallel extends RecursiveTask<Integer>{

    private static final int CUTOFF = 50;
    private int i;

    private int[] grid;
    private int start;
    private int end;


    public FireLineParallel(int[] grid, int start, int end) {
        this.grid = grid;
        this.start = start;
        this.end = end;
    }

    

    public void run() {


    }

    public static void main(String[] args) {
         if (args.length < 5 || args.length > 11 || (args.length > 8 && args.length < 11)) {
            printUsage();
            System.exit(1);
        }


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
