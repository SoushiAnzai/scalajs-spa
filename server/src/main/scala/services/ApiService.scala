package services

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import spatutorial.shared._

class ApiService extends Api {
  var todos = Seq(
    TodoItem("1", 0x61626364, "洗濯物しまう", TodoLow, completed = true),
    TodoItem("2", 0x61626364, "カレー作る", TodoNormal, completed = false),
    TodoItem("3", 0x61626364, "まどたんとモンブランたべる", TodoHigh, completed = false),
    TodoItem("4", 0x61626364, "Terraformのキャッチアップをする", TodoNormal, completed = false)
  )

  override def welcomeMsg(name: String): String =
    s"そうまるSPAにようこそ $name！ ただ今の時間は ${nowDateJapaneseFormat()} です！"

  def nowDateJapaneseFormat(): String = {
    // 現在時間日本語表示
    val df: SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    df.format(new Date)
  }


  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
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

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }
}
