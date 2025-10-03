package org.aop.aspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aop.annotations.Cached;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Aspect
@Component
public class CachedAspect {
    private final Map<String, Map<Object, CacheEntry>> cacheStore= new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupScheduler= Executors.newScheduledThreadPool(1);
    public CachedAspect(){
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 1,1, TimeUnit.MINUTES);
    }

    @Around("@annotation(cached)")
    public Object cacheMethodResult(ProceedingJoinPoint joinPoint, Cached cached) throws Throwable{
      String cacheName=getCacheName(joinPoint, cached);
      Object cacheKey=generateCacheKey(joinPoint);
      CacheEntry cachedResult= getFromCache(cacheName,cacheKey);
      if (cachedResult!=null){
          log.debug("Cache hit for {}.{}", cacheName,cacheKey);
          return cachedResult.getData();
      }
      log.debug("Cache miss for {}.{}, executing method", cacheName, cacheKey);
      Object result=joinPoint.proceed();
      if(result!=null){
          putToCache(cacheName,cacheKey,result,cached.ttl());
      }
      return result;
    }
    private String getCacheName(ProceedingJoinPoint joinPoint, Cached cached) {
    if(!cached.cacheName().isEmpty()){return cached.cacheName();}
    return joinPoint.getTarget().getClass().getSimpleName()+"."+joinPoint.getSignature().getName();
    } private Object generateCacheKey(ProceedingJoinPoint joinPoint) {
        Object[] args=joinPoint.getArgs();
        if(args.length == 1){
            Object arg=args[0];
            if(arg instanceof Long || arg instanceof Integer || arg instanceof String){return arg;}
            return arg.hashCode();
        }
        return Arrays.hashCode(args);
    }
    private CacheEntry getFromCache(String cacheName, Object key) {
   Map<Object, CacheEntry> cache=cacheStore.get(cacheName);
   if (cache==null){
       return null;
   }CacheEntry entry=cache.get(key);
   if(entry!=null && !entry.isExpired()){
       return entry;
   }
   if ((entry!=null)){
       cache.remove(key);
   }return null;
    }
    private void putToCache(String cacheName, Object key, Object data, long ttl) {
    cacheStore.computeIfAbsent(cacheName, k-> new ConcurrentHashMap<>())
            .put(key, new CacheEntry(data, System.currentTimeMillis()+ttl));
    }
    public void cleanupExpiredEntries(){
        long now = System.currentTimeMillis();
        cacheStore.forEach((cacheName, cache)->{
            cache.entrySet().removeIf(entry->entry.getValue().isExpired(now));
        });
    }


    @RequiredArgsConstructor
private static class CacheEntry{
        @Getter
    private final Object data;
    private final long expirationTime;
        public boolean isExpired(){
            return isExpired(System.currentTimeMillis());
        }
        public boolean isExpired(long currentTime) {
            return currentTime > expirationTime;
        }
}}