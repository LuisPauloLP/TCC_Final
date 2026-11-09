package com.example.ged

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.IOException
import com.canhub.cropper.CropImageView
import com.example.ged.database.DocumentoDAO
import com.example.ged.model.Documento

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ged.ocr.TesseractOCR

import android.content.Intent
import android.util.Log
import com.example.ged.ocr.ExtratorDadosOCR
import android.widget.TextView
import android.graphics.BitmapFactory
class VisualizacaoActivity : AppCompatActivity() {

    private lateinit var btnAnterior: Button
    private lateinit var btnProximo: Button

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageIndex = 0
    private lateinit var btnConfirmarAjuste: Button
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var currentBitmap: Bitmap? = null // Para guardar o bitmap atual da página
    private lateinit var cropImageView: CropImageView
    private var documentoEhImagem =
        false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizacao)

        val txtErroOCR =
            findViewById<TextView>(R.id.txtErroOCR)

        // Apague o findViewById do imgPaginaPdf e viewCropFrame
        cropImageView = findViewById(R.id.cropImageView)

       // Desative a grade de corte ao iniciar a tela (para parecer um ImageView normal)
        cropImageView.isShowCropOverlay = false
        btnAnterior = findViewById(R.id.btnAnterior)
        btnProximo = findViewById(R.id.btnProximo)

        val pdfUriString =
            intent.getStringExtra(
                "PDF_URI"
            )

        val imagemUriString =
            intent.getStringExtra(
                "IMAGEM_URI"
            )


        when {

            pdfUriString != null -> {

                val pdfUri =
                    Uri.parse(
                        pdfUriString
                    )

                abrirPdf(
                    pdfUri
                )
            }


            imagemUriString != null -> {

                val imagemUri =
                    Uri.parse(
                        imagemUriString
                    )

                abrirImagem(
                    imagemUri
                )
            }


            else -> {

                Toast.makeText(
                    this,
                    "Nenhum documento foi recebido",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        // Navegação de páginas
        btnAnterior.setOnClickListener {
            mostrarPagina(currentPageIndex - 1)
        }

        btnProximo.setOnClickListener {
            mostrarPagina(currentPageIndex + 1)
        }

        // Mapear os novos views
        btnConfirmarAjuste = findViewById(R.id.btnConfirmarAjuste)
        val btnAjustar = findViewById<Button>(R.id.btnAjustar)

        // Configuração da Gaveta
        val bottomSheet = findViewById<android.widget.LinearLayout>(R.id.bottomSheetGaveta)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Modo de Ajuste (Entrar)
//        btnAjustar.setOnClickListener {
//
//            // Fecha completamente a gaveta
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//            // Esconde os botões de navegação
//            btnAnterior.visibility = View.INVISIBLE
//            btnProximo.visibility = View.INVISIBLE
//
//            // Ativa o modo de recorte
//            cropImageView.isShowCropOverlay = true
//
//            // Exibe o botão de confirmação
//            btnConfirmarAjuste.visibility = View.VISIBLE
//
//            // Garante que o botão fique acima dos demais componentes
//            btnConfirmarAjuste.bringToFront()
//            Toast.makeText(this, "Modo de ajuste ativado", Toast.LENGTH_SHORT).show()
//        }

        btnAjustar.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            btnAnterior.visibility = View.INVISIBLE
            btnProximo.visibility = View.INVISIBLE

            cropImageView.isShowCropOverlay = true

            btnConfirmarAjuste.visibility = View.VISIBLE
        }

        // Confirmar Ajuste (Cortar Imagem)
        btnConfirmarAjuste.setOnClickListener {
            // A biblioteca extrai a imagem recortada automaticamente de forma otimizada
            val croppedBitmap = cropImageView.croppedImage

            if (croppedBitmap != null) {
                // Define a nova imagem cortada no visualizador
                cropImageView.setImageBitmap(croppedBitmap)
                currentBitmap = croppedBitmap
            }

            // Esconde as linhas de corte e o botão
            cropImageView.isShowCropOverlay = false
            btnConfirmarAjuste.visibility = View.GONE

            // Traz a interface de volta ao normal
            if (documentoEhImagem) {

                btnAnterior.visibility =
                    View.GONE

                btnProximo.visibility =
                    View.GONE

            } else {

                btnAnterior.visibility =
                    View.VISIBLE

                btnProximo.visibility =
                    View.VISIBLE
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }



        val edtNomeArquivo = findViewById<EditText>(R.id.edtNomeArquivo)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        btnSalvar.setOnClickListener {

            txtErroOCR.text = ""
            txtErroOCR.visibility = View.GONE

            val nome = edtNomeArquivo.text
                .toString()
                .trim()

            if (nome.isBlank()) {
                edtNomeArquivo.error = "O nome é obrigatório"
                return@setOnClickListener
            }

            val bitmap = currentBitmap

            if (bitmap == null) {

                Toast.makeText(
                    this,
                    "Nenhum documento carregado",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Evita clicar várias vezes enquanto processa
            btnSalvar.isEnabled = false

            Toast.makeText(
                this,
                "Processando documento...",
                Toast.LENGTH_SHORT
            ).show()


            Thread {

                try {

                    // ============================
                    // 1. OCR
                    // ============================

                    val ocr = TesseractOCR(this)

                    val textoReconhecido =
                        ocr.reconhecerTexto(bitmap)


                    Log.d(
                        "OCR_GED",
                        textoReconhecido
                    )


                    // ============================
                    // 2. EXTRAIR DADOS
                    // ============================

                    val dados =
                        ExtratorDadosOCR.extrair(
                            textoReconhecido
                        )

                    val camposNaoIdentificados =
                        mutableListOf<String>()


                    if (dados.vencimento.isNullOrBlank()) {
                        camposNaoIdentificados.add("Vencimento")
                    }


                    if (dados.beneficiario.isNullOrBlank()) {
                        camposNaoIdentificados.add("Beneficiário")
                    }


                    if (dados.valor == null) {
                        camposNaoIdentificados.add("Valor")
                    }

                    if (camposNaoIdentificados.isNotEmpty()) {

                        runOnUiThread {

                            btnSalvar.isEnabled = true

                            txtErroOCR.visibility = View.VISIBLE

                            txtErroOCR.text =
                                "Não foi possível identificar:\n" +
                                        camposNaoIdentificados.joinToString(
                                            separator = "\n"
                                        ) { campo ->
                                            "• $campo"
                                        } +
                                        "\n\nAjuste o documento e tente novamente."
                        }

                        return@Thread
                    }


                    Log.d(
                        "OCR_DADOS",
                        """
                Linha digitável: ${dados.linhaDigitavel}
                Vencimento: ${dados.vencimento}
                Valor: ${dados.valor}
                Beneficiário: ${dados.beneficiario}
                """.trimIndent()
                    )


                    // ============================
                    // 3. SALVAR IMAGEM
                    // ============================

                    val caminhoArquivo =
                        salvarImagemInternamente(
                            bitmap,
                            nome
                        )


                    if (caminhoArquivo == null) {

                        runOnUiThread {

                            btnSalvar.isEnabled = true

                            Toast.makeText(
                                this,
                                "Erro ao salvar a imagem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        return@Thread
                    }


                    // ============================
                    // 4. DATA DE CADASTRO
                    // ============================

                    val dataAtual =
                        SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(
                            Date()
                        )


                    // ============================
                    // 5. CRIAR DOCUMENTO
                    // ============================

                    val documento =
                        Documento()


                    documento.nomeArquivo =
                        nome

                    documento.caminhoArquivo =
                        caminhoArquivo

                    documento.dataCadastro =
                        dataAtual

                    documento.textoOCR =
                        textoReconhecido

                    documento.linhaDigitavel =
                        dados.linhaDigitavel

                    documento.valor =
                        dados.valor

                    documento.vencimento =
                        dados.vencimento

                    documento.beneficiario =
                        dados.beneficiario


                    // ============================
                    // 6. SALVAR NO SQLITE
                    // ============================

                    val documentoDAO =
                        DocumentoDAO(this)


                    val id =
                        documentoDAO.inserir(
                            documento
                        )


                    // ============================
                    // 7. VOLTAR PARA A UI
                    // ============================

                    runOnUiThread {

                        btnSalvar.isEnabled = true


                        if (id != -1L) {

                            Toast.makeText(
                                this,
                                "Documento salvo com sucesso!",
                                Toast.LENGTH_LONG
                            ).show()


                            val intent =
                                Intent(
                                    this,
                                    MenuInicial::class.java
                                )


                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP


                            startActivity(intent)

                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Erro ao salvar documento",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                } catch (e: Exception) {

                    e.printStackTrace()


                    runOnUiThread {

                        btnSalvar.isEnabled = true


                        Toast.makeText(
                            this,
                            "Erro ao processar documento: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }.start()
        }

//        btnSalvar.setOnClickListener {
//
//            val nome = edtNomeArquivo.text.toString().trim()
//
//            // Verifica se foi informado um nome
//            if (nome.isBlank()) {
//                edtNomeArquivo.error = "O nome é obrigatório"
//                return@setOnClickListener
//            }
//
//            // Verifica se existe uma imagem carregada
//            val bitmap = currentBitmap
//
//            if (bitmap == null) {
//
//                Toast.makeText(
//                    this,
//                    "Nenhum documento carregado",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                return@setOnClickListener
//            }
//
//            // Salva fisicamente a imagem
//            val caminhoArquivo =
//                salvarImagemInternamente(bitmap, nome)
//
//            if (caminhoArquivo == null) {
//
//                Toast.makeText(
//                    this,
//                    "Erro ao salvar o arquivo",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                return@setOnClickListener
//            }
//
//            // Data atual
//            val dataAtual = SimpleDateFormat(
//                "dd/MM/yyyy HH:mm",
//                Locale.getDefault()
//            ).format(Date())
//
//            // Cria objeto Documento
//            val documento = Documento()
//
//            documento.nomeArquivo = nome
//            documento.caminhoArquivo = caminhoArquivo
//            documento.dataCadastro = dataAtual
//
//            // Estes campos serão preenchidos futuramente pelo OCR
//            documento.linhaDigitavel = null
//            documento.valor = null
//            documento.vencimento = null
//            documento.beneficiario = null
//
//            // Acessa o SQLite
//            val documentoDAO = DocumentoDAO(this)
//
//            // Insere
//            val id = documentoDAO.inserir(documento)
//
//            if (id != -1L) {
//
//                Toast.makeText(
//                    this,
//                    "Documento salvo com sucesso!",
//                    Toast.LENGTH_LONG
//                ).show()
//
//                val intent = android.content.Intent(
//                    this,
//                    MenuInicial::class.java
//                )
//
//                // Evita criar várias telas de menu empilhadas
//                intent.flags =
//                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                            android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
//
//                startActivity(intent)
//
//                finish()
//            } else {
//
//                Toast.makeText(
//                    this,
//                    "Erro ao salvar documento no banco",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
    }

    private fun salvarImagemInternamente(
        bitmap: Bitmap,
        nomeArquivo: String
    ): String? {

        return try {

            // Cria a pasta:
            // /data/user/0/com.example.ged/files/documentos/
            val pastaDocumentos = File(filesDir, "documentos")

            if (!pastaDocumentos.exists()) {
                pastaDocumentos.mkdirs()
            }

            // Remove caracteres inadequados do nome
            val nomeSeguro = nomeArquivo
                .trim()
                .replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚãõÃÕçÇ_-]"), "_")

            val arquivo = File(
                pastaDocumentos,
                "$nomeSeguro.jpg"
            )

            FileOutputStream(arquivo).use { outputStream ->

                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    95,
                    outputStream
                )
            }

            arquivo.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    private fun abrirPdf(uri: Uri) {
        try {
            // Abre o arquivo recebido pelo gerenciador
            fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            fileDescriptor?.let {
                pdfRenderer = PdfRenderer(it)
                mostrarPagina(0) // Mostra a primeira página
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Falha ao ler o arquivo PDF", Toast.LENGTH_SHORT).show()
        }
    }
    private fun abrirImagem(
        uri: Uri
    ) {


        try {

            documentoEhImagem = true
            val inputStream =
                contentResolver.openInputStream(
                    uri
                )

            val bitmap =
                BitmapFactory.decodeStream(
                    inputStream
                )

            inputStream?.close()


            if (bitmap != null) {

                currentBitmap =
                    bitmap

                cropImageView
                    .setImageBitmap(
                        bitmap
                    )


                // Foto possui somente uma página
                btnAnterior.visibility =
                    View.GONE

                btnProximo.visibility =
                    View.GONE

            } else {

                Toast.makeText(
                    this,
                    "Não foi possível carregar a imagem",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Erro ao carregar a imagem",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun mostrarPagina(index: Int) {
        val renderer = pdfRenderer ?: return
        if (index < 0 || index >= renderer.pageCount) return

        currentPage?.close()
        currentPage = renderer.openPage(index)

        currentPage?.let { page ->
            // RESOLUÇÃO CORRETA: Limita a largura a 1080px para evitar travamentos e sumiço da imagem
            val screenWidth = resources.displayMetrics.widthPixels
            val safeWidth = Math.min(screenWidth, 1080)
            val safeHeight = (safeWidth.toFloat() * page.height / page.width).toInt()

            val bitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            currentBitmap = bitmap
            cropImageView.setImageBitmap(bitmap)
            currentPageIndex = index

            btnAnterior.isEnabled = currentPageIndex > 0
            btnProximo.isEnabled = currentPageIndex < renderer.pageCount - 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // OBRIGATÓRIO: Liberar a memória fechando o renderizador e o arquivo
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
}