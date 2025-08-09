package de.md5lukas.configurate

import java.lang.reflect.Type
import org.spongepowered.configurate.objectmapping.meta.Constraint
import org.spongepowered.configurate.serialize.SerializationException

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Positive(val includeZero: Boolean = false) {
  object Factory : Constraint.Factory<Positive, Number?> {
    override fun make(
        data: Positive,
        type: Type,
    ): Constraint<Number?> {
      return Constraint<Number?> {
        if (it != null) {
          if (data.includeZero) {
            if (it.toDouble() < 0) {
              throw SerializationException("$it must be positive or zero")
            }
          } else {
            if (it.toDouble() <= 0) {
              throw SerializationException("$it must be positive")
            }
          }
        }
      }
    }
  }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Min(val min: Int) {
  object Factory : Constraint.Factory<Min, Number?> {
    override fun make(
        data: Min,
        type: Type,
    ): Constraint<Number?> {
      return Constraint<Number?> {
        if (it != null && it.toDouble() < data.min) {
          throw SerializationException("$it is less than the required minimum of ${data.min}")
        }
      }
    }
  }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Max(val max: Int) {
  object Factory : Constraint.Factory<Max, Number?> {
    override fun make(
        data: Max,
        type: Type,
    ): Constraint<Number?> {
      return Constraint<Number?> {
        if (it != null && it.toDouble() > data.max) {
          throw SerializationException("$it is more than the required maximum of ${data.max}")
        }
      }
    }
  }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NonEmptyString {
  object Factory : Constraint.Factory<NonEmptyString, String?> {
    override fun make(
        data: NonEmptyString,
        type: Type,
    ): Constraint<String?> {
      return Constraint<String?> {
        if (it.isNullOrEmpty()) {
          throw SerializationException("The value is empty or not present")
        }
      }
    }
  }
}
