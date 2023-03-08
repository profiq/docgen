package com.profiq.codexor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.testFramework.LightPlatformTestCase;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;


public class GenerateDocumentationActionTest extends LightPlatformTestCase {

    @Mock
    private AnActionEvent mockEvent;
    @Mock
    private Editor mockEditor;
    @Mock
    private Document mockDocument;

    private GenerateDocumentationAction mockAction;
    private JavaCodeInsightTestFixture fixture;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.openMocks(this);

        fixture = JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture((IdeaProjectTestFixture) getProject());
        fixture.setUp();
        mockAction = new GenerateDocumentationAction();
    }

    @Test
    public void testActionPerformed() throws IOException {
        //when(mockEvent.getData(Key.create("editor"))).thenReturn(mockEditor);
        when(mockEditor.getDocument()).thenReturn(mockDocument);
        when(mockDocument.getText()).thenReturn("test code");
        when(mockAction.getApiKey()).thenReturn("testApiKey");
        when(mockAction.getLanguage(mockEvent)).thenReturn("Python");
        when(mockAction.getPrompt()).thenReturn("test prompt {{LANG}}");

        ProgressManager.getInstance().runProcess(() -> {
            mockAction.actionPerformed(mockEvent);
        }, new EmptyProgressIndicator());

        verify(mockAction, times(1)).getApiKey();
        verify(mockAction, times(1)).getLanguage(mockEvent);
        verify(mockAction, times(1)).getPrompt();
        verify(mockEditor, times(1)).getDocument();
        verify(mockDocument, times(1)).getText();
        verify(mockAction, times(1)).showError(anyString());
    }

    @Override
    protected void tearDown() throws Exception {
        fixture.tearDown();
        super.tearDown();
    }
}