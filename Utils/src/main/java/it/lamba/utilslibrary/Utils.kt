package it.lamba.utilslibrary

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.util.TypedValue
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.widget.*
import java.io.*
import java.util.*


object Utils {
    fun hideKeyboardFrom(context: Context, view: EditText) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
        (view as? AutoCompleteTextView)?.dismissDropDown()
    }

    fun DpToPx(r: Resources, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
    }

    fun showError(it: Exception, context: Context){
        Toast.makeText(context, "Qualcosa è andato storto ¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show()
        it.printStackTrace()
    }
    fun isNameValid(name: String): Boolean {
        return name.length > 2
    }
    fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }
    fun getImageSizes(file: File): Point {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        return Point(options.outWidth, options.outWidth)
    }
    fun scaleSizes(file: File, dp: Float, r: Resources) = scaleSizes(getImageSizes(file), dp, r)

    fun scaleSizes(input: Point, dp: Float, r: Resources): Point{
        val x = (input.y*DpToPx(r, dp)/input.x).toInt()
        val y = DpToPx(r, dp).toInt()
        return Point(x, y)
    }

    fun getImagePathFromInputStreamUri(uri: Uri, contentResolver: ContentResolver): File? {
        var inputStream: InputStream? = null
        var photoFile: File? = null

        if (uri.authority != null) {
            try {
                inputStream = contentResolver.openInputStream(uri) // context needed
                photoFile = createTemporalFileFrom(inputStream)

            } catch (e: FileNotFoundException) {
                // log
            } catch (e: IOException) {
                // log
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return photoFile
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null

        if (inputStream != null) {
            val buffer = ByteArray(8 * 1024)
            var read = inputStream.read(buffer)

            targetFile = createTemporalFile()
            val outputStream = FileOutputStream(targetFile)

            while (read != -1) {
                outputStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outputStream.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return targetFile
    }

    fun createTemporalFile(extension: String = "jpg"): File {
        return File.createTempFile(UUID.randomUUID().toString(), extension)
    }

    fun encodeEmail(email: String): String {
        return email.replace(".", "%2E").replace("@", "%40")
    }

    fun createChipBitmap(file: File, context: Context): Bitmap{
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeFile(file.path),
                DpToPx(context.resources, 35f).toInt(),
                DpToPx(context.resources, 35f).toInt(),
                false)
    }

}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.addOnScrollChangedListener(onScroll: (X: Int, Y: Int) -> Unit){
    this.viewTreeObserver.addOnScrollChangedListener {
        onScroll(this.scrollX, this.scrollY)
    }
}

fun Bundle.putString_(key: String, value: String): Bundle {
    putString(key, value)
    return this
}