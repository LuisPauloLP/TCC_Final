package com.example.ged.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ged.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_DOCUMENTOS = "documentos";

    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome_arquivo";
    public static final String COL_CAMINHO = "caminho_arquivo";
    public static final String COL_DATA = "data_cadastro";

    public static final String COL_LINHA_DIGITAVEL = "linha_digitavel";
    public static final String COL_VALOR = "valor";
    public static final String COL_VENCIMENTO = "vencimento";
    public static final String COL_BENEFICIARIO = "beneficiario";
    public static final String COL_TEXTO_OCR = "texto_ocr";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_DOCUMENTOS + " (" +

                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                COL_NOME + " TEXT NOT NULL, " +

                COL_CAMINHO + " TEXT NOT NULL, " +

                COL_DATA + " TEXT, " +

                COL_LINHA_DIGITAVEL + " TEXT, " +

                COL_VALOR + " REAL, " +

                COL_VENCIMENTO + " TEXT, " +

                COL_BENEFICIARIO + " TEXT, " +
                COL_TEXTO_OCR + " TEXT" +

                ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_DOCUMENTOS
        );

        onCreate(db);
    }
}
