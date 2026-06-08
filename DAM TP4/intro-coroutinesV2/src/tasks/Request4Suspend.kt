package tasks

import contributors.*

suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {

    val repos = service.getOrgRepos(req.org)//suspende a coroutine ate receber a lista de repos do pedido        .getOrgReposCall(req.org)
//        .execute() // Executes request and blocks the current thread
        .also { logRepos(req, it) }.bodyList() //imprime o estado e obtem o body do http

    return repos.flatMap { repo ->
        service.getRepoContributors(req.org, repo.name) // para cada repo suspende
//            .execute() // Executes request and blocks the current thread
            .also { logUsers(repo, it) }.bodyList() //e devolve a lista de users
    }.aggregate() //ordena e agrupa
}