package org.stagemonitor.ehcache;

import static org.stagemonitor.core.metrics.metrics2.MetricName.name;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.RatioGauge;
import net.sf.ehcache.Ehcache;
import org.stagemonitor.core.metrics.metrics2.Metric2Set;
import org.stagemonitor.core.metrics.metrics2.MetricName;

/**
 * An instrumented {@link net.sf.ehcache.Ehcache} instance.
 */
public class EhCacheMetricSet implements Metric2Set {

	private final String cacheName;
	private final Ehcache cache;
	private final StagemonitorCacheEventListener cacheEventListener;

	public EhCacheMetricSet(String cacheName, Ehcache cache, StagemonitorCacheEventListener cacheEventListener) {
		this.cacheName = cacheName;
		this.cache = cache;
		this.cacheEventListener = cacheEventListener;
	}

	@Override
	public Map<MetricName, Metric> getMetrics() {
		final Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();

		metrics.put(name("cache_hits").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().cacheHitCount();
			}
		});

		metrics.put(name("cache_in_memory_hits").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localHeapHitCount();
			}
		});

		metrics.put(name("cache_off_heap_hits").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localOffHeapHitCount();
			}
		});

		metrics.put(name("cache_on_disk_hits").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localDiskHitCount();
			}
		});

		metrics.put(name("cache_misses").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().cacheMissCount();
			}
		});

		metrics.put(name("cache_in_memory_misses").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localHeapMissCount();
			}
		});

		metrics.put(name("cache_off_heap_misses").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localOffHeapMissCount();
			}
		});

		metrics.put(name("cache_on_disk_misses").tag("cache_name", cacheName).tier("All").build(), new Gauge() {
			@Override
			public Long getValue() {
				return cache.getStatistics().localDiskMissCount();
			}
		});

		metrics.put(name("cache_hit_ratio").tag("cache_name", cacheName).tier("All").build(), new RatioGauge() {
			@Override
			public Ratio getRatio() {
				return cacheEventListener.getHitRatio1Min();
			}
		});

		metrics.put(name("cache_size_count").tag("cache_name", cacheName).tier("All").build(), new Gauge<Long>() {
			@Override
			public Long getValue() {
				return cache.getStatistics().getSize();
			}
		});

		metrics.put(name("cache_size_bytes").tag("cache_name", cacheName).tier("All").build(), new Gauge<Long>() {
			@Override
			public Long getValue() {
				return cache.getStatistics().getLocalDiskSizeInBytes() +
						cache.getStatistics().getLocalHeapSizeInBytes() +
						cache.getStatistics().getLocalOffHeapSizeInBytes();
			}
		});

		return metrics;
	}

}
