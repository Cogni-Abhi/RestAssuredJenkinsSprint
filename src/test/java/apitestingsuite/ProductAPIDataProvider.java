package apitestingsuite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;

public class ProductAPIDataProvider {

    @DataProvider(name = "testData")
    public Object[][] getJsonData() throws IOException {
        try (FileInputStream fis = new FileInputStream("./src/test/resources/testData.json")) {

            JSONArray jsonArray = new JSONArray(new JSONTokener(fis));

            Object[][] dataArray = new Object[jsonArray.length()][1];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);
                dataArray[i][0] = product;
            }
            return dataArray;
        }
    }
}
