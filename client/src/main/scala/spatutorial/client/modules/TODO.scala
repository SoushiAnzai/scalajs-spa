package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components._
import spatutorial.client.logger._
import spatutorial.client.services._
import spatutorial.shared._
import scalacss.ScalaCssReact._
import spatutorial.shared.model.{TodoHigh, TodoItem, TodoLow, TodoNormal}

object Todo {

  case class Props(proxy: ModelProxy[Pot[Todos]])

  case class State(selectedItem: Option[TodoItem] = None, showTodoForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshTodos))

    def editTodo(item: Option[TodoItem]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showTodoForm = true))

    def todoEdited(item: TodoItem, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Todo editing cancelled")
      } else {
        Callback.log(s"Todo edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateTodo(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showTodoForm = false))
    }

    def render(p: Props, s: State) =
      Panel(Panel.Props("TODOリスト"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(todos => TodoList(todos.items, item => p.proxy.dispatchCB(UpdateTodo(item)),
          item => editTodo(Some(item)), item => p.proxy.dispatchCB(DeleteTodo(item)))),
        Button(Button.Props(editTodo(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showTodoForm) TodoForm(TodoForm.Props(s.selectedItem, todoEdited))
        else // otherwise add an empty placeholder
          VdomArray.empty())
  }

  // create the React component for To Do management
  val component = ScalaComponent.builder[Props]("TODO")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Todos]]) = component(Props(proxy))
}

object TodoForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[TodoItem], submitHandler: (TodoItem, Boolean) => Callback)

  case class State(item: TodoItem, cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      // mark it as NOT cancelled (which is the default)
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
      // call parent handler with the new item and whether form was OK or cancelled
      props.submitHandler(state.item, state.cancelled)

    def updateDescription(e: ReactEventFromInput) = {
      val text = e.target.value
      // update TodoItem content
      t.modState(s => s.copy(item = s.item.copy(content = text)))
    }

    def updatePriority(e: ReactEventFromInput) = {
      // update TodoItem priority
      val newPri = e.currentTarget.value match {
        case p if p == TodoHigh.toString => TodoHigh
        case p if p == TodoNormal.toString => TodoNormal
        case p if p == TodoLow.toString => TodoLow
      }
      t.modState(s => s.copy(item = s.item.copy(priority = newPri)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a todo or two")
      val headerText = if (s.item.id == "") "新しくTODOを作成" else "TODOを編集"
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        // footer has the OK button that submits the form before hiding it
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        // this is called after the modal has been hidden (animation is completed)
        closed = formClosed(s, p)),
        <.div(bss.formGroup,
          <.label(^.`for` := "description", "内容"),
          <.input.text(bss.formControl, ^.id := "description", ^.value := s.item.content,
            ^.placeholder := "TODOを記入", ^.onChange ==> updateDescription)),
        <.div(bss.formGroup,
          <.label(^.`for` := "priority", "重要度"),
          // using defaultValue = "Normal" instead of option/selected due to React
          <.select(bss.formControl, ^.id := "priority", ^.value := s.item.priority.toString, ^.onChange ==> updatePriority,
            <.option(^.value := TodoHigh.toString, "大事"),
            <.option(^.value := TodoNormal.toString, "ふつう"),
            <.option(^.value := TodoLow.toString, "そんなに")
          )
        )
      )
    }
  }

  val component = ScalaComponent.builder[Props]("TodoForm")
    .initialStateFromProps(p => State(p.item.getOrElse(TodoItem("", 0, "", TodoNormal, completed = false))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}