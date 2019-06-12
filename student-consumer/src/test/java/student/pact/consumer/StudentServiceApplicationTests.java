package student.pact.consumer;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceApplicationTests {

	PactSpecVersion pactSpecVersion;
	@Autowired
	StudentProvider studentProvider;

	private void checkResult(PactVerificationResult result) {
		if (result instanceof PactVerificationResult.Error) {
			throw new RuntimeException(((PactVerificationResult.Error)result).getError());
		}
		assertEquals(PactVerificationResult.Ok.INSTANCE, result);
	}

	@Test
	public void testGETWithPactDSLJsonBody() {

		//Version of Pact to be used
		MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);

		//Setting up the mock data
		DslPart body = new PactDslJsonBody()
				.stringType("name", "Mike")
				.integerType("age", 10)
				.integerType("marks", 90);

		//We tell Pact that - given the mock data created above, use the API endpoint, and test the Contract
		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("StudentConsumer")
				.hasPactWith("StudentProvider")
				.given("")
				.uponReceiving("Student Request")
				.path("/students/getStudents")
				.method("GET")
				.willRespondWith()
				.status(200)
				.body(body)
				.toPact();

		//as of now, only the "name" is ready and thus please check if the name is "Mike" or not
		PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
			studentProvider.setBackendURL(mockServer.getUrl(), true);
			Student std = studentProvider.getStudentFromProvider();
			assertEquals(std.getName(), "Mike");
		});
		checkResult(result);
	}

//	@Test
//	public void testPOSTWithPactDSLJsonBody() {
//
//		//Version of Pact to be used
//		MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);
//
//		//Setting up the payload
//		DslPart payload = new PactDslJsonBody()
//				.stringType("name", "Mark")
//				.integerType("age", 11)
//				.integerType("marks", 91);
//
//		//Setting up the mock data
//		DslPart resp = new PactDslJsonArray()
//				.object()
//				.stringType("name", "Mike")
//				.integerType("age", 10)
//				.integerType("marks", 90)
//				.closeObject()
//				.object()
//				.stringType("name", "Mark")
//				.integerType("age", 11)
//				.integerType("marks", 91)
//				.closeObject();
//
//		//We tell Pact that - given the mock data created above, use the API endpoint, and test the Contract
//		RequestResponsePact pact = ConsumerPactBuilder
//				.consumer("StudentConsumer")
//				.hasPactWith("StudentProvider")
//				.given("")
//				.uponReceiving("Student Request")
//				.path("/students/addStudent")
//				.body(payload)
//				.method("POST")
//				.willRespondWith()
//				.status(200)
//				.body(resp)
//				.toPact();
//
//		//as of now, only the "name" is ready and thus please check if the name is "Mark" or not, also the length
//		PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
//			studentProvider.setBackendURL(mockServer.getUrl(), false);
//			Map<String, Object> payLoad = new HashMap<>();
//			payLoad.put("name", "Darwin");
//			payLoad.put("age", 11);
//			payLoad.put("marks", 29);
//			List<Student> std = studentProvider.addStudentFromProvider(payLoad);
//			assertEquals(3, std.size());
//			assertEquals("Darwin", std.get(1).getName());
//		});
//
//	}
}
