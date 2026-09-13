package org.kmptemplate.project.architecture

import com.lemonappdev.konsist.api.Konsist

private fun isProductionPath(path: String): Boolean =
    "/commonTest/" !in path && "/androidHostTest/" !in path && "/iosTest/" !in path

/**
 * Scope untuk aturan-aturan yang hanya berlaku pada module `:sharedLogic` — MVI purity,
 * disiplin data layer, visibility DTO. Preview & fake data source di `:androidApp` /
 * `commonTest` sengaja dikecualikan supaya scaffolding testing tidak melanggar aturan.
 */
internal fun sharedLogicScope() = Konsist
    .scopeFromProject()
    .files
    .filter { file ->
        val path = file.path.replace('\\', '/')
        "/sharedLogic/src/" in path && isProductionPath(path)
    }

/**
 * Scope untuk aturan lintas-module — mis. isolasi fitur (auth ⊥ walkthrough).
 * Mencakup baik `:sharedLogic` maupun `:androidApp` production sources agar
 * cross-feature import terlarang di layer UI juga terjaring.
 */
internal fun productionScope() = Konsist
    .scopeFromProject()
    .files
    .filter { file ->
        val path = file.path.replace('\\', '/')
        ("/sharedLogic/src/" in path || "/androidApp/src/main/" in path) && isProductionPath(path)
    }
