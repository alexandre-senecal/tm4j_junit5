/*
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package senecal.tm4j;

import static com.adaptavist.tm4j.junit.file.CustomFormatFile.generateCustomFormatFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptavist.tm4j.junit.customformat.CustomFormatConstants;
import com.adaptavist.tm4j.junit.customformat.CustomFormatContainer;
import com.adaptavist.tm4j.junit.customformat.CustomFormatExecution;
import com.adaptavist.tm4j.junit.customformat.CustomFormatTestCase;

public class Tm4jTestExecutionListener implements TestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(Tm4jTestExecutionListener.class);

    private CustomFormatContainer customFormatContainer;

    private final Map<String, CustomFormatTestCase> testIdentifierToTestCaseMap = new HashMap<>();

    @Override
    public void testPlanExecutionStarted(final TestPlan testPlan) {
        customFormatContainer = new CustomFormatContainer();
    }

    @Override
    public void reportingEntryPublished(final TestIdentifier testIdentifier, final ReportEntry entry) {
        Map<String, String> map = entry.getKeyValuePairs();

        CustomFormatTestCase c = new CustomFormatTestCase();
        if (map.containsKey(Tm4jExtension.TEST_CASE_KEY)) {
            c.setKey(map.get(Tm4jExtension.TEST_CASE_KEY));
        }

        testIdentifierToTestCaseMap.put(testIdentifier.getUniqueId(), c);
    }

    @Override
    public void testPlanExecutionFinished(final TestPlan testPlan) {
        try {
            generateCustomFormatFile(customFormatContainer);
        } catch (IOException e) {
            LOG.error("Failed to generate tm4j log", e);
        }
    }

    @Override
    public void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest() && hasTm4jTestCase(testIdentifier)) {
            switch (testExecutionResult.getStatus()) {
            case SUCCESSFUL:
                CustomFormatExecution passedExecution = new CustomFormatExecution();
                passedExecution.setResult(CustomFormatConstants.PASSED);
                passedExecution.setSource(testIdentifier.getDisplayName());
                passedExecution.setTestCase(testIdentifierToTestCaseMap.get(testIdentifier.getUniqueId()));
                customFormatContainer.addResult(passedExecution);
                break;
            case ABORTED:
            case FAILED:
                CustomFormatExecution failedExecution = new CustomFormatExecution();
                failedExecution.setResult(CustomFormatConstants.FAILED);
                failedExecution.setSource(testIdentifier.getDisplayName());
                failedExecution.setTestCase(testIdentifierToTestCaseMap.get(testIdentifier.getUniqueId()));
                customFormatContainer.addResult(failedExecution);
                break;
            default:
                throw new PreconditionViolationException(
                        "Unsupported execution status:" + testExecutionResult.getStatus());
            }
        }
    }

    private boolean hasTm4jTestCase(final TestIdentifier testIdentifier) {
        return testIdentifierToTestCaseMap.containsKey(testIdentifier.getUniqueId());
    }

}
