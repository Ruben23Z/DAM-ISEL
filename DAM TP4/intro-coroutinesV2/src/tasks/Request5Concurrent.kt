package tasks

import contributors.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {

    val repos = service.getOrgRepos(req.org).also { logRepos(req, it) }.bodyList()
    val deferreds: List<Deferred<List<User>>> = repos.map { repo -> //pede contribuitors de um repo e devolve uma list<user>
        async(Dispatchers.Default) { //especifica a thread poll que a coroutine corre
            // lanca nova coroutine que corre concorrentemente
            log("starting loading for ${repo.name}")
            service.getRepoContributors(req.org, repo.name).also { logUsers(repo, it) }.bodyList() //obtem o contribuidor do repo
        } }

    deferreds.awaitAll().flatten().aggregate() // so se chama o flattend e agregate depois do await
}