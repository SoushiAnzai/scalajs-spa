package spatutorial.client.services

import diode.ActionResult._
import diode.RootModelRW
import diode.data._
import spatutorial.shared._
import spatutorial.shared.model.{TodoHigh, TodoItem, TodoLow, TodoNormal}
import utest._

object SPACircuitTests extends TestSuite {
  def tests = TestSuite {
    'TodoHandler - {
      val model = Ready(Todos(Seq(
        TodoItem("1", 0, "Test1", TodoLow, completed = false),
        TodoItem("2", 0, "Test2", TodoLow, completed = false),
        TodoItem("3", 0, "Test3", TodoHigh, completed = true)
      )))

      val newTodos = Seq(
        TodoItem("3", 0, "Test3", TodoHigh, completed = true)
      )

      def build = new TodoHandler(new RootModelRW(model))

      'RefreshTodos - {
        val h = build
        val result = h.handle(RefreshTodos)
        result match {
          case EffectOnly(effects) =>
            assert(effects.size == 1)
          case _ =>
            assert(false)
        }
      }

      'UpdateAllTodos - {
        val h = build
        val result = h.handle(UpdateAllTodos(newTodos))
        assert(result == ModelUpdate(Ready(Todos(newTodos))))
      }

      'UpdateTodoAdd - {
        val h = build
        val result = h.handle(UpdateTodo(TodoItem("4", 0, "Test4", TodoNormal, completed = false)))
        result match {
          case ModelUpdateEffect(newValue, effects) =>
            assert(newValue.get.items.size == 4)
            assert(newValue.get.items(3).id == "4")
            assert(effects.size == 1)
          case _ =>
            assert(false)
        }
      }

      'UpdateTodo - {
        val h = build
        val result = h.handle(UpdateTodo(TodoItem("1", 0, "Test111", TodoNormal, completed = false)))
        result match {
          case ModelUpdateEffect(newValue, effects) =>
            assert(newValue.get.items.size == 3)
            assert(newValue.get.items.head.content == "Test111")
            assert(effects.size == 1)
          case _ =>
            assert(false)
        }
      }

      'DeleteTodo - {
        val h = build
        val result = h.handle(DeleteTodo(model.get.items.head))
        result match {
          case ModelUpdateEffect(newValue, effects) =>
            assert(newValue.get.items.size == 2)
            assert(newValue.get.items.head.content == "Test2")
            assert(effects.size == 1)
          case _ =>
            assert(false)
        }
      }
    }

    'MotdHandler - {
      val model: Pot[String] = Ready("Message of the Day!")
      def build = new MotdHandler(new RootModelRW(model))

      'UpdateMotd - {
        val h = build
        var result = h.handle(UpdateMotd())
        result match {
          case ModelUpdateEffect(newValue, effects) =>
            assert(newValue.isPending)
            assert(effects.size == 1)
          case _ =>
            assert(false)
        }
        result = h.handle(UpdateMotd(Ready("New message")))
        result match {
          case ModelUpdate(newValue) =>
            assert(newValue.isReady)
            assert(newValue.get == "New message")
          case _ =>
            assert(false)
        }
      }
    }
  }
}
