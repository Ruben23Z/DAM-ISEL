package processor

import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

// Regista automaticamente este processor no sistema
@AutoService(Processor::class)

// Define que suporta Java 22
@SupportedSourceVersion(SourceVersion.RELEASE_22)

// Diz qual annotation este processor vai tratar
@SupportedAnnotationTypes("annotations.Greeting")

class GreetingProcessor : AbstractProcessor() {

    // Método principal executado pelo compilador
    override fun process(
        annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment
    ): Boolean {
        // Mapa:
        // Classe -> lista de métodos anotados
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()
        // Procura todos os elementos anotados com @Greeting
        for (element in roundEnv.getElementsAnnotatedWith(Greeting::class.java)) {
            // Se for método/função
            if (element is ExecutableElement) {
                // Obtém a classe onde o método está
                val enclosingClass = element.enclosingElement as TypeElement
                // Se a classe ainda não existir no mapa, cria lista
                // Depois adiciona o método
                classMethodMap.computeIfAbsent(enclosingClass) {
                    mutableListOf()
                }.add(element)
            }
        }
        // Para cada classe encontrada
        for ((classElement, methods) in classMethodMap) {
            // Gera classe Wrapper
            generateKotlinWrapperClass(
                classElement, methods
            )
        }
        // Diz ao compilador que processou tudo
        return true
    }

    // Gera classe Wrapper automaticamente
    private fun generateKotlinWrapperClass(
        classElement: TypeElement, methods: List<ExecutableElement>
    ) {
        // Nome do package original
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        // Nome da classe original
        val originalClassName = classElement.simpleName.toString()
        // Nome da nova classe
        // Exemplo: MyClassWrapper
        val wrapperClassName = "${originalClassName}Wrapper"
        // ===============================
        // CRIAR CLASSE WRAPPER
        // ===============================
        val classBuilder = TypeSpec.Companion.classBuilder(wrapperClassName)
            // Construtor:
            // MyClassWrapper(original: MyClass)
            .primaryConstructor(
                FunSpec.Companion.constructorBuilder().addParameter(
                    "original", ClassName(
                        packageName, originalClassName
                    )
                ).build()
            )
            // Guarda referência da classe original
            .addProperty(
                PropertySpec.Companion.builder(
                    "original", ClassName(
                        packageName, originalClassName
                    )
                ).initializer("original").build()
            )
            // Classe pública final
            .addModifiers(
                KModifier.PUBLIC, KModifier.FINAL
            )
        // ===============================
        // GERAR MÉTODOS WRAPPER
        // ===============================
        for (method in methods) {
            // Nome do método
            val methodName = method.simpleName.toString()
            // Parâmetros do método original
            val parameters = method.parameters.map { param ->
                ParameterSpec.Companion.builder(
                    param.simpleName.toString(), param.asType().asTypeName()
                ).build()
            }
            // Só nomes dos parâmetros
            // Exemplo: x, y
            val arguments = method.parameters.joinToString(", ") {
                it.simpleName.toString()
            }
            // Lê texto da annotation
            val greetingMessage = method.getAnnotation(Greeting::class.java)?.message ?: "Hello!"
            // Cria novo método wrapper
            val methodBuilder = FunSpec.Companion.builder(methodName).addModifiers(
                KModifier.PUBLIC, KModifier.FINAL
            )
                // adiciona parâmetros
                .addParameters(parameters)
                // imprime mensagem
                .addStatement(
                    "println(%S)", greetingMessage
                )
                // chama método original
                .addStatement(
                    "original.$methodName($arguments)"
                )
            // adiciona método à classe
            classBuilder.addFunction(
                methodBuilder.build()
            )
        }
        // ===============================
        // CONSTRUIR FICHEIRO .KT
        // ===============================
        val file = FileSpec.Companion.builder(
            packageName, wrapperClassName
        ).addType(classBuilder.build()).build()
        // ===============================
        // ESCREVER FICHEIRO GERADO
        // ===============================
        try {
            // Pasta onde o kapt gera código
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                // Escreve ficheiro .kt
                file.writeTo(
                    File(kaptKotlinGeneratedDir)
                )
            } else {
                // Se pasta não existir
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR, "kapt.kotlin.generated not found"
                )
            }
        } catch (e: Exception) {
            // Se der erro ao gerar ficheiro
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR, "Error generating Kotlin file: ${e.message}"
            )
        }
    }
}