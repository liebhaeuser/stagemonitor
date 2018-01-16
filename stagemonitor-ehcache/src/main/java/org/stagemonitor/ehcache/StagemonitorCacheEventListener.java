package org.stagemonitor.ehcache;

import com.codahale.metrics.Meter;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.statistics.CacheUsageListener;

import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.RatioGauge.Ratio;
import static org.stagemonitor.core.metrics.metrics2.MetricName.name;

public class StagemonitorCacheEventListener implements CacheEventListener {

	private final Metric2Registry registry;
	private final boolean timeGet;
	private String cacheName;
	private final MetricName getMetricName;
	private final MetricName deleteEvictionMetricName;
	private final MetricName deleteExpireMetricName;
	private final MetricName deleteRemovedMetricName;


	public StagemonitorCacheEventListener(String cacheName, Metric2Registry registry, boolean timeGet) {
		this.cacheName = cacheName;
		this.registry = registry;
		this.timeGet = timeGet;
		getMetricName = name("cache_get").tag("cache_name", cacheName).tier("All").build();
		deleteEvictionMetricName = name("cache_delete").tag("cache_name", cacheName).tag("reason", "eviction").tier("All").build();
		deleteExpireMetricName = name("cache_delete").tag("cache_name", cacheName).tag("reason", "expire").tier("All").build();
		deleteRemovedMetricName = name("cache_delete").tag("cache_name", cacheName).tag("reason", "remove").tier("All").build();
	}

	public Ratio getHitRatio1Min() {
		final Meter hitRate = registry.getMeters().get(name("cache_hits").tag("cache_name", cacheName).tier("All").build());
		final Meter missRate = registry.getMeters().get(name("cache_misses").tag("cache_name", cacheName).tier("All").build());
		final double oneMinuteHitRate = hitRate.getOneMinuteRate();
		return Ratio.of(oneMinuteHitRate * 100.0, oneMinuteHitRate + missRate.getOneMinuteRate());
	}

	@Deprecated
	public void notifyGetTimeNanos(long nanos) {
		if (timeGet) {
			registry.timer(getMetricName).update(nanos, TimeUnit.NANOSECONDS);
		} else {
			registry.meter(getMetricName).mark();
		}
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
		registry.meter(deleteRemovedMetricName).mark();
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		registry.meter(deleteExpireMetricName).mark();
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		registry.meter(deleteEvictionMetricName).mark();
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return null;
	}
}
