package tobyspring.splearn.application.required

import org.springframework.data.repository.Repository
import tobyspring.splearn.domain.Member
import java.util.UUID

interface MemberRepository : Repository<Member, UUID> {
    fun save(member: Member): Member
}
