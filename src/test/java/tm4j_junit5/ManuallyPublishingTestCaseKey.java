package tm4j_junit5;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import senecal.tm4j.Tm4jExtension;
import tm4j_junit5.SampleTests.TestData;

public class ManuallyPublishingTestCaseKey {

	@ParameterizedTest
	@MethodSource("methodSource")
	public void forParameterizedTest(TestData testData, TestReporter testReporter) {
		testReporter.publishEntry(Tm4jExtension.TEST_CASE_KEY, testData.getTestCaseKey());
		testData.run();
	}

	public static Stream<TestData> methodSource() {
		return Stream.of(new TestData("key-p1", Assert::fail), new TestData("key-p2", () -> {
		}));
	}
}
