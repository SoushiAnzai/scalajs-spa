package services

import java.text.SimpleDateFormat
import java.util.Date

import spatutorial.shared.api.Dashboard

trait DashboardService extends Dashboard {

  override def welcomeMsg(name: String): String =
    s"そうまるSPAにようこそ $name！ ただ今の時間は ${nowDateJapaneseFormat()} です！"

  def nowDateJapaneseFormat(): String = {
    // 現在時間日本語表示
    val df: SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    df.format(new Date)
  }
}
