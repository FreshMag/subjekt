package io.github.subjekt.utils

import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.resolved.ResolvedParameter

/**
 * Utility object for generating permutations.
 */
object Permutations {

  /**
   * Generates all possible permutations of the given [List] of [Parameter]s and calls the [parameterConfigurationConsumer]
   * for each permutation.
   */
  fun List<Parameter>.permute(parameterConfigurationConsumer: (List<ResolvedParameter>) -> Unit) {
    val cartesianProduct = this.map { it.values }.fold(sequenceOf(emptyList<Any>())) { acc, values ->
      acc.flatMap { combination ->
        values.asSequence().map { combination + it }
      }
    }

    cartesianProduct.map { combination ->
      combination.mapIndexed { index, value ->
        ResolvedParameter(this[index].name, value)
      }
    }.forEach {
      parameterConfigurationConsumer(it)
    }
  }

  /**
   * Generates all possible permutations of the given [List] of [List]s and returns them.
   * For example: `[["a", "b"], ["1", "2"]]` will return `[["a", "1"], ["a", "2"], ["b", "1"], ["b", "2"]]`.
   */
  fun <T> Iterable<Iterable<T>>.permute(): Iterable<Iterable<T>> = fold(listOf(emptyList<T>()) as Iterable<List<T>>) { acc, iterable ->
    acc.flatMap { combination ->
      iterable.map { element ->
        combination + element
      }
    }
  }
}
