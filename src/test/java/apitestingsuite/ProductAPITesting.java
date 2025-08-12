package apitestingsuite;

import org.json.JSONObject;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

public class ProductAPITesting extends ReponseSetup {
    private String accessToken;

    public ProductAPITesting() {
        accessToken = AuthAccessCode.getAccessToken("user1", "pass123");
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void addProduct(JSONObject product) {
        setup();
        String requestBody = product.toString();

        Response response = reqSpec
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/addProduct")
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat("Status code should be 200", statusCode, equalTo(200));
            assertThat("Content-Type should be JSON", contentType, containsString("application/json"));
            List<Map<String, String>> products = response.jsonPath().getList("");
            Map<String, Object> expectedProduct = product.toMap();
            expectedProduct.replaceAll((k, v) -> String.valueOf(v)); 
            boolean found = products.stream().anyMatch(p -> p.equals(expectedProduct));
            assertThat("Product should be present in response", found, equalTo(true));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat("Error body should contain 'error'", response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void viewAllProducts(JSONObject product) {
        setup();
        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .get("/getAllProducts")
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(contentType, containsString("application/json"));
            assertThat("Response should be a JSON array", response.jsonPath().getList("") instanceof List, equalTo(true));
            assertThat("Response JSON length should be 3", response.jsonPath().getList("").size(), equalTo(3));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void viewProductbyId(JSONObject product) {
        setup();
        int id = product.getInt("id");

        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .get("/getProductbyId/" + id)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(contentType, containsString("application/json"));
            assertThat("ID should be "+id, response.jsonPath().getString("id"), equalTo(id+""));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void viewProductbyName(JSONObject product) {
        setup();
        String name = product.getString("name");

        Response response = reqSpec
                .queryParam("name", name)
                .header("Authorization", accessToken)
                .when()
                .get("/viewProductByName")
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(contentType, containsString("application/json"));
            assertThat("Name should be "+name, response.jsonPath().getString("name"), equalTo(name));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void updateProduct(JSONObject product) {
        setup();
        String requestBody = product.toString();
        int id = product.getInt("id");

        Response response = reqSpec
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/updateProduct/" + id)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(contentType, containsString("application/json"));
            List<Map<String, String>> products = response.jsonPath().getList("");
            Map<String, Object> expectedProduct = product.toMap();
            expectedProduct.replaceAll((k, v) -> String.valueOf(v)); 
            boolean found = products.stream().anyMatch(p -> p.equals(expectedProduct));
            assertThat("Product should be present in response and updated", found, equalTo(true));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void deleteProdcutbyId(JSONObject product) {
        setup();
        int id = product.getInt("id");

        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .delete("/delProduct/" + id)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String contentType = response.getHeader("Content-Type");

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(contentType, containsString("application/json"));
            List<Map<String, String>> products = response.jsonPath().getList("");
            Map<String, Object> expectedProduct = product.toMap();
            expectedProduct.replaceAll((k, v) -> String.valueOf(v)); 
            boolean found = products.stream().anyMatch(p -> p.equals(expectedProduct));
            assertThat("Product should not be present in response and updated", found, equalTo(false));
            
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }
}
