package net.tetromi.idocs.client.util;

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.GenericRow;
import com.google.code.gwt.database.client.service.*;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.core.client.GWT;
import net.tetromi.idocs.client.data.Async;

import java.util.List;

import static net.tetromi.idocs.client.util.LOG.log;

/**
 * Unified implementation of local HTML5 properties persistence.
 * Client code can either use Database or Local Storage.
 */
public interface DataDao {
    public void init(Async<Void> result);

    public void set(String field, String data, Async<Void> result);

    public void get(String field, Async<String> result);

    public void remove(String field, Async<Void> result);

    public void clear(Async<Void> result);

    public static final class DB implements DataDao {
        @Connection(name = "DataStore", version = "1.0", description = "GWT Database", maxsize = 1024 * 1024 * 5)
        public interface DBDataService extends DataService {
            @Update("CREATE TABLE IF NOT EXISTS data ("
                    + "id TEXT NOT NULL PRIMARY KEY, "
                    + "json TEXT)") void initDatabase(VoidCallback result);

            @Select("SELECT json FROM data WHERE id = {id}") void get(String id, ListCallback<GenericRow> result);

            @Update("INSERT INTO data (id, json ) VALUES ( {id}, {value})") void insert(String id, String value, VoidCallback result);

            @Update("UPDATE data SET json = {value} WHERE id = {id}") void update(String id, String value, VoidCallback result);

            @Update("DELETE FROM data WHERE id = {id}") void remove(String id, VoidCallback result);

            @Update("DROP TABLE data") void clear(VoidCallback result);
        }

        private DBDataService db;

        public static boolean isSupported() {
            return Database.isSupported();
        }

        @Override public void init(final Async<Void> result) {
            log("DB init");
            db = GWT.create(DBDataService.class);
            db.initDatabase(new VoidCallback() {
                @Override public void onSuccess() {
                    log("DB init successful");
                    result.onSuccess(null);
                }
                @Override public void onFailure(DataServiceException error) {
                    result.onFailure(error);
                }
            });
        }
        @Override public void set(final String field, final String data, final Async<Void> result) {
//            log("DB:set [" + field + "]");
            db.get(field, new ListCallback<GenericRow>() {
                @Override public void onSuccess(List<GenericRow> oldData) {
                    if (oldData.isEmpty()) {
                        db.insert(field, data, new VoidCallback() {
                            @Override public void onSuccess() {
//                                log("DB:insert complete");
                                result.onSuccess(null);
                            }
                            @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
                        });
                    } else {
                        db.update(field, data, new VoidCallback() {
                            @Override public void onSuccess() {
//                                log("DB:update complete");
                                result.onSuccess(null);
                            }
                            @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
                        });
                    }
                }
                @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
            });
        }

        @Override public void get(final String field, final Async<String> result) {
//            log("DB:get [" + field + "]");
            db.get(field, new ListCallback<GenericRow>() {
                @Override public void onSuccess(final List<GenericRow> data) {
                    if (data.isEmpty()) {
//                        log("DB:get [" + field + "]" + " not found");
                        result.onSuccess(null);
                    } else {
                        result.onSuccess(data.get(0).getString("json"));
                    }
                }
                @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
            });
        }
        @Override public void remove(String id, final Async<Void> result) {
//            log("DB:remove [" + id + "]");
            db.remove(id, new VoidCallback() {
                @Override public void onSuccess() {
//                    log("DB:remove complete");
                    result.onSuccess(null);
                }
                @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
            });
        }

        @Override public void clear(final Async<Void> result) {
            log("Clearing database");
            db.clear(new VoidCallback() {
                @Override public void onSuccess() {result.onSuccess(null);}
                @Override public void onFailure(DataServiceException error) {result.onFailure(error);}
            });
        }
    }

    public static final class Store implements DataDao {
        private Storage storage;
        public static boolean isSupported() {
            return Storage.isSupported();
        }
        @Override public void init(Async<Void> result) {
            try {
                storage = Storage.getLocalStorage();
                result.onSuccess(null);
            } catch (Exception e) { result.onFailure(e); }
        }
        @Override public void set(String field, String data, Async<Void> result) {
            try {
                storage.setItem(field, data);
                result.onSuccess(null);
            } catch (Exception e) { result.onFailure(e); }
        }
        @Override public void get(String field, Async<String> result) {
            try {
                result.onSuccess(storage.getItem(field));
            } catch (Exception e) { result.onFailure(e); }
        }
        @Override public void remove(String field, Async<Void> result) {
            try {
                storage.removeItem(field);
                result.onSuccess(null);
            } catch (Exception e) { result.onFailure(e); }
        }
        @Override public void clear(Async<Void> result) {
            storage.clear();
            result.onSuccess(null);
        }
    }
}
