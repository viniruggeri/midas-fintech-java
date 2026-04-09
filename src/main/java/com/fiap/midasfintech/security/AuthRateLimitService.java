package com.fiap.midasfintech.security;

import com.fiap.midasfintech.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@RequiredArgsConstructor
public class AuthRateLimitService {

    private final SecurityProperties securityProperties;

    private final Map<String, Deque<Long>> attemptsByKey = new ConcurrentHashMap<>();

    public boolean tryConsume(String key) {
        int maxAttempts = securityProperties.getAuth().getRateLimit().getMaxAttempts();
        int windowSeconds = securityProperties.getAuth().getRateLimit().getWindowSeconds();

        long now = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1000L;

        Deque<Long> attempts = attemptsByKey.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        synchronized (attempts) {
            while (!attempts.isEmpty() && now - attempts.peekFirst() > windowMillis) {
                attempts.pollFirst();
            }

            if (attempts.size() >= maxAttempts) {
                return false;
            }

            attempts.addLast(now);
            return true;
        }
    }
}
