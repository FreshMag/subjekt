package io.github.subjekt.nodes.suite

import io.github.subjekt.nodes.Context
import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.resolved.ResolvedOutcome

sealed class Outcome(open val message: Resolvable) {
  data class Warning(override val message: Resolvable) : Outcome(message)
  data class Error(override val message: Resolvable) : Outcome(message)

  fun toResolvedOutcome(context: Context): ResolvedOutcome =
    when (this) {
      is Warning -> ResolvedOutcome.Warning(message.resolveOne(context))
      is Error -> ResolvedOutcome.Error(message.resolveOne(context))
    }


  companion object {
    fun fromYamlOutcome(yamlOutcome: io.github.subjekt.yaml.Outcome): Outcome {
      return if (yamlOutcome.warning != null) {
        Warning(Template.parse(yamlOutcome.warning))
      } else if (yamlOutcome.error != null) {
        Error(Template.parse(yamlOutcome.error))
      } else {
        throw IllegalArgumentException("Illegal outcome definition. Expected 'warning' or 'error' in $yamlOutcome")
      }
    }
  }
}