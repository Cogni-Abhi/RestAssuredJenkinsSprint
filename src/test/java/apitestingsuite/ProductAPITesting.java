package apitestingsuite;

import org.json.JSONObject;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

public class ProductAPITesting extends ReponseSetup {
    private String accessToken;
    private static ExtentReports extent;
    private static ExtentTest test;

    public ProductAPITesting() {
        accessToken = AuthAccessCode.getAccessToken("user1", "pass123");
    }

    @BeforeClass
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("./extentReport/ProductAPIReport.html");
        extent = new ExtentReports();
        spark.config().setDocumentTitle("Product API Test Report");
        spark.config().setReportName("API Automation Suite - Product Module");
        spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
        extent.attachReporter(spark);
        extent.setSystemInfo("Project Name", "Product API Testing");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Build", "1.0");
        extent.setSystemInfo("Framework", "Rest Assured + TestNG");
    }

    @AfterClass
    public void tearDownReport() {
        extent.flush();
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void addProduct(JSONObject product) {
        test = extent.createTest("Add Product - " + product.getString("name"));
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
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat("Status code should be 200", statusCode, equalTo(200));
            assertThat("Content-Type should be JSON", response.getHeader("Content-Type"), containsString("application/json"));
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
        test = extent.createTest("View All Products");
        setup();
        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .get("/getAllProducts")
                .then()
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(response.getHeader("Content-Type"), containsString("application/json"));
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
        test = extent.createTest("View Product by ID - " + product.getInt("id"));
        setup();
        int id = product.getInt("id");

        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .get("/getProductbyId/" + id)
                .then()
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(response.getHeader("Content-Type"), containsString("application/json"));
            assertThat("ID should be "+id, response.jsonPath().getString("id"), equalTo(id+""));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void viewProductbyName(JSONObject product) {
        test = extent.createTest("View Product by Name - " + product.getString("name"));
        setup();
        String name = product.getString("name");

        Response response = reqSpec
                .queryParam("name", name)
                .header("Authorization", accessToken)
                .when()
                .get("/viewProductByName")
                .then()
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(response.getHeader("Content-Type"), containsString("application/json"));
            assertThat("Name should be "+name, response.jsonPath().getString("name"), equalTo(name));
        } else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            assertThat(response.asString(), containsString("error"));
        } else {
            throw new AssertionError("Unexpected status: " + statusCode + "\nResponse: " + response.asPrettyString());
        }
    }

    @Test(dataProvider = "testData", dataProviderClass = ProductAPIDataProvider.class)
    public void updateProduct(JSONObject product) {
        test = extent.createTest("Update Product - " + product.getString("name"));
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
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(response.getHeader("Content-Type"), containsString("application/json"));
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
        test = extent.createTest("Delete Product by ID - " + product.getInt("id"));
        setup();
        int id = product.getInt("id");

        Response response = reqSpec
                .header("Authorization", accessToken)
                .when()
                .delete("/delProduct/" + id)
                .then()
                .log().all()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        logTestResult(test, statusCode, response);

        if (statusCode == 200) {
            assertThat(statusCode, equalTo(200));
            assertThat(response.getHeader("Content-Type"), containsString("application/json"));
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

    private void logTestResult(ExtentTest test, int statusCode, Response response) {
        if (statusCode == 200) {
            test.pass("Positive Testcase Passed\nStatus Code: " + statusCode +
                      "\nResponse: " + response.asPrettyString());
        } 
        else if (statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 415) {
            test.pass("Negative Testcase Passed\nStatus Code: " + statusCode +
                      "\nResponse: " + response.asPrettyString());
        } 
        else {
            test.fail("Unexpected Status\nStatus Code: " + statusCode +
                      "\nResponse: " + response.asPrettyString());
        }
    }

}
