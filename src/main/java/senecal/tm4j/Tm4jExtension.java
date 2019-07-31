package senecal.tm4j;

import java.util.Optional;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class Tm4jExtension implements BeforeEachCallback {

    public static final String TEST_CASE_KEY = "testCaseKey";

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        Optional<Tm4jTest> testCaseOption = AnnotationSupport.findAnnotation(context.getTestMethod(), Tm4jTest.class);
        testCaseOption.ifPresent(testCase -> publishAnnotation(testCase, context));
    }

    private void publishAnnotation(final Tm4jTest testCase, final ExtensionContext context) {
        context.publishReportEntry(TEST_CASE_KEY, testCase.key());
    }

}
