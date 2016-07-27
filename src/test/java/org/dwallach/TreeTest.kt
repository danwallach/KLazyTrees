package org.dwallach

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TreeTest {
    @Test fun emptyTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()

        assertTrue(emptyTree.empty())
    }

    @Test fun insertTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()
        val oneElemTree: Tree<String> = emptyTree.insert("Hello")

        assertFalse(oneElemTree.empty())
        assertTrue(oneElemTree.left().empty())
        assertTrue(oneElemTree.right().empty())
    }

    @Test fun listTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")
        val seq: Sequence<String> = sequenceOf("Alice", "Bob", "Charlie", "Dorothy", "Eve")

        assertEquals(seq.joinToString(","), tree.toEagerSequence().joinToString(","))
    }

    @Test fun findTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")
        val seq: Sequence<String> = sequenceOf("Alice", "Bob", "Charlie", "Dorothy", "Eve")

        seq.forEach {
            assertEquals(it, tree.find(it))
        }

        seq.map(String::toUpperCase).forEach {
            assertEquals(null, tree.find(it))
        }
    }

    @Test fun coroutineTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")
        val seq: Sequence<String> = sequenceOf("Alice", "Bob", "Charlie", "Dorothy", "Eve")

        assertEquals(seq.joinToString(","), tree.toEagerSequence().joinToString(","))
        assertEquals(seq.joinToString(","), tree.toLazySequence().joinToString(","))
    }

    @Test fun inorderTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")
        var counter: Int = 0
        var map: Map<Int, String> = mapOf()

        tree.inorder {
            map = map + (counter to it)
            counter++
        }


        val seq: Sequence<String> = sequenceOf("Alice", "Bob", "Charlie", "Dorothy", "Eve")
        val integers: Sequence<Int> = sequenceOf(0, 1, 2, 3, 4)

        assertEquals(seq.zip(integers) { str, int -> str to int }.joinToString(","),
                map.asSequence().map { it.value to it.key }.joinToString(","))
    }
}