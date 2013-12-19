package registry

import components.Registry
import org.specs2.mock.Mockito

trait MockEnvironment extends Registry with Mockito {
  override val httpClient = mock[HttpClient]
  override val playerGateway = mock[PlayerGateway]
  override val videoGateway = mock[VideoGateway]
  override val topVideoService = mock[TopVideoService]
}
