import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Bees algorithm, the Stigen way
 */

public class BA {
    private final JSSP jssp;
    private final GUI gui;
    private final Job[] jobs;
    private final int jobCount, machineCount, total, bestPossibleMakespan;
    private final BA.Vertex root;
    private final ArrayList<BA.Vertex> vertices = new ArrayList<>();
    private final Comparator<BeeSolution> makespanComparator;

    BA(Job[] jobs, int machineCount, int jobCount, JSSP jssp, GUI gui, int bestPossibleMakespan){
        this.jobs = jobs;
        this.machineCount = machineCount;
        this.jobCount = jobCount;
        this.jssp = jssp;
        this.gui = gui;
        this.bestPossibleMakespan = bestPossibleMakespan;
        this.total = machineCount * jobCount;
        this.makespanComparator = new makespanComparator();

        root = new BA.Vertex(-1, -1, -1);
        vertices.add(root);
        root.edges = new BA.Vertex[jobCount];
        //root.pheromones = new double[jobCount];
        for (int i = 0; i < jobCount; i ++) {
            final int machineNumber = jobs[i].requirements[0][0];
            final int timeRequired = jobs[i].requirements[0][1];
            final int jobNumber = jobs[i].jobNumber;
            final BA.Vertex neighbour = new BA.Vertex(machineNumber, jobNumber, timeRequired);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
            //root.pheromones[i] = tMax;
        }
    }

    Solution solve(int iterations, int beeCount) {

        //Initial population
        ArrayList<BeeSolution> flowerPatches = new ArrayList<>();
        for(int i=0; i<beeCount;i++){
            flowerPatches.add(findSolution(null,0));
        }
        flowerPatches.sort(makespanComparator);

        for (BeeSolution beeSolution: flowerPatches){
            findSolution(beeSolution,3);
        }

        //iterations
        for(int i=0; i<iterations;i++){

            double bestSites =  0.4 * flowerPatches.size();  //number of best sites are 40% of the population
            double eliteSites =  0.1 * flowerPatches.size();    //number of best sites are 10% of the population



            final double percent = (double) bestPossibleMakespan / flowerPatches.get(0).solution.getMakespan();
            if (percent >= 0.9) {
                return flowerPatches.get(0).solution;

            }
            gui.setBestSolution(flowerPatches.get(0).solution.getMakespan(), percent);
            gui.addIteration((double) bestPossibleMakespan / flowerPatches.get(0).solution.getMakespan());

        }

        for(Integer index: flowerPatches.get(0).path){
            System.out.println(index);
        }
        return flowerPatches.get(0).solution;
    }

        private BeeSolution findSolution(BeeSolution beeSolution, int neighbourhood) {

        final int[] visited = new int[jobCount];
        final int[] jobTime = new int[jobCount];
        final int[] machineTime = new int[machineCount];
        final int[][][] path = new int[machineCount][jobCount][2];

        int makespan = 0;

        BA.Vertex current = root;
        final ArrayList<Integer> vertexPath = new ArrayList<>();

        //if we are performing neighbourhood search, do this first
        if(beeSolution != null){
            for(int k = 0; k<beeSolution.path.size() - neighbourhood;k++){
                vertexPath.add(beeSolution.path.get(k));
                current = current.edges[beeSolution.path.get(k)];
                visited[current.jobNumber] ++;

                final int machineNumber = current.machineNumber;
                final int jobNumber = current.jobNumber;
                final int timeRequired = current.timeRequired;

                // Start time
                final int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
                path[machineNumber][jobNumber][0] = startTime;
                // Time required
                path[machineNumber][jobNumber][1] = timeRequired;
                // Updating variables
                final int time = startTime + timeRequired;
                jobTime[jobNumber] = time;
                machineTime[machineNumber] = time;
                if (time > makespan) {
                    makespan = time;
                }
            }
        }

        while (vertexPath.size() != total) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);

            //Fixing random exception
            if (index == -1) {
                return findSolution(null,0);
            }

            vertexPath.add(index);
            current = current.edges[index];
            visited[current.jobNumber] ++;

            final int machineNumber = current.machineNumber;
            final int jobNumber = current.jobNumber;
            final int timeRequired = current.timeRequired;

            // Start time
            final int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
            path[machineNumber][jobNumber][0] = startTime;
            // Time required
            path[machineNumber][jobNumber][1] = timeRequired;
            // Updating variables
            final int time = startTime + timeRequired;
            jobTime[jobNumber] = time;
            machineTime[machineNumber] = time;
            if (time > makespan) {
                makespan = time;
            }


            // New Vertex
            if (current.edges == null) {

                // Adding next option
                final ArrayList<BA.Vertex> choices = new ArrayList<>();
                for (int i = 0; i < jobCount; i ++) {
                    if (visited[i] < machineCount) {
                        final int neighbourMachineNumber = jobs[i].requirements[visited[i]][0];
                        final int neighbourTimeRequired = jobs[i].requirements[visited[i]][1];
                        final BA.Vertex neighbour = new BA.Vertex(neighbourMachineNumber, jobs[i].jobNumber, neighbourTimeRequired);
                        choices.add(neighbour);
                        addVertex(neighbour);
                    }
                }
                current.edges = new BA.Vertex[choices.size()];
                //current.pheromones = new double[current.edges.length];
                choices.toArray(current.edges);
                //Arrays.fill(current.pheromones, tMax);
            }
        }

        return new BeeSolution(new Solution(path), vertexPath, makespan);
    }

    private synchronized int selectPath(BA.Vertex current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1.0, b = 1.0;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = /*Math.pow(current.pheromones[i], a) */ Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

        if (denominator == 0.0) {
            Random random = new Random();
            return random.nextInt(current.edges.length);
        }

        double cumulativeProbability = 0;
        double threshold = Math.random();
        for (int i = 0; i < current.edges.length; i ++) {
            cumulativeProbability += probability[i] / denominator;
            if (threshold <= cumulativeProbability) {
                return i;
            }
        }
        return -1;
    }

    private synchronized void addVertex(BA.Vertex vertex) {
        vertices.add(vertex);
    }

    private synchronized double heuristic(BA.Vertex vertex, int[] jobTime, int[] machineTime, int makespan) {
        final int startTime = Math.max(jobTime[vertex.jobNumber], machineTime[vertex.machineNumber]);
//        heuristic =  1.0 / Math.max(startTime + vertex.timeRequired, makespan);

        double heuristic = makespan - (startTime + vertex.timeRequired);
        if (heuristic < 0.0) {
            return 0;
        }

        return heuristic;
    }

    class Vertex {
        final int machineNumber, jobNumber, timeRequired;
        BA.Vertex[] edges;
        //double[] pheromones;

        private Vertex(int machineNumber, int jobNumber, int timeRequired) {
            this.machineNumber = machineNumber;
            this.jobNumber = jobNumber;
            this.timeRequired = timeRequired;
        }
    }

    class BeeSolution {
        final Solution solution;
        final ArrayList<Integer> path;
        final int makespan;
        public int neighbourhood;

        private BeeSolution(Solution solution, ArrayList<Integer> path, int makespan) {
            this.solution = solution;
            this.path = path;
            this.makespan = makespan;
        }
    }

    class makespanComparator implements Comparator<BeeSolution>
    {
        @Override
        public int compare(BeeSolution x, BeeSolution y)
        {
            if (x.makespan > y.makespan)
            {
                return 1;
            }
            if (x.makespan < y.makespan)
            {
                return -1;
            }
            return 0;
        }
    }
}
