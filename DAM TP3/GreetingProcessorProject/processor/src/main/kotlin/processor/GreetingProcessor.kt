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

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_22)
@SupportedAnnotationTypes("annotations.Greeting")



//Prof
class GreetingProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment
    ): Boolean {

        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()
        for (element in roundEnv.getElementsAnnotatedWith(Greeting::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement

                classMethodMap.computeIfAbsent(enclosingClass) {
                    mutableListOf()
                }.add(element)
            }
        }
        // Generate wrapper classes for each class containing annotated methods
        for ((classElement, methods) in classMethodMap) {
            // Gera classe Wrapper
            generateKotlinWrapperClass(
                classElement, methods
            )
        }
        return true
    }

    private fun generateKotlinWrapperClass(
        classElement: TypeElement, methods: List<ExecutableElement>
    ) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()

        val wrapperClassName = "${originalClassName}Wrapper"

        val classBuilder = TypeSpec.Companion.classBuilder(wrapperClassName)
            .primaryConstructor(
                FunSpec.Companion.constructorBuilder().addParameter(
                    "original", ClassName(
                        packageName, originalClassName
                    )
                ).build()
            ).addProperty(
                PropertySpec.Companion.builder(
                    "original", ClassName(
                        packageName, originalClassName
                    )
                ).initializer("original").build()
            ).addModifiers(
                KModifier.PUBLIC, KModifier.FINAL
            )

        for (method in methods) {
            val methodName = method.simpleName.toString()
            val parameters = method.parameters.map { param ->
                ParameterSpec.Companion.builder(
                    param.simpleName.toString(), param.asType().asTypeName()
                ).build()
            }

            val arguments = method.parameters.joinToString(", ") {
                it.simpleName.toString()
            }
            val greetingMessage = method.getAnnotation(Greeting::class.java)?.message ?: "Hello!"
            val methodBuilder = FunSpec.Companion.builder(methodName).addModifiers(
                KModifier.PUBLIC, KModifier.FINAL
            ).addParameters(parameters).addStatement(
                "println(%S)", greetingMessage
            ).addStatement(
                "original.$methodName($arguments)"
            )
            classBuilder.addFunction(
                methodBuilder.build()
            )
        }

        // Build the Kotlin file
        val file = FileSpec.Companion.builder(
            packageName, wrapperClassName
        ).addType(classBuilder.build()).build()
// Write the generated file
        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(
                    File(kaptKotlinGeneratedDir)
                )
            } else {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR, "kapt.kotlin.generated not found"
                )
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR, "Error generating Kotlin file: ${e.message}"
            )
        }
    }
}