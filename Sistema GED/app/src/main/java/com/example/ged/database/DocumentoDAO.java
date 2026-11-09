package com.example.ged.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ged.model.Documento;

import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    private DatabaseHelper databaseHelper;

    public DocumentoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public long inserir(Documento documento) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                DatabaseHelper.COL_NOME,
                documento.getNomeArquivo()
        );

        values.put(
                DatabaseHelper.COL_CAMINHO,
                documento.getCaminhoArquivo()
        );

        values.put(
                DatabaseHelper.COL_DATA,
                documento.getDataCadastro()
        );

        values.put(
                DatabaseHelper.COL_LINHA_DIGITAVEL,
                documento.getLinhaDigitavel()
        );

        if (documento.getValor() != null) {
            values.put(
                    DatabaseHelper.COL_VALOR,
                    documento.getValor()
            );
        }

        values.put(
                DatabaseHelper.COL_VENCIMENTO,
                documento.getVencimento()
        );

        values.put(
                DatabaseHelper.COL_BENEFICIARIO,
                documento.getBeneficiario()
        );

        values.put(
                DatabaseHelper.COL_TEXTO_OCR,
                documento.getTextoOCR()
        );

        long id = db.insert(
                DatabaseHelper.TABLE_DOCUMENTOS,
                null,
                values
        );

        db.close();

        return id;
    }

    public List<Documento> listarTodos() {

        List<Documento> documentos = new ArrayList<>();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_DOCUMENTOS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COL_ID + " DESC"
        );

        if (cursor.moveToFirst()) {

            do {

                Documento documento = new Documento();

                documento.setId(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_ID
                                )
                        )
                );

                documento.setNomeArquivo(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_NOME
                                )
                        )
                );

                documento.setCaminhoArquivo(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_CAMINHO
                                )
                        )
                );

                documento.setDataCadastro(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_DATA
                                )
                        )
                );

                documento.setLinhaDigitavel(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_LINHA_DIGITAVEL
                                )
                        )
                );

                int valorIndex =
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_VALOR
                        );

                if (!cursor.isNull(valorIndex)) {
                    documento.setValor(
                            cursor.getDouble(valorIndex)
                    );
                }

                documento.setVencimento(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_VENCIMENTO
                                )
                        )
                );

                documento.setBeneficiario(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_BENEFICIARIO
                                )
                        )
                );

                documento.setTextoOCR(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_TEXTO_OCR
                                )
                        )
                );

                documentos.add(documento);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return documentos;
    }

    public int excluir(long id) {

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        int resultado = db.delete(
                DatabaseHelper.TABLE_DOCUMENTOS,
                DatabaseHelper.COL_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();

        return resultado;
    }
}
