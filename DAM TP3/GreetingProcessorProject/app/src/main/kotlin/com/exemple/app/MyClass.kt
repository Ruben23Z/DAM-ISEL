package com.exemple.app
import annotations.Greeting



//PROF
//por defeito é tudo final
//open classe permite criar subclasses desta
open class MyClass {
    @Greeting(" Hello from MyClass !") //1º
    open fun sayHello() { //open permite o override destas funcoes
        println(" Executing sayHello method ") //2º
    }

    @Greeting(" Welcome to the compute function !") //3º
    open fun compute() {
        println(" Computing something important ...") //4º
    }
}

