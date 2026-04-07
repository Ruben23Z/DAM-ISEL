package Section1 // Define o pacote onde esta classe está localizada

// Define uma classe de dados Stage que representa um estágio no pipeline
// Cada estágio tem um nome e uma função de transformação que recebe uma lista de strings e retorna uma lista de strings
data class Stage(val name: String, val transform: (List<String>) -> List<String>)

// Classe Pipeline que gerencia uma sequência de estágios de transformação
class Pipeline {
    // Lista mutável privada que armazena os estágios do pipeline
    private val stages = mutableListOf<Stage>()

    // Função para adicionar um novo estágio ao pipeline
    // Recebe o nome do estágio e a função de transformação
    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(Stage(name, transform)) // Adiciona um novo Stage à lista de stages
    }

    // Função para executar o pipeline em uma lista de entrada
    // Recebe uma lista de strings e retorna uma lista de strings transformada
    fun execute(input: List<String>): List<String> {
        var output = input // Inicializa a saída com a entrada
        for (stage in stages) { // Itera sobre cada estágio no pipeline
            output = stage.transform(output) // Aplica a transformação do estágio atual à saída
        }
        return output // Retorna a saída final após todas as transformações
    }

    // Função para descrever os estágios do pipeline
    fun describe() {
        println("Pipeline stages:") // Imprime o cabeçalho
        stages.forEachIndexed { index, stage -> // Itera sobre os stages com índice
            println("${index + 1}. ${stage.name}") // Imprime o número e nome de cada estágio
        }
    }

    fun compose(nome1: String, nome2: String): Stage {
        val stage1 = stages.find {
            it.name == nome1
        }
        val stage2 = stages.find {
            it.name == nome2
        }
        if (stage1 == null || stage2 == null) throw IllegalArgumentException("Stages not found")
        val transform = { input: List<String> ->
            val output1 = stage1.transform(input)
            stage2.transform(output1)
        }
        val stage3 = Stage("${stage1.name} + ${stage2.name}", transform)
        val index = stages.indexOf(stage1)
        // Remover antigas
        stages.remove(stage1)
        stages.remove(stage2)
        stages.add(index, stage3)// Inserir na posição correta
        return stage3
    }

    fun fork(input: List<String>, p1: Pipeline, p2: Pipeline): Pair<List<String>, List<String>> {
        val r1 = p1.execute(input)
        val r2 = p2.execute(input)

        return Pair(r1, r2)
    }

}

// Função de construção para criar um pipeline usando um bloco de código
// Recebe um bloco que configura o pipeline e retorna o pipeline configurado
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline() // Cria uma nova instância de Pipeline
    pipeline.block() // Executa o bloco de configuração no pipeline
    return pipeline // Retorna o pipeline configurado
}

// Função principal para demonstrar o uso do pipeline
fun main() {
    // Define uma lista de logs de exemplo
    val logs = listOf(
        " INFO : server started ", // Log de informação
        " ERROR : disk full ", // Log de erro
        " DEBUG : checking config ", // Log de debug
        " ERROR : out of memory ", // Log de erro
        " INFO : request received ", // Log de informação
        " ERROR : connection timeout " // Log de erro
    )

    // Constrói um pipeline usando a função buildPipeline
    val pipeline = buildPipeline {
        // Adiciona estágio para remover espaços em branco
        addStage("Trim") { list ->
            list.map { it.trim() } // Aplica trim a cada string da lista
        }

        // Adiciona estágio para filtrar apenas logs de erro
        addStage("Filter errors") { list ->
            list.filter { it.contains("ERROR") } // Mantém apenas strings que contêm "ERROR"
        }

        // Adiciona estágio para converter para maiúsculas
        addStage("Uppercase") { list ->
            list.map { it.uppercase() } // Converte cada string para maiúsculas
        }

        // Adiciona estágio para adicionar índices aos logs
        addStage("Add index") { list ->
            list.mapIndexed { index, value -> // Mapeia com índice
                "${index + 1}. $value" // Adiciona o número do índice no início
            }
        }
    }

    pipeline.describe() // Descreve os estágios do pipeline

    val result = pipeline.execute(logs) // Executa o pipeline nos logs

    println("Result:") // Imprime o cabeçalho do resultado
    result.forEach { println(it) } // Imprime cada linha do resultado
}
