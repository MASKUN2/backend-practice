package com.example.rediswaitingqueue

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@Configuration
// "default" 프로파일이거나, 활성 프로파일이 없을 때 (그리고 "test" 프로파일이 아닐 때)
@Profile("default", "!test")
class TestContainersConfig {

    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var redisContainer: GenericContainer<*>

    // Testcontainers는 싱글톤으로 관리되어야 하며, 여러 컨텍스트에서 재사용될 수 있도록 static으로 선언하거나
    // Spring 컨텍스트 라이프사이클에 맞추는 것이 좋습니다.
    // 여기서는 Spring Bean 라이프사이클(@PostConstruct, @PreDestroy)을 따릅니다.
    companion object {
        // 애플리케이션 재시작 시 컨테이너 재사용을 위해 static으로 선언할 수 있으나,
        // Spring의 @Profile과 함께 사용할 때는 Bean 라이프사이클에 맞추는 것이 더 일반적입니다.
        // 좀 더 강력한 재사용을 원한다면 Testcontainers의 Ryuk (resource reaper) 비활성화나
        // ~/.testcontainers.properties 파일 설정을 고려할 수 있습니다.
        // 여기서는 withReuse(true)를 사용합니다.
        private val REDIS_IMAGE: DockerImageName = DockerImageName.parse("redis:7.2-alpine")
        private const val REDIS_PORT = 6379
    }

    @PostConstruct
    fun startRedisContainer() {
        // Testcontainers의 withReuse(true) 옵션은 동일 설정의 컨테이너가 이미 실행 중이면 재사용합니다.
        redisContainer = GenericContainer<Nothing>(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT)

        try {
            redisContainer.start()
            logger.info("TestContainers Redis started for default profile on host: {}, port: {}",
                redisContainer.host, redisContainer.getMappedPort(REDIS_PORT))

            // 생성된 호스트와 포트를 시스템 프로퍼티에 설정하여 Spring Boot가 자동으로 인식하도록 함
            System.setProperty("spring.data.redis.host", redisContainer.host)
            System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())

        } catch (e: Exception) {
            logger.error("Could not start TestContainers Redis. Check Docker environment.", e)
            // Docker가 실행되지 않았거나 문제가 있을 경우 애플리케이션 시작에 실패할 수 있습니다.
            // 이 경우, application.properties에 정의된 기본 Redis 설정을 사용하도록 fallback 로직을 추가할 수 있습니다.
            // (단, 여기서는 Testcontainers 사용이 주목적이므로 실패 시 에러를 명확히 합니다.)
            throw IllegalStateException("Failed to start TestContainers Redis", e)
        }
    }

    // Testcontainers가 시작된 후, Spring Boot의 RedisProperties를 참조하여
    // LettuceConnectionFactory를 생성합니다.
    // 이 Bean은 Testcontainers에 의해 설정된 시스템 프로퍼티를 사용합니다.
    @Bean
    fun lettuceConnectionFactory(redisProperties: RedisProperties): LettuceConnectionFactory {
        logger.info("Configuring LettuceConnectionFactory with properties: host={}, port={}",
            redisProperties.host, redisProperties.port)

        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        redisProperties.password?.let { redisStandaloneConfiguration.setPassword(it) }
        redisStandaloneConfiguration.database = redisProperties.database

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }


    @PreDestroy
    fun stopRedisContainer() {
        redisContainer.stop()
        logger.info("TestContainers Redis stopped")
    }
}