package dam51388.gminieapistarter

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class HistoryHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gemini_sweet_history", Context.MODE_PRIVATE)

    fun getHistory(): List<HistoryItem> {
        val list = mutableListOf<HistoryItem>()
        val jsonStr = prefs.getString("history_items", "[]") ?: "[]"
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    HistoryItem(
                        id = obj.optString("id", ""),
                        timestamp = obj.optString("timestamp", ""),
                        imageName = obj.optString("imageName", ""),
                        customImageUriString = if (obj.isNull("customImageUriString")) null else obj.getString("customImageUriString"),
                        prompt = obj.optString("prompt", ""),
                        response = obj.optString("response", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.reversed() // Show latest first
    }

    fun saveHistoryItem(item: HistoryItem) {
        // Read history in original insertion order (oldest to newest)
        val currentList = getHistory().reversed().toMutableList()
        currentList.add(item)
        
        // Limit to 10 entries to preserve space and memory
        if (currentList.size > 10) {
            currentList.removeAt(0)
        }

        val jsonArray = JSONArray()
        for (hist in currentList) {
            val obj = JSONObject()
            obj.put("id", hist.id)
            obj.put("timestamp", hist.timestamp)
            obj.put("imageName", hist.imageName)
            obj.put("customImageUriString", hist.customImageUriString)
            obj.put("prompt", hist.prompt)
            obj.put("response", hist.response)
            jsonArray.put(obj)
        }
        prefs.edit().putString("history_items", jsonArray.toString()).apply()
    }

    fun clearHistory() {
        prefs.edit().remove("history_items").apply()
    }
}
