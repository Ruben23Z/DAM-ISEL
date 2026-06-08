package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    service.getOrgReposCall(req.org).onResponse { responseRepos -> //pede a lista de repos; quando chega, executa o lambda
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        var nprocessos = 0

        val allUsers = mutableListOf<User>()
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                //lanca varios pedidos assincronos em paralelo
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users //quando termina o pedido
                nprocessos++
                if( nprocessos== repos.size){
                    updateResults(allUsers.aggregate())
                }
            }
        }
//antes era chamado sem que os pedidos terminasssem
    }
}

inline fun <T> Call<T>.onResponse(crossinline callback: (Response<T>) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}
