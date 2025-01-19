package com.labscs;

import spark.Spark;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import com.google.gson.JsonObject;

public class AppServer implements Runnable {
    private Vector<String> images_ = new Vector<String>();
    private Vector<String> texts_ = new Vector<String>();
    private Path path = Path.of("C:/file.txt");

    public void run() {
        JsonObject paramObject = new JsonObject();

        try {
            String s = Files.readString(path);
            String objects[] = s.split(":");

            for (String object : objects) {
                String[] obj = object.split("_");

                if ("img".equals(obj[0])) {
                    images_.add(object);
                }
                if ("text".equals(obj[0])) {
                    texts_.add(object);
                }
            }

        } catch (Exception err) {
            err.printStackTrace();
        }

        Spark.port(4545);

        Spark.get("/object/list", (req, res) -> {
            String allObjects = "";

            int idx = 0;
            for (String item : images_) {
                allObjects += "(" + idx + ")_" + item + ":";
                idx++;
            }

            idx = 0;
            for (String item : texts_) {
                allObjects += "(" + idx + ")_" + item + ":";
                idx++;
            }

            paramObject.addProperty("data", allObjects);
            return paramObject;
        });

        Spark.get("/object/img", (req, res) -> {
            String msg = req.queryParams("cmd");
            String obj = images_.get(Integer.parseInt(msg));

            paramObject.addProperty("data", obj);
            return paramObject;
        });

        Spark.get("/object/text", (req, res) -> {
            String msg = req.queryParams("cmd");
            String obj = texts_.get(Integer.parseInt(msg));

            paramObject.addProperty("data", obj);
            return paramObject;
        });

        Spark.delete("/object/delete", (req, res) -> {
            String msg = req.queryParams("cmd");

            String[] obj = msg.split("_");
            int idx = Integer.parseInt(obj[1]);

            if ("img".equals(obj[0])) {
                this.images_.remove(idx);
                paramObject.addProperty("data", "image removed");
            }

            if ("text".equals(obj[0])) {
                this.texts_.remove(idx);
                paramObject.addProperty("data", "text removed");
            }

            new FileWriter("C:/file.txt", false).close();
            return paramObject;
        });

        Spark.post("/object/add", (req, res) -> {

            String msg = req.queryParams("cmd");
            String[] cmd = msg.split("_");

            if ("img".equals(cmd[0])) {
                images_.add(msg);
            }
            if ("text".equals(cmd[0])) {
                texts_.add(msg);
            }

            Files.writeString(path, msg + ":");
            paramObject.addProperty("data", "new object added");
            return paramObject;
        });
    }
}
