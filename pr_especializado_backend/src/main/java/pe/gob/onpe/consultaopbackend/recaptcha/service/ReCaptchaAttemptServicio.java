package pe.gob.onpe.consultaopbackend.recaptcha.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("reCaptchaAttemptService")
public class ReCaptchaAttemptServicio {


	private LoadingCache<String, Integer> attemptsCache;

	public ReCaptchaAttemptServicio() {
		super();
		attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(4, TimeUnit.HOURS)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(final String key) {
						return 0;
					}
				});
	}

	public void reCaptchaSucceeded(final String key) {
		attemptsCache.invalidate(key);
	}

	public void reCaptchaFailed(final String key) {
		int attempts = attemptsCache.getUnchecked(key);
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public boolean isBlocked(final String key) {
		return attemptsCache.getUnchecked(key) >= 4;
	}
}
