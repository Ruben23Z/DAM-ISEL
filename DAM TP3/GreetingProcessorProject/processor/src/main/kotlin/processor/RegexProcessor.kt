package processor

import annotations.Extract
import annotations.Greeting
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet .*
import java.io.File
import javax.annotation.processing .*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.management.ConstructorParameters
import javax.tools.Diagnostic
import kotlin.collections.iterator


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_22)
@SupportedAnnotationTypes("annotations.Extract") // mudei
class RegexProcessor : AbstractProcessor() {
    override fun process(
        annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment
    ): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>() //Map<Classe, ListaDeMetodos>
//        DataProcessor1 -> [met1, metodo2]
//        DataProcessor2 -> [met1, metodo2]
        // Find all methods annotated with @Greeting
        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) { //mudei para o extract annotation
            if (element is ExecutableElement) { //garante que é metodo, pois executabeelement são metodos ou fucnoes
                val enclosingClass = element.enclosingElement as TypeElement //obtem a classe envolvente
                classMethodMap.computeIfAbsent(enclosingClass) {
                    mutableListOf()
                }.add(element)
            }
        }
// Generate wrapper classes for each class containing annotated methods
        for ((classElement, methods) in classMethodMap) {
            generateKotlinWrapperClass(classElement, methods)
        }
        return true
    }



    private fun generateKotlinWrapperClass(
        classElement: TypeElement, methods: List<ExecutableElement>
    ) {
        val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
        val originalClassName = classElement.simpleName.toString()
        val wrapperClassName = "${originalClassName}Extractor" //------> para o sufixo extractor
// Create the wrapper class using composition


        val classBuilder = TypeSpec.classBuilder(wrapperClassName).primaryConstructor(
            FunSpec.constructorBuilder().addParameter(
                //define aqui construtor e parametros das devidas variaveis
                "input",
                String::class.asTypeName()  //----> diz que o tipo é string e converte para kotlinpoeat, que é diferente do kotlin normal
            )
                .build()
        )
            //define que a classe criada é herdada de DataProcess e é o mesmo que dizer que é do tipo DataProcessor, onde da se o nome da classe e o packgae
            .superclass(ClassName(packageName, originalClassName))


            // para que o input que se recebe tem ainda que ser passado ao pai
            //obriga que a classe filha passa argumentos ao construtor da classe pai quando é criada, isto para criar primeiro a classe pai e
            //dps a filha
            //) : DataProcessor(input) {
            .addSuperclassConstructorParameter("input")



            //faz final e public
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)






// Generate wrapper methods
        for (method in methods) {
            val methodName = method.simpleName.toString() //obtem o nome do metodo
            val parameters = method.parameters.map { param ->
                ParameterSpec.builder(
                    param.simpleName.toString(), param.asType().asTypeName()
                ).build()
            } //passa os metodos em obj do KotlinPoet


            //mudar para extract(identificar o @extract) e o .regex para obter o que esta dentro dos ( )
            val regex = method.getAnnotation(Extract::class.java)?.regex ?: "Hello!"

            val methodBuilder =
                FunSpec.builder(methodName).addModifiers(KModifier.OVERRIDE) //PASSA A OVERRIDE
                    .addParameters(parameters)

                    // o \ diz ao compilador que as aspas são um caracter especial dentro da string, para meter o " dentro do codigo
                    //senao ficava: "val match = Regex("
//                    .addStatement("val match = Regex(\"$regex\").find(input)") //altera a expressao e adiciona
                    .addStatement("val match = Regex(%S).find(input)", regex) //para fazer as \ e " de forma auto senao da erro, %S é especifico para aspas e \
                    .addStatement("return match?.groupValues?.get(1)")
                    .returns(String::class.asTypeName().copy(nullable = true))// o copy(nullable=true) permite que seja do tipo String que pode ser null, e copy pois o typename é imutavel
                        classBuilder.addFunction(methodBuilder.build())
        }


// Build the Kotlin file
        val file = FileSpec.builder(packageName, wrapperClassName).addType(classBuilder.build()).build()
// Write the generated file
        try {
            val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptKotlinGeneratedDir != null) {
                file.writeTo(File(kaptKotlinGeneratedDir)) // Correct way to write Kotlin files
            } else {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR, "kapt.kotlin.generated not found"
                )
            }
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR, "Error generating Kotlin file :${e.message}"
            )
        }
    }
}