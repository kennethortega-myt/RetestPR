package pe.gob.onpe.pradminbackend.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLISTED_VALUE = "blacklisted";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlacklist(String token, long expirationInSeconds) {
        redisTemplate.opsForValue().set(token, BLACKLISTED_VALUE, expirationInSeconds,
                TimeUnit.SECONDS);
    }

    public Boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

    public void saveUserToken(String userId, String token, long expirationInSeconds) {
        String key = USER_TOKENS_PREFIX + userId;

        redisTemplate.opsForSet().add(key, token);
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS);
    }

    public void blacklistAllUserTokens(String userId) {
        String key = USER_TOKENS_PREFIX + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(key);

        for (String token : tokens) {

            long remainingTime = redisTemplate.getExpire(token, TimeUnit.SECONDS);

            if (remainingTime > 0) {
                addToBlacklist(token, remainingTime);
            }
        }
        redisTemplate.delete(key);
    }

}
