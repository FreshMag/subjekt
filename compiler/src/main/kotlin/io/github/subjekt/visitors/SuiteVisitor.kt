package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.Context.Companion.emptyContext
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Outcome
import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.nodes.suite.Subject
import io.github.subjekt.nodes.suite.Suite
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.Permutations.permute

class SuiteVisitor(
  private val messageCollector: MessageCollector,
  modules: List<Any> = emptyList(),
) : SuiteIrVisitor<Unit> {

  private var context: Context = emptyContext()

  val resolvedSubjects = mutableSetOf<ResolvedSubject>()

  init {
    modules.forEach { module -> context.registerModule(module, messageCollector) }
  }

  override fun visitSuite(suite: Suite) {
    context.suiteName = suite.name
    context.configuration = suite.configuration
    suite.macros.forEach { mac -> visitMacro(mac) }
    val previousContext = context
    suite.parameters.permute { parConfiguration ->
      context = previousContext
      parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
      suite.subjects.forEach { sub -> visitSubject(sub) }
    }
    context = previousContext
  }

  override fun visitMacro(macro: Macro) {
    context.putMacro(macro)
  }

  override fun visitSubject(subject: Subject) {
    context.subjektName = subject.name.source
    val previousContext = context
    subject.macros.forEach { macro -> context.putMacro(macro) }
    subject.parameters.permute { parConfiguration ->
      context = previousContext
      parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
      val outcomes = subject.outcomes.map { it.toResolvedOutcome(context, messageCollector) }
      subject.code.resolve(context, messageCollector).forEach { code ->
        resolvedSubjects += ResolvedSubject(
          subject.name.resolveOne(context, messageCollector),
          code,
          outcomes,
        )
      }
    }
  }

  override fun visitOutcome(outcome: Outcome) {}

  override fun visitTemplate(template: Template) {}

  override fun visitParameter(parameter: Parameter) {}
}
