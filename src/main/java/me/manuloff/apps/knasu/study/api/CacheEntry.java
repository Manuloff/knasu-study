package me.manuloff.apps.knasu.study.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Manuloff
 * @since 01:02 11.12.2024
 */
@RequiredArgsConstructor
class CacheEntry {

	@Getter
	private final Object cachedResponse;
	private final long executedTime;

	public boolean isExpired(long cacheLifeTime) {
		return System.currentTimeMillis() - this.executedTime > cacheLifeTime;
	}
}
