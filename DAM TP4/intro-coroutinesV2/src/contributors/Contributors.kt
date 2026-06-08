package contributors

import contributors.Contributors.LoadingStatus.*
import contributors.Variant.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import tasks.*
import java.awt.event.ActionListener
import javax.swing.SwingUtilities
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess


enum class Variant {
    BLOCKING,         // Request1Blocking
    BACKGROUND,       // Request2Background
    CALLBACKS,        // Request3Callbacks
    SUSPEND,          // Request4Coroutine
    CONCURRENT,       // Request5Concurrent
    NOT_CANCELLABLE,  // Request6NotCancellable
    PROGRESS,         // Request6Progress
    CHANNELS          // Request7Channels
}

interface Contributors : CoroutineScope {


    enum class LoadingStatus { INIT, COMPLETED, CANCELED, IN_PROGRESS }
    data class LoadingStateData(
        val status: LoadingStatus = LoadingStatus.INIT, val startTime: Long? = null, val elapsedTime: String = ""
    )
//    private enum class LoadingStatus { COMPLETED, CANCELED, IN_PROGRESS } antigo


    val loadingState: StateFlow<LoadingStateData>


    private fun calculateElapsedTime(startTime: Long): String {
        val time = System.currentTimeMillis() - startTime
        return "${(time / 1000)}.${time % 1000 / 100} sec"
    }

    fun updateLoadingStatus(newStatus: LoadingStateData)

    private fun updateResults(users: List<User>, startTime: Long, completed: Boolean = true) {
        updateContributors(users)
        val status = if (completed) COMPLETED else IN_PROGRESS
        val elapsedTime = calculateElapsedTime(startTime)
        updateLoadingStatus(
            LoadingStateData(
                status = status, startTime = startTime, elapsedTime = elapsedTime
            )
        )
        if (completed) {
            setActionsStatus(newLoadingEnabled = true)
        }
    }/* do prof

    private fun updateResults(
        users: List<User>, startTime: Long, completed: Boolean = true
    ) {
        updateContributors(users)
        updateLoadingStatus(if (completed) COMPLETED else IN_PROGRESS, startTime)
        if (completed) {
            setActionsStatus(newLoadingEnabled = true)
        }
    }*/


    val job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun init() {
        // Start a new loading on 'load' click
        addLoadListener {
            saveParams()
            loadContributors()
        }

        // Save preferences and exit on closing the window
        addOnWindowClosingListener {
            job.cancel()
            saveParams()
            exitProcess(0)
        }

        // Load stored params (user & password values)
        loadInitialParams()
    }

    fun loadContributors() {
        val (username, password, org, _) = getParams()
        val req = RequestData(username, password, org)

        clearResults()
        val service = createGitHubService(req.username, req.password)

        val startTime = System.currentTimeMillis()
        when (getSelectedVariant()) {
            BLOCKING -> { // Blocking UI thread
                val users = loadContributorsBlocking(service, req)
                updateResults(users, startTime)
            }

            BACKGROUND -> { // Blocking a background thread
                loadContributorsBackground(service, req) { users ->
                    SwingUtilities.invokeLater {
                        updateResults(users, startTime)
                    }
                }
            }

            CALLBACKS -> { // Using callbacks
                loadContributorsCallbacks(service, req) { users ->
                    SwingUtilities.invokeLater {
                        updateResults(users, startTime)
                    }
                }
            }

            SUSPEND -> { // Using coroutines
                launch {
                    val users = loadContributorsSuspend(service, req)
                    updateResults(users, startTime)
                }.setUpCancellation()
            }

            CONCURRENT -> { // Performing requests concurrently
                launch {
                    val users = loadContributorsConcurrent(service, req)
                    updateResults(users, startTime)
                }.setUpCancellation()
            }

            NOT_CANCELLABLE -> { // Performing requests in a non-cancellable way
                launch {
                    val users = loadContributorsNotCancellable(service, req)
                    updateResults(users, startTime)
                }.setUpCancellation()
            }

            PROGRESS -> { // Showing progress
                launch(Dispatchers.Default) {
                    loadContributorsProgress(service, req) { users, completed ->
                        withContext(Dispatchers.Main) {
                            updateResults(users, startTime, completed)
                        }
                    }
                }.setUpCancellation()
            }


            CHANNELS -> {

                // Performing requests concurrently and showing progress
                /*launch(Dispatchers.Default) {
                    loadContributorsChannels(service, req) { users, completed ->
                        withContext(Dispatchers.Main) {
                            updateResults(users, startTime, completed)
                        }
                    }
                }.setUpCancellation()

                 */



                launch(Dispatchers.Default) {
                    val progressChannel = Channel<Pair<List<User>, Boolean>>(Channel.BUFFERED) //buffer de 64 espaços por default
                    val a= launch(Dispatchers.Default) {
                        loadContributorsChannels(service, req) { users, completed ->
                            progressChannel.send(Pair(users, completed))// envia um par de utilziadores e bool como especificado no channel
                        }
                        progressChannel.close()  //fecha o canal que ja n é preciso


                    }
                    //a.join() //espera que o loadContribuitorsChannels termine, se usasse o a.setUpCancellation() fechava logo antes de terminar
//ele espera que termine naturalmente, sem precisar de cancelar

                    for ((users, completed) in progressChannel) {
                        withContext(Dispatchers.Main) {
                            updateResults(users, startTime, completed)
                        }
                    }
                    //o for vai lendo a medida que que o launch vai enviando



                }.setUpCancellation()
            }
        }
    }


    private fun clearResults() {
        updateContributors(listOf())
//        updateLoadingStatus(IN_PROGRESS)


//alterou-se para usar as funções override de ContribuitorsUI,
// como também para dividir a responsabilidade,  com o updateLoadingStatus que emite o LoadingStateData para o _loadingState
        // e o observeLoadingStatus que recolhe os valores e trata da formatação e atualização da UI
        updateLoadingStatus(LoadingStateData(IN_PROGRESS))


        setActionsStatus(newLoadingEnabled = false)

    }


    private fun updateLoadingStatus(
        status: LoadingStatus, startTime: Long? = null
    ) {
        val time = if (startTime != null) {
            val time = System.currentTimeMillis() - startTime
            "${(time / 1000)}.${time % 1000 / 100} sec"
        } else ""

        val text = "Loading status: " + when (status) {
            COMPLETED -> "completed in $time"
            IN_PROGRESS -> "in progress $time"
            CANCELED -> "canceled"
            else -> {}
        }
        setLoadingStatus(text, status == IN_PROGRESS)
    }

    private fun Job.setUpCancellation() {
        // make active the 'cancel' button
        setActionsStatus(newLoadingEnabled = false, cancellationEnabled = true)

        val loadingJob = this

        // cancel the loading job if the 'cancel' button was clicked
        val listener = ActionListener {
            loadingJob.cancel()
            //alterou-se para usar as funções override de ContribuitorsUI,
// como também para dividir a responsabilidade,  com o updateLoadingStatus que emite o LoadingStateData para o _loadingState
            // e o observeLoadingStatus que recolhe os valores e trata da formatação e atualização da UI

            updateLoadingStatus(LoadingStateData(CANCELED))
        }
        addCancelListener(listener)

        // update the status and remove the listener after the loading job is completed
        launch {
            loadingJob.join()
            setActionsStatus(newLoadingEnabled = true)
            removeCancelListener(listener)
        }
    }

    fun loadInitialParams() {
        setParams(loadStoredParams())
    }

    fun saveParams() {
        val params = getParams()
        if (params.username.isEmpty() && params.password.isEmpty()) {
            removeStoredParams()
        } else {
            saveParams(params)
        }
    }

    fun getSelectedVariant(): Variant

    fun updateContributors(users: List<User>)

    fun setLoadingStatus(text: String, iconRunning: Boolean)

    fun setActionsStatus(newLoadingEnabled: Boolean, cancellationEnabled: Boolean = false)

    fun addCancelListener(listener: ActionListener)

    fun removeCancelListener(listener: ActionListener)

    fun addLoadListener(listener: () -> Unit)

    fun addOnWindowClosingListener(listener: () -> Unit)

    fun setParams(params: Params)

    fun getParams(): Params
}
