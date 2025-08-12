package apitestingsuite;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AuthAccessCode extends ReponseSetup {

    public static String getAccessToken(String username, String password) {
        String authCode = getAuthCode(username, password);  
        return requestAccessToken(authCode);
    }

    private static String getAuthCode(String username, String password) {
        setup();
        Response auth_code_response = reqSpec
                .contentType(ContentType.URLENC)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/auth/login");

        return auth_code_response.jsonPath().getString("auth_code");
    }

    private static String requestAccessToken(String authCode) {
        setup();
        Response access_token_response = reqSpec
                .contentType(ContentType.URLENC)
                .formParam("auth_code", authCode)
                .when()
                .post("/auth/token");

        return access_token_response.jsonPath().getString("access_token");
    }
}

