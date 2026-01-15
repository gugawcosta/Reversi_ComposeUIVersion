package reversi_data.mongodb
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider

/**
 * Objeto singleton responsável por configurar a conexão com o MongoDB
 * e fornecer acesso à coleção tipada de jogos [GameState].
 * Utiliza o driver oficial do MongoDB com suporte a POJOs/Kotlin.
 */

object MongoRepository {
    private const val URI = "mongodb+srv://alunotds:reversi2026@reversicluster.5qf1gsm.mongodb.net/?appName=ReversiCluster" // definida por mim (gustavo)

    // Configuração para permitir converter Classes Kotlin automaticamente
    private val pojoCodecRegistry = fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    // Configuração do Cliente
    private val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(URI))
        .codecRegistry(pojoCodecRegistry)
        .build()

    private val client = MongoClients.create(settings)
    private val database = client.getDatabase("ReversiDB") // Nome da nossa BD

    // 4. Acesso à coleção tipada
    val gamesCollection: MongoCollection<GameState> = database.getCollection("games", GameState::class.java)

    /**
     * Exemplos de operações CRUD básicas na coleção de jogos.
     */
    // Exemplo: Criar um jogo
    fun saveGame(game: GameState) {
        gamesCollection.insertOne(game)
    }

    // Exemplo: Procurar um jogo
    fun getGame(name: String): GameState? {
        return gamesCollection.find(Filters.eq("_id", name)).first()
    }
}

/**
 * Função principal para testar a conexão com o MongoDB.
 * Tenta contar quantos documentos existem na coleção de jogos.
 * Imprime o resultado ou o erro ocorrido.
 */
fun main() {
    try {
        // Tenta apenas contar quantos jogos existem
        val count = MongoRepository.gamesCollection.countDocuments()
        println("SUCESSO! Conectado ao MongoDB. Jogos encontrados: $count")
    } catch (e: Exception) {
        println("ERRO: Não foi possível conectar.")
        println("Detalhes: ${e.message}")
        e.printStackTrace() // Mostra o erro completo
    }
}