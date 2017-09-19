package com.afg.tess.commands.api

/**
 * Created by AFlyingGrayson on 9/18/17
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Command(val aliases : Array<String>)
