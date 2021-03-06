package au.com.dius.pact.consumer.junit.formpost

import au.com.dius.pact.consumer.junit.ConsumerPactTest
import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.PactTestExecutionContext
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.core.model.RequestResponsePact

class FormPostWithQueryParametersTest extends ConsumerPactTest {

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {
        builder.given('grizzly bear can be added to zoo')
          .uponReceiving('a POST request to add a grizzly bear named Bubbles with query parameters')
            .path('/zoo-ws/animals')
            .method('POST')
            .headers(['Content-Type': 'application/x-www-form-urlencoded'])
            .body('type=grizzly+bear&name=Bubbles')
            .matchQuery('level', '\\d+', '6')
          .willRespondWith()
            .status(200)
            .headers(['Content-Type': 'application/json'])
            .body(new PactDslJsonBody()
              .stringValue('animalType', 'grizzly bear')
              .stringType('name', 'Bubbles')
              .array('feedingLog')
              .closeArray())
          .toPact()
    }

    @Override
    protected String providerName() { 'zoo-ws' }

    @Override
    protected String consumerName() { 'zoo-client' }

    @Override
    protected void runTest(MockServer mockServer, PactTestExecutionContext context) {
        ZooClient fakeZooClient = new ZooClient(mockServer.url)

        Animal grizzly = fakeZooClient.saveAnimal('grizzly bear', 'Bubbles', '6')

        assert grizzly.animalType == 'grizzly bear'
        assert grizzly.name == 'Bubbles'
        assert grizzly.feedingLog.empty
    }
}
