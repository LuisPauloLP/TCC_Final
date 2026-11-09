package com.example.ged.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ged.R
import com.example.ged.model.Documento
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class DocumentoAdapter(

    private var documentos: MutableList<Documento>,

    private val onAbrir: (Documento) -> Unit,
    private val onDeletar: (Documento) -> Unit,

    private val onSelecaoMultiplaAlterada:
        (Set<Long>) -> Unit

) : RecyclerView.Adapter<DocumentoAdapter.DocumentoViewHolder>() {


    // Documento selecionado com clique normal
    private var documentoSelecionado: Long? = null


    // IDs selecionados no modo múltiplo
    private val selecionadosMultiplos =
        mutableSetOf<Long>()


    private var modoSelecaoMultipla = false


    inner class DocumentoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val card: MaterialCardView =
            itemView.findViewById(R.id.cardDocumento)

        val txtNome: TextView =
            itemView.findViewById(R.id.txtNomeDocumento)

        val txtVencimento: TextView =
            itemView.findViewById(R.id.txtVencimento)

        val txtBeneficiario: TextView =
            itemView.findViewById(R.id.txtBeneficiario)

        val txtValor: TextView =
            itemView.findViewById(R.id.txtValor)

        val txtSelecionado: TextView =
            itemView.findViewById(R.id.txtSelecionado)

        val layoutAcoes: LinearLayout =
            itemView.findViewById(R.id.layoutAcoesDocumento)

        val btnAbrir: Button =
            itemView.findViewById(R.id.btnAbrirDocumento)

        val btnDeletar: Button =
            itemView.findViewById(R.id.btnDeletarDocumento)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DocumentoViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_documento,
                parent,
                false
            )

        return DocumentoViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: DocumentoViewHolder,
        position: Int
    ) {

        val documento = documentos[position]


        holder.txtNome.text =
            documento.nomeArquivo


        holder.txtVencimento.text =
            if (documento.vencimento.isNullOrBlank()) {

                "Venc: --"

            } else {

                "Venc: ${documento.vencimento}"
            }


        holder.txtBeneficiario.text =
            if (documento.beneficiario.isNullOrBlank()) {

                "Beneficiário: --"

            } else {

                documento.beneficiario
            }


        holder.txtValor.text =
            if (documento.valor == null) {

                "R$ --"

            } else {

                String.format(
                    Locale("pt", "BR"),
                    "R$ %.2f",
                    documento.valor
                )
            }


        val estaSelecionadoMultiplo =
            selecionadosMultiplos.contains(documento.id)


        val estaSelecionadoNormal =
            documentoSelecionado == documento.id


        // ==============================
        // APARÊNCIA DA SELEÇÃO
        // ==============================

        if (
            estaSelecionadoMultiplo ||
            estaSelecionadoNormal
        ) {

            holder.card.strokeWidth = 5

            holder.card.setStrokeColor(
                android.graphics.Color.rgb(
                    76,
                    175,
                    80
                )
            )

        } else {

            holder.card.strokeWidth = 1

            holder.card.setStrokeColor(
                android.graphics.Color.LTGRAY
            )
        }


        // Check apenas no modo múltiplo
        holder.txtSelecionado.visibility =
            if (estaSelecionadoMultiplo) {
                View.VISIBLE
            } else {
                View.GONE
            }


        // Botões Abrir / Deletar aparecem
        // somente no clique normal
        holder.layoutAcoes.visibility =
            if (
                estaSelecionadoNormal &&
                !modoSelecaoMultipla
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }


        // ==============================
        // CLIQUE NORMAL
        // ==============================

        holder.card.setOnClickListener {

            if (modoSelecaoMultipla) {

                alternarSelecaoMultipla(documento)

            } else {

                documentoSelecionado =
                    if (
                        documentoSelecionado ==
                        documento.id
                    ) {

                        null

                    } else {

                        documento.id
                    }

                notifyDataSetChanged()
            }
        }


        // ==============================
        // CLIQUE LONGO
        // ==============================

        holder.card.setOnLongClickListener {

            documentoSelecionado = null

            modoSelecaoMultipla = true

            alternarSelecaoMultipla(documento)

            true
        }


        holder.btnAbrir.setOnClickListener {

            onAbrir(documento)
        }


        holder.btnDeletar.setOnClickListener {

            onDeletar(documento)
        }
    }


    private fun alternarSelecaoMultipla(
        documento: Documento
    ) {

        if (
            selecionadosMultiplos
                .contains(documento.id)
        ) {

            selecionadosMultiplos
                .remove(documento.id)

        } else {

            selecionadosMultiplos
                .add(documento.id)
        }


        // Sai automaticamente do modo múltiplo
        // se nada estiver selecionado
        if (selecionadosMultiplos.isEmpty()) {

            modoSelecaoMultipla = false
        }


        onSelecaoMultiplaAlterada(
            selecionadosMultiplos.toSet()
        )


        notifyDataSetChanged()
    }


    fun atualizarLista(
        novaLista: List<Documento>
    ) {

        documentos.clear()

        documentos.addAll(novaLista)

        documentoSelecionado = null

        selecionadosMultiplos.clear()

        modoSelecaoMultipla = false

        notifyDataSetChanged()
    }


    fun limparSelecaoMultipla() {

        selecionadosMultiplos.clear()

        modoSelecaoMultipla = false

        notifyDataSetChanged()

        onSelecaoMultiplaAlterada(
            emptySet()
        )
    }


    override fun getItemCount(): Int {
        return documentos.size
    }
}