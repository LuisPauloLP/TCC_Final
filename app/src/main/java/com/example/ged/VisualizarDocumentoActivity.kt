package com.example.ged

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VisualizarDocumentoActivity :
    AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_visualizar_documento
        )


        val imgDocumento =
            findViewById<ImageView>(
                R.id.imgDocumento
            )


        val txtNomeDocumento =
            findViewById<TextView>(
                R.id.txtNomeDocumento
            )


        val caminho =
            intent.getStringExtra(
                "CAMINHO_DOCUMENTO"
            )


        val nome =
            intent.getStringExtra(
                "NOME_DOCUMENTO"
            )


        txtNomeDocumento.text =
            nome ?: "Documento"


        if (caminho == null) {

            Toast.makeText(
                this,
                "Arquivo não encontrado",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        val arquivo =
            File(caminho)


        if (!arquivo.exists()) {

            Toast.makeText(
                this,
                "O arquivo não existe",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        val bitmap =
            BitmapFactory.decodeFile(
                arquivo.absolutePath
            )


        imgDocumento.setImageBitmap(
            bitmap
        )
    }
}