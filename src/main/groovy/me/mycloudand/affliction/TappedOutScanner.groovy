package me.mycloudand.affliction

import com.google.common.hash.BloomFilter
import com.google.common.hash.Funnel
import com.google.common.hash.PrimitiveSink
import groovy.util.logging.Slf4j
import net.sf.javaml.clustering.Clusterer
import net.sf.javaml.clustering.KMeans
import net.sf.javaml.core.Dataset
import net.sf.javaml.core.DefaultDataset
import net.sf.javaml.core.DenseInstance
import net.sf.javaml.core.Instance
import net.sf.javaml.tools.DatasetTools
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import java.awt.*
import java.awt.image.BufferedImage

/**
 * Created with IntelliJ IDEA.
 * User: pwagner
 * Date: 15/06/13
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class TappedOutScanner {


    private static final int COLOUR_HOT_PIXEL = 0xFF0000
    private static final int COLOR_CLICK_PIXEL = 0x0000FF

    Set<Point> globalPixels = [] as Set
    Map<Long, BloomFilter<Point>> pixelHistory = [:] as TreeMap
    Set<Point> wantToClick = [] as Set
    Point lastClick

    ScreenWatcher watcher

    /**
     * Scan an image.<br/>
     * Record interesting pixels (by color value) into:
     *   - Global set of interesting pixels
     *   - Time-indexed bloom filter
     * @param image
     * @return
     */
    def scanScreen(BufferedImage image) {
        long t1 = System.nanoTime()

        // Interesting pixels (by color value)
        BloomFilter<Point> hotPixels = BloomFilter.create(new Funnel<Point>() {
            @Override
            void funnel(Point from, PrimitiveSink into) {
                into.putDouble(from.x)
                into.putDouble(from.y)
            }
        }, 2000)
        int pixelCount = 0

        def imageData = image.data

        // Shortcut: is the "detail" popup open:
        int[] detailPopup = imageData.getPixel(540, 345, (int[]) null)
        log.debug("{}, {}, {}", detailPopup)
        if (detailPopup[0] == 194 && detailPopup[1] == 194 && detailPopup[2] == 194) {
            log.trace("Dismissing in game popup")
            watcher.clickTappedOut(540, 345)
        }


        for (int x = 0; x < imageData.getWidth(); x += SCAN_RESOLUTION_X) {
            for (int y = 0; y < imageData.getHeight(); y += SCAN_RESOLUTION_Y) {
                int[] pixelData = imageData.getPixel(x, y, (int[]) null)

                boolean isHot = false
                // TODO: these colors are an enum

                // Dollar sign on buildings:
                if (pixelData[0] == 254 && pixelData[1] == 203 && pixelData[2] == 51) {
                    isHot = true
                }

                // Cash register: face
//                if (pixelData[0] == 186 && pixelData[1] == 85 && pixelData[2] == 51) {
//                    isHot = true
//                }

                // Black check on restauraunts
//                if (pixelData[0] == 51 && pixelData[1] == 51 && pixelData[2] == 51) {
//                    isHot = true
//                }

                // Thumbs up on buildings
//                if (pixelData[0] == 254 && pixelData[1] == 220 && pixelData[2] == 34) {
//                    isHot = true
//                }

                // Cash on buildings:
//                if (pixelData[0] == 68 && pixelData[1] == 135 && pixelData[2] == 34) {
//                    isHot = true
//                }

                if (isHot) {
                    image.setRGB(x, y, COLOUR_HOT_PIXEL)

                    def hotPixel = new Point(x, y)
                    hotPixels.put(hotPixel)
                    globalPixels.add(hotPixel)
                    pixelCount++
                }
            }
        }

        pixelHistory.put(System.currentTimeMillis(), hotPixels)


        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.GREEN)
        wantToClick.each { Point pixel ->
            g2d.fillOval(pixel.x - 20 as Integer, pixel.y - 20 as Integer, 40, 40)
        }

        if (lastClick != null) {
            g2d.setColor(Color.RED)
            g2d.fillOval(lastClick.x - 20 as Integer, lastClick.y - 20 as Integer, 40, 40)

        }

        long t2 = System.nanoTime()
        int scanTimer = (t2 - t1) / 1000000
        log.trace("Scanned screen in {}ms, found {} pixels", scanTimer, pixelCount)

        return image
    }

    def findHotPixels(BufferedImage image) {
        wantToClick.clear()
        lastClick = null

        log.trace("Scanning {} interesting pixels", globalPixels.size())
        Dataset data = new DefaultDataset()

        // Use the bloom filters to find pixels that aren't always the target color
        // This detects motion (since our targets bob)
        globalPixels.each { Point pixel ->
            def missingFromFilter = pixelHistory.values().find { !it.mightContain(pixel) }
            if (missingFromFilter != null) {
                double[] attributes = new double[2]
                attributes[0] = pixel.x
                attributes[1] = pixel.y
                Instance pixelInstance = new DenseInstance(attributes)
                data.add(pixelInstance)
            }
        }
        log.trace("Motion filter retained {} pixels", data.size())

        if (!data.isEmpty()) {
            boolean hasClicked = false

            int numObjects = data.size() / 500
            int numClusters = Math.max(numObjects * 2, 1)

            Clusterer clusterer = new KMeans(numClusters, 1000)
            def clusteredData = clusterer.cluster(data)

            clusteredData.each { Dataset dataSet ->
                if (dataSet.size() > 30) {
                    Instance dataSetAverage = DatasetTools.average(dataSet)

                    def clusterX = dataSetAverage.get(0) as Integer
                    def clusterY = dataSetAverage.get(1) as Integer
                    wantToClick << new Point(clusterX, clusterY)
                }
            }

            if (!wantToClick.isEmpty()) {
                lastClick = wantToClick.find { true }
                watcher.clickTappedOut(lastClick.x as Integer, lastClick.y as Integer)
            }
        }

        pixelHistory.clear()
        globalPixels.clear()
        return image
    }
}

