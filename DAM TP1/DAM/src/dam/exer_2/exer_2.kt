package dam.exer_2


fun main() {
    while (true) {

        println("=== MENU DE OPERAÇÕES ===")
        println("1 - Operações aritméticas (+, -, *, /)")
        println("2 - Operadores booleanos (&&, ||, !)")
        println("3 - Operadores shift (shift esquerda, shift direita)")
        println("4 - Mostrar resultados (decimal, hexadecimal, boolean)")
        print("Escolha uma opção: ")

        val opcao = readln().toInt()
        if (opcao == 1) {
            try {
                println("indique a operação (+, -, *, /): ")
                val operacao = readln()
                print("Digite o primeiro número: ")
                val num1 = readln().toInt()
                print("Digite o segundo número: ")
                val num2 = readln().toInt()
                if (operacao == "+") {
                    println("Resultado: ${num1 + num2}")
                } else if (operacao == "-") {
                    println("Resultado: ${num1 - num2}")
                } else if (operacao == "*") {
                    println("Resultado: ${num1 * num2}")
                } else if (operacao == "/") {
                    println("Resultado: ${num1 / num2}")
                } else {
                    println("Operação inválida.")
                    break
                }
            } catch (e: Exception) {
                print("error: ${e.message}")
            }
        } else if (opcao == 2) {
            print("Digite o primeiro valor booleano (true/false): ")
            val bool1 = readln().toBoolean()
            print("Digite o segundo valor booleano (true/false): ")
            val bool2 = readln().toBoolean()

            println("indique a operação (&&, ||, !): ")
            val operacao = readln()
            if (operacao == "&&") {
                print("Resultado: ${bool1 && bool2}")
            } else if (operacao == "||") {
                print("Resultado: ${bool1 || bool2}")
            } else if (operacao == "!") {
                print("Resultado: ${!(bool1 && bool2)}")
            } else {
                println("Operação inválida.")
                break
            }

        } else if (opcao == 3) {
            println("Digite um número inteiro do shift: ")
            val valorShift = readln().toInt()
            println("indique a operação (shl,shr): ")
            val operacao = readln()

            print("Digite um número inteiro: ")
            val numero = readln().toInt()
            if (operacao == "shl") {
                print("Resultado: ${numero shl valorShift}")
            } else if (operacao == "shr") {
                print("Resultado: ${numero shr valorShift}")
            } else {
                println("Operação inválida.")
                break
            }
        } else if (opcao == 4) {
            print("Digite um número inteiro: ")
            val num = readln().toInt()
            println("Decimal: $num")
            println("Hexadecimal: ${num.toString(16)}")
            if (num == 0) println("Boolean: False")
            else println("Boolean: True")
        } else {
            println("Opção inválida.")
            break
        }

        print("--------------------------------------------------------------\n")
    }
}