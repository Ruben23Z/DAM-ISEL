package damA51388.core.storage

import android.content.SharedPreferences
import damA51388.core.model.ImageItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class FavoritesManagerTest {

    @Mock
    private lateinit var mockPrefs: SharedPreferences
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var favoritesManager: FavoritesManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        
        // Initial load: empty
        `when`(mockPrefs.getString("favorites_list", null)).thenReturn(null)
        
        favoritesManager = FavoritesManager(mockPrefs)
    }

    @Test
    fun `toggleFavorite should add item if not present`() {
        val item = ImageItem(id = "1", url = "url1", breed = "labrador")
        
        val result = favoritesManager.toggleFavorite(item)
        
        assertTrue(result)
        assertTrue(favoritesManager.isFavorite("1"))
        assertEquals(1, favoritesManager.getFavorites().size)
        verify(mockEditor).putString(eq("favorites_list"), anyString())
    }

    @Test
    fun `toggleFavorite should remove item if already present`() {
        val item = ImageItem(id = "1", url = "url1", breed = "labrador")
        favoritesManager.toggleFavorite(item) // Add first
        
        val result = favoritesManager.toggleFavorite(item) // Toggle again
        
        assertFalse(result)
        assertFalse(favoritesManager.isFavorite("1"))
        assertEquals(0, favoritesManager.getFavorites().size)
    }

    @Test
    fun `FIFO logic should remove oldest item when exceeding 5`() {
        // Add 5 items
        for (i in 1..5) {
            favoritesManager.toggleFavorite(ImageItem(id = "$i", url = "url$i", breed = "breed$i"))
        }
        
        assertEquals(5, favoritesManager.getFavorites().size)
        assertTrue(favoritesManager.isFavorite("1")) // Oldest
        
        // Add 6th item
        val item6 = ImageItem(id = "6", url = "url6", breed = "breed6")
        favoritesManager.toggleFavorite(item6)
        
        assertEquals(5, favoritesManager.getFavorites().size)
        assertFalse(favoritesManager.isFavorite("1")) // "1" should be removed (FIFO)
        assertTrue(favoritesManager.isFavorite("6"))  // "6" should be present
    }
}
