package org.kmptemplate.project.architecture

import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

/**
 * Menjaga package `mvi` tetap murni: hanya berbicara ke repository melalui interface,
 * tidak boleh tahu tentang framework HTTP (Ktor) atau format wire (DTO).
 */
class MviPurityTest {

    @Test
    fun `mvi package must not depend on Ktor`() {
        sharedLogicScope()
            .filter { it.packagee?.name.orEmpty().contains(".mvi") }
            .assertFalse(additionalMessage = "MVI harus bebas dari framework HTTP. Delegasikan ke repository.") { file ->
                file.hasImport { it.name.startsWith("io.ktor") }
            }
    }

    @Test
    fun `mvi package must not depend on DTOs`() {
        sharedLogicScope()
            .filter { it.packagee?.name.orEmpty().contains(".mvi") }
            .assertFalse(additionalMessage = "DTO adalah detail wire format — jangan bocor ke state.") { file ->
                file.hasImport { it.name.contains(".data.dto.") }
            }
    }

    @Test
    fun `state classes must be data classes`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withPackage("..mvi..")
            .withNameEndingWith("State")
            .assertTrue(additionalMessage = "State harus data class agar `.copy()` tersedia dan equality by value.") { klass ->
                klass.hasDataModifier
            }
    }

    @Test
    fun `intent classes must be sealed`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withPackage("..mvi..")
            .withNameEndingWith("Intent")
            .assertTrue(additionalMessage = "Intent adalah aksi terbatas — harus sealed agar `when` exhaustive.") { klass ->
                klass.hasSealedModifier
            }
    }

    @Test
    fun `state properties must all be val`() {
        sharedLogicScope()
            .flatMap { it.classes() }
            .withPackage("..mvi..")
            .withNameEndingWith("State")
            .flatMap { it.properties() }
            .assertTrue(
                additionalMessage = "State harus immutable — properti wajib `val`, bukan `var`."
            ) { property -> property.isVal }
    }
}
