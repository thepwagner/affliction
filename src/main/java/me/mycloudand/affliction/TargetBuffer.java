package me.mycloudand.affliction;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.math.IntMath;
import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.screen.clickable.ClickableTargetsEvent;
import me.mycloudand.affliction.screen.clickable.PixelBuffer;
import me.mycloudand.affliction.screen.clicker.ScreenClicker;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.RoundingMode;
import java.util.Set;

@Singleton
public class TargetBuffer {
    private static final Logger log = LoggerFactory.getLogger(TargetBuffer.class);

    @Inject
    private PixelBuffer pixelBuffer;
    @Inject
    private ScreenClicker clicker;

    private Set<Pixel> targets;

    public TargetBuffer() {
        targets = Sets.newCopyOnWriteArraySet();
    }

    @Subscribe
    public void onTarget(ClickableTargetsEvent targetsEvent) {
        targets.addAll(targetsEvent.getTargets());
        log.debug("There are {} total targets.", targets.size());

        clicker.onClickableTargetsEvent(targetsEvent);
    }

    public void update() {
        Dataset data = getDataset();

        int numClusters = getNumClusters(data);
        log.trace("Breaking into {} clusters.", numClusters);

        Clusterer clusterer = new KMeans(numClusters, 1000);
        Dataset[] clusteredData = clusterer.cluster(data);
        log.trace("Clustering completed: {} clusters.", clusteredData.length);

        for (Dataset dataSet : clusteredData) {
            if (dataSet.size() > 30) {
                Pixel clusterCenter = toPixel(dataSet);
                targets.add(clusterCenter);
            }
        }
        log.trace("Targets updated: {}", targets.size());
    }

    private Dataset getDataset() {
        log.trace("Building dataset.");
        Dataset data = new DefaultDataset();
        for (Pixel pixel : pixelBuffer.getGlobalPixels()) {
            double[] attributes = {pixel.getX(), pixel.getY()};
            Instance pixelInstance = new DenseInstance(attributes);
            data.add(pixelInstance);
        }
        log.trace("Retained {} pixels.", data.size());
        return data;
    }

    private Pixel toPixel(Dataset dataSet) {
        Instance dataSetAverage = DatasetTools.average(dataSet);
        int clusterX = dataSetAverage.get(0).intValue();
        int clusterY = dataSetAverage.get(1).intValue();
        return new Pixel(clusterX, clusterY);
    }

    private int getNumClusters(Dataset data) {
        int numObjects = data.size() / 500;
        return Math.max(numObjects * 2, 1);
    }

    public Set<Pixel> getTargets() {
        return targets;
    }

    public void removeTarget(final Pixel pixel) {
        log.trace("Removing pixel: {},{}", pixel.getX(), pixel.getY());
        synchronized (targets) {
            for (Pixel input : targets) {
                int dist = IntMath.sqrt(
                        IntMath.pow(input.getX() - pixel.getX(), 2) +
                                IntMath.pow(input.getY() - pixel.getY(), 2),
                        RoundingMode.HALF_EVEN
                );
                if (dist < 20) {
                    try {
                        targets.remove(input);
                    } catch (Exception e) {
                        log.warn(null, e);
                    }
                }
            }

        }
    }
}
