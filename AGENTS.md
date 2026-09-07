# AGENTS — Quick guide for AI coding agents

Purpose
- Help an AI agent become productive quickly in this multi-module Gradle Java workspace.

Quick checklist (startup)
- Read `settings.gradle` to discover modules and boundaries.
- Run `./gradlew :<module>:build` or `./gradlew build` to compile all modules.
- Run tests with `./gradlew test` or `./gradlew :<module>:test`.
 - Read `settings.gradle.kts` (or `settings.gradle` if present) to discover modules and boundaries.
 - Run `./gradlew :<module>:build` or `./gradlew build` to compile all modules.
 - Run tests with `./gradlew test` or `./gradlew :<module>:test`.

Big picture (architecture)
- Multi-module Gradle Java project. The canonical module list is in `settings.gradle` (root). Examples of major modules:
 - Multi-module Gradle Java project. The canonical module list is in `settings.gradle.kts` (root). Examples of major modules:
  - `binding/` — library with MavenPublication and POM customization (`binding/build.gradle.kts`).
  - `cellular-machines/` — application module (Spring Boot) with `mainClass` and `bootRun` configuration (`cellular-machines/build.gradle.kts`).
  - `jconky/` — JavaFX + Spring Boot application; platform/natives handled via `javafx` config (`jconky/build.gradle.kts`).
  - `dependency-utils/` — application launcher and utility tooling; defines `application` plugin `mainClass` (`dependency-utils/build.gradle.kts`).
  - `meta-model/` — code-generation tasks and maintenance scripts (`meta-model/build.gradle.kts`).

Where agents matter in this repo
 - Tests: the root `build.gradle.kts` (Kotlin DSL) configures a `-javaagent:` JVM arg (used for mocking/coverage instrumentation). See the Test task configuration that appends `-javaagent:` to `jvmArgs` — agents are often injected here for tests.
- Runtime: application modules use `application` or Spring Boot `bootRun` tasks. Attach agents by
  - adding to `bootRun.jvmArgs = ['-javaagent:/path/to/agent.jar']` in the module `build.gradle`, or
  - export `JAVA_TOOL_OPTIONS="-javaagent:/path/to/agent.jar"` before running `./gradlew :<module>:bootRun` or the generated start script.

Project-specific conventions
- Root-managed plugins & conventions: root `build.gradle` centralizes plugin versions and `subprojects` configuration — don't duplicate plugin versions per module.
- Publishing: `binding/build.gradle` customizes `MavenPublication` and the POM; follow that pattern for any module that is published.
- Codegen and maintenance tasks live in modules like `meta-model/` (look for custom tasks such as `deleteAppFolder`); expect non-standard Gradle tasks.
- JavaFX platform handling: `jconky` shows `javafx` block and platform-specific settings — native packaging is handled per-module.

 - Kotlin DSL note: this repository has migrated to Gradle Kotlin DSL in most places (`*.gradle.kts`). When editing build scripts prefer `build.gradle.kts` and `settings.gradle.kts`. Some legacy or backup files exist alongside `.kts` files (e.g., `build.gradle_` or `settings.gradle.kts_`) — prefer the `.kts` files unless you can confirm the underscore file is actively used.

Developer workflows (explicit commands)
- Build all: `./gradlew build` (root wrapper exists).
- Build single module: `./gradlew :cellular-machines:build`.
- Run Spring Boot app (module): `./gradlew :cellular-machines:bootRun` (or `:jconky:bootRun` for jconky).
- Run as application (if module uses `application` plugin): `./gradlew :dependency-utils:run`.
- Run tests with agent injection (already configured for CI/tests): `./gradlew test` will pick up the root test JVM arg. To inject a custom agent: `./gradlew test -Dtest.jvmArgs="-javaagent:/abs/path/agent.jar"` or set `JAVA_TOOL_OPTIONS`.

Integration points & external deps
- Modules depend on each other via normal Gradle project dependencies; check module `build.gradle` files (e.g., `dependency-utils`, `binding`).
- Native and platform-specific dependencies appear in `jconky` (JavaFX). Agents that interact with native code must be tested on target platforms.
- Publishing targets are configured in modules like `binding` — check `publishing { publications { mavenJava { ... } } }`.

Patterns and examples to look for
- Test agent injection (root `build.gradle.kts`): look for `test { jvmArgs += "-javaagent:${...}" }`.
- Spring Boot main class & bootRun examples: `cellular-machines/build.gradle.kts` and `jconky/build.gradle.kts` define `mainClass` and `bootRun` behavior.
- Custom Gradle tasks: `meta-model/build.gradle.kts` contains small maintenance tasks; search for `task ` definitions.

Where to read next (key files)
 - `settings.gradle.kts` (or `settings.gradle`) — canonical module list and project name.
 - `build.gradle.kts` (root) — shared plugin and test agent configuration.
 - `binding/build.gradle.kts`, `cellular-machines/build.gradle.kts`, `jconky/build.gradle.kts`, `dependency-utils/build.gradle.kts`, `meta-model/build.gradle.kts` — examples of module responsibilities and conventions.
- `.idea/workspace.xml` — indicates IDE Copilot persistence (developer tooling note).

Notes for AI agents
- Prefer modifying module `build.gradle.kts` files for runtime agent injection rather than changing root behavior, unless the change is intended to be global.
- When running or debugging, prefer the Gradle wrapper (`./gradlew`) to ensure consistent JVM/tooling versions.
- Cite exact file lines when suggesting changes (e.g., "edit `build.gradle.kts` Test task where `jvmArgs` is set").

If you add this file to code review
- Include a short snippet of the actual `-javaagent:` line you intend to add and the target module. Reference the module `build.gradle.kts` path for reviewers.

— End of AGENTS.md

