package services

import spatutorial.shared.api.Api

// 各サービスクラスを継承する
class ApiService extends Api
  with DashboardService
  with TodoService
{
}
