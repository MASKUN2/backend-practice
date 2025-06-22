plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("org.springframework.boot") version "3.4.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.0.21" // https://kotlinlang.org/docs/no-arg-plugin.html

    /**
     * ./gradlew detekt: 코드 분석 후 리포트 생성
     * ./gradlew detektGenerateConfig: 커스터마이징할 수 있는 detekt.yml 설정 파일 생성
     * detekt가 ktlint 포맷팅 규칙을 포함하므로, detekt --auto-correct 옵션을 통해 스타일 문제 자동 수정 가능
     */
    id("io.gitlab.arturbosch.detekt") version "1.23.8" // 정적 분석 도구 (The Brain)
}

group = "tobyspring"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8") // detekt가 ktlint의 포맷팅 규칙을 사용하도록 의존성 추가
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// detekt 플러그인에 대한 상세 설정
detekt {
    buildUponDefaultConfig = true // 기본 설정 위에 커스텀 설정을 덧붙여 사용
    config.setFrom(files("$rootDir/config/detekt/detekt.yml")) // 커스텀 설정 파일 경로 (detektGenerateConfig 태스크로 생성 가능)
    source.setFrom(files("src/main/kotlin", "src/test/kotlin")) // 분석할 소스 코드 경로 설정
}


// (선택) check 태스크가 detekt를 실행하도록 설정
tasks.check { dependsOn(tasks.detekt) }