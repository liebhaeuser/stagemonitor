package org.stagemonitor.ehcache;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.ehcache.CacheOperationOutcomes;
import org.stagemonitor.core.instrument.StagemonitorByteBuddyTransformer;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class EhCacheTransformer extends StagemonitorByteBuddyTransformer {

	@Override
	protected ElementMatcher.Junction<TypeDescription> getTypeMatcher() {
		return named("OperationObserver").and(ElementMatchers.isInterface());
	}

	@Override
	protected ElementMatcher.Junction<MethodDescription> getExtraMethodElementMatcher() {
		return named("end")
				.and(takesArguments(CacheOperationOutcomes.GetOutcome.class));
	}

	@Advice.OnMethodExit
	public static void addDirectMonitorMethodCall() {

	}


}
