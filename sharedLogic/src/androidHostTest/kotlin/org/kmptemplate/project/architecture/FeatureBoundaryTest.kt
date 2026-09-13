package org.kmptemplate.project.architecture

import com.lemonappdev.konsist.api.verify.assertFalse
import kotlin.test.Test

/**
 * Menjaga fitur-fitur (auth, walkthrough) tetap saling independen.
 *
 * Kalau salah satu fitur perlu tahu tentang fitur lain, promosikan konsepnya
 * ke package berbagi (mis. `core` / `shared`) — jangan cross-import langsung.
 */
class FeatureBoundaryTest {

    @Test
    fun `auth feature does not depend on walkthrough feature`() {
        productionScope()
            .filter { it.packagee?.name.orEmpty().startsWith("$ROOT.auth") }
            .assertFalse(additionalMessage = FEATURE_ISOLATION_HINT) { file ->
                file.hasImport { import -> import.name.startsWith("$ROOT.walkthrough") }
            }
    }

    @Test
    fun `walkthrough feature does not depend on auth feature`() {
        productionScope()
            .filter { it.packagee?.name.orEmpty().startsWith("$ROOT.walkthrough") }
            .assertFalse(additionalMessage = FEATURE_ISOLATION_HINT) { file ->
                file.hasImport { import -> import.name.startsWith("$ROOT.auth") }
            }
    }

    private companion object {
        const val ROOT = "org.kmptemplate.project"
        const val FEATURE_ISOLATION_HINT =
            "Fitur harus independen. Kalau butuh konsep bersama, angkat ke package `core`/shared."
    }
}
