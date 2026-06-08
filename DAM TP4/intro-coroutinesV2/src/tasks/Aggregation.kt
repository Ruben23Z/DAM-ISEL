package tasks

import contributors.User
import javax.lang.model.element.Name
import kotlin.collections.sumOf

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/




/* era para ser assim mas da erro
this.groupBy { it.login } //agrupa pelo users pelo login
.map { User(it.key, sumOf { it.contributions }) } // transforma cada grupo num user com as contruibuicoes somadas
.sortedByDescending { it.contributions } //ordem decrescente pelas contibuicaos feitas
*///eventual achado do site :( depois dos bugs
fun List<User>.aggregate(): List<User> =
    groupBy { it.login }.map { (login, group) -> User(login, group.sumOf { it.contributions }) }
        .sortedByDescending { it.contributions }