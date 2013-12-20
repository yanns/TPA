package integrationtests


import play.api.test._

class IndexPageIntegrationTest extends PlaySpecification {

  "The TPA index page" should {

    "contain at least one video" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("Top Videos of the week!")
      browser.$(".tba-video").size() mustNotEqual 0
    }
  }
}
