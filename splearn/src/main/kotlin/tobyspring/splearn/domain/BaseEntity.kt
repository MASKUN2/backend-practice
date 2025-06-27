package tobyspring.splearn.domain

import com.fasterxml.uuid.Generators
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PostLoad
import jakarta.persistence.PrePersist
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.Persistable
import java.util.UUID

@MappedSuperclass
abstract class BaseEntity : Persistable<UUID> {

    @Id
    @JvmField
    final val id: UUID = Generators.timeBasedEpochRandomGenerator().generate()

    @Transient
    private var _isNew: Boolean = true

    @PrePersist
    fun prePersist() {
        _isNew = false
    }

    @PostLoad
    fun preLoad() {
        _isNew = false
    }

    override fun getId(): UUID = id

    override fun isNew(): Boolean = _isNew

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as BaseEntity

        return id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "BaseEntity(id=$id, _isNew=$_isNew)"
    }
}
