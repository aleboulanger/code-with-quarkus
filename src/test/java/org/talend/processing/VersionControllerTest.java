package org.talend.processing;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class VersionControllerTest {

    @Test
    void getApplicationFeatures() {
        Response response = given()
                .headers("Accept", ContentType.JSON)
                .when().get("/version")
                .then().contentType(ContentType.JSON).extract().response();

        assertEquals(response.jsonPath().getList("apps"), List.of("SparkLocal", "SemanticTypesFullRun"));

    }
}