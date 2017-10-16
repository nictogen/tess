package com.afg.tess.util

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

/**
 * Created by AFlyingGrayson on 10/7/17
 */
interface ISaveable {

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : ISaveable> loadData(folderPath: String, list: ArrayList<T>, clear: Boolean) {
            if(clear) list.clear()
            val dr = File(folderPath)
            dr.mkdirs()
            val files = dr.listFiles()

            files.forEach {
                val data = HashMap<String, String>()
                val scanner = Scanner(it)
                while (scanner.hasNextLine()) {
                    val s = scanner.nextLine()
                    data.put(TessUtils.getKey(s), TessUtils.getValue(s))
                }
                scanner.close()
                val obj = T::class.createInstance()

                obj::class.memberProperties.filter { it is KMutableProperty<*> }.forEach { property ->
                    val varProperty = property as KMutableProperty<*>
                    if (data[property.name] != null)
                        when {
                            property.returnType == Int::class.starProjectedType -> varProperty.setter.call(obj, Integer.parseInt(data[property.name]))
                            property.returnType == Long::class.starProjectedType -> varProperty.setter.call(obj, data[property.name]!!.toLong())
                            property.returnType == Double::class.starProjectedType -> varProperty.setter.call(obj, data[property.name]!!.toDouble())
                            property.returnType == String::class.starProjectedType -> varProperty.setter.call(obj, data[property.name])
                            property.returnType == Boolean::class.starProjectedType -> varProperty.setter.call(obj, data[property.name] == "true")
                            property.returnType.isSubtypeOf(ArrayList::class.starProjectedType) -> {
                                val arrayList = property.getter.call(obj) as ArrayList<*>
                                arrayList.clear()
                                val dataStrings = TessUtils.listFromString(data[property.name]!!)
                                dataStrings.forEach {
                                    val args = it.split("$")
                                    if (args.size > 1 || args[0].length > 1)
                                        when (property.name) {
                                            "nearby" -> (arrayList as ArrayList<String>).add(args[0])
                                        }
                                }
                            }
                        }
                }
                list.add(obj)
            }
        }
    }

    fun saveData() {
        val data = HashMap<String, String>()
        this::class.memberProperties.forEach { property -> data.put(property.name, property.getter.call(this).toString()) }
        val dir = File(getFolderPath())
        dir.mkdirs()
        val dataFile = File(dir, getFileName())
        dataFile.createNewFile()
        val fileWriter = FileWriter(dataFile)
        val printWriter = PrintWriter(fileWriter)
        data.forEach { k, v -> printWriter.println(k + "=" + v) }
        printWriter.close()
    }

    fun getFolderPath(): String

    fun getFileName(): String
}