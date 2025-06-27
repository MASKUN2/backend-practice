package tobyspring.splearn.domain

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import tobyspring.splearn.application.required.MemberRepository

@Component
class TestRunner(
    val memberRepository: MemberRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("run!!")
        val member = memberRepository.save(Member.of("<EMAIL>", "Tomas"))
        println("member: $member")
        println("member id: ${member.id}")
    }
}
