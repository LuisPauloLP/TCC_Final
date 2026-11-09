package com.example.ged

    import android.graphics.Bitmap
    import android.graphics.Color
    import android.os.Bundle
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import com.google.zxing.BarcodeFormat
    import com.google.zxing.EncodeHintType
    import com.google.zxing.qrcode.QRCodeWriter
    import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

    class QRCodeActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_qrcode)

            val imgQRCode =
                findViewById<ImageView>(R.id.imgQRCode)

            val txtCodigo =
                findViewById<TextView>(R.id.txtCodigoAcesso)

            val txtQuantidade =
                findViewById<TextView>(R.id.txtQuantidadeDocumentos)

            val btnConcluir =
                findViewById<Button>(R.id.btnConcluirQRCode)

            val url = intent.getStringExtra("URL_CONSOLIDADO")
            val codigo = intent.getStringExtra("CODIGO_ACESSO")
            val quantidade = intent.getIntExtra("QUANTIDADE_DOCUMENTOS", 0)

            if (url.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Não foi possível obter a URL do consolidado.",
                    Toast.LENGTH_LONG
                ).show()

                finish()
                return
            }

            txtCodigo.text = "Código de acesso: ${codigo ?: "--"}"
            txtQuantidade.text = "$quantidade documento(s) selecionado(s)"

            try {
                imgQRCode.setImageBitmap(gerarQRCode(url))
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Erro ao gerar QR Code: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            btnConcluir.setOnClickListener {
                finish()
            }
        }

        private fun gerarQRCode(conteudo: String): Bitmap {
            val tamanho = 800

            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN to 2
            )

            val matriz = QRCodeWriter().encode(
                conteudo,
                BarcodeFormat.QR_CODE,
                tamanho,
                tamanho,
                hints
            )

            val bitmap = Bitmap.createBitmap(
                tamanho,
                tamanho,
                Bitmap.Config.ARGB_8888
            )

            for (x in 0 until tamanho) {
                for (y in 0 until tamanho) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (matriz[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }

            return bitmap
        }
    }

