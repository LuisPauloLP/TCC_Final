package com.example.ged

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ged.adapter.DocumentoAdapter
import com.example.ged.database.DocumentoDAO
import com.example.ged.model.Documento
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Locale
import com.example.ged.network.RetrofitClient
import com.example.ged.network.dto.ConsolidadoRequest
import com.example.ged.network.dto.ConsolidadoResponse
import com.example.ged.network.dto.DocumentoRequest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import android.util.Log

class AcessarDocumentosActivity :
    AppCompatActivity() {

    private lateinit var documentoDAO: DocumentoDAO
    private lateinit var adapter: DocumentoAdapter

    private lateinit var layoutAcoesMultiplas: LinearLayout

    private lateinit var edtPesquisar: EditText

    private lateinit var btnFiltroNome: Button
    private lateinit var btnFiltroLinhaDigitavel: Button
    private lateinit var btnFiltroValor: Button
    private lateinit var btnFiltroVencimento: Button
    private lateinit var btnFiltroBeneficiario: Button

    private lateinit var btnSelecionarTodosFiltros: Button
    private lateinit var btnLimparFiltros: Button

    private var idsSelecionados: Set<Long> =
        emptySet()

    /*
     * Por padrão, todos os campos participam
     * da pesquisa.
     */
    private val filtrosSelecionados =
        mutableSetOf(
            "nome",
            "linhaDigitavel",
            "valor",
            "vencimento",
            "beneficiario"
        )

    /*
     * Mantém todos os documentos carregados
     * do SQLite.
     *
     * O Adapter recebe apenas os documentos
     * que passaram pelo filtro.
     */
    private var todosDocumentos =
        listOf<Documento>()


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_acessar_documentos
        )


        documentoDAO =
            DocumentoDAO(this)

        edtPesquisar =
            findViewById(
                R.id.edtPesquisar
            )


        btnFiltroNome =
            findViewById(
                R.id.btnFiltroNome
            )

        btnFiltroLinhaDigitavel =
            findViewById(
                R.id.btnFiltroLinhaDigitavel
            )

        btnFiltroValor =
            findViewById(
                R.id.btnFiltroValor
            )

        btnFiltroVencimento =
            findViewById(
                R.id.btnFiltroVencimento
            )

        btnFiltroBeneficiario =
            findViewById(
                R.id.btnFiltroBeneficiario
            )


        btnSelecionarTodosFiltros =
            findViewById(
                R.id.btnSelecionarTodosFiltros
            )

        btnLimparFiltros =
            findViewById(
                R.id.btnLimparFiltros
            )


        val recyclerDocumentos =
            findViewById<RecyclerView>(
                R.id.recyclerDocumentos
            )


        val btnFiltros =
            findViewById<Button>(
                R.id.btnFiltros
            )


        val layoutFiltros =
            findViewById<LinearLayout>(
                R.id.layoutFiltros
            )


        layoutAcoesMultiplas =
            findViewById(
                R.id.layoutAcoesMultiplas
            )


        val btnEnviarDocumentos =
            findViewById<Button>(
                R.id.btnEnviarDocumentos
            )


        val btnDeletarSelecionados =
            findViewById<Button>(
                R.id.btnDeletarSelecionados
            )


        // ==============================
        // FILTROS
        // ==============================

        btnFiltros.setOnClickListener {

            if (
                layoutFiltros.visibility ==
                View.GONE
            ) {

                layoutFiltros.visibility =
                    View.VISIBLE

                btnFiltros.text =
                    "Filtros ▲"

            } else {

                layoutFiltros.visibility =
                    View.GONE

                btnFiltros.text =
                    "Filtros ▼"
            }
        }

        btnFiltroNome.setOnClickListener {
            alternarFiltro("nome")
        }

        btnFiltroLinhaDigitavel.setOnClickListener {
            alternarFiltro("linhaDigitavel")
        }

        btnFiltroValor.setOnClickListener {
            alternarFiltro("valor")
        }

        btnFiltroVencimento.setOnClickListener {
            alternarFiltro("vencimento")
        }

        btnFiltroBeneficiario.setOnClickListener {
            alternarFiltro("beneficiario")
        }

        btnSelecionarTodosFiltros.setOnClickListener {

            filtrosSelecionados.clear()

            filtrosSelecionados.addAll(
                listOf(
                    "nome",
                    "linhaDigitavel",
                    "valor",
                    "vencimento",
                    "beneficiario"
                )
            )

            atualizarVisualFiltros()

            filtrarDocumentos(
                edtPesquisar.text.toString()
            )
        }

        btnLimparFiltros.setOnClickListener {

            filtrosSelecionados.clear()

            atualizarVisualFiltros()

            filtrarDocumentos(
                edtPesquisar.text.toString()
            )
        }

        edtPesquisar.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filtrarDocumentos(
                        s?.toString() ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )


        // ==============================
        // RECYCLER VIEW
        // ==============================

        recyclerDocumentos.layoutManager =
            LinearLayoutManager(this)


        adapter = DocumentoAdapter(

            mutableListOf(),

            onAbrir = { documento ->

                abrirDocumento(documento)
            },

            onDeletar = { documento ->

                confirmarExclusaoUnica(
                    documento
                )
            },

            onSelecaoMultiplaAlterada =
                { ids ->

                    idsSelecionados = ids

                    layoutAcoesMultiplas.visibility =
                        if (ids.isEmpty()) {

                            View.GONE

                        } else {

                            View.VISIBLE
                        }
                }
        )


        recyclerDocumentos.adapter =
            adapter


        // ==============================
        // BOTÕES INFERIORES
        // ==============================

        btnEnviarDocumentos
            .setOnClickListener {

                enviarDocumentosSelecionados(
                    btnEnviarDocumentos
                )
            }


        btnDeletarSelecionados
            .setOnClickListener {

                confirmarExclusaoMultipla()
            }


        carregarDocumentos()
    }

    private fun enviarDocumentosSelecionados(
        btnEnviarDocumentos: Button
    ) {

        // ==============================
        // 1. VERIFICAR SE HÁ SELEÇÃO
        // ==============================

        if (idsSelecionados.isEmpty()) {

            Toast.makeText(
                this,
                "Selecione pelo menos um documento",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ==============================
        // 2. PEGAR DOCUMENTOS
        // ==============================

        val documentosSelecionados =
            todosDocumentos.filter { documento ->

                idsSelecionados.contains(
                    documento.id
                )
            }


        if (documentosSelecionados.isEmpty()) {

            Toast.makeText(
                this,
                "Nenhum documento selecionado foi encontrado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ==============================
        // 3. CONVERTER PARA DTO DA API
        // ==============================

        val documentosRequest =
            documentosSelecionados.map { documento ->

                DocumentoRequest(

                    nomeArquivo =
                        documento.nomeArquivo,

                    linhaDigitavel =
                        documento.linhaDigitavel,

                    valor =
                        documento.valor,

                    vencimento =
                        documento.vencimento,

                    beneficiario =
                        documento.beneficiario
                )
            }


        // ==============================
        // 4. CRIAR CONSOLIDADO
        // ==============================

        val request =
            ConsolidadoRequest(
                documentos = documentosRequest
            )


        // Evita vários envios
        btnEnviarDocumentos.isEnabled = false

        btnEnviarDocumentos.text =
            "Enviando..."


        // ==============================
        // 5. ENVIAR PARA SPRING BOOT
        // ==============================

        RetrofitClient.api
            .criarConsolidado(request)
            .enqueue(

                object :
                    Callback<ConsolidadoResponse> {


                    override fun onResponse(
                        call: Call<ConsolidadoResponse>,
                        response: Response<ConsolidadoResponse>
                    ) {

                        btnEnviarDocumentos.isEnabled =
                            true

                        btnEnviarDocumentos.text =
                            "Enviar documentos"


                        if (response.isSuccessful) {

                            val consolidado =
                                response.body()


                            if (consolidado != null) {

                                val intent = Intent(
                                    this@AcessarDocumentosActivity,
                                    QRCodeActivity::class.java
                                )

                                intent.putExtra(
                                    "URL_CONSOLIDADO",
                                    consolidado.url
                                )

                                intent.putExtra(
                                    "CODIGO_ACESSO",
                                    consolidado.codigoAcesso
                                )

                                intent.putExtra(
                                    "QUANTIDADE_DOCUMENTOS",
                                    documentosSelecionados.size
                                )

                                startActivity(intent)
                            }

                        } else {

                            Log.e(
                                "GED_API",
                                "Erro HTTP: ${response.code()}"
                            )


                            Toast.makeText(
                                this@AcessarDocumentosActivity,
                                "Erro no servidor: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<ConsolidadoResponse>,
                        t: Throwable
                    ) {

                        btnEnviarDocumentos.isEnabled =
                            true

                        btnEnviarDocumentos.text =
                            "Enviar documentos"


                        Log.e(
                            "GED_API",
                            "Erro de conexão",
                            t
                        )


                        Toast.makeText(
                            this@AcessarDocumentosActivity,
                            "Erro de conexão: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun filtrarDocumentos(
        pesquisa: String
    ) {

        val termo =
            pesquisa
                .trim()
                .lowercase()


        // Nenhum filtro selecionado
        if (filtrosSelecionados.isEmpty()) {

            adapter.atualizarLista(
                emptyList()
            )

            return
        }


        // Campo de pesquisa vazio:
        // mostra todos os documentos
        if (termo.isEmpty()) {

            adapter.atualizarLista(
                todosDocumentos
            )

            return
        }


        val documentosFiltrados =
            todosDocumentos.filter { documento ->

                var encontrou = false


                // =========================
                // NOME
                // =========================

                if (
                    "nome" in filtrosSelecionados &&
                    documento.nomeArquivo
                        ?.lowercase()
                        ?.contains(termo) == true
                ) {

                    encontrou = true
                }


                // =========================
                // LINHA DIGITÁVEL
                // =========================

                if (
                    "linhaDigitavel" in filtrosSelecionados &&
                    documento.linhaDigitavel
                        ?.lowercase()
                        ?.contains(termo) == true
                ) {

                    encontrou = true
                }


                // =========================
                // VALOR
                // =========================

                if (
                    "valor" in filtrosSelecionados &&
                    documento.valor != null
                ) {

                    val valor =
                        String.format(
                            Locale("pt", "BR"),
                            "%.2f",
                            documento.valor
                        )


                    /*
                     * Permite pesquisar:
                     *
                     * 185
                     * 185,37
                     * 185.37
                     */
                    if (
                        valor.contains(termo) ||
                        valor
                            .replace(",", ".")
                            .contains(termo)
                    ) {

                        encontrou = true
                    }
                }


                // =========================
                // VENCIMENTO
                // =========================

                if (
                    "vencimento" in filtrosSelecionados &&
                    documento.vencimento
                        ?.lowercase()
                        ?.contains(termo) == true
                ) {

                    encontrou = true
                }


                // =========================
                // BENEFICIÁRIO
                // =========================

                if (
                    "beneficiario" in filtrosSelecionados &&
                    documento.beneficiario
                        ?.lowercase()
                        ?.contains(termo) == true
                ) {

                    encontrou = true
                }


                encontrou
            }


        adapter.atualizarLista(
            documentosFiltrados
        )
    }

    private fun alternarFiltro(
        filtro: String
    ) {

        if (filtro in filtrosSelecionados) {

            filtrosSelecionados.remove(
                filtro
            )

        } else {

            filtrosSelecionados.add(
                filtro
            )
        }


        atualizarVisualFiltros()


        filtrarDocumentos(
            edtPesquisar.text.toString()
        )
    }

    private fun atualizarVisualFiltros() {

        val nomeSelecionado =
            "nome" in filtrosSelecionados

        val linhaSelecionada =
            "linhaDigitavel" in filtrosSelecionados

        val valorSelecionado =
            "valor" in filtrosSelecionados

        val vencimentoSelecionado =
            "vencimento" in filtrosSelecionados

        val beneficiarioSelecionado =
            "beneficiario" in filtrosSelecionados


        btnFiltroNome.isSelected =
            nomeSelecionado

        btnFiltroLinhaDigitavel.isSelected =
            linhaSelecionada

        btnFiltroValor.isSelected =
            valorSelecionado

        btnFiltroVencimento.isSelected =
            vencimentoSelecionado

        btnFiltroBeneficiario.isSelected =
            beneficiarioSelecionado


        btnFiltroNome.text =
            if (nomeSelecionado)
                "Nome"
            else
                "Nome"


        btnFiltroLinhaDigitavel.text =
            if (linhaSelecionada)
                "Núm. digitável"
            else
                "Núm. digitável"


        btnFiltroValor.text =
            if (valorSelecionado)
                "Valor total"
            else
                "Valor total"


        btnFiltroVencimento.text =
            if (vencimentoSelecionado)
                "Vencimento"
            else
                "Vencimento"


        btnFiltroBeneficiario.text =
            if (beneficiarioSelecionado)
                "Beneficiário"
            else
                "Beneficiário"
    }


    private fun carregarDocumentos() {

        todosDocumentos =
            documentoDAO.listarTodos()


        filtrarDocumentos(
            edtPesquisar.text.toString()
        )


        layoutAcoesMultiplas.visibility =
            View.GONE
    }


    private fun abrirDocumento(
        documento: Documento
    ) {

        val intent =
            Intent(
                this,
                VisualizarDocumentoActivity::class.java
            )

        intent.putExtra(
            "CAMINHO_DOCUMENTO",
            documento.caminhoArquivo
        )

        intent.putExtra(
            "NOME_DOCUMENTO",
            documento.nomeArquivo
        )

        startActivity(intent)
    }


    private fun confirmarExclusaoUnica(
        documento: Documento
    ) {

        MaterialAlertDialogBuilder(this)

            .setTitle(
                "Deletar documento"
            )

            .setMessage(
                "Deseja realmente deletar \"${documento.nomeArquivo}\"?"
            )

            .setNegativeButton(
                "Cancelar",
                null
            )

            .setPositiveButton(
                "Deletar"
            ) { _, _ ->

                excluirDocumento(
                    documento
                )
            }

            .show()
    }


    private fun excluirDocumento(
        documento: Documento
    ) {

        val resultado =
            documentoDAO.excluir(
                documento.id
            )


        if (resultado > 0) {

            // Remove também a imagem física
            // salva pelo aplicativo
            try {

                val arquivo =
                    File(
                        documento.caminhoArquivo
                    )

                if (arquivo.exists()) {
                    arquivo.delete()
                }

            } catch (_: Exception) {
            }

            atualizarVisualFiltros()
            carregarDocumentos()
        }
    }


    private fun confirmarExclusaoMultipla() {

        if (idsSelecionados.isEmpty()) {
            return
        }


        MaterialAlertDialogBuilder(this)

            .setTitle(
                "Deletar documentos"
            )

            .setMessage(
                "Deseja realmente deletar os ${idsSelecionados.size} documentos selecionados?"
            )

            .setNegativeButton(
                "Cancelar",
                null
            )

            .setPositiveButton(
                "Deletar"
            ) { _, _ ->

                deletarSelecionados()
            }

            .show()
    }


    private fun deletarSelecionados() {

        val documentos =
            documentoDAO.listarTodos()


        documentos.forEach { documento ->

            if (
                idsSelecionados.contains(
                    documento.id
                )
            ) {

                documentoDAO.excluir(
                    documento.id
                )


                try {

                    val arquivo =
                        File(
                            documento.caminhoArquivo
                        )

                    if (arquivo.exists()) {
                        arquivo.delete()
                    }

                } catch (_: Exception) {
                }
            }
        }


        idsSelecionados =
            emptySet()


        adapter.limparSelecaoMultipla()

        carregarDocumentos()
    }


    override fun onResume() {

        super.onResume()

        if (::adapter.isInitialized) {
            carregarDocumentos()
        }
    }
}