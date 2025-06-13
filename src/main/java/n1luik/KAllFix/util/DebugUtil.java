package n1luik.KAllFix.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtil {
    private static final Logger LOGGER_POS = LoggerFactory.getLogger("[debug-pos]");
    public static void debugPos(String text) {
        LOGGER_POS.debug(text, new Exception());
    }
}
