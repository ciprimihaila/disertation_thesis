/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import com.mongodb.MongoClient;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class TrainTestDataGenerator {

    private static final String SAVE_PATH = "/home/ciprian/crf_wkp/jcrfsuite-master/example/tweet-pos/test5h/";
    private static final String TRAIN_FILE_NAME = "/trainWeb";
    private static final String TEST_FILE_NAME = "/testWeb";
    private static final String COLLECTION_NAME = "test5h2";

    public static void generateCRFTrainTestForAppAndUsers(List<String> users, String app, double trainWeight,
            String collection) {
        MongoClient client = DbUtils.getMongoClient();

        File dirApp = new File(SAVE_PATH + app);
        dirApp.mkdir();

        for (String user : users) {
            File dir = new File(dirApp + "/" + user);
            dir.mkdir();

            Map<String, BsonValue> rowContent = new HashMap();
            rowContent.put(Constants.USER_KEY, new BsonString(user));
            rowContent.put(Constants.APP_KEY, new BsonString(app));
            List<Document> db = DbUtils.find(client, Constants.DB_NAME, collection, rowContent);

            int trainSize = (int) ((int) db.size() * trainWeight);

            List<Document> trainData = db.subList(0, trainSize);
            List<Document> testData = db.subList(trainSize, db.size());

            List<String> train = new ArrayList<>();
            Document prevprev = null;
            Document prev = null;

            for (Document trainObj : trainData) {
                boolean addLine = true;

                String currentVertical = trainObj.get(Constants.CATEGORY_KEY).toString();
                //System.out.println(currentVertical);
                StringBuilder builder = new StringBuilder();
                builder.append(currentVertical);
                builder.append("\t");
                builder.append("w[0]=");
                builder.append(user);
                builder.append("\t");
                builder.append("pos[0]=");
                builder.append(currentVertical);//trainObj.get("vertical").toString());

                if (prev != null) {
                    String prevVertical = prev.get(Constants.CATEGORY_KEY).toString();
                    if (!prevVertical.equals(currentVertical)) {
                        builder.append("\t");
                        builder.append("pos[-1]|pos[0]=");
                        builder.append(prevVertical).append("|").append(currentVertical);

                        if (prevprev != null) {
                            String prevPrevVertical = prevprev.get(Constants.CATEGORY_KEY).toString();
                            builder.append("\t");
                            builder.append("pos[-2]|pos[-1]|pos[0]=");
                            builder.append(prevPrevVertical).append("|");
                            builder.append(prevVertical).append("|").append(currentVertical);
                        }
                    } else {
                        addLine = false;
                    }
                }

                if (addLine) {
                    train.add(builder.toString());
                    prevprev = prev;
                    prev = trainObj;
                }

            }

            int i = 0;
            for (Document testObj : testData) {

                String currentVertical = testObj.get(Constants.CATEGORY_KEY).toString();
                //System.out.println(currentVertical);
                StringBuilder builder2 = new StringBuilder();
                builder2.append(currentVertical);
                builder2.append("\t");
                builder2.append("w[0]=");
                builder2.append(user);

                List<String> test = new ArrayList<>();
                test.add(builder2.toString());

                boolean addLine = true;
                builder2.append("\t");
                builder2.append("pos[0]=");
                builder2.append(currentVertical);//trainObj.get("vertical").toString());

                if (prev != null) {
                    String prevVertical = prev.get(Constants.CATEGORY_KEY).toString();
                    if (!prevVertical.equals(currentVertical)) {
                        builder2.append("\t");
                        builder2.append("pos[-1]|pos[0]=");
                        builder2.append(prevVertical).append("|").append(currentVertical);

                        if (prevprev != null) {
                            String prevPrevVertical = prevprev.get(Constants.CATEGORY_KEY).toString();
                            builder2.append("\t");
                            builder2.append("pos[-2]|pos[-1]|pos[0]=");
                            builder2.append(prevPrevVertical).append("|");
                            builder2.append(prevVertical).append("|").append(currentVertical);
                        }

                        FileUtils.writeToFile(dir.getPath() + TRAIN_FILE_NAME + i + ".txt", train);
                        FileUtils.writeToFile(dir.getPath() + TEST_FILE_NAME + i + ".txt", test);
                        i++;

                    } else {
                        addLine = false;
                    }
                }

                if (addLine) {
                    train.add(builder2.toString());
                    prevprev = prev;
                    prev = testObj;
                }

            }

        }
    }

    public static void main(String[] args) {

        String app = UserParser.getMostUsedApps(COLLECTION_NAME, 2000).get(0);
        List<String> users = UserParser.getUsersForApp(app, COLLECTION_NAME, 200);
        generateCRFTrainTestForAppAndUsers(users, app, 0.5, COLLECTION_NAME);

    }
}
