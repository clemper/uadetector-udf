/**
 * 
 */
package com.sovrn.udf.uadetector;

import java.util.List;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Return a consise description of the user agent and operating system as
 * determined by uadetector.
 * 
 * @author clemper
 */
public class UadetectorUDF extends UDF {
    private static final Joiner VERSION_JOINER = Joiner.on(',').skipNulls();

    private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

    private final Cache<String, Text> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Text evaluate(final Text input) {
        if (input != null) {
            return parse(input.toString());
        }
        return null;
    }

    protected Text parse(final String userAgentString) {
        Text result = cache.getIfPresent(userAgentString);
        if (result == null) {
            final ReadableUserAgent userAgent = parser.parse(userAgentString);
            final StringBuilder sb = new StringBuilder();
            sb.append(userAgent.getFamily());
            sb.append(' ');
            sb.append(shortVersion(userAgent));
            sb.append(" on ");
            sb.append(userAgent.getOperatingSystem().getName());
            result = new Text(sb.toString());
            cache.put(userAgentString, result);
        }
        return result;
    }

    protected String shortVersion(final ReadableUserAgent userAgent) {
        final VersionNumber versionNumber = userAgent.getVersionNumber();
        if (versionNumber != null) {
            final List<String> groups = versionNumber.getGroups();
            if (groups != null && groups.size() > 0) {
                return VERSION_JOINER.join(groups.subList(0, Math.min(2, groups.size())));
            }
        }
        return "?";
    }

}
