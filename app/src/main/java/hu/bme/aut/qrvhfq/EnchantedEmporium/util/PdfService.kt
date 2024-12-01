package hu.bme.aut.qrvhfq.EnchantedEmporium.util
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.orders.Order
import hu.bme.aut.qrvhfq.myapplication.R
import java.io.File
import java.io.FileOutputStream

object PdfService {
    fun generateOrderPdf(context: Context, order: Order): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint()
            paint.textSize = 14f

            // Draw logo
            val logoBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 60, 60, false)
            canvas.drawBitmap(scaledLogo, 20f, 20f, paint)

            // Title Section
            paint.color = Color.BLACK
            paint.isFakeBoldText = true
            canvas.drawText("PURCHASE ORDER", 250f, 30f, paint)

            paint.textSize = 10f
            paint.isFakeBoldText = false


            // Bill To Section
            paint.color = Color.rgb(167, 167, 136)
            canvas.drawRect(20f, 110f, 580f, 140f, paint)

            paint.color = Color.WHITE
            paint.isFakeBoldText = true
            canvas.drawText("Bill To", 30f, 130f, paint)

            paint.color = Color.BLACK
            paint.isFakeBoldText = false
            canvas.drawText("Name: ${order.address.fullName}", 30f, 160f, paint)
            canvas.drawText("Street: ${order.address.street}", 30f, 175f, paint)
            canvas.drawText("City: ${order.address.city}, ${order.address.country}", 30f, 190f, paint)
            canvas.drawText("Postal Code: ${order.address.postalCode}", 30f, 205f, paint)

            // Order Info Section
            paint.color = Color.rgb(167, 167, 136)
            canvas.drawRect(20f, 220f, 580f, 250f, paint)

            paint.color = Color.WHITE
            paint.isFakeBoldText = true
            canvas.drawText("Order Details", 30f, 240f, paint)

            paint.color = Color.BLACK
            paint.isFakeBoldText = false
            canvas.drawText("Order Status: ${order.orderStatus}", 30f, 270f, paint)
            canvas.drawText("Total Price: $${String.format("%.2f", order.totalPrice)}", 30f, 285f, paint)

            // Product List Section
            canvas.drawLine(20f, 300f, 580f, 300f, paint)
            paint.isFakeBoldText = true
            canvas.drawText("Description", 30f, 320f, paint)
            canvas.drawText("Quantity", 350f, 320f, paint)
            canvas.drawText("Amount", 500f, 320f, paint)
            canvas.drawLine(20f, 330f, 580f, 330f, paint)

            paint.isFakeBoldText = false
            var currentY = 350f
            order.products.forEach { product ->
                canvas.drawText(product.product.name, 30f, currentY, paint)
                canvas.drawText(product.quantity.toString(), 350f, currentY, paint)
                canvas.drawText("$${String.format("%.2f", product.product.price * product.quantity)}", 500f, currentY, paint)
                currentY += 20f
            }

            // Total Section
            canvas.drawLine(20f, currentY, 580f, currentY, paint)
            currentY += 20f
            paint.isFakeBoldText = true
            canvas.drawText("Total:", 450f, currentY, paint)
            paint.isFakeBoldText = false
            canvas.drawText("$${String.format("%.2f", order.totalPrice)}", 500f, currentY, paint)

            pdfDocument.finishPage(page)

            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(fileDir, "Order_${System.currentTimeMillis()}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}