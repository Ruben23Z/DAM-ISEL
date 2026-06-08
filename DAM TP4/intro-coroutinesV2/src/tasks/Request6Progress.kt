package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService, req: RequestData, updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {

    val allUsers = mutableListOf<User>();
    val repos = service.getOrgRepos(req.org).also { logRepos(req, it) }.bodyList() //suspende ate receber a lista de repos e extrai o body dos pedidos

    for ((idx, repo) in repos.withIndex()) {
        val users = service.getRepoContributors(req.org, repo.name)//suspende ate receber contribuidores do repo
            .also { logUsers(repo, it) }.bodyList()

        allUsers += users //add dos novos users
        if (idx == repos.lastIndex) {
            // atualiza a UI com o completed=true só no último repo
            updateResults(allUsers.aggregate(), true)
        } else updateResults(allUsers.aggregate(), false)
    }
}

