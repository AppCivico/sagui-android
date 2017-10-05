package com.eokoe.sagui.extensions

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * @author Pedro Silva
 * @since 04/10/17
 */
fun File.copy(dst: File) {
    FileInputStream(this).use { input ->
        if (!dst.exists()) {
            val parent = File(dst.parent)
            if (!parent.exists()) parent.mkdirs()
            dst.createNewFile()
        }
        FileOutputStream(dst).use { out ->
            val buf = ByteArray(1024)
            while (true) {
                val len = input.read(buf)
                if (len > 0) out.write(buf, 0, len)
                else break
            }
        }
    }
}