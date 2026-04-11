package damA51388.galeriaaleatoria.model

import java.io.Serializable

/**
 * Classe de Dados ImageItem: Representa a entidade fundamental de informação da aplicação.
 * Implementa a interface Serializable para permitir a transmissão de instâncias entre componentes (Activities).
 */
data class ImageItem(
    val id: String,           // Identificador unívoco da imagem
    val url: String,          // Localizador Uniforme de Recursos (endereço remoto da imagem)
    val breed: String,        // Identificação da raça (formato bruto proveniente da API)
    var isLiked: Boolean = false,    // Estado volátil de preferência (Gosto)
    var isFavorite: Boolean = false  // Estado de persistência em favoritos
) : Serializable {

    /**
     * Propriedade computada para formatação estética do nome da raça.
     * Converte identificadores hifenizados em cadeias de caracteres capitalizadas.
     */
    val displayBreed: String
        get() = breed
            .split("-") // Fragmentação da cadeia pelo delimitador hífen
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) } // Capitalização de cada segmento

    companion object {
        /**
         * Método de fabricação (Factory Method) para instanciar ImageItem a partir de um URL.
         * Efetua a extração semântica da raça contida na estrutura do caminho do URL.
         */
        fun fromUrl(url: String): ImageItem {
            // Tentativa de extração da raça com tratamento de exceções (runCatching)
            val breed = runCatching {
                val parts = url.split("/")
                val breedsIndex = parts.indexOf("breeds")
                // A raça encontra-se imediatamente após o segmento "breeds" no URL da Dog CEO API
                if (breedsIndex >= 0) parts[breedsIndex + 1] else "unknown"
            }.getOrDefault("unknown") // Valor por omissão em caso de falha na extração

            return ImageItem(
                id    = url.hashCode().toString(), // Geração de ID baseado no código de dispersão (Hash)
                url   = url,
                breed = breed
            )
        }
    }
}
