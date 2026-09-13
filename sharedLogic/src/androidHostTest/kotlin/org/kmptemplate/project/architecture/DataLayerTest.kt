package org.kmptemplate.project.architecture

import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

/**
 * Menjaga data layer tetap tertutup: DTO adalah detail internal package,
 * dan repository adalah pintu masuk resmi ke sumber data.
 */
class DataLayerTest {

    @Test
    fun `DTO classes must be internal`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withPackage("..data.dto..")
            .assertTrue(
                additionalMessage = "DTO seharusnya `internal` — tidak boleh bocor keluar module."
            ) { klass -> klass.hasInternalModifier }
    }

    @Test
    fun `Repository implementations live under data package`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withNameEndingWith("RepositoryImpl")
            .assertTrue(additionalMessage = "Implementasi repository harus berada di package `data`.") { klass ->
                klass.packagee?.name.orEmpty().contains(".data")
            }
    }

    @Test
    fun `Concrete DataSource implementations live under data package`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withNameEndingWith("DataSource")
            .filter { !it.hasAbstractModifier && !it.hasSealedModifier }
            .assertTrue(additionalMessage = "Implementasi data source harus berada di package `data`.") { klass ->
                klass.packagee?.name.orEmpty().contains(".data")
            }
    }
}
