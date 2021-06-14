package com.sq26.libannotationprocessor

import com.sq26.libannotation.CodeText
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.sq26.libannotation.CodeText")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class CodeTextProcessor : AbstractProcessor() {
    lateinit var message: Messager

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        message = p0!!.messager

    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment): Boolean {
//        createFile("aaa","hhhh")

        for (item in p1.getElementsAnnotatedWith(CodeText::class.java)){
            println("注解开始")
            val executableElement =  item as TypeElement

            println(executableElement.qualifiedName)
            println(executableElement.simpleName)

            for (it in executableElement.enclosedElements)
                println(it.asType().kind.name)
            println("注解结束")
            return true
        }

        return false
//        val greeterClass = ClassName("", "Greeter")
//        val file = FileSpec.builder("", "HelloWorld")
//            .addType(
//                TypeSpec.classBuilder("Greeter")
//                .primaryConstructor(
//                    FunSpec.constructorBuilder()
//                    .addParameter("name", String::class)
//                    .build())
//                .addProperty(
//                    PropertySpec.builder("name", String::class)
//                    .initializer("name")
//                    .build())
//                .addFunction(FunSpec.builder("greet")
//                    .addStatement("println(%P)", "Hello, \$name")
//                    .build())
//                .build())
//            .addFunction(FunSpec.builder("main")
//                .addParameter("args", String::class, KModifier.VARARG)
//                .addStatement("%T(args[0]).greet()", greeterClass)
//                .build())
//            .build()
//
//        file.writeTo(System.out)
    }

//    fun createFile( className:String, output:String ) {
//        val cls =  StringBuilder();
//        cls.append("package apt;\n\npublic class ")
//            .append(className)
//            .append(" {\n  public static void main(String[] args) {\n")
//            .append("    System.out.println(\"")
//            .append(output)
//            .append("\");\n  }\n}");
//        try {
//            val sourceFile = JavaFileObject .createSourceFile("apt." + className);
//            val writer = sourceFile.openWriter();
//            writer.write(cls.toString());
//            writer.flush();
//            writer.close();
//        } catch (e: IOException) {
//            e.printStackTrace();
//        }
//    }
}