package com.example.ged // Lembre-se de manter o SEU pacote aqui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.ged.database.DocumentoDAO
import com.example.ged.model.Documento
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class MenuInicial : AppCompatActivity() {

    // Variáveis globais para o tutorial
    private var tutorialStep = 0
    private lateinit var prefs: SharedPreferences

    // Declaração dos views para o tutorial acessar
    private lateinit var btnDigitalizar: Button
    private lateinit var btnInserir: Button
    private lateinit var btnAcessar: Button
    private lateinit var btnInfo: Button
    private lateinit var btnNotas: Button
    private lateinit var viewDarken: View
    private lateinit var viewClickInterceptor: View
    private lateinit var txtTutorial: TextView

    // Registra o lançador de arquivos
    private val selecionarPdfLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val intent = android.content.Intent(this, VisualizacaoActivity::class.java)
            intent.putExtra("PDF_URI", uri.toString())
            startActivity(intent)
        }
    }

    private var fotoUri: Uri? = null

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { sucesso ->

            if (sucesso) {

                fotoUri?.let { uri ->

                    val intent =
                        Intent(
                            this,
                            VisualizacaoActivity::class.java
                        )

                    intent.putExtra(
                        "IMAGEM_URI",
                        uri.toString()
                    )

                    startActivity(intent)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_inicial)

        // Inicialização do SharedPreferences
        prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

        // Mapeando os Views
        btnDigitalizar = findViewById(R.id.btnDigitalizar)
        btnInserir = findViewById(R.id.btnInserir)
        btnAcessar = findViewById(R.id.btnAcessar)
        btnInfo = findViewById(R.id.btnInfo)
        btnNotas = findViewById(R.id.btnNotas)
        viewDarken = findViewById(R.id.viewDarken)
        viewClickInterceptor = findViewById(R.id.viewClickInterceptor)
        txtTutorial = findViewById(R.id.txtTutorial)

        // Lógica para verificar se o tutorial deve iniciar
        val tutorialJaVisto = prefs.getBoolean("tutorial_visto", false)
        val sempreMostrar = prefs.getBoolean("sempre_mostrar_tutorial", false)

        if (!tutorialJaVisto || sempreMostrar) {
            iniciarTutorial()
        }

        // Lógica do Clique no Tutorial
        viewClickInterceptor.setOnClickListener {
            tutorialStep++
            mostrarPassoTutorial()
        }

        // Ações dos Botões Normais
        btnInfo.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Informações")
                .setMessage("Informações sobre o app, contato, questionário e outras informações úteis.")
                .setPositiveButton("Fechar", null)
                .show()
        }

//        btnNotas.setOnClickListener {
//            // Verifica o estado atual para marcar/desmarcar a caixa ao abrir
//            val isSempreMostrar = prefs.getBoolean("sempre_mostrar_tutorial", false)
//            val options = arrayOf("Sempre mostrar os tutoriais ao acessar")
//            val checkedItems = booleanArrayOf(isSempreMostrar)
//
//            MaterialAlertDialogBuilder(this)
//                .setTitle("n  Notas")
//                .setMessage("Notas contendo manuais de uso do aplicativo.\n")
//                .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
//                    // Salva a preferência do usuário em tempo real
//                    prefs.edit().putBoolean("sempre_mostrar_tutorial", isChecked).apply()
//                }
//                .setPositiveButton("Fechar", null)
//                .show()
//        }

        btnNotas.setOnClickListener {
            // 1. Cria um layout vertical programaticamente
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(64, 32, 64, 32) // Margens internas para ficar bonito
            }

            // 2. Cria o texto da mensagem
            val textoNotas = android.widget.TextView(this).apply {
                text = "Notas contendo manuais de uso do aplicativo.\n"
                textSize = 16f
            }

            // 3. Cria a CheckBox e já define o valor salvo anteriormente
            val checkBoxTutorial = android.widget.CheckBox(this).apply {
                text = "Sempre mostrar os tutoriais ao acessar"
                isChecked = prefs.getBoolean("sempre_mostrar_tutorial", false)

                // Salva a opção no banco de dados assim que o usuário clica
                setOnCheckedChangeListener { _, isChecked ->
                    prefs.edit().putBoolean("sempre_mostrar_tutorial", isChecked).apply()
                }
            }

            // 4. Adiciona o texto e a checkbox dentro do layout
            layout.addView(textoNotas)
            layout.addView(checkBoxTutorial)

            // 5. Exibe o Alerta passando o layout customizado (.setView)
            MaterialAlertDialogBuilder(this)
                .setTitle("Notas")
                .setView(layout) // Usa o layout em vez de .setMessage()
                .setPositiveButton("Fechar", null)
                .show()
        }

        //teste banco
        btnDigitalizar.setOnClickListener {

            abrirCamera()
        }
        btnInserir.setOnClickListener { selecionarPdfLauncher.launch("application/pdf") }
        btnAcessar.setOnClickListener {

            val intent =
                android.content.Intent(
                    this,
                    AcessarDocumentosActivity::class.java
                )

            startActivity(intent)
        }
    }

    private fun abrirCamera() {

        try {

            val pasta =
                File(
                    cacheDir,
                    "camera"
                )

            if (!pasta.exists()) {
                pasta.mkdirs()
            }


            val arquivoFoto =
                File.createTempFile(
                    "documento_",
                    ".jpg",
                    pasta
                )


            val uri =
                FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    arquivoFoto
                )

            fotoUri = uri

            cameraLauncher.launch(uri)

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Não foi possível abrir a câmera",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // FUNÇÕES DO TUTORIAL
    // ==========================================

    private fun iniciarTutorial() {
        viewDarken.visibility = View.VISIBLE
        txtTutorial.visibility = View.VISIBLE
        viewClickInterceptor.visibility = View.VISIBLE
        tutorialStep = 0
        mostrarPassoTutorial()
    }

    private fun mostrarPassoTutorial() {
        // Primeiro, volta todos os botões para a elevação normal (atrás da camada escura)
        resetarElevacaoBotoes()

        // Ajusta o foco e o texto baseado no passo atual
        when (tutorialStep) {
            0 -> {
                txtTutorial.text = "Bem-vindo! Vamos fazer um rápido tour pelo aplicativo.\n\n(Toque na tela para avançar)"
            }
            1 -> {
                txtTutorial.text = "Aqui temos 3 possíveis opções principais para o gerenciamento."
                btnDigitalizar.translationZ = 50f
                btnInserir.translationZ = 50f
                btnAcessar.translationZ = 50f
            }
            2 -> {
                txtTutorial.text = "1. Digitalizar: Use a câmera para escanear novos arquivos físicos."
                btnDigitalizar.translationZ = 50f
            }
            3 -> {
                txtTutorial.text = "2. Inserir: Faça o upload de arquivos já existentes no seu dispositivo."
                btnInserir.translationZ = 50f
            }
            4 -> {
                txtTutorial.text = "3. Acessar: Visualize os documentos que você já processou."
                btnAcessar.translationZ = 50f
            }
            5 -> {
                txtTutorial.text = "Botão 'i': Acesse as informações e contatos do aplicativo."
                btnInfo.translationZ = 50f
            }
            6 -> {
                txtTutorial.text = "Botão 'n': Acesse os manuais e configure a exibição do tutorial."
                btnNotas.translationZ = 50f
            }
            7 -> {
                // Finaliza o tutorial
                encerrarTutorial()
            }
        }
    }

    private fun resetarElevacaoBotoes() {
        btnDigitalizar.translationZ = 0f
        btnInserir.translationZ = 0f
        btnAcessar.translationZ = 0f
        btnInfo.translationZ = 0f
        btnNotas.translationZ = 0f
    }

    private fun encerrarTutorial() {
        // Esconde as telas do tutorial
        viewDarken.visibility = View.GONE
        txtTutorial.visibility = View.GONE
        viewClickInterceptor.visibility = View.GONE
        resetarElevacaoBotoes()

        // Marca no sistema que o tutorial foi concluído
        prefs.edit().putBoolean("tutorial_visto", true).apply()
    }
}
//package com.example.ged
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//
//class MenuInicial : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.menu_inicial)
//
//        val btnInfo = findViewById<Button>(R.id.btnInfo)
//        val btnNotas = findViewById<Button>(R.id.btnNotas)
//        val btnDigitalizar = findViewById<Button>(R.id.btnDigitalizar)
//        val btnInserir = findViewById<Button>(R.id.btnInserir)
//        val btnAcessar = findViewById<Button>(R.id.btnAcessar)
//
//        // 2. Para o botão Info:
//        btnInfo.setOnClickListener {
//            MaterialAlertDialogBuilder(this)
//                .setTitle("i  Informações")
//                .setMessage("Informações sobre o app, contato, questionário e outras informações úteis.")
//                .setPositiveButton("Fechar", null)
//                .show()
//        }
//
//// 3. Para o botão Notas:
//        btnNotas.setOnClickListener {
//            val options = arrayOf("Desejo rever as notas de primeiro acesso")
//            val checkedItems = booleanArrayOf(false)
//
//            MaterialAlertDialogBuilder(this)
//                .setTitle("n  Notas")
//                .setMessage("Notas contendo manuais de uso do aplicativo.\n")
//                .setMultiChoiceItems(options, checkedItems) { _, _, _ ->
//                    // Lógica do checkbox
//                }
//                .setPositiveButton("Fechar", null)
//                .show()
//        }
//
//        // Navegação para outras telas
//        btnDigitalizar.setOnClickListener {
//            Toast.makeText(this, "Abrir tela de Digitalizar", Toast.LENGTH_SHORT).show()
//            // val intent = Intent(this, DigitalizarActivity::class.java)
//            // startActivity(intent)
//        }
//
//        btnInserir.setOnClickListener {
//            Toast.makeText(this, "Abrir tela de Inserir", Toast.LENGTH_SHORT).show()
//        }
//
//        btnAcessar.setOnClickListener {
//            Toast.makeText(this, "Abrir tela de Acessar Documentos", Toast.LENGTH_SHORT).show()
//        }
//    }
//}