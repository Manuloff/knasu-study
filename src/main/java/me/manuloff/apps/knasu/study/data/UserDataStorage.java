package me.manuloff.apps.knasu.study.data;

import lombok.NonNull;
import me.manuloff.apps.knasu.study.KnasuStudy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Manuloff
 * @since 21:08 09.12.2024
 */
public final class UserDataStorage {

	private static final long CACHE_LIFE_TIME = TimeUnit.HOURS.toMillis(4);

	private final Map<Long, UserData> storage = new ConcurrentHashMap<>();

	public UserDataStorage() {
		KnasuStudy.getInstance().getExecutorService().scheduleAtFixedRate(() -> {
			for (UserData data : this.storage.values()) {
				if (System.currentTimeMillis() - data.getLastAccessTime() > CACHE_LIFE_TIME) {
					this.storage.remove(data.getUserId());
				}
			}

		}, 30, 30, TimeUnit.SECONDS);
	}

	public void set(@NonNull UserData data) {
		this.storage.put(data.getUserId(), data);
	}

	public boolean has(long userId) {
		return this.storage.containsKey(userId);
	}

	public UserData get(long userId) {
		UserData data = this.storage.get(userId);
		if (data == null) {
			throw new IllegalStateException("Data for " + userId + " now loaded");
		}

		return data;
	}
}
