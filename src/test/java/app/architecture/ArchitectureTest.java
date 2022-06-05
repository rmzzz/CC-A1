package app.architecture;


import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "app")
public class ArchitectureTest {
  @ArchTest
  ArchRule noJavaUtilLogging = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

  @ArchTest
  ArchRule domainDependencies = noClasses().that().resideInAPackage("app.domain")
          .should().dependOnClassesThat().resideInAPackage("app.service");
}
