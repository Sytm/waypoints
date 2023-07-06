package de.md5lukas.ksp.coroutines

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.concurrent.CompletableFuture

class CoroutinesInteropProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

  private val createCoroutine = MemberName("kotlinx.coroutines", "CoroutineScope")
  private val emptyCoroutineContext = ClassName("kotlin.coroutines", "EmptyCoroutineContext")
  private val scopeFuture = MemberName("kotlinx.coroutines.future", "future")

  private var invoked = false

  override fun process(resolver: Resolver): List<KSAnnotated> {
    if (invoked) return emptyList()
    invoked = true

    val required = getRequiredFunctions(resolver)

    required.forEach(::genClass)

    return emptyList()
  }

  private fun genClass(
      data: JavaAdapterContextData,
  ) {
    val originalPoetClass = data.clazz.toClassName()
    val adapterClass = originalPoetClass.adapterName

    val typeParameterResolver = data.clazz.typeParameters.toTypeParameterResolver()

    FileSpec.builder(adapterClass)
        .addType(
            TypeSpec.objectBuilder(adapterClass)
                .also { typeBuilder ->
                  data.directFunctions.forEach { function ->
                    typeBuilder.addFunction(
                        createActualFunction(originalPoetClass, typeParameterResolver, function))
                  }
                  data.superTypeFunctions.forEach { function ->
                    typeBuilder.addFunction(
                        createDelegatingFunction(
                            originalPoetClass,
                            typeParameterResolver,
                            function.first.toClassName().adapterName,
                            function.second))
                  }
                }
                .build())
        .build()
        .writeTo(codeGenerator, Dependencies(false, *data.involvedFiles))
  }

  private fun createActualFunction(
      originalPoetClass: ClassName,
      typeParameterResolver: TypeParameterResolver,
      function: KSFunctionDeclaration
  ): FunSpec =
      FunSpec.builder(function.simpleName.asString())
          .also { funBuilder ->
            val params =
                funBuilder.applyFromKSDec(originalPoetClass, typeParameterResolver, function)

            funBuilder.beginControlFlow(
                "return %M(%T).%M", createCoroutine, emptyCoroutineContext, scopeFuture)
            funBuilder.addStatement(
                "instance.${function.simpleName.asString()}(${params.joinToString()})")
            funBuilder.endControlFlow()
          }
          .build()

  private fun createDelegatingFunction(
      originalPoetClass: ClassName,
      typeParameterResolver: TypeParameterResolver,
      superTypeAdapterPoetClass: ClassName,
      function: KSFunctionDeclaration,
  ): FunSpec =
      FunSpec.builder(function.simpleName.asString())
          .also { funBuilder ->
            val params =
                funBuilder.applyFromKSDec(originalPoetClass, typeParameterResolver, function)

            params.add(0, "instance")
            funBuilder.addStatement(
                "return %T.${function.simpleName.asString()}(${params.joinToString()})",
                superTypeAdapterPoetClass)
          }
          .build()

  private fun getRequiredFunctions(resolver: Resolver): List<JavaAdapterContextData> =
      resolver
          .getAllFiles()
          .filter { it.validate() }
          .flatMap { it.declarations }
          .filterIsInstance<KSClassDeclaration>()
          .mapNotNull { classDec ->
            val involvedFiles = mutableSetOf<KSFile>()
            val directFunctions = mutableListOf<KSFunctionDeclaration>()
            val superTypeFunctions =
                mutableListOf<Pair<KSClassDeclaration, KSFunctionDeclaration>>()

            classDec.containingFile?.let { involvedFiles += it }

            fun functionFilter(func: KSFunctionDeclaration): Boolean =
                func.functionKind === FunctionKind.MEMBER &&
                    func.isPublic() &&
                    Modifier.SUSPEND in func.modifiers

            classDec.getDeclaredFunctions().filter(::functionFilter).forEach { function ->
              directFunctions += function
            }

            classDec
                .getAllSuperTypes()
                .map(KSType::declaration)
                .filterIsInstance<KSClassDeclaration>()
                .forEach { superClass ->
                  superClass.getDeclaredFunctions().filter(::functionFilter).forEach { function ->
                    superTypeFunctions += superClass to function
                    superClass.containingFile?.let { involvedFiles += it }
                  }
                }

            if (directFunctions.isEmpty() && superTypeFunctions.isEmpty()) return@mapNotNull null

            JavaAdapterContextData(
                classDec, involvedFiles.toTypedArray(), directFunctions, superTypeFunctions)
          }
          .toList()

  private fun FunSpec.Builder.applyFromKSDec(
      originalPoetClass: ClassName,
      parentResolver: TypeParameterResolver,
      function: KSFunctionDeclaration
  ): MutableList<String> {
    val functionTypeParameterResolver =
        function.typeParameters.toTypeParameterResolver(parentResolver)

    addModifiers(KModifier.PUBLIC)
    addAnnotation(JvmStatic::class)
    addParameter("instance", originalPoetClass)

    val params = mutableListOf<String>()
    function.parameters.forEachIndexed { index, parameter ->
      val paramName = parameter.name?.asString() ?: "param$index"
      params += paramName
      addParameter(
          paramName,
          parameter.type.toTypeName(functionTypeParameterResolver),
      )
    }
    returns(
        CompletableFuture::class.asClassName().parameterizedBy(function.returnType!!.toTypeName()))

    return params
  }

  private val ClassName.adapterName: ClassName
    get() = peerClass("J${this.simpleName}")

  private class JavaAdapterContextData(
      val clazz: KSClassDeclaration,
      val involvedFiles: Array<KSFile>,
      val directFunctions: List<KSFunctionDeclaration>,
      val superTypeFunctions: List<Pair<KSClassDeclaration, KSFunctionDeclaration>>
  )
}
