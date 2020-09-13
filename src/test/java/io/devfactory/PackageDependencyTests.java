package io.devfactory;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = StudyApplication.class)
public class PackageDependencyTests {

  public static final String STUDY = "..study..";
  public static final String EVENT = "..event..";
  public static final String ACCOUNT = "..account..";
  public static final String TAG = "..tag..";
  public static final String ZONE = "..zone..";

  @ArchTest
  ArchRule modulesPackageRule = classes().that().resideInAPackage("io.devfactory..")
      .should().onlyBeAccessed().byClassesThat()
      .resideInAnyPackage("io.devfactory..");

  @ArchTest
  ArchRule studyPackageRule = classes().that().resideInAPackage("..study..")
      .should().onlyBeAccessed().byClassesThat()
      .resideInAnyPackage(STUDY, EVENT);

  @ArchTest
  ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
      .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

  @ArchTest
  ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
      .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

  // TODO: globals 패키지 제거 및 되도록 수정 
  @ArchTest
  ArchRule cycleCheck = slices().matching("io.devfactory.(*)..")
      .should().beFreeOfCycles();

}
