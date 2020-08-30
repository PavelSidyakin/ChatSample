package com.example.chatsample.utils

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

fun serializeToByteArray(s: Serializable?): ByteArray? {
    val bos = ByteArrayOutputStream()
    var oos: ObjectOutputStream? = null
    var byteArray: ByteArray? = null
    try {
        oos = ObjectOutputStream(bos)
        oos.writeObject(s)
        oos.flush()
        byteArray = bos.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        closeQuietly(oos)
        closeQuietly(bos)
    }
    return byteArray
}

fun parseByteArray(byteArray: ByteArray?): Any? {
    val bis = ByteArrayInputStream(byteArray)
    var ois: ObjectInputStream? = null
    try {
        ois = ObjectInputStream(bis)
        return ois.readObject()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } finally {
        closeQuietly(ois)
        closeQuietly(bis)
    }
    return null
}

fun serializeToString(s: Serializable?): String? {
    return Base64.encodeToString(serializeToByteArray(s), Base64.DEFAULT)
}

fun parseString(byteArray: String?): Any? {
    return parseByteArray(Base64.decode(byteArray, Base64.DEFAULT))
}

fun closeQuietly(c: Closeable?) {
    if (c != null) {
        try {
            c.close()
        } catch (var2: IOException) {
            // temp
            var2.printStackTrace()
        }
    }
}