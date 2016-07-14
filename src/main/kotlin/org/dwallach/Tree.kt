package org.dwallach

import java.util.*

/**
 * Super-simple unbalanced binary tree, generic over anything that's Comparable.
 */
interface Tree<T: Comparable<T>> {
    fun empty(): Boolean

    fun insert(newbie: T): Tree<T>

    fun find(query: T): T?

    fun left(): Tree<T>

    fun right(): Tree<T>

    fun value(): T

    fun <R> match(emptyFunc: () -> R, nonEmptyFunc: (value: T, left: Tree<T>, right: Tree<T>) -> R): R

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

    /*
    fun toLazyList() = generate<T> {
        match({}, { value, left, right ->
            left.toLazyList()
            yield(value)
            right.toLazyList()
        })
    }
    */

    private class NonEmptyTree<T: Comparable<T>>(val nodeValue: T, val treeLeft: Tree<T>, val treeRight: Tree<T>): Tree<T> {
        override fun empty() = false

        override fun insert(newbie: T): Tree<T> {
            val comparison = newbie.compareTo(nodeValue)
            return when {
                comparison == 0 -> this // no change, it's already there
                comparison < 0 -> NonEmptyTree(nodeValue, treeLeft.insert(newbie), treeRight)
                else -> NonEmptyTree(nodeValue, treeLeft, treeRight.insert(newbie))
            }
        }

        override fun find(query: T): T? {
            val comparison = query.compareTo(nodeValue)
            return when {
                comparison == 0 -> nodeValue // no change, it's already there
                comparison < 0 -> treeLeft.find(query)
                else -> treeRight.find(query)
            }
        }

        override fun left() = treeLeft

        override fun right() = treeRight

        override fun value() = nodeValue

        override fun <R> match(emptyFunc: () -> R, nonEmptyFunc: (value: T, left: Tree<T>, right: Tree<T>) -> R) =
                nonEmptyFunc(nodeValue, treeLeft, treeRight)
    }

    private object emptyTreeSingleton: Tree<Comparable<Any>> {
        override fun insert(newbie: Comparable<Any>): Tree<Comparable<Any>> = NonEmptyTree(newbie, this, this)

        override fun find(query: Comparable<Any>): Comparable<Any>? = null

        override fun <R> match(emptyFunc: () -> R,
                               nonEmptyFunc: (Comparable<Any>, Tree<Comparable<Any>>, Tree<Comparable<Any>>) -> R): R = emptyFunc()

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
        fun <T : Comparable<T>> emptyTree(): Tree<T> {
            @Suppress("CAST_NEVER_SUCCEEDS")
            val typedEmptyTree = emptyTreeSingleton as Tree<T>
            return typedEmptyTree
        }
    }
}
