package com.sia.gemfire.client.impl;

import com.sia.gemfire.client.config.GFCacheConfig;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import java.util.Properties;

public class GFCacheProvider {
    private static volatile ClientCache cache = null;

    /**
     * Get instance of GemFire client cache.
     * @param props configuration for client cache.
     * @return instance of GF client cache.
     */
    public static ClientCache getCache(Properties props){
        if(cache == null){
            synchronized (GFCacheProvider.class){
                if(cache == null){
                    System.out.println("Test creating cache..!");
                    cache = new ClientCacheFactory()
                            .addPoolLocator(GFCacheConfig.LOCATOR_HOST, GFCacheConfig.LOCATOR_PORT)
                            .set("log-level", GFCacheConfig.LOG_LEVEL)
                            .set("log-file", GFCacheConfig.LOG_FILE_PATH)
                            .create();
                }
            }
        }

        return cache;
    }

    public static ClientCache getCache(){
        return getCache(null);
    }

    /**
     * API to close GF client cache
     */
    public static void closeCache(){
        if(cache != null && !cache.isClosed()){
            cache.close();
        }
    }
}
