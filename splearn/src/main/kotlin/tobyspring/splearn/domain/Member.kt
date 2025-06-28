package tobyspring.splearn.domain

import jakarta.persistence.Entity

@Entity
class Member private constructor(
    val email: String,
    name: String
) : BaseEntity() {
    var name: String = name
        protected set

    companion object {
        fun of(email: String, name: String): Member = Member(email, name)
    }

    override fun toString(): String {
        return "${super.toString()}, Member(email='$email', name='$name')"
    }
}
