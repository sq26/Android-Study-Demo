package com.sq26.libannotationprocessor

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.sq26.libannotation.AntiShake")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class AntiShakeProcessor : AbstractProcessor() {
    lateinit var message:Messager
    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
        message = p0.messager
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {

        message.printMessage(Diagnostic.Kind.MANDATORY_WARNING ,"注解启动")
        message.printMessage(Diagnostic.Kind.ERROR ,"注解启动")
        return false
    }
}