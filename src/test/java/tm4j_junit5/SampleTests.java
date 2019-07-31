package tm4j_junit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import senecal.tm4j.HasTestCaseKey;
import senecal.tm4j.Tm4jTest;

public class SampleTests {

	@Tm4jTest(key = "key-11")
	void testName() {
		assertTrue(true, "This one works");
	}

	interface Run {
		void run();
	}

	public static class TestData implements HasTestCaseKey {

		private String testCaseKey;
		private Runnable run;

		public TestData(String testCaseKey, Runnable run) {
			this.testCaseKey = testCaseKey;
			this.run = run;
		}

		@Override
		public String getTestCaseKey() {
			return testCaseKey;
		}
		
		public void run() {
			run.run();
		}

	}

	@ParameterizedTest
	@MethodSource("methodSource")
	public void forParameterizedTest(TestData testData) {
		testData.run();
	}

	public static Stream<TestData> methodSource() {
		return Stream.of(new TestData("key-p1", Assert::fail), new TestData("key-p2", () -> {
		}));
	}
}
