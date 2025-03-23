plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.github.ajharry69"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val jaxwsSourceDir = "${layout.buildDirectory.get().asFile.absolutePath}/generated/sources/jaxws"

configurations {
    create("jaxws")
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

tasks.register("wsimport") {
    description = "Generate classes from wsdl using wsimport"

    doLast {
        project.mkdir(jaxwsSourceDir)
        ant {
            invokeMethod(
                "taskdef",
                mapOf(
                    "name" to "wsimport",
                    "classname" to "com.sun.tools.ws.ant.WsImport",
                    "classpath" to configurations.getByName("jaxws").asPath,
                ),
            )
            val args = mapOf(
                "keep" to true,
                "destdir" to jaxwsSourceDir,
                "extension" to "true",
                "verbose" to true,
                "xnocompile" to true,
            )
            invokeMethod(
                "wsimport",
                args + mapOf(
                    "wsdl" to "https://kycapitest.credable.io/service/customerWsdl.wsdl",
                    "package" to "com.github.ajharry69.lms.services.customer.integration.wsdl",
                ),
            )
            invokeMethod(
                "wsimport",
                args + mapOf(
                    "wsdl" to "https://trxapitest.credable.io/service/transactionWsdl.wsdl",
                    "package" to "com.github.ajharry69.lms.services.loan.integration.transaction.wsdl",
                ),
            )
//            invokeMethod("xjcarg", mapOf("value" to "-XautoNameResolution"))
        }
    }
}

sourceSets {
    main {
        java.srcDirs(jaxwsSourceDir)
    }
}

tasks.named("compileJava", JavaCompile::class) {
    dependsOn("wsimport")
}

dependencies {
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    configurations.getByName("jaxws").dependencies.addAll(
        listOf(
            "com.sun.xml.ws:jaxws-tools:3.0.0",
            "jakarta.xml.ws:jakarta.xml.ws-api:3.0.0",
            "jakarta.xml.bind:jakarta.xml.bind-api:3.0.0",
            "jakarta.activation:jakarta.activation-api:2.0.0",
            "com.sun.xml.ws:jaxws-rt:3.0.0"
        ).map<String, Dependency?>(::implementation)
    )
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
