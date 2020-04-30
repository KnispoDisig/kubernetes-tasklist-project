package controllers

import javax.inject.{Inject, Singleton}
import models.TaskListInMemoryModel
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.data._
import play.api.data.Forms._

case class LoginData(username: String, password: String)

@Singleton
class TaskList1 @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val loginForm = Form(mapping(
    "Username" -> text(3, 10),
    "Password" -> text(8)
  )(LoginData.apply)(LoginData.unapply))

  def login = Action {implicit request =>
    Ok(views.html.login())
  }

  def validateLoginPost = Action {implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map{args =>
      val username = args("username").head
      val password = args("password").head
      if (TaskListInMemoryModel.validateUser(username, password)) {
        Redirect(routes.TaskList1.taskList()).withSession("username" -> username)
      } else {
        Redirect(routes.TaskList1.login()).flashing("error" -> "Invalid username or password")
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def validateLoginForm = Action {implicit request =>
    Ok("")
  }

  def createUser = Action {implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map{args =>
      val username = args("username").head
      val password = args("password").head
      if (TaskListInMemoryModel.createUser(username, password)) {
        Redirect(routes.TaskList1.taskList()).withSession("username" -> username)
      } else {
        Redirect(routes.TaskList1.login()).flashing("error" -> "The user already exists")
      }
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def validateLoginGet(username: String, password: String) = Action {
    Ok(s"$username entered with password $password!")
  }

  def taskList = Action {implicit request =>
    val usernameOptional = request.session.get("username")
    usernameOptional.map { username =>
      val tasks = TaskListInMemoryModel.getTasks(username)
      Ok(views.html.TaskList1(tasks))
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def logout = Action {
    Redirect(routes.TaskList1.login()).withNewSession
  }

  def addTask = Action {implicit request =>
    val usernameOptional = request.session.get("username")
    usernameOptional.map { username =>
      val postVals = request.body.asFormUrlEncoded
      postVals.map { args =>
        val task = args("newTask").head
        TaskListInMemoryModel.addTask(username, task)
        Redirect(routes.TaskList1.taskList())
      }.getOrElse(Redirect(routes.TaskList1.taskList()))
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }

  def deleteTask = Action {implicit request =>
    val usernameOptional = request.session.get("username")
    usernameOptional.map { username =>
      val postVals = request.body.asFormUrlEncoded
      postVals.map { args =>
        val index = args("index").head.toInt
        TaskListInMemoryModel.removeTask(username, index)
        Redirect(routes.TaskList1.taskList())
      }.getOrElse(Redirect(routes.TaskList1.taskList()))
    }.getOrElse(Redirect(routes.TaskList1.login()))
  }
}
