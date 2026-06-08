package tasks

import contributors.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun loadContributorsChannels(
    service: GitHubService, req: RequestData, updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {

    val channel = Channel<List<User>>()
    val repos = service.getOrgRepos(req.org).also { logRepos(req, it) }.bodyList()

    coroutineScope {
        for (repo in repos) {
            launch {

                val users = service.getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList() //obtem o contribuidor do repo
                channel.send(users)

            }

        }

        val allUsers = mutableListOf<User>();
        repeat(repos.size) {
            val users = channel.receive()

            allUsers += users //add dos novos users
            if (it == repos.lastIndex) {
                // atualiza a UI com o completed=true só no último repo
                updateResults(allUsers.aggregate(), true)
            } else updateResults(allUsers.aggregate(), false)

        }
    }
    channel.close()
}