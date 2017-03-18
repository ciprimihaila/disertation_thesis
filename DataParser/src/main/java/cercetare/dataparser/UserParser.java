/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class UserParser {

    private static final int APP_MIN = 1000;

    private static final int USER_MIN = 200;

    public static Map<String, List<String>> extractUsersPerApp(String collection) {

        Map<String, List<String>> appUsers = new HashMap<>();

        MongoClient client = DbUtils.getMongoClient();

        AggregateIterable mostUsedApp = DbUtils.aggregate(client, Constants.DB_NAME, collection, "appKey", null, -1);

        MongoCursor mostUsedAppCursor = mostUsedApp.iterator();

        while (mostUsedAppCursor.hasNext()) {
            Object nextc = mostUsedAppCursor.next();
            int count = (Integer) ((Document) nextc).get("count");

            if (count > APP_MIN) {

                String appId = ((Document) nextc).get("_id").toString();
                System.out.println(appId);
                Map<String, Object> match = new HashMap();
                match.put("appKey", appId);
                AggregateIterable usersWithMostEntries = DbUtils.aggregate(client, Constants.DB_NAME, collection, "appInstanceUid", match, -1);
                MongoCursor cursorUser = usersWithMostEntries.iterator();
                List<String> users = new ArrayList<>();
                while (cursorUser.hasNext()) {
                    Object nextUser = cursorUser.next();
                    int nextUserCount = (Integer) ((Document) nextUser).get("count");
                    if (nextUserCount > USER_MIN) {
                        String user = ((Document) nextUser).get("_id").toString();
                        System.out.println("\t" + user + " count: " + nextUserCount);
                        users.add(user);
                    }
                }
                if (users.size() > 0) {
                    appUsers.put(appId, users);
                }
            }
        }
        return appUsers;
    }

    public static List<String> getMostUsedApps(String collection, int minUse) {
        List<String> apps = new ArrayList<>();
        MongoClient client = DbUtils.getMongoClient();
        AggregateIterable mostUsedApp = DbUtils.aggregate(client, Constants.DB_NAME,
                collection, "appKey", null, -1);

        MongoCursor mostUsedAppCursor = mostUsedApp.iterator();
        while (mostUsedAppCursor.hasNext()) {
            Object nextc = mostUsedAppCursor.next();
            int count = (Integer) ((Document) nextc).get("count");
            if (count > APP_MIN) {
                String appId = ((Document) nextc).get("_id").toString();
                apps.add(appId);
            }
        }
        return apps;
    }

    public static List<String> getUsersForApp(String app, String collection, int userMin) {
        List<String> users = new ArrayList<>();
        MongoClient client = DbUtils.getMongoClient();

        Map<String, Object> match = new HashMap();
        match.put("appKey", app);
        AggregateIterable resultu = DbUtils.aggregate(client, Constants.DB_NAME, collection,
                "appInstanceUid", match, -1);

        MongoCursor cursorUser = resultu.iterator();
        while (cursorUser.hasNext()) {
            Object nextUser = cursorUser.next();
            int nextUserCount = (Integer) ((Document) nextUser).get("count");
            if (nextUserCount > userMin) {
                String user = ((Document) nextUser).get("_id").toString();
                users.add(user);
            }
        }

        return users;
    }

    public static List<String> extractUsers(String collection) {

        List<String> users = new ArrayList<>();

        MongoClient client = DbUtils.getMongoClient();

        AggregateIterable result = DbUtils.aggregate(client, Constants.DB_NAME, collection, "appKey", null, -1);

        MongoCursor cursor = result.iterator();

        while (cursor.hasNext()) {
            Object nextc = cursor.next();
            int count = (Integer) ((Document) nextc).get("count");
            if (count > APP_MIN) {
                String appId = ((Document) nextc).get("_id").toString();
                System.out.println(appId);

                Map<String, Object> match = new HashMap();
                match.put("appKey", appId);
                AggregateIterable resultu = DbUtils.aggregate(client, Constants.DB_NAME, collection,
                        "appInstanceUid", match, -1);

                MongoCursor cursorUser = resultu.iterator();
                while (cursorUser.hasNext()) {
                    Object nextUser = cursorUser.next();
                    int nextUserCount = (Integer) ((Document) nextUser).get("count");
                    if (nextUserCount > USER_MIN) {
                        String user = ((Document) nextUser).get("_id").toString();
                        System.out.println("\t" + user + " count: " + nextUserCount);
                        users.add(appId + " " + user);
                    }

                }
            }

        }

        return users;
    }

}
