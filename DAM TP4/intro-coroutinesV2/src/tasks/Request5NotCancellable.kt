package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

suspend fun loadContributorsNotCancellable(service: GitHubService, req: RequestData): List<User> {


    val repos = service.getOrgRepos(req.org).also { logRepos(req, it) }.bodyList()


    val deferreds: List<Deferred<List<User>>> =
        repos.map { repo -> //pede contribuitors de um repo e devolve uma list<user>
            GlobalScope.async { // cria coroutines independentes que n sao filhas, ou seja ao serem canceladas n afetm as outras
                log("starting loading for ${repo.name}")
                delay(3000)
                service.getRepoContributors(req.org, repo.name).also { logUsers(repo, it) }
                    .bodyList() //obtem o contribuidor do repo

            }
        }
    return deferreds.awaitAll().flatten().aggregate() // so se chama o flattend e agregate depois do await
}
