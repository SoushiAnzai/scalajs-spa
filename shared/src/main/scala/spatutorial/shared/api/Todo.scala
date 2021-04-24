package spatutorial.shared.api

import spatutorial.shared.model.TodoItem

trait Todo {

  // やること全部取得
  def getAllTodos(): Seq[TodoItem]

  // やること更新
  def updateTodo(item: TodoItem): Seq[TodoItem]

  // やること削除
  def deleteTodo(itemId: String): Seq[TodoItem]
}
