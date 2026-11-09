package com.example.ged.ocr

import java.text.Normalizer

data class DadosExtraidosOCR(
    val linhaDigitavel: String?,
    val valor: Double?,
    val vencimento: String?,
    val beneficiario: String?
)

object ExtratorDadosOCR {

    fun extrair(textoOcr: String): DadosExtraidosOCR {

        val texto = normalizarTexto(textoOcr)

        return DadosExtraidosOCR(
            linhaDigitavel = extrairLinhaDigitavel(texto),
            valor = extrairValor(texto),
            vencimento = extrairVencimento(texto),
            beneficiario = extrairBeneficiario(texto)
        )
    }


    private fun normalizarTexto(texto: String): String {

        return texto
            .replace("\r", "")
            .replace(Regex("[ \t]+"), " ")
            .trim()
    }


    /*
     * Usado somente para comparação de palavras.
     *
     * Exemplo:
     *
     * Beneficiário -> beneficiario
     * Beneiiciário  -> beneiiciario
     * Vencimento    -> vencimento
     */
    private fun normalizarParaBusca(texto: String): String {

        val semAcentos = Normalizer.normalize(
            texto.lowercase(),
            Normalizer.Form.NFD
        ).replace(
            Regex("\\p{InCombiningDiacriticalMarks}+"),
            ""
        )

        return semAcentos
    }


    // =====================================================
    // LINHA DIGITÁVEL
    // =====================================================

    private fun extrairLinhaDigitavel(
        texto: String
    ): String? {

        /*
         * Formato típico de boleto bancário:
         *
         * 03399.07537 02510.001577
         * 21657.301012 4 15630000018537
         *
         * O código "033-7" que pode aparecer antes NÃO
         * faz parte da linha digitável.
         */

        val regexBoleto = Regex(
            """(?<!\d)(\d{5}[.\s-]?\d{5})\s+""" +
                    """(\d{5}[.\s-]?\d{6})\s+""" +
                    """(\d{5}[.\s-]?\d{6})\s+""" +
                    """(\d)\s+""" +
                    """(\d{14})(?!\d)"""
        )


        val encontrado =
            regexBoleto.find(texto)


        if (encontrado != null) {

            val somenteNumeros =
                encontrado.value.replace(
                    Regex("[^0-9]"),
                    ""
                )

            if (somenteNumeros.length == 47) {
                return somenteNumeros
            }
        }


        /*
         * Segunda tentativa.
         *
         * Analisa cada linha separadamente para
         * contas de arrecadação de 48 dígitos.
         */

        val linhas = texto.lines()


        for (linha in linhas) {

            val numeros =
                linha.replace(
                    Regex("[^0-9]"),
                    ""
                )

            if (numeros.length == 48) {
                return numeros
            }
        }


        return null
    }


    // =====================================================
    // VENCIMENTO
    // =====================================================

    private fun extrairVencimento(
        texto: String
    ): String? {

        val linhas = texto.lines()


        /*
         * Aceita:
         *
         * 08/09/2026
         * 08.09.2026
         * 08-09-2026
         * 08.09 2026
         * 0809 2026
         */

        val regexData = Regex(
            """\b(\d{2})\s*[./-]?\s*(\d{2})\s*[./-]?\s*(\d{4})\b"""
        )


        for (i in linhas.indices) {

            val linhaBusca =
                normalizarParaBusca(
                    linhas[i]
                )


            /*
             * Usamos "ven" em vez de "vencimento".
             *
             * Assim também reconhecemos erros do OCR:
             *
             * vencimento
             * venaimento
             * venclmento
             */
            if (linhaBusca.contains("ven")) {


                // Tenta a própria linha
                extrairDataValida(
                    linhas[i],
                    regexData
                )?.let {

                    return it
                }


                /*
                 * Procura nas próximas duas linhas.
                 */
                for (
                j in (i + 1)..minOf(
                    i + 2,
                    linhas.lastIndex
                )
                ) {

                    extrairDataValida(
                        linhas[j],
                        regexData
                    )?.let {

                        return it
                    }
                }
            }
        }


        return null
    }


    private fun extrairDataValida(
        texto: String,
        regex: Regex
    ): String? {

        val resultado =
            regex.find(texto)
                ?: return null


        val dia =
            resultado.groupValues[1]
                .toIntOrNull()
                ?: return null


        val mes =
            resultado.groupValues[2]
                .toIntOrNull()
                ?: return null


        val ano =
            resultado.groupValues[3]
                .toIntOrNull()
                ?: return null


        if (dia !in 1..31) {
            return null
        }


        if (mes !in 1..12) {
            return null
        }


        if (ano !in 2000..2100) {
            return null
        }


        return String.format(
            "%02d/%02d/%04d",
            dia,
            mes,
            ano
        )
    }


    // =====================================================
    // VALOR
    // =====================================================

    private fun extrairValor(
        texto: String
    ): Double? {

        val linhas =
            texto.lines()


        val regexValor = Regex(
            """(?:R\$\s*)?(\d{1,3}(?:\.\d{3})*|\d+),(\d{2})"""
        )


        val rotulos =
            listOf(
                "valor do documento",
                "valor documento",
                "valor total",
                "valor cobrado"
            )


        for (i in linhas.indices) {

            val linhaBusca =
                normalizarParaBusca(
                    linhas[i]
                )


            val encontrouRotulo =
                rotulos.any {

                    linhaBusca.contains(
                        normalizarParaBusca(it)
                    )
                }


            if (!encontrouRotulo) {
                continue
            }


            regexValor
                .find(linhas[i])
                ?.let {

                    return converterValor(
                        it.value
                    )
                }


            if (i + 1 < linhas.size) {

                regexValor
                    .find(linhas[i + 1])
                    ?.let {

                        return converterValor(
                            it.value
                        )
                    }
            }
        }


        return null
    }


    private fun converterValor(
        texto: String
    ): Double? {

        return texto
            .replace("R$", "")
            .replace(" ", "")
            .replace(".", "")
            .replace(",", ".")
            .toDoubleOrNull()
    }


    // =====================================================
    // BENEFICIÁRIO
    // =====================================================

    private fun extrairBeneficiario(
        texto: String
    ): String? {

        val linhas =
            texto.lines()
                .map {
                    it.trim()
                }
                .filter {
                    it.isNotBlank()
                }


        for (i in linhas.indices) {

            val linhaBusca =
                normalizarParaBusca(
                    linhas[i]
                )


            /*
             * O OCR do seu teste produziu:
             *
             * Beneiiciário
             *
             * Por isso não usamos apenas
             * "beneficiario".
             */

            val pareceBeneficiario =

                linhaBusca.contains(
                    "benef"
                ) ||

                        linhaBusca.contains(
                            "benei"
                        ) ||

                        linhaBusca.contains(
                            "benel"
                        )


            if (!pareceBeneficiario) {
                continue
            }


            /*
             * Caso o nome esteja na própria linha:
             *
             * Beneficiário: EMPRESA LTDA
             */

            val mesmaLinha =
                limparBeneficiario(
                    linhas[i]
                )


            /*
             * Só aceita a mesma linha se realmente
             * houver conteúdo depois do rótulo.
             */
            if (
                mesmaLinha != null &&
                mesmaLinha.length >= 4
            ) {

                return mesmaLinha
            }


            /*
             * Normalmente o boleto aparece:
             *
             * Beneficiário / Agência...
             * EMPRESA LTDA CNPJ: ...
             *
             * Então verificamos as próximas
             * três linhas.
             */

            for (
            j in (i + 1)..minOf(
                i + 3,
                linhas.lastIndex
            )
            ) {

                val candidato =
                    linhas[j]


                val candidatoLimpo =
                    limparLinhaEmpresa(
                        candidato
                    )


                if (
                    candidatoLimpo != null &&
                    pareceNomeEmpresa(
                        candidatoLimpo
                    )
                ) {

                    return candidatoLimpo
                }
            }
        }


        return null
    }


    private fun limparBeneficiario(
        linha: String
    ): String? {

        val normalizada =
            normalizarParaBusca(linha)


        val posicoes =
            listOf(
                normalizada.indexOf("benef"),
                normalizada.indexOf("benei"),
                normalizada.indexOf("benel")
            )
                .filter {
                    it >= 0
                }


        if (posicoes.isEmpty()) {
            return null
        }


        /*
         * Se a linha contém Agência/Código,
         * provavelmente é apenas o cabeçalho.
         */

        if (
            normalizada.contains("agencia") ||
            normalizada.contains("codigo")
        ) {
            return null
        }


        /*
         * Não retornamos a própria palavra
         * Beneficiário.
         */

        if (
            linha.length < 20
        ) {
            return null
        }


        return null
    }


    private fun limparLinhaEmpresa(
        linha: String
    ): String? {

        var resultado =
            linha.trim()


        // Remove CNPJ e tudo que vier depois
        resultado =
            resultado.replace(
                Regex(
                    """(?i)\s*CNPJ\s*[:\-]?.*$"""
                ),
                ""
            )


        // Remove CGC e tudo que vier depois
        resultado =
            resultado.replace(
                Regex(
                    """(?i)\s*CGC\s*[:\-]?.*$"""
                ),
                ""
            )


        // Remove espaços exagerados, tabs etc.
        resultado =
            resultado.replace(
                Regex("""\s+"""),
                " "
            )


        resultado =
            resultado.trim(
                ' ',
                '-',
                '|',
                ':'
            )


        if (resultado.length < 4) {
            return null
        }

        // Limita o beneficiário a no máximo 25 caracteres
        if (resultado.length > 25) {
            resultado = resultado.take(25).trim()
        }

        return resultado
    }

    private fun pareceNomeEmpresa(
        texto: String
    ): Boolean {

        /*
         * Deve possuir letras.
         */

        val quantidadeLetras =
            texto.count {
                it.isLetter()
            }


        if (quantidadeLetras < 4) {
            return false
        }


        /*
         * Não pode ser basicamente números.
         */

        val quantidadeNumeros =
            texto.count {
                it.isDigit()
            }


        if (
            quantidadeNumeros >
            quantidadeLetras
        ) {
            return false
        }


        val busca =
            normalizarParaBusca(texto)


        val termosInvalidos =
            listOf(
                "vencimento",
                "valor do documento",
                "desconto",
                "abatimento",
                "instrucoes",
                "pagador",
                "nosso numero",
                "agencia",
                "codigo"
            )


        return termosInvalidos.none {

            busca.contains(
                normalizarParaBusca(it)
            )
        }
    }
}