package io.toolisticon.compiletesting;

import io.toolisticon.compiletesting.impl.CompileTestConfiguration;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class CompileTestBuilderTest {

    @Test
    public void test_UnitTest_successfullCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = JavaFileObjectUtils.readFromString("Jupp.txt", "TATA!");
        CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useProcessor(
                        new UnitTestProcessor() {
                            @Override
                            public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "MANDATORY_WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "WARNING");
                                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "NOTE");


                                try {
                                    FileObject fileObject = processingEnvironment.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "root", "Jupp.txt", typeElement);
                                    Writer writer = fileObject.openWriter();
                                    writer.write("TATA!");
                                    writer.close();


                                } catch (IOException e) {

                                }

                            }
                        })
                .addWarningChecks("WARNING")
                .addMandatoryWarningChecks("MANDATORY_WARNING")
                .addNoteChecks("NOTE")
                .compilationShouldSucceed()
                .testCompilation();


    }

    @Test
    public void test_UnitTest_failingCompilation_build() {

        JavaFileObject testSource = Mockito.mock(JavaFileObject.class);
        JavaFileObject expectedGeneratedSource = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useProcessor(new UnitTestProcessor() {
                    @Override
                    public void unitTest(ProcessingEnvironment processingEnvironment, TypeElement typeElement) {

                        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR");


                    }
                })
                .addErrorChecks("ERROR")
                .compilationShouldFail()
                .testCompilation();


    }


    @Test
    public void test_addWarningChecks() {

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .addWarningChecks("WARN1")
                ;

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1"));

        CompileTestBuilder.CompileTimeTestBuilder builder2 = builder
                .addWarningChecks("WARN2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1", "WARN2"));

        CompileTestBuilder.CompileTimeTestBuilder builder3 = builder2
                .addWarningChecks()
                .addWarningChecks(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getWarningMessageCheck(), Matchers.containsInAnyOrder("WARN1", "WARN2"));


    }

    public void test_addMandatoryWarningChecks() {

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .addMandatoryWarningChecks("MWARN1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1"));

        CompileTestBuilder.CompileTimeTestBuilder builder2 = builder
                .addMandatoryWarningChecks("MWARN2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1", "MWARN2"));

        CompileTestBuilder.CompileTimeTestBuilder builder3 = builder2
                .addMandatoryWarningChecks()
                .addMandatoryWarningChecks(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getMandatoryWarningMessageCheck(), Matchers.containsInAnyOrder("MWARN1", "MWARN2"));


    }

    public void test_addNoteChecks() {

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .addNoteChecks("NOTE1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1"));

        CompileTestBuilder.CompileTimeTestBuilder builder2 = builder
                .addNoteChecks("NOTE2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1", "NOTE2"));

        CompileTestBuilder.CompileTimeTestBuilder builder3 = builder2
                .addNoteChecks()
                .addNoteChecks(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getNoteMessageCheck(), Matchers.containsInAnyOrder("NOTE1", "NOTE2"));


    }

    public void test_addErrorChecks() {

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .addErrorChecks("ERROR1");

        MatcherAssert.assertThat(builder.createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1"));

        CompileTestBuilder.CompileTimeTestBuilder builder2 = builder
                .addErrorChecks("ERROR2");

        MatcherAssert.assertThat(builder2
                .createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1", "ERROR2"));

        CompileTestBuilder.CompileTimeTestBuilder builder3 = builder2
                .addErrorChecks()
                .addErrorChecks(null);

        MatcherAssert.assertThat(builder3
                .createCompileTestConfiguration()
                .getErrorMessageCheck(), Matchers.containsInAnyOrder("ERROR1", "ERROR2"));


    }

    @Test
    public void test_compilationShouldSucceeed() {

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest();

        MatcherAssert.assertThat(builder.compilationShouldSucceed().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.TRUE));
        MatcherAssert.assertThat(builder.compilationShouldFail().createCompileTestConfiguration().getCompilationShouldSucceed(), Matchers.is(Boolean.FALSE));


    }

    @Test
    public void test_addSource() {

        JavaFileObject testSource1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject testSource2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .addSources(testSource1)
                .addSources(testSource2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.containsInAnyOrder(testSource1, testSource2));


    }


    @Test
    public void test_useProcessors() {

        Processor testProcessor1 = Mockito.mock(Processor.class);
        Processor testProcessor2 = Mockito.mock(Processor.class);

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .useProcessors(testProcessor1)
                .useProcessors(testProcessor2)
                .useProcessors(null)
                .useProcessors();


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessors(), Matchers.containsInAnyOrder(testProcessor1, testProcessor2));


    }

    @Test
    public void test_useProcessorAndExpectException() {

        Processor testProcessor1 = Mockito.mock(Processor.class);
        Processor testProcessor2 = Mockito.mock(Processor.class);

        CompileTestBuilder.CompileTimeTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .useProcessorAndExpectException(testProcessor1, IllegalArgumentException.class)
                .useProcessorAndExpectException(testProcessor2, IllegalStateException.class);


        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getProcessorsWithExpectedExceptions(), Matchers.<CompileTestConfiguration.ProcessorWithExpectedException>hasSize(2));


    }

    @Test
    public void test_useSource_addSingleSource() {

        JavaFileObject javaFileObject = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useSource(javaFileObject);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject));

    }

    @Test
    public void test_useSource_addSourceTwice_onlySecondSourceShouldBeUsed() {

        JavaFileObject javaFileObject1 = Mockito.mock(JavaFileObject.class);
        JavaFileObject javaFileObject2 = Mockito.mock(JavaFileObject.class);

        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useSource(javaFileObject1)
                .useSource(javaFileObject2);

        MatcherAssert.assertThat(builder.createCompileTestConfiguration().getSourceFiles(), Matchers.contains(javaFileObject2));

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useSource_addNullValuedSource() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useSource(null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useProcessor((Processor) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_useProcessor_addNullValuedUnitTestProcessor() {


        CompileTestBuilder.UnitTestBuilder builder = CompileTestBuilder.createCompileTestBuilder()
                .unitTest()
                .useProcessor((UnitTestProcessor) null);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedProcessor() {

        CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .useProcessorAndExpectException(null, IllegalStateException.class);


    }

    @Test(expected = IllegalArgumentException.class)
    public void test_CompileTimeTestBuilder_useProcessorAndExpectException_addNullValuedException() {

        CompileTestBuilder.createCompileTestBuilder()
                .compilationTest()
                .useProcessorAndExpectException(new AbstractProcessor() {
                    @Override
                    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                        return false;
                    }
                }, null);


    }


}
