package tobyspring.splearn.domain

import jakarta.persistence.Entity

@Entity
class Member private constructor(
    val email: String,
    var name: String,
) : BaseEntity() {

    companion object {
        fun of(email: String, name: String): Member = Member(email, name)
    }

    override fun toString(): String {
        return "${super.toString()}, Member(email='$email', name='$name')"
    }
}
