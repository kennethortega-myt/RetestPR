package pe.gob.onpe.consultaopbackend.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String USER_TOKENS_PREFIX = "consulta-op:user:tokens:";
    private static final String TOKEN_PREFIX = "consulta-op:token:";
    private static final String TOKEN_BLACKLIST_PREFIX = "consulta-op:blacklist:";

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlacklist(String token, long expirationInSeconds) {

        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue()
                .set(blacklistKey, "1", expirationInSeconds, TimeUnit.SECONDS);
    }

    public Boolean isBlacklisted(String token) {

        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(blacklistKey);
    }

    public void saveUserToken(String user, String token, long expirationInSeconds) {

        String userKey = USER_TOKENS_PREFIX + user;
        String tokenKey = TOKEN_PREFIX + token;

        redisTemplate.opsForValue()
                .set(tokenKey, user, expirationInSeconds, TimeUnit.SECONDS);

        redisTemplate.opsForSet().add(userKey, token);
    }

    public void blacklistAllUserTokens(String userId) {

        String userKey = USER_TOKENS_PREFIX + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(userKey);

        for (String token : tokens) {

            String tokenKey = TOKEN_PREFIX + token;

            long remainingTime =
                    redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);

            if (remainingTime > 0) {
                addToBlacklist(token, remainingTime);
            }

            redisTemplate.delete(tokenKey);
        }

        redisTemplate.delete(userKey);
    }

}
