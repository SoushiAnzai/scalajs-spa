package services

import java.util.UUID

import spatutorial.shared.api.Todo
import spatutorial.shared.model.{TodoHigh, TodoItem, TodoLow, TodoNormal}

trait TodoService extends Todo {

  var todos = Seq(
    TodoItem("1", 0x61626364, "洗濯物しまう", TodoLow, completed = true),
    TodoItem("2", 0x61626364, "カレー作る", TodoNormal, completed = false),
    TodoItem("3", 0x61626364, "まどたんとモンブランたべる", TodoHigh, completed = false),
    TodoItem("4", 0x61626364, "Terraformのキャッチアップをする", TodoNormal, completed = false)
  )

  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // やること更新
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }

  // やること削除
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }
}
