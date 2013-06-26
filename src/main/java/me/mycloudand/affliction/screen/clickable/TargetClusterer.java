package me.mycloudand.affliction.screen.clickable;

import com.google.common.collect.Sets;
import me.mycloudand.affliction.model.Pixel;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class TargetClusterer {
    private static final Logger log = LoggerFactory.getLogger(TargetClusterer.class);

    public Set<Pixel> getClickTargets(Set<Pixel> pixels) {
        long clusterStart = System.currentTimeMillis();
        Dataset data = getDataset(pixels);
        int numClusters = getNumClusters(data);
        log.trace("Breaking into {} clusters.", numClusters);

        Clusterer clusterer = new KMeans(numClusters, 400);
        Dataset[] clusteredData = clusterer.cluster(data);

        Set<Pixel> targets = Sets.newHashSet();
        for (Dataset dataSet : clusteredData) {
            if (dataSet.size() > 30) {
                Pixel clusterCenter = toPixel(dataSet);
                targets.add(clusterCenter);
            }
        }
        log.trace("Targets discovered: {}", targets.size());
        long clusterTime = System.currentTimeMillis() - clusterStart;
        log.info("Discovered {} targets in {}ms.", targets.size(), clusterTime);
        return targets;
    }


    private Dataset getDataset(Set<Pixel> pixels) {
        log.trace("Building dataset.");
        Dataset data = new DefaultDataset();
        for (Pixel pixel : pixels) {
            double[] attributes = {pixel.getX(), pixel.getY()};
            Instance pixelInstance = new DenseInstance(attributes);
            data.add(pixelInstance);
        }
        log.trace("Retained {} pixels.", data.size());
        return data;
    }

    private int getNumClusters(Dataset data) {
        int numObjects = data.size() / 200;
        return Math.max(numObjects * 2, 1);
    }

    private Pixel toPixel(Dataset dataSet) {
        Instance dataSetAverage = DatasetTools.average(dataSet);
        int clusterX = dataSetAverage.get(0).intValue();
        int clusterY = dataSetAverage.get(1).intValue();
        return new Pixel(clusterX, clusterY);
    }
}
