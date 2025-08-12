package apitestingsuite;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class ReponseSetup {
	protected static RequestSpecification reqSpec;
	public static void setup() {
		RestAssured.baseURI = "https://webapps.tekstac.com/OAuthRestApi/webapi";
		reqSpec = RestAssured.given()
					.header("Content-Type","application/json");
	}
}
