package com.example

import com.example.TaskRepository.tasks
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val content: String,
    val isDone: Boolean
)

@Serializable
data class TaskRequest(
    val content: String,
    val isDone: Boolean
)

object TaskRepository {
    val tasks = mutableListOf<Task>(
        Task(id = 1, content = "Learn Ktor", isDone = true),
        Task(id = 2, content = "Build a REST API", isDone = false),
        Task(id = 3, content = "Write Unit Tests", isDone = false)
    )
}
fun getAll(): List<Task> = TaskRepository.tasks
fun add(task: Task){
    TaskRepository.tasks.add(task)
}
fun update(id: Int, updatedTask: Task){    val index = TaskRepository.tasks.indexOfFirst { it.id == id }
    if (index != -1) {
        TaskRepository.tasks[index] = updatedTask
    }
}

fun Application.configureRouting() {
    routing {
        route("/tasks") {
            get("/") {
                call.respondText("Hello Praew")
            }
            get {
                if (TaskRepository.tasks.isNotEmpty()) {
                    call.respond(TaskRepository.tasks)
                } else {
                    call.respondText("No task found", status = HttpStatusCode.NotFound)
                }
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val task = TaskRepository.tasks.find { it.id == id }
                if (task != null) {
                    call.respond(task)
                } else
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)
            }

            post {
                val request = call.receive<TaskRequest>()
                val newId = (TaskRepository.tasks.maxByOrNull { it.id }?.id ?: 0) + 1
                val task = Task(id = newId, content = request.content, isDone = request.isDone)
                TaskRepository.tasks.add(task)
                println("Task list now: ${TaskRepository.tasks}")
                call.respondText("Created", status = HttpStatusCode.Created)
            }
            put("/{id}") {
                val id = call.parameters["id"]!!.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@put
                }
                val request = call.receive<TaskRequest>()
                val index = TaskRepository.tasks.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updatedTask = Task(id = id!!, content = request.content, isDone = request.isDone)
                    TaskRepository.tasks[index] = updatedTask
                    call.respondText("Updated", status = HttpStatusCode.OK)
                } else
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)

            }
            delete("/{id}") {
                val id = call.parameters["id"]!!.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                    return@delete
                }
                val removeTask = TaskRepository.tasks.removeIf { it.id == id }
                if (removeTask) {
                    call.respondText("Deleted", status = HttpStatusCode.OK)
                } else
                    call.respondText("Deleted", status = HttpStatusCode.NotFound)
            }
        }
        }

        }
