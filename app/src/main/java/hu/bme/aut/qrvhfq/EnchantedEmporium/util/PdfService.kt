package hu.bme.aut.qrvhfq.EnchantedEmporium.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.orders.Order
import java.io.File
import java.io.FileOutputStream

object PdfService {
    fun generateOrderPdf(context: Context, order: Order): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = android.graphics.Paint()

            // Add Order Details
            val yOffset = 20
            var currentY = yOffset
            canvas.drawText("Order Summary", 10f, currentY.toFloat(), paint)
            currentY += yOffset

            canvas.drawText("Order Status: ${order.orderStatus}", 10f, currentY.toFloat(), paint)
            currentY += yOffset

            canvas.drawText("Total Price: $${String.format("%.2f", order.totalPrice)}", 10f, currentY.toFloat(), paint)
            currentY += yOffset

            canvas.drawText("Delivery Address:", 10f, currentY.toFloat(), paint)
            currentY += yOffset
            canvas.drawText("Name: ${order.address.fullName}", 10f, currentY.toFloat(), paint)
            currentY += yOffset
            canvas.drawText("Street: ${order.address.street}", 10f, currentY.toFloat(), paint)
            currentY += yOffset
            canvas.drawText("City: ${order.address.city}, ${order.address.country}", 10f, currentY.toFloat(), paint)
            currentY += yOffset
            canvas.drawText("Postal Code: ${order.address.postalCode}", 10f, currentY.toFloat(), paint)
            currentY += yOffset

            canvas.drawText("Products:", 10f, currentY.toFloat(), paint)
            currentY += yOffset

            order.products.forEach { product ->
                canvas.drawText(
                    "${product.product.name} x${product.quantity} - $${product.product.price * product.quantity}",
                    10f,
                    currentY.toFloat(),
                    paint
                )
                currentY += yOffset
            }

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