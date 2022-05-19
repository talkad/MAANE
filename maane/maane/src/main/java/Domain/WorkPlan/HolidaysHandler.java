package Domain.WorkPlan;

import Persistence.HolidaysQueries;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class HolidaysHandler {
    int year;
    String informationString;
    ArrayList<String[]> informationArray;

    public HolidaysHandler (int year){
        this.year = year;
        informationString = fetchAPI(year);
        informationArray = new ArrayList<>();
        writeToDb();
    }

    private String fetchAPI(int year) {
        StringBuilder informationString = new StringBuilder();
        try {
            URL url = new URL("https://www.hebcal.com/hebcal?v=1&cfg=json&maj=on&mod=on&year="+year+"&c=off&month=x&geonameid=293397");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Check if connect is made
            int responseCode = conn.getResponseCode();

            // 200 OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                //informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                //Close the scanner
                scanner.close();
            }
        } catch (Exception e) {e.printStackTrace();}
        return informationString.toString();
    }

    private void fillArray(){
        JsonObject gsonObj = new Gson().fromJson(informationString, JsonObject.class);
        JsonArray jsonArray = gsonObj.getAsJsonArray("items");
        for (int i = 0; i < jsonArray.size(); i++) {
            String title = jsonArray.get(i).getAsJsonObject().get("title").getAsString();
            String date = jsonArray.get(i).getAsJsonObject().get("date").getAsString();
            String category = jsonArray.get(i).getAsJsonObject().get("category").getAsString();
            if (!title.startsWith("Havdalah") && !category.equals("candles")){
                String [] column = { title, date, category };
                informationArray.add(column);
            }
        }
    }

    private void writeToDb(){
        fillArray();
        HolidaysQueries.getInstance().insertHolidaysDates(informationArray, year);
    }

    public void printArray(){
        for (String[] entry : informationArray) {
            System.out.println(entry[0] + " on date: " + entry[1] + " and category " + entry[2]);
        }
    }


}
