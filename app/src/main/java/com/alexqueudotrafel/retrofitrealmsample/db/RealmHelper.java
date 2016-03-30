package com.alexqueudotrafel.retrofitrealmsample.db;

import android.util.Log;

import com.alexqueudotrafel.retrofitrealmsample.BuildConfig;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

import static io.realm.Realm.Transaction;
import static io.realm.Realm.getDefaultInstance;

/**
 * Created by alexqueudotrafel on 17/03/16.
 */
public class RealmHelper {

    private final static String TAG = "RealmHelper";
    private final static boolean D = BuildConfig.DEBUG;

    /**
     * Copies or updates a single object to Realm DB
     *
     * @param object
     * @param <E>
     */
    public static <E extends RealmObject> void copyOrUpdate(final E object) {
        Realm realm = getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
    }

    /**
     * Copies or updates a single object to Realm DB ASYNCHRONOUSLY
     *
     * @param object
     * @param <E>
     */
    public static <E extends RealmObject> void copyOrUpdateAsync(final E object) {
        Realm realm = getDefaultInstance();
        realm.executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        });
    }

    /**
     * Copies or updates a single object to Realm DB ASYNCHRONOUSLY with CALLBACKS
     *
     * @param object
     * @param successCallback
     * @param errorCallback
     * @param <E>
     */
    public static <E extends RealmObject> void copyOrUpdateAsync(final E object, Transaction.OnSuccess successCallback, Transaction.OnError errorCallback) {
        Realm realm = getDefaultInstance();
        realm.executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        }, successCallback, errorCallback);
    }

    /**
     * Copies or updates a list object to Realm DB (one by one)
     *
     * @param objects
     * @param <E>
     */
    public static <E extends RealmObject> void copyOrUpdate(List<E> objects) {
        if (objects == null || !objects.iterator().hasNext()) {
            return;
        }
        for (E object : objects) {
            copyOrUpdate(object);
        }
    }

    /**
     * Copies or updates a list object to Realm DB ASYNCHRONOUSLY (one by one)
     *
     * @param objects
     * @param <E>
     */
    private static final int MAX_RETRY = 3;
    private static int retryCounter = 0; // Control how many times we try to copy an object to realm

    public static <E extends RealmObject> void copyOrUpdateAsync(final List<E> objects) {
        if (objects == null || objects.size() == 0) {
            return;
        }
        final Transaction.OnSuccess successCallback = new Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // success copying item
                retryCounter = 0;
                objects.remove(0);
                // copy next item (if there is, else we finished the list)
                if (objects.size() > 0) {
                    copyOrUpdateAsync(objects);
                }
            }
        };
        final Transaction.OnError errorCallback = new Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Check realm is still open (=the app is running)
                if(Realm.getDefaultInstance()==null || Realm.getDefaultInstance().isClosed()){
                    if(D) Log.e(TAG, "Realm has been closed, remaining items will not be stored");
                    return;
                }
                if (D) Log.e(TAG, "Error for object: " + objects.get(0).toString() + "\n" + error.toString() + "\nRetries: " + retryCounter);
                if (retryCounter < MAX_RETRY) {
                    retryCounter++;
                    copyOrUpdateAsync(objects);
                } else {
                    if (D)
                        Log.e(TAG, "Max retries reached, the following object will not be copied: " + objects.get(0).toString());
                    retryCounter = 0;
                    objects.remove(0);
                    if (objects.size() > 0) {
                        copyOrUpdateAsync(objects);
                    }
                }
            }
        };
        copyOrUpdateAsync(objects.get(0), successCallback, errorCallback);
    }

    public static <E extends RealmObject> void removeEntry(final E object){
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        object.removeFromRealm();
        realm.commitTransaction();
    }
}
