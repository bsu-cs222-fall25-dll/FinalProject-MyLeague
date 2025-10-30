package edu.bsu.cs222;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class apiTestTemp {
    public static void main(String[] args) throws IOException, InterruptedException {
        String content = new String(Files.readAllBytes(Paths.get("response.json")));
        JSONObject jsonObject = new JSONObject(content).getJSONArray("body").getJSONObject(0);
        JSONObject receiving =  jsonObject.getJSONObject("Receiving");
        System.out.println(receiving.getString("recYds"));
        System.out.println(receiving.getInt("recYds") * 2);
    }
}
