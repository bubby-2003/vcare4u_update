package com.vcare4u.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
        // Application context should load
    }

    // -------------------- Doctor Service --------------------
    @Test
    void testDoctorServiceGetAllDoctors() {
        webTestClient.get()
            .uri("/doctor-service/doctors")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                byte[] responseBody = response.getResponseBody();
                System.out.println("Doctor Service Response: " + new String(responseBody));
                assert responseBody != null && responseBody.length > 0 : "Response body is empty";
            });
    }

    // -------------------- Patient Service --------------------

    @Test
    void testPatientServiceGetAllPatients() {
        webTestClient.get()
            .uri("/patient-service/patients")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                byte[] responseBody = response.getResponseBody();
                System.out.println("Patient Service Response: " + new String(responseBody));
                assert responseBody != null && responseBody.length > 0 : "Response body is empty";
            });
    }

    // -------------------- Appointment Service --------------------

    @Test
    void testAppointmentServiceGetAllAppointments() {
        webTestClient.get()
            .uri("/appointment-service/appointments")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                byte[] responseBody = response.getResponseBody();
                System.out.println("Appointment Service Response: " + new String(responseBody));
                assert responseBody != null && responseBody.length > 0 : "Response body is empty";
            });
    }

    // -------------------- Auth Service --------------------
//
//    @Test
//    void testAuthServiceUnauthorizedLogin() {
//        webTestClient.post()
//            .uri("/auth-service/api/auth/login")
//            .bodyValue(Map.of("username", "invalid", "password", "invalid"))
//            .exchange()
//            .expectStatus()
//            .isUnauthorized()
//            .expectBody()
//            .consumeWith(response -> {
//                int statusCode = response.getStatus().value();
//                if (statusCode == 401) {
//                    System.out.println("Unauthorized access");
//                } else {
//                    System.out.println("Unexpected status code: " + statusCode);
//                }
//            });
//    }



//    @Test
//    void testAuthServiceRegisterBadRequest() {
//        webTestClient.post()
//            .uri("/auth-service/api/auth/register")
//            .bodyValue(Map.of("username", "", "password", ""))
//            .exchange()
//            .expectBody()
//            .consumeWith(response -> {
//                int statusCode = response.getStatus().value();
//                System.out.println("Register Bad Request Status: " + statusCode);
//                assert statusCode == 400 : "Expected status 400 but got " + statusCode;
//            });
//    }

//    @Test
//    void testAuthServicePublicTestEndpoint() {
//        webTestClient.get()
//            .uri("/auth-service/api/auth/test")
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody(String.class)
//            .value(body -> {
//                System.out.println("Auth Test Response: " + body);
//                assert body.equals("JWT is valid. You are authenticated.") : "Unexpected response body: " + body;
//            });
//    }


    // -------------------- Lab Service --------------------

    @Test
    void testLabServiceGetAllTests() {
        webTestClient.get()
            .uri("/lab-service/lab/tests")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                byte[] responseBody = response.getResponseBody();
                System.out.println("Lab Tests Response: " + new String(responseBody));
                assert responseBody != null && responseBody.length > 0 : "Response body is empty";
            });
    }

    @Test
    void testLabServiceInvalidPathReturns404() {
        webTestClient.get()
            .uri("/lab-service/lab/invalid")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                int status = response.getStatus().value();
                System.out.println("Lab Invalid Path Status: " + status);
                assert status == 404 : "Expected 404 but got " + status;
            });
    }

    // -------------------- General Negative Tests --------------------

    @Test
    void testInvalidServiceRouteReturns404() {
        webTestClient.get()
            .uri("/nonexistent-service/test")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                int status = response.getStatus().value();
                System.out.println("Invalid Service Route Status: " + status);
                assert status == 404 : "Expected 404 but got " + status;
            });
    }

    @Test
    void testMalformedUriReturns404() {
        webTestClient.get()
            .uri("/doctor-service/")
            .exchange()
            .expectBody()
            .consumeWith(response -> {
                int status = response.getStatus().value();
                System.out.println("Malformed URI Status: " + status);
                assert status == 404 : "Expected 404 but got " + status;
            });
    }
}