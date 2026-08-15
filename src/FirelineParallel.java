/**
 * A Parallel version of the FireLine project that will send data to the FireMap.java file to create a map using the FORKJOINPOOL framework
 * @author Tauriq Petersen
 * @version 7b6d2e52f0ebd2221fca77e5ef6973de2a614a2a
 * 
 * Due Date: 24/08/2026
 * 
 */






import java.util.concurrent.*;

public class FirelineParallel extends RecursiveTask<FireMap.StepResult>{

    private static final int DEFAULT_MAXIMUM_STEPS = 5_000;
    private static final double DEFAULT_TOLERANCE = 0.05;
    private static final int CUTOFF = 50;

    private int start;
    private int end;
    private FireMap map;
    private FireMap.Mode mode;

    public FirelineParallel(FireMap map,FireMap.Mode mode, int start, int end) {

        this.map = map;
        this.mode = mode;
        this.start = start;
        this.end = end;

    }
    public static void main(String[] args) {
        if (args.length < 5 || args.length > 11 || (args.length > 8 && args.length < 11)) {
            printUsage();
            System.exit(1);
        }

        try {
            int rows = parsePositiveInteger(args[0], "rows");
            int columns = parsePositiveInteger(args[1], "columns");
            long seed = Long.parseLong(args[2]);
            FireMap.Mode mode = FireMap.Mode.fromString(args[3]);
            String outputPrefix = args[4].trim();
            int maximumSteps = args.length >= 6
                    ? parsePositiveInteger(args[5], "maximum steps")
                    : DEFAULT_MAXIMUM_STEPS;
            double tolerance = args.length >= 7
                    ? parsePositiveDouble(args[6], "tolerance")
                    : DEFAULT_TOLERANCE;
            FireMap.Landscape landscape = args.length >= 8
                    ? FireMap.Landscape.fromString(args[7])
                    : FireMap.Landscape.MIXED;

            Integer ignitionTopRow = null;
            Integer ignitionLeftColumn = null;
            Integer ignitionPatchSize = null;
            if (args.length == 11) {
                ignitionTopRow = parseNonNegativeInteger(
                        args[8], "ignition top row");
                ignitionLeftColumn = parseNonNegativeInteger(
                        args[9], "ignition left column");
                ignitionPatchSize = parsePositiveInteger(
                        args[10], "ignition patch size");
            }

            if (outputPrefix.isEmpty()) {
                throw new IllegalArgumentException(
                        "The output prefix may not be empty."); 
            }

            // Here we instantiate a new FireMap object to parse data into
            FireMap map = new FireMap(
                    rows, columns, seed, mode, landscape,
                    ignitionTopRow, ignitionLeftColumn, ignitionPatchSize);

            long startTime = System.nanoTime(); 
            FireMap.StepResult result = null;
            int stepsCompleted = 0;
            boolean converged = false;

            // Create a ForkJoinPool object that creates a pool of threads so it can be used in the following code
            ForkJoinPool pool = new ForkJoinPool(8);
            while (stepsCompleted < maximumSteps) {

                map.prepareNextState();
                // Create a new FirelineParallel object that contains the map, mode, and how the grid gets parsed:
                    // -> SO for this example, we will do do it in linear strips. ie: [x_1 , x_2 , x_3 , x_4 , x_5 , ...., x_n] => a new task.

                FirelineParallel ParallelTask = new FirelineParallel(map,mode,1,map.getRows() -1);

                result = pool.submit(ParallelTask).join();

                map.completeStep();
                stepsCompleted++;

                 if (mode == FireMap.Mode.WILDFIRE) {
                    converged = result.getBurningCells() == 0
                            && result.getMaximumTemperatureChange() < tolerance;
                } else {
                    converged = result.getMaximumTemperatureChange() < tolerance;
                }

                if (converged) {
                    break;
                }

            }
            pool.shutdown();
            long endTime = System.nanoTime();
            double elapsedMilliseconds = (endTime - startTime) / 1_000_000.0;

            map.writeImages(outputPrefix);

            System.out.println("Fireline Parallel ation");
            System.out.printf("Mode: %s%n", mode.name().toLowerCase());
            System.out.printf("Rows: %d, Columns: %d%n", rows, columns);
            System.out.printf("Random seed: %d%n", seed);
            System.out.printf("Landscape: %s%n",
                    landscape.name().toLowerCase());
            System.out.printf("Initial source: %s%n",
                    map.getSourceDescription());
            System.out.printf("Timesteps completed: %d%n", stepsCompleted);
            System.out.printf("Converged: %s%n", converged ? "yes" : "no");
            System.out.printf("Final burning cells: %d%n",
                    result == null ? 0 : result.getBurningCells());
            System.out.printf("Cells burned: %d%n", map.countBurnedCells());
            System.out.printf("Maximum peak temperature: %.3f%n",
                    map.getMaximumPeakTemperature());
            System.out.printf("Maximum change in final timestep: %.6f%n",
                    result == null
                            ? 0.0
                            : result.getMaximumTemperatureChange());
            System.out.printf("Core simulation time: %.3f ms%n",
                    elapsedMilliseconds);
            System.out.printf("Images written with prefix: %s%n", outputPrefix);

            if (!converged) {
                System.out.println(
                        "Warning: maximum timestep limit reached before convergence.");
            }

        } catch (NumberFormatException exception) {
            System.err.println("Invalid numeric argument: " + exception.getMessage());
            printUsage();
            System.exit(1);
        } catch (IllegalArgumentException exception) {
            System.err.println("Input error: " + exception.getMessage());
            printUsage();
            System.exit(1);
        } catch (Exception exception) {
            System.err.println("Simulation failed: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(1);
        }
    }

    @Override
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
    protected FireMap.StepResult compute() {
     
        if(end - start <= CUTOFF) {
            return map.updateRegion(mode, start, end,1,map.getColumns() -1);
            
        }

        int mid = (start + end)/2;

        FirelineParallel ThreadA = new FirelineParallel(map,mode, start, mid);
        FirelineParallel ThreadB = new FirelineParallel(map,mode, mid,end);

        ThreadA.fork();

        FireMap.StepResult resultB = ThreadB.compute();
        FireMap.StepResult resultA = ThreadA.join();
        
        return FireMap.StepResult.combine(resultB,resultA);
        

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
                "Usage: java FirelineParallel <rows> <columns> <seed> "
                + "<diffusion|wildfire> <output-prefix> "
                + "[max-steps] [tolerance] [mixed|grass] "
                + "[ignition-top-row ignition-left-column patch-size]");
        System.err.println("Examples:");
        System.err.println(
                "  java FirelineParallel 300 300 42 wildfire "
                + "output/fireline");
        System.err.println(
                "  java FirelineParallel 2000 2000 17 wildfire "
                + "output/benchmark 50000 0.05 grass 20 20 9");
    }

    

}
