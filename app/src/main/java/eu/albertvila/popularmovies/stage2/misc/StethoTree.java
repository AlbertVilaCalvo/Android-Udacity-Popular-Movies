package eu.albertvila.popularmovies.stage2.misc;

import android.util.Log;

import com.facebook.stetho.inspector.console.CLog;
import com.facebook.stetho.inspector.console.ConsolePeerManager;
import com.facebook.stetho.inspector.protocol.module.Console;

import timber.log.Timber;

/**
 * Timber tree implementation which forwards logs to the Chrome Dev console.
 * Plant it using {@link Timber#plant(Timber.Tree)}
 * <pre>
 *   {@code
 *   Timber.plant(new StethoTree())
 *   }
 * </pre>
 */
// https://github.com/facebook/stetho/blob/master/stetho-timber/src/main/java/com/facebook/stetho/timber/StethoTree.java
public class StethoTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        ConsolePeerManager peerManager = ConsolePeerManager.getInstanceOrNull();
        if (peerManager == null) {
            return;
        }

        Console.MessageLevel logLevel;

        switch (priority) {
            case Log.VERBOSE:
            case Log.DEBUG:
                logLevel = Console.MessageLevel.DEBUG;
                break;
            case Log.INFO:
                logLevel = Console.MessageLevel.LOG;
                break;
            case Log.WARN:
                logLevel = Console.MessageLevel.WARNING;
                break;
            case Log.ERROR:
            case Log.ASSERT:
                logLevel = Console.MessageLevel.ERROR;
                break;
            default:
                logLevel = Console.MessageLevel.LOG;
        }

        CLog.writeToConsole(
                logLevel,
                Console.MessageSource.OTHER,
                message
        );
    }
}