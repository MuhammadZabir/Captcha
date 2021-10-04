package com.test.captcha;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.test.captcha.web.rest.ImageResource;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.test.captcha");

        noClasses()
            .that()
            .resideInAnyPackage("com.test.captcha.service..")
            .or()
            .resideInAnyPackage("com.test.captcha.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.test.captcha.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
