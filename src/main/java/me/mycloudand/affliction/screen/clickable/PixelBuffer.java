package me.mycloudand.affliction.screen.clickable;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import me.mycloudand.affliction.model.Pixel;
import me.mycloudand.affliction.screen.CaptureScreenEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Tracks interesting pixels across scans.
 * This includes all interesting pixels, and a time-indexed series.
 */
@Singleton
public class PixelBuffer {
    private static final Logger log = LoggerFactory.getLogger(PixelBuffer.class);

    private Set<Pixel> globalPixels;
    private SortedMap<Long, BloomFilter<Pixel>> pixelHistory;

    @Inject
    private EventBus eventBus;
    @Inject
    private PixelMatcher pixelMatcher;
    @Inject
    private TargetClusterer clusterer;


    PixelBuffer() {
        globalPixels = Sets.newHashSet();
        pixelHistory = Maps.newTreeMap();
    }

    @Subscribe
    public void onScreenCapture(CaptureScreenEvent captureEvent) {
        Map<Pixel, Integer> pixelValues = pixelMatcher.scanPixels(captureEvent.getImage());
        Set<Pixel> pixels = pixelValues.keySet();

        // Global set of interesting pixels:
        globalPixels.addAll(pixels);

        // Bloom filter of time-indexed interesting pixels (motion detection):
        BloomFilter<Pixel> hotPixels = BloomFilter.create(POINT_FUNNEL, 2000);
        Collections2.filter(pixels, hotPixels);
        pixelHistory.put(System.currentTimeMillis(), hotPixels);

        long historyDuration = pixelHistory.lastKey() - pixelHistory.firstKey();
        int historySize = pixelHistory.size();
        log.trace("Updated buffer with {} pixels, current history: {} frames ({}ms)", pixels.size(), historySize, historyDuration);

        if (historyDuration > 5000) {
            Set<Pixel> movingPixels = findMovingPixels();
            if (!movingPixels.isEmpty()) {
                Set<Pixel> clickTargets = clusterer.getClickTargets(movingPixels);
                eventBus.post(new ClickableTargetsEvent(clickTargets));
            }
            clear();
        }
    }

    private Set<Pixel> findMovingPixels() {
        log.trace("Running motion filtering with {} points...", globalPixels.size());
        Set<Pixel> retainedPoints = new HashSet<Pixel>();

        long motionDetectStart = System.currentTimeMillis();
        for (final Pixel point : getGlobalPixels()) {
            // Being in the global set, but missing from a particular snapshot, mans we're moving.
            BloomFilter<Pixel> missingFromFilter = Iterables.find(pixelHistory.values(), new Predicate<BloomFilter<Pixel>>() {
                @Override
                public boolean apply(BloomFilter<Pixel> input) {
                    return !input.mightContain(point);
                }
            });

            if (missingFromFilter != null) {
                retainedPoints.add(point);
            }
        }
        long motionDetectEnd = System.currentTimeMillis();
        log.trace("Motion filtering retained {} points in {}ms.", retainedPoints.size(), motionDetectEnd - motionDetectStart);
        return retainedPoints;
    }

    public void clear() {
        globalPixels.clear();
        pixelHistory.clear();
    }

    public Set<Pixel> getGlobalPixels() {
        return Sets.newHashSet(globalPixels);
    }

    private static final Funnel<Pixel> POINT_FUNNEL = new Funnel<Pixel>() {
        @Override
        public void funnel(Pixel from, PrimitiveSink into) {
            into.putInt(from.getX());
            into.putInt(from.getY());
        }
    };
}
