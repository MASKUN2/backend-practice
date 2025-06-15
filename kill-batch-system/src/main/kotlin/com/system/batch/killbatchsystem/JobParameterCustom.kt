package com.system.batch.killbatchsystem

import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@StepScope
data class JobParameterCustom(
    @Value("#{jobParameters[customName]}") val customName: String
)