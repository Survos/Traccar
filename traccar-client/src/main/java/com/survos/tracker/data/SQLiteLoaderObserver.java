
package com.survos.tracker.data;

/**
 * SQLite Loader entry for keeping a track of loaders and the tables with which
 * they are associated for notification
 */
public class SQLiteLoaderObserver {
    public SQLiteLoader loader;
    public String table;

    /**
     * @param loader The {@link SQLiteLoader} to add as the entry
     * @param table The Table name
     */
    public SQLiteLoaderObserver(final SQLiteLoader loader, final String table) {
        this.loader = loader;
        this.table = table;
    }

}
