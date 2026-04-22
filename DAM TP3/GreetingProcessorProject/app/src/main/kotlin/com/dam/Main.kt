import com.dam.DataProcessorExtractor

fun main() {
    val input = " Name : John Address : 123 Street "
// Using the generated DataProcessorExtractor
    val extractor = DataProcessorExtractor(input)
    println(" Name : ${extractor.getName()}")
    println(" Address : ${extractor.getAddress()}")
}