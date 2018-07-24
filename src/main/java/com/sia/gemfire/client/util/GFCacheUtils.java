package com.sia.gemfire.client.util;

import com.sia.gemfire.client.impl.GFCacheProvider;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class GFCacheUtils {
    public static Region<Object, Object> getRegion(final String regionName){
        Region<Object, Object> region = GFCacheProvider.getCache().getRegion(regionName);
        if(region == null) {
            region = GFCacheProvider.getCache()
                    .<Object, Object>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                    .create(regionName);
        }
        return region;
    }
}
