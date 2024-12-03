package io.github.subjekt.nodes.suite

import io.github.subjekt.yaml.Configuration

data class Suite(
  val name: String,
  val subjects: List<Subject>,
  val configuration: Configuration = Configuration(),
  val macros: List<Macro> = emptyList(),
  val parameters: List<Parameter> = emptyList(),
) {

  companion object {
    fun fromYamlSuite(yamlSuite: io.github.subjekt.yaml.Suite): Suite {
      val config = yamlSuite.config ?: Configuration()
      return Suite(
        yamlSuite.name,
        yamlSuite.subjects.map { Subject.fromYamlSubject(it, config) },
        config,
        yamlSuite.macros?.map { Macro.fromYamlMacro(it, config) } ?: emptyList(),
        yamlSuite.parameters?.map { Parameter.fromYamlParameter(it) } ?: emptyList(),
      )
    }
  }
}
