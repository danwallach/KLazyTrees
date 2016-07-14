package org.dwallach

import kotlin.test.*
import org.junit.Test

class TreeTest {
    @Test fun emptyTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()

        assertTrue(emptyTree.empty())
    }

    @Test fun insertTest(): Unit {
        val emptyTree: Tree<String> = Tree.emptyTree()
        val oneElemTree: Tree<String> = emptyTree.insert("Hello")

        assertFalse(oneElemTree.empty())
    }

    @Test fun containsTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")

        assertTrue(tree.find("Alice") != null)
        assertTrue(tree.find("Charlie") != null)
        assertTrue(tree.find("Eve") != null)
        assertFalse(tree.find("ZZZ") != null)
    }

    @Test fun listTest(): Unit {
        val tree: Tree<String> = Tree.of("Charlie", "Dorothy", "Bob", "Alice", "Eve")
        val list: List<String> = listOf("Alice", "Bob", "Charlie", "Dorothy", "Eve")

        assertEquals(list, tree.toEagerList())
    }
}