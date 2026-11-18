package com.lost.fts.security.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final Map<String, Deque<Instant>> requests = new ConcurrentHashMap<>();

    public boolean allow(String key, int limit, Duration window) {

        Instant now = Instant.now();
        Deque<Instant> timestamps = requests.computeIfAbsent(key, ignored -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(now.minus(window))) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= limit) {
                return false;
            }

            timestamps.addLast(now);
            return true;
        }
    }
}