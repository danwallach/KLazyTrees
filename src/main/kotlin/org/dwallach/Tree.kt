package org.dwallach

import java.util.*
import kotlinx.coroutines.*

/**
 * Super-simple unbalanced binary tree, generic over anything that's Comparable.
 */
interface Tree<T: Comparable<T>> {
    fun empty(): Boolean

    fun insert(newbie: T): Tree<T>

    fun find(query: T): T? =
      if(empty()) null else {
          val nodeValue = value()
          val comparison = query.compareTo(nodeValue)
          when {
              comparison == 0 -> nodeValue // no change, it's already there
              comparison < 0 -> left().find(query)
              else -> right().find(query)
          }
      }

    fun left(): Tree<T>

    fun right(): Tree<T>

    fun value(): T

    typealias nonEmptyFuncAlias<T,R> = (T, Tree<T>, Tree<T>) -> R

    /**
     * Structural pattern matching on a tree: calls one or the other lambda depending on whether the
     * tree node in question is a leaf or an internal node.
     */
    fun <R> match(emptyFunc: () -> R, nonEmptyFunc: nonEmptyFuncAlias<T,R>): R

    fun size(): Int = match({ 0 }, { value, left, right -> left.size() + right.size() + 1 })

    /**
     * Simple in-order tree traversal.
     */
    fun inorder(consumer: (T) -> Unit): Unit = match({}, { value, left, right ->
            left.inorder(consumer)
            consumer(value)
            right.inorder(consumer)
    })

    /**
     * Eager tree -> sorted list traversal. Relies on list concatenation, runs in time linear in the size of the tree.
     */
    fun toEagerList(): List<T> = match({ emptyList() }, { value, left, right -> left.toEagerList() + value + right.toEagerList() })

    /**
     * Lazy version using the shiny new coroutine generator.
     */
    fun toLazyList(): Sequence<T> = generate {
        if(!empty()) {
            left().toLazyList().forEach { yield(it) }
            yield(value())
            right().toLazyList().forEach { yield(it) }
        }
    }

    private data class NonEmptyTree<T: Comparable<T>>(val nodeValue: T, val treeLeft: Tree<T>, val treeRight: Tree<T>): Tree<T> {
        override fun <R> match(emptyFunc: () -> R, nonEmptyFunc: nonEmptyFuncAlias<T,R>): R = nonEmptyFunc(nodeValue, treeLeft, treeRight)

        override fun empty() = false

        override fun insert(newbie: T): Tree<T> {
            val comparison = newbie.compareTo(nodeValue)
            return when {
                comparison == 0 -> this // no change, it's already there
                comparison < 0 -> NonEmptyTree(nodeValue, treeLeft.insert(newbie), treeRight)
                else -> NonEmptyTree(nodeValue, treeLeft, treeRight.insert(newbie))
            }
        }

        override fun left() = treeLeft

        override fun right() = treeRight

        override fun value() = nodeValue
    }

    private object emptyTreeSingleton: Tree<Comparable<Any>> {
        // this works
        override fun <R> match(emptyFunc: () -> R, nonEmptyFunc: (Comparable<Any>, Tree<Comparable<Any>>, Tree<Comparable<Any>>) -> R): R = emptyFunc()

        // this says that nonEmptyFuncAlias requires three type parameters, which is incorrect
//        override fun <R> match(emptyFunc: () -> R, nonEmptyFunc: nonEmptyFuncAlias<Comparable<Any>, R>): R = emptyFunc()

        override fun insert(newbie: Comparable<Any>): Tree<Comparable<Any>> = NonEmptyTree(newbie, this, this)

        override fun left(): Tree<Comparable<Any>> {
            throw NoSuchElementException("can't take left() of an empty tree")
        }

        override fun right(): Tree<Comparable<Any>> {
            throw NoSuchElementException("can't take right() of an empty tree")
        }

        override fun value(): Comparable<Any> {
            throw NoSuchElementException("no value for an empty tree")
        }

        override fun empty() = true
    }

    companion object {
        fun <T: Comparable<T>> emptyTree(): Tree<T> {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val typedEmptyTree = emptyTreeSingleton as Tree<T>
            return typedEmptyTree
        }

        fun <T: Comparable<T>> of(vararg elements: T): Tree<T> =
                elements.fold(emptyTree()) { tree, t -> tree.insert(t) }
    }
}
