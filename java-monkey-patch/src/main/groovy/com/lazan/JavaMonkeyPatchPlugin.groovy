package com.lazan

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.file.FileCopyDetails
import java.util.*


class JavaMonkeyPatchPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            apply plugin: 'java'
            configurations {
                monkeyPatchNonTransitive { transitive = false }
                monkeyPatchTransitive
            }
            extensions.add('monkeyPatch', new MonkeyPatchExtension(project))
            afterEvaluate {
                boolean initialized = project.extensions.getByType(MonkeyPatchExtension).initialized
                if (!initialized) throw new RuntimeException("monkeyPatch.target not initialized")
            }

        }
    }

    static class MonkeyPatchExtension {
        private final Project project
        private boolean initialized

        MonkeyPatchExtension(Project project) {
            this.project = project
        }

        void setTarget(Object target) {
            if (initialized) {
                throw new RuntimeException("Attempted to set monkey patch target twice")
            }

            project.with {
                dependencies {
                    monkeyPatchTransitive target
                    monkeyPatchNonTransitive target
                    compileOnly(target) {
                        transitive = false
                    }
                }

                Set<Map>  depSet = [] as Set
                ResolvedDependency topDependency = configurations.monkeyPatchTransitive.resolvedConfiguration.firstLevelModuleDependencies.iterator().next()
                topDependency.children.each { ResolvedDependency child ->
                    child.allModuleArtifacts.each { ResolvedArtifact artifact ->
                        ModuleVersionIdentifier mvi = artifact.moduleVersion.id
                        def dependency = [
                                group  : mvi.group,
                                name   : mvi.name,
                                version: mvi.version,
                                ext    : artifact.extension
                        ]
                        if (artifact.classifier) {
                            dependency['classifier'] = artifact.classifier
                        }
                        depSet << dependency
                    }
                }

                depSet.each { Map dependency ->
                    logger.info "Adding $dependency"
                    dependencies.compile(dependency) {
                        transitive = false
                    }
                }

                jar {
                    with copySpec {
                        from zipTree(configurations.monkeyPatchNonTransitive.singleFile)
                        eachFile { FileCopyDetails action ->
                            String path = action.relativePath.pathString
                            def output = sourceSets.main.output
                            boolean patched = new File(output.classesDir, path).exists() || new File(output.resourcesDir, path).exists()
                            if (patched) {
                                logger.info "Using patched version of $path"
                                action.exclude()
                            }
                        }
                    }
                }
            }

            initialized = true
        }

        boolean isInitialized() {
            return initialized
        }
    }
}