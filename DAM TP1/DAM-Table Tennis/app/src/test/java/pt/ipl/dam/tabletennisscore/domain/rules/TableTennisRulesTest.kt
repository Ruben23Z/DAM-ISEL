package pt.ipl.dam.tabletennisscore.domain.rules

import org.junit.Assert.*
import org.junit.Test

class TableTennisRulesTest {

    @Test
    fun `hasWonSet returns true when score reaches pointsPerSet with lead of 2`() {
        assertTrue(TableTennisRules.hasWonSet(11, 9, 11))
        assertTrue(TableTennisRules.hasWonSet(21, 19, 21))
    }

    @Test
    fun `hasWonSet returns false when score reaches pointsPerSet but lead is 1`() {
        assertFalse(TableTennisRules.hasWonSet(11, 10, 11))
        assertFalse(TableTennisRules.hasWonSet(21, 20, 21))
    }

    @Test
    fun `hasWonSet returns true in deuce when score is over pointsPerSet and lead is 2`() {
        assertTrue(TableTennisRules.hasWonSet(13, 11, 11))
        assertTrue(TableTennisRules.hasWonSet(25, 23, 21))
    }

    @Test
    fun `isDeuce returns true when both scores are AT LEAST pointsPerSet - 1`() {
        assertTrue(TableTennisRules.isDeuce(10, 10, 11))
        assertTrue(TableTennisRules.isDeuce(11, 11, 11))
        assertTrue(TableTennisRules.isDeuce(13, 13, 11))

        assertTrue(TableTennisRules.isDeuce(20, 20, 21))
        assertTrue(TableTennisRules.isDeuce(21, 21, 21))
    }

    @Test
    fun `isDeuce returns false when scores are low`() {
        assertFalse(TableTennisRules.isDeuce(9, 9, 11))
        assertFalse(TableTennisRules.isDeuce(10, 9, 11))
    }

    @Test
    fun `currentServer alternates every 2 points in normal play`() {
        // Player 0 serves points 0 and 1
        assertEquals(0, TableTennisRules.currentServer(0, false))
        assertEquals(0, TableTennisRules.currentServer(1, false))
        // Player 1 serves points 2 and 3
        assertEquals(1, TableTennisRules.currentServer(2, false))
        assertEquals(1, TableTennisRules.currentServer(3, false))
        // Player 0 serves points 4 and 5
        assertEquals(0, TableTennisRules.currentServer(4, false))
        assertEquals(0, TableTennisRules.currentServer(5, false))
    }

    @Test
    fun `currentServer alternates every 1 point in deuce`() {
        // At 10-10 (total 20 points), player 0 serves
        assertEquals(0, TableTennisRules.currentServer(20, true))
        // At 11-10 (total 21 points), player 1 serves
        assertEquals(1, TableTennisRules.currentServer(21, true))
        // At 11-11 (total 22 points), player 0 serves
        assertEquals(0, TableTennisRules.currentServer(22, true))
    }

    @Test
    fun `setsToWin test`() {
        assertEquals(2, TableTennisRules.setsToWin(3))
        assertEquals(3, TableTennisRules.setsToWin(5))
        assertEquals(4, TableTennisRules.setsToWin(7))
    }

    @Test
    fun `hasWonMatch test`() {
        assertTrue(TableTennisRules.hasWonMatch(3, 5))
        assertFalse(TableTennisRules.hasWonMatch(2, 5))
        assertTrue(TableTennisRules.hasWonMatch(4, 7))
    }
}
