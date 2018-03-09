import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Segment {

//    final Pixel root;
//    final HashMap<Pixel, ArrayList<PixelEdge>> pixelEdgeMap = new HashMap<>();
    final HashSet<Pixel> pixels = new HashSet<>();
//    final ArrayList<PixelEdge> edges = new ArrayList<>();

    // Segment color variables
    private int alphaTotal, redTotal, greenTotal, blueTotal, pixelCount;

    Segment(Pixel root) {
        pixels.add(root);
        alphaTotal = root.color.getAlpha();
        redTotal = root.color.getRed();
        greenTotal = root.color.getGreen();
        blueTotal = root.color.getBlue();
        pixelCount = 1;
    }

    Segment() { }

//    boolean contains(Pixel pixel) {
//        return pixelEdgeMap.containsKey(pixel);
//    }

//    public void add(PixelEdge pixelEdge, Pixel oldPixel, Pixel newPixel) {
////        ArrayList<PixelEdge> currentPixelEdges = pixelEdgeMap.computeIfAbsent(oldPixel, k -> new ArrayList());
////        currentPixelEdges.add(pixelEdge);
////        pixelEdgeMap.put(newPixel, new ArrayList<>());
//        pixels.add(newPixel);
////        edges.add(pixelEdge);
//
//        // Updating segment color
//        alphaTotal += pixelEdge.pixelB.color.getAlpha();
//        redTotal += pixelEdge.pixelB.color.getRed();
//        greenTotal += pixelEdge.pixelB.color.getGreen();
//        blueTotal += pixelEdge.pixelB.color.getBlue();
//        pixelCount ++;
//    }

    void add(Pixel pixel) {
        pixels.add(pixel);
        alphaTotal += pixel.color.getAlpha();
        redTotal += pixel.color.getRed();
        greenTotal += pixel.color.getGreen();
        blueTotal += pixel.color.getBlue();
        pixelCount ++;
    }


    int getArgb() {
        return new Color(redTotal / pixelCount, greenTotal / pixelCount, blueTotal / pixelCount, alphaTotal / pixelCount).getRGB();
    }

    Color getColor() {
        return new Color(redTotal / pixelCount, greenTotal / pixelCount, blueTotal / pixelCount, alphaTotal / pixelCount);
    }

    boolean containsAllNeighbours(Pixel pixel) {

        for (PixelEdge edge : pixel.edgeList) {
            if (!pixels.contains(edge.pixelB)) {
                return false;
            }
        }

        return true;
    }

}