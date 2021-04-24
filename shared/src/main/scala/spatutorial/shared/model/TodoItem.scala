package spatutorial.shared.model

import boopickle.Default._

sealed trait TodoPriority

case object TodoLow extends TodoPriority

case object TodoNormal extends TodoPriority

case object TodoHigh extends TodoPriority

case class TodoItem(
                     // ID
                     id: String,
                     // タイムスタンプ
                     timeStamp: Int,
                     // 内容
                     content: String,
                     // 重要度
                     priority: TodoPriority,
                     // 完了:True、未完:False
                     completed: Boolean
                   )

object TodoPriority {
  implicit val todoPriorityPickler: Pickler[TodoPriority] = generatePickler[TodoPriority]
}
