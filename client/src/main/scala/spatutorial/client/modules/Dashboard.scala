package spatutorial.client.modules

import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import spatutorial.client.SPAMain.{Loc, TodoLoc}
import spatutorial.client.components._

import scala.util.Random
import scala.language.existentials

object Dashboard {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[String]])

  case class State(motdWrapper: ReactConnectProxy[Pot[String]])

  // create dummy data for the chart
  val cp = Chart.ChartProps(
    "Test チャート",
    Chart.BarChart,
    ChartData(
      Random.alphanumeric.map(_.toUpper.toString).distinct.take(10),
      Seq(ChartDataset(Iterator.continually(Random.nextDouble() * 10).take(10).toSeq, "まどデータ"))
    )
  )

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("")
    // create and store the connect proxy in state for later use
    .initialStateFromProps(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      <.div(
        // header, MessageOfTheDay and chart components
        <.h2("ダッシュボード"),
        state.motdWrapper(Motd(_)),
        Chart(cp),
        // create a link to the To Do view
        <.div(props.router.link(TodoLoc)("TODOを確認しよう!"))
      )
    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[String]]) = component(Props(router, proxy))
}
