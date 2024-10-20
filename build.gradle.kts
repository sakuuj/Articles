import java.util.stream.Collectors

tasks.register("prepareForDocker") {

    description = "Prepares the project to be ready for 'docker compose up'"
    group = "other"

    val noClean = project.gradle.startParameter.projectProperties["noClean"];

    val services = project(":services")
        .subprojects
        .stream()
        .filter { proj -> proj.path != ":services:common" }
        .map { proj -> proj.path }
        .toList()

    val tasksToDependOnOrdered = mutableListOf<String>();

    val starterProject = project(":index-creator-elasticsearch-spring-boot-starter");
    val cleanStarterTask = starterProject
        .tasks
        .getByName("clean")

    if (noClean == null) {
        tasksToDependOnOrdered.add(cleanStarterTask.path)
        tasksToDependOnOrdered.addAll(
            services
                .stream()
                .map { name -> "${name}:clean" }
                .collect(Collectors.toList()))
    }

    val publishStarterTask = starterProject
        .tasks
        .getByName("publishToMavenLocal")
    tasksToDependOnOrdered.add(publishStarterTask.path)

    tasksToDependOnOrdered.addAll(
        services
            .stream()
            .filter { name -> !name.startsWith(":services:common") }
            .map { name -> "${name}:bootJar" }
            .collect(Collectors.toList()))

    println(tasksToDependOnOrdered)

    dependsOn(ordered(tasksToDependOnOrdered))
}

fun ordered(dependencyPaths: List<String>): List<Task> {

    val dependencies = dependencyPaths.stream()
        .map { path -> tasks.getByPath(path) }
        .toList()

    for (i in dependencies.indices) {

        if (i == dependencies.indices.last) {
            break
        }

        dependencies[i + 1].mustRunAfter(dependencies[i])
    }

    return dependencies
}