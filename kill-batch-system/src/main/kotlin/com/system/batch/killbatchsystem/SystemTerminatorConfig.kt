//Copied by KILL9 with love ☠️
// ./gradlew bootRun --info --args='spring.batch.job.name=processTerminatorJob string=text int=1 double=1.0 localDate=2025-01-01 localTime=12:55 localDateTime=2025-01-01T13:20 repeatStatus=FINISHED customName=ATTA '

package com.system.batch.killbatchsystem

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.converter.JsonJobParametersConverter
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Configuration
class SystemTerminatorConfig{
    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun processTerminatorJob(jobRepository: JobRepository, terminationStep: Step): Job {
        return JobBuilder("processTerminatorJob", jobRepository)
            .start(terminationStep)
            .build()
    }

    @Bean
    fun jsonJobParameterConvertor(): JsonJobParametersConverter {
        return JsonJobParametersConverter()
    }

    @Bean
    fun terminationStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        terminatorTasklet: Tasklet
    ): Step {
        return StepBuilder("terminationStep", jobRepository)
            .tasklet(terminatorTasklet, transactionManager)
            .build()
    }

    @Bean
    @StepScope
    fun terminatorTasklet(
        @Value("#{jobParameters['string']}") string: String,
        @Value("#{jobParameters['int']}") int: Int,
        @Value("#{jobParameters['double']}") double: Double,
        @Value("#{jobParameters['localDate']}") localDate: LocalDate,
        @Value("#{jobParameters['localTime']}") localTime: LocalTime,
        @Value("#{jobParameters['localDateTime']}") localDateTime: LocalDateTime,
        @Value("#{jobParameters['repeatStatus']}") repeatStatus: RepeatStatus,
        jobParameterCustom: JobParameterCustom,
    ): Tasklet {
        return Tasklet { contribution, chunkContext ->
            log.info("String: $string")
            log.info("Int: $int")
            log.info("Double: $double")
            log.info("LocalDate: $localDate")
            log.info("LocalTime: $localTime")
            log.info("LocalDateTime: $localDateTime")
            log.info("RepeatStatus: $repeatStatus")
            log.info("JobParameterCustom: $jobParameterCustom")
            RepeatStatus.FINISHED
        }
    }
}