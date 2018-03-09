import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Solution {

    public double score;
    public double edgeValue;
    public double overallDeviation;
    public int dominationRank;

    final Segment[] segments;
    final ArrayList<PixelEdge>[] pixelEdges;

    public Solution(ArrayList<PixelEdge>[] pixelEdges, Segment[] segments){
        this.segments = segments;
        this.pixelEdges = pixelEdges;
    }

    Solution(Solution parent1, Solution parent2, int splitPoint, Pixel[][] pixels) {
        pixelEdges = new ArrayList[parent1.pixelEdges.length];

        // Filling out the new pixelEdges - Genotype
        System.arraycopy(parent1.pixelEdges, 0, pixelEdges, 0, splitPoint);
        System.arraycopy(parent2.pixelEdges, splitPoint, pixelEdges, splitPoint, pixelEdges.length - splitPoint);

        // The hard part, defining segments - Phenotype

        // Removing duplicate edges from new solution
        HashSet<PixelEdge> edges = new HashSet<>();
        for (int i = 0; i < pixels[0].length; i ++) {
            edges.addAll(parent1.pixelEdges[splitPoint - 1 - i]);
        }
        for (int i = 0; i < pixels[0].length; i ++) {
            final ArrayList<PixelEdge> removeList = new ArrayList<>();
            for (PixelEdge pixelEdge : parent2.pixelEdges[splitPoint + i]) {
                // If duplicate edge
                if (edges.contains(pixelEdge)) {
                    removeList.add(pixelEdge);
                }
            }
            parent2.pixelEdges[splitPoint + i].removeAll(removeList);
        }

        // Building Segments from PixelEdges
        // Wrapper classes
//        final class Edge {
//            final Pixel current, neighbour;
//            final PixelEdge pixelEdge;
//
//            Edge(Pixel current, Pixel neighbour, PixelEdge pixelEdge) {
//                this.current = current;
//                this.neighbour = neighbour;
//                this.pixelEdge = pixelEdge;
//            }
//        }
        final class Phenotype {
            final ArrayList<Pixel> pixels = new ArrayList<>();
//            Segment segment = new Segment();
            final ArrayList<Phenotype> connections = new ArrayList<>();
        }


        final HashMap<Pixel, Phenotype> phenotypeMap = new HashMap<>();
        final ArrayList<Segment> segments = new ArrayList<>();
        final ArrayList<Phenotype> phenotypes = new ArrayList<>();

        for (ArrayList<PixelEdge> pixelEdges : pixelEdges) {
            for (PixelEdge pixelEdge : pixelEdges) {

                final Phenotype phenotypeA = phenotypeMap.get(pixelEdge.pixelA);
                final Phenotype phenotypeB = phenotypeMap.get(pixelEdge.pixelB);

                //If both have different Phenotype we need to link them together
                if (phenotypeA != null && phenotypeB != null) {
                    phenotypeA.connections.add(phenotypeB);
                    phenotypeB.connections.add(phenotypeA);
//                    final Segment segment = new Segment();
//                    phenotypeA.segment = segment;
//                    phenotypeB.segment = segment;
                    continue;
                }

                if (phenotypeA != null) {
//                    phenotypeA.edges.add(new Edge(pixelEdge.pixelA, pixelEdge.pixelB, pixelEdge));
                    phenotypeA.pixels.add(pixelEdge.pixelB);
                    phenotypeMap.put(pixelEdge.pixelB, phenotypeA);
                    continue;
                }

                if (phenotypeB != null) {
//                    phenotypeB.edges.add(new Edge(pixelEdge.pixelB, pixelEdge.pixelA, pixelEdge));
                    phenotypeB.pixels.add(pixelEdge.pixelA);
                    phenotypeMap.put(pixelEdge.pixelA, phenotypeB);
                    continue;
                }

                final Phenotype phenotype = new Phenotype();
//                phenotype.edges.add(new Edge(pixelEdge.pixelA, pixelEdge.pixelB, pixelEdge));
                phenotype.pixels.add(pixelEdge.pixelA);
                phenotype.pixels.add(pixelEdge.pixelB);
//                phenotype.pixelEdges.add(pixelEdge);
                phenotypeMap.put(pixelEdge.pixelA, phenotype);
                phenotypeMap.put(pixelEdge.pixelB, phenotype);
                phenotypes.add(phenotype);
            }
        }

        // Creating the Segments from Phenotypes
        final HashSet<Phenotype> visited = new HashSet<>();
        for (Phenotype phenotype : phenotypes) {
            if (visited.contains(phenotype)) {
                continue;
            }
            visited.add(phenotype);
            final Segment segment = new Segment();
            for (Pixel pixel : phenotype.pixels) {
               segment.add(pixel);
            }
            ArrayList<Phenotype> connections = phenotype.connections;
            while (!connections.isEmpty()) {
                final Phenotype phenotype1 = connections.remove(0);
                if (visited.contains(phenotype1)) {
                    continue;
                }
                visited.add(phenotype1);
                connections.addAll(phenotype1.connections);

                for (Pixel pixel : phenotype1.pixels) {
                    segment.add(pixel);
                }
            }

            segments.add(segment);
        }
//        System.out.println("Visited: " + visited.size());
//        System.out.println("Phenotypes: " + phenotypes.size());
//        System.out.println("Segments: " + segments.size());
        this.segments = segments.toArray(new Segment[0]);

//        final HashMap<Pixel, PixelEdge> parentMap = new HashMap<>();
//        final HashMap<Pixel, ArrayList<PixelEdge>> childMap = new HashMap<>();
//        final HashSet<Pixel> reassignSet = new HashSet<>();
//        //[Height][Width]
//        final boolean[][] hasParent = new boolean[pixels.length][pixels[0].length];
//        int pixelParentCount = 0;
//
//        // Creating a map of each pixels incoming PixelEdge
//        for (int i = 0; i < splitPoint; i ++) {
//            for (PixelEdge pixelEdge : parent1.pixelEdges[i]) {
//                parentMap.put(pixelEdge.pixelB, pixelEdge);
//                childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                hasParent[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
//                pixelParentCount ++;
//            }
//        }
//
//        // Checking that no pixels has outgoing edges to the same pixel
//        for (int i = splitPoint; i < pixelEdges.length; i ++) {
//            for (PixelEdge pixelEdge : parent2.pixelEdges[i]) {
//
//                final PixelEdge oldEdge = parentMap.get(pixelEdge.pixelB);
//
//                // Parent1 also has an outgoing edge to the pixelB (Segment merge collides)
//                if (oldEdge != null) {
//
//
//                    // Edges are compared and the best edge by distance is kept
////                    if (pixelEdge.distance < oldEdge.distance) {
////                        childMap.get(oldEdge.pixelA).remove(oldEdge);
////                        parentMap.put(pixelEdge.pixelB, pixelEdge);
////                        childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                }
//                else {
//                    parentMap.put(pixelEdge.pixelB, pixelEdge);
//                    childMap.computeIfAbsent(pixelEdge.pixelA, k -> new ArrayList<>()).add(pixelEdge);
//                    hasParent[pixelEdge.pixelB.row][pixelEdge.pixelB.column] = true;
//                    pixelParentCount ++;
//                }
//            }
//        }
//
//
//        //Creating Segments from parentMap
//        segments = new Segment[pixelEdges.length - pixelParentCount];
//        int index = 0;
//        for (int i = 0; i < hasParent.length; i ++) {
//            for (int j = 0; j < hasParent[i].length; j ++) {
//                // Found a root pixel
//                if (!hasParent[i][j]) {
//                    segments[index] = new Segment(pixels[i][j]);
//                    // getOrDefault in case pixel has no children
//                    ArrayList<PixelEdge> children = new ArrayList<>(childMap.getOrDefault(pixels[i][j], new ArrayList<>()));
//                    while (!children.isEmpty()) {
//                        final PixelEdge pixelEdge = children.remove(0);
////                        segments[index].add(pixelEdge);
//                        children.addAll(childMap.getOrDefault(pixelEdge.pixelB, new ArrayList<>()));
//                    }
//                    index ++;
//                }
//            }
//        }


    }

    public void scoreSolution(double edgeValue, double overallDeviation, double edgeWeight, double overAlldeviationWeight){
        this.edgeValue = edgeValue;
        this.overallDeviation = overallDeviation;

        //Overall deviation should be maximized while edgeValue should be maximized
        //To keep similarity for the score we negate edgeValue, high edgeValue gives low score
        //and low score is good.
        this.score = (overAlldeviationWeight*overallDeviation) - (edgeWeight*edgeValue);
    }

    //check if this solution is dominated by another
    public boolean isDominatedBy(Solution solution){
        //solution is the solution to check if it is dominating this solution

        if(this.overallDeviation > solution.overallDeviation && this.edgeValue <= solution.edgeValue){
            return true;
        }else if(this.overallDeviation >= solution.overallDeviation && this.edgeValue < solution.edgeValue){
            return true;
        }

        //This solution is not dominated
        return false;
    }

}