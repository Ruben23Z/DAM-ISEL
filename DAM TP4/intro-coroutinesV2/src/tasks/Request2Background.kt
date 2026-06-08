package tasks

import contributors.GitHubService
import contributors.RequestData
import contributors.User
import kotlin.concurrent.thread

fun loadContributorsBackground(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    thread { //inicia a trhead que arranca em background, se bloqueia não afeta a ui
        val contributors = loadContributorsBlocking(service, req)
        updateResults(contributors)//quando termina chama updateresults
    }

    //    updateResults(loadContributorsBlocking(service, req)) solucao do site, passa o processo para uma thread diferente e que é chamado quando acaba o processo
}