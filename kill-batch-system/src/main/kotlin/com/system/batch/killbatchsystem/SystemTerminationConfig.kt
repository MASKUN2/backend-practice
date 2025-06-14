// Powered by KILL-9 with love 💀
package com.system.batch.killbatchsystem

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus.CONTINUABLE
import org.springframework.batch.repeat.RepeatStatus.FINISHED
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.atomic.AtomicInteger

@Configuration
class SystemTerminationConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {
    private val processesKilled: AtomicInteger = AtomicInteger(0)
    private val requiredProcessKills = 5
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun systemTerminationSimulation(
        enterWorldStep: Step,
        meetNPCStep: Step,
        defeatProcessStep: Step,
        completeQuestStep: Step,
    ): Job {
        return JobBuilder("systemTerminationSimulationJob", jobRepository)
            .start(enterWorldStep)
            .next(meetNPCStep)
            .next(defeatProcessStep)
            .next(completeQuestStep)
            .build()
    }

    @Bean
    fun enterWorldStep(): Step {
        return StepBuilder("enterWorldStep", jobRepository)
            .tasklet(
                { contribution, chunkContext ->
                    log.info("System Termination 시뮬레이션 세계에 접속했습니다!")
                    FINISHED
                },
                transactionManager
            ).build()
    }

    @Bean
    fun meetNPCStep(): Step {
        return StepBuilder("meetNPCStep", jobRepository)
            .tasklet(
                { contribution, chunkContext ->
                    log.info("시스템 관리자 NPC를 만났습니다.")
                    log.info("첫 번째 미션: 좀비 프로세스(참조가 사라지지 않은 메모리 유출 프로세스) " + requiredProcessKills + "개 처형하기")
                    FINISHED
                },
                transactionManager
            ).build()
    }

    @Bean
    fun defeatProcessStep(): Step {
        return StepBuilder("defeatProcessStep", jobRepository)
            .tasklet(
                { contribution, chunkContext ->
                    val killCount: Int = processesKilled.incrementAndGet()
                    log.info("좀비 프로세스 처형 완료! (현재 $killCount/$requiredProcessKills)")
                    if (killCount < requiredProcessKills) CONTINUABLE else FINISHED
                },
                transactionManager
            ).build()
    }

    @Bean
    fun completeQuestStep(): Step {
        return StepBuilder("completeQuestStep", jobRepository)
            .tasklet(
                { contribution, chunkContext ->
                    log.info("미션 완료! 좀비 프로세스 " + requiredProcessKills + "개 처형 성공!")
                    log.info("보상: kill -9 권한 획득, 시스템 제어 레벨 1 달성")
                    FINISHED
                },
                transactionManager
            ).build()
    }
}