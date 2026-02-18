package com.jsp.assessment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jsp.assessment.dto.RegistrationRequest;
import com.jsp.assessment.dto.SubmissionRequest;
import com.jsp.assessment.dto.WebhookResponse;

@SpringBootApplication
public class AssessmentApplication implements CommandLineRunner {
	
	
	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(AssessmentApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
        String registrationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        RegistrationRequest reg = new RegistrationRequest("John Doe", "REG12347", "john@example.com");

        try {
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                    registrationUrl, reg, WebhookResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = response.getBody().accessToken(); // [cite: 6]
                String sqlSolution = "SELECT * FROM employees WHERE department = 'Sales';"; 
                submitSolution(token, sqlSolution);
            }
        } catch (Exception e) {
            System.err.println("Error during process: " + e.getMessage());
        }
    }
	
	private void submitSolution(String token, String query) {
        String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        SubmissionRequest submission = new SubmissionRequest(query);
        HttpEntity<SubmissionRequest> entity = new HttpEntity<>(submission, headers);

        ResponseEntity<String> result = restTemplate.postForEntity(submitUrl, entity, String.class);
        System.out.println("Submission Status: " + result.getStatusCode());
        System.out.println("Response: " + result.getBody());
    }

}
