package com.exemple.app

import com.dam.DataProcessor
import com.dam.DataProcessorExtractor


fun main() {
    val myClass = MyClass()
    val wrappedMyClass = MyClassWrapper(myClass)
    wrappedMyClass.sayHello()
    wrappedMyClass.compute()



    println("\n-------------------------------")
        val input = "Name : John Address : 123 Street "
// Using the generated DataProcessorExtractor
        val extractor = DataProcessorExtractor(input)
        println("Name: ${extractor.getName()}")
        println("Address: ${extractor.getAddress()}")



//            ./gradlew clean
//            ./gradlew build
}


//1º define a classe
//depois o greeting para a print e chama o metodo da classe original

//
//public final class MyClassWrapper(
//    public val original: MyClass,
//) {
//    public final fun sayHello() {
//        println(" Hello from MyClass !")
//        original.sayHello()
//    }
//
//    public final fun compute() {
//        println(" Welcome to the compute function !")
//        original.compute()
//    }
//}
