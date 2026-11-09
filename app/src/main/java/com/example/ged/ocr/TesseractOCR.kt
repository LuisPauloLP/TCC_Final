package com.example.ged.ocr

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class TesseractOCR(
    private val context: Context
) {

    private val tessDataPath =
        File(context.filesDir, "tesseract")

    private val idioma = "por"


    private fun prepararTesseract() {

        val tessdata =
            File(
                tessDataPath,
                "tessdata"
            )

        if (!tessdata.exists()) {
            tessdata.mkdirs()
        }

        val arquivoTreinamento =
            File(
                tessdata,
                "$idioma.traineddata"
            )

        if (!arquivoTreinamento.exists()) {

            copiarArquivoTreinamento(
                arquivoTreinamento
            )
        }
    }


    private fun copiarArquivoTreinamento(
        destino: File
    ) {

        context.assets.open(
            "tessdata/$idioma.traineddata"
        ).use { input ->

            FileOutputStream(
                destino
            ).use { output ->

                input.copyTo(output)
            }
        }
    }


    fun reconhecerTexto(
        bitmap: Bitmap
    ): String {

        prepararTesseract()

        val tess = TessBaseAPI()

        try {

            val iniciou =
                tess.init(
                    tessDataPath.absolutePath,
                    idioma
                )

            if (!iniciou) {
                return ""
            }

            tess.setImage(bitmap)

            return tess.utF8Text ?: ""

        } finally {

            tess.recycle()
        }
    }
}