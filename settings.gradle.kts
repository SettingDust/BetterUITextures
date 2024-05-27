apply(
    from = "https://github.com/SettingDust/FabricKotlinTemplate/raw/main/common.settings.gradle.kts"
)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven2.bai.lol")
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
    }
}

val minecraft = settings.extra["minecraft"]
val kotlin = settings.extra["kotlin"]

dependencyResolutionManagement.versionCatalogs.named("catalog") {
    plugin("explosion", "lol.bai.explosion").version("0.1.0")
    // https://modrinth.com/mod/fwaystones/versions
    library("fabric-waystones", "maven.modrinth", "fwaystones").version("3.3.2+mc$minecraft")
    // https://github.com/wisp-forest/owo-lib/releases/
    library("owo", "io.wispforest", "owo-lib").version("0.11.2+1.20")
    // https://modrinth.com/mod/moonlight/versions
    val moonlight = "1.20-2.11.2"
    library("moonlight-fabric", "maven.modrinth", "moonlight").version("fabric_$moonlight")
    library("moonlight-forge", "maven.modrinth", "moonlight").version("forge_$moonlight")
    // https://modrinth.com/mod/spell-engine/versions
    library("spell-engine", "maven.modrinth", "spell-engine").version("0.12.4+$minecraft")
    // https://modrinth.com/mod/trinkets/versions
    library("trinkets", "dev.emi", "trinkets").version("3.7.1")
    // https://modrinth.com/mod/spell-power/versions
    library("spell-power", "maven.modrinth", "spell-power").version("0.9.19+$minecraft-fabric")
    // https://modrinth.com/mod/cloth-config/versions
    library("cloth-config-fabric", "me.shedaniel.cloth", "cloth-config-fabric").version("11.1.118")
    // https://maven.kosmx.dev/dev/kosmx/player-anim/player-animation-lib-fabric/?C=M&O=D
    library("playeranimator-fabric", "dev.kosmx.player-anim", "player-animation-lib-fabric")
        .version("1.0.2-rc1+1.20")
    // https://modrinth.com/mod/runes/versions
    library("runes", "maven.modrinth", "runes").version("0.9.10+$minecraft-fabric")

    // https://modrinth.com/mod/amethyst-imbuement/versions
    library("amethyst-imbuement", "maven.modrinth", "amethyst-imbuement").version("$minecraft-18")
    // https://modrinth.com/mod/fzzy-core/versions
    library("fzzy-core", "maven.modrinth", "fzzy-core").version("0.3.4+$minecraft")
    // https://modrinth.com/mod/patchouli/versions
    library("patchouli", "vazkii.patchouli", "Patchouli").version("$minecraft-84-FABRIC")
    // https://modrinth.com/mod/amethyst-core/versions
    library("amethyst-core", "maven.modrinth", "amethyst-core").version("1.4.5+$minecraft")

    // https://modrinth.com/mod/farmers-delight-fabric/versions
    library("farmers-delight-fabric", "maven.modrinth", "farmers-delight-fabric").version("1.4.3")

    // https://modrinth.com/mod/illager-invasion/versions
    library("illager-invasion", "maven.modrinth", "illager-invasion")
        .version("v8.0.3-$minecraft-Fabric")
    // https://github.com/Fuzss/modresources/tree/main/maven/fuzs/puzzleslib/puzzleslib-fabric
    library("puzzleslib-fabric", "fuzs.puzzleslib", "puzzleslib-fabric").version("8.1.11")
    // https://github.com/Fuzss/modresources/tree/main/maven/fuzs/forgeconfigapiport/forgeconfigapiport-fabric
    library("forgeconfigapiport-fabric", "fuzs.forgeconfigapiport", "forgeconfigapiport-fabric")
        .version("8.0.0")

    // https://modrinth.com/mod/pack-it-up/versions
    library("pack-it-up", "maven.modrinth", "pack-it-up").version("0.3.1+$minecraft")

    // https://github.com/pearxteam/kasechange
    library("kasechange", "net.pearx.kasechange", "kasechange-jvm").version("1.4.1")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    // https://github.com/DanySK/gradle-pre-commit-git-hooks
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.6"
}

gitHooks {
    preCommit {
        from {
            //             git diff --cached --name-only --diff-filter=ACMR | while read -r a; do
            // echo ${'$'}(readlink -f ${"$"}a); ./gradlew spotlessApply -q
            // -PspotlessIdeHook="${'$'}(readlink -f ${"$"}a)" </dev/null; done
            """
            export JAVA_HOME="${System.getProperty("java.home")}"
            ./gradlew spotlessApply spotlessCheck
            """
                .trimIndent()
        }
    }
    commitMsg { conventionalCommits { defaultTypes() } }
    hook("post-commit") {
        from {
            """
            files="${'$'}(git show --pretty= --name-only | tr '\n' ' ')"
            git add ${'$'}files
            git -c core.hooksPath= commit --amend -C HEAD
            """
                .trimIndent()
        }
    }
    createHooks(true)
}

val name: String by settings

rootProject.name = name

include("mod")

include("quilt")

include("forge")
