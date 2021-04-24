package spatutorial.shared.api

// 各apiトレイトをミックスインする
trait Api
    extends Dashboard
    with Todo
{
}
