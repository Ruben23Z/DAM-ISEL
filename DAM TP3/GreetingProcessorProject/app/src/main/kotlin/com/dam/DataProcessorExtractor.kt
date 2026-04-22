package com.dam

import kotlin.String
import kotlin.text.Regex

public class DataProcessorExtractor(
    input: String,
) : DataProcessor(input) {
    override fun getName(): String? {
        val match = Regex(" Name : (\\w+)").find(input)
        return match?.groupValues?.get(1)
    }

    override fun getAddress(): String? {
        val match = Regex(" Address : (.+)").find(input)
        return match?.groupValues?.get(1)
    }
}