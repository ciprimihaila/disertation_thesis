/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import cercetare.dataparser.Constants;
import cercetare.dataparser.DbUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class Preprocessing {

    public static enum SEQDELIM {
        START,
        CONTINUE,
        STOP
    }

    /**
     *
     * @param app
     * @param user
     * @param collection
     * @return
     */
    public static List loadCategoriesForUserAndApp(String app, String user, String collection) {
        MongoClient client = DbUtils.getMongoClient();
        Map<String, BsonValue> rowContent = new HashMap();
        rowContent.put(Constants.USER_KEY, new BsonString(user));
        rowContent.put(Constants.APP_KEY, new BsonString(app));
        return DbUtils.find(client, Constants.DB_NAME, collection, rowContent);
    }

    public static List<String> getUsersForApp(String app, String collection, int userMin) {
        List<String> users = new ArrayList<>();
        MongoClient client = DbUtils.getMongoClient();

        Map<String, Object> match = new HashMap();
        match.put(Constants.APP_KEY, app);
        AggregateIterable resultu = DbUtils.aggregate(client, Constants.DB_NAME, collection,
                Constants.USER_KEY, match, -1);

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

    public static Map<String, Integer> extractCategories(String app, String user, String collection) {
        Map<String, Integer> resultMap = new HashMap<>();
        MongoClient client = DbUtils.getMongoClient();

        Map<String, Object> match = new HashMap();
        match.put(Constants.APP_KEY, app);
        match.put(Constants.USER_KEY, user);

        AggregateIterable result = DbUtils.aggregate(client, Constants.DB_NAME,
                collection, Constants.CATEGORY_KEY, match, 0);

        int id = 0;
        MongoCursor cursor = result.iterator();
        while (cursor.hasNext()) {
            Document nextc = (Document) cursor.next();
            String category = nextc.get("_id").toString();
            if (resultMap.get(category) == null) {
                resultMap.put(category, id);
                id++;
            }
        }

        return sortByValues((HashMap) resultMap);
    }

    public static Integer getIdForDelimiter(int categSize, SEQDELIM delim) {
        switch (delim) {
            case START:
                return categSize + 1;
            case CONTINUE:
                return categSize + 2;
            default:
                return categSize + 3;
        }
    }

    public static boolean cycleDetectionTree(List<Node> sequence, String category) {
        for (Node page : sequence) {
            if (page.getCategory().equals(category)) {
                return true;
            }
        }
        return false;
    }

    public static Tree buildHistoryTree(Iterable data, Map<String, Integer> categories) {

        Tree tree = new Tree();

        List<Integer> delta = new ArrayList<>();
        long sum = 0;

        Iterator dataIterator = data.iterator();
        List<Node> sequence = new ArrayList<>();
        List<Node> previousSeq = new ArrayList<>(); //use prev sequence to avoid current vector size eq 1

        String prevCategory = "";

        LocalTime prevTime = null;

        while (dataIterator.hasNext()) {

            Document object = (Document) dataIterator.next();
            String category = ((Document) object.get("vertical")).get("category").toString();

            if (!prevCategory.equals(category)) {

                Date date = ((Date) object.get("datetime"));
                LocalTime currentTime = LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault()).toLocalTime();

                if (prevTime != null) {//TODO add diff only for entered sequences !!!
                    int diff = currentTime.minusSeconds(prevTime.toSecondOfDay()).toSecondOfDay();
                    tree.addDiff(category, diff);
                    delta.add(diff); //check seconds
                    sum += diff;
                }

                prevTime = currentTime;
                //System.out.println(category + "  " + date.toString());
                if (cycleDetectionTree(sequence, category)) {

                    for (Node s : sequence) {
                        System.out.print(s.getCategory() + " -> ");
                    }
                    System.out.println(" ");
                    if (previousSeq.size() > 0) {
                        tree.addSequence(previousSeq, false);
                    }
                    previousSeq = sequence;
                    sequence = new ArrayList<>();
                }
                sequence.add(new Node(category, date, new ArrayList<>()));

            }

            if (prevCategory.equals("")) {
                tree.addDiff(category, 0);
            }

            prevCategory = category;
        }

        //use logger instead
        //System.out.println("Count: " + i);
        for (Node s : sequence) {
            System.out.print(s.getCategory() + " -> ");
        }

        //eliminare secventa de 1 element !!!!
        if (sequence.size() < 2) {
            tree.addSequence(previousSeq, true);//try to remove mean and dev std
        } else {
            tree.addSequence(previousSeq, false);
            tree.addSequence(sequence, true);
        }
        double mean = (double) sum / delta.size();
        double devs = Utils.computeStandardDeviation(delta, mean);

        tree.setStatisticValues(mean, devs);

        return tree;
    }

    private static Map sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
