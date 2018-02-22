# Integrating Prometheus client into akka-http

These instructions will allow you to integrate this library into your akka-http project to generate prometheus metrics.

## Supported metrics

- **Counter:** A cumulative metric that represents a single numerical value that only ever goes up.
- **Gauge:** A gauge is a metric that represents a single numerical value that can arbitrarily go up and down. For example number of active connections.
- **Histogram:** A histogram samples observations (usually things like request durations or response sizes) and counts them in configurable buckets. It also provides a sum of all observed values. Most commonplace metrics fall under this category.
- **Summary:** Similar to a histogram, a summary samples observations (usually things like request durations and response sizes). While it also provides a total count of observations and a sum of all observed values, it calculates configurable quantiles over a sliding time window. A typical example is getting the 95th percentile of requests duration to understand the worst case responsiveness of our system.

## Adding metrics

### To endpoints

To add metrics to akka-http endpoints you should use the following traits:
- `com.full360.prometheus.http.akka.AkkaHttpCounter`, it contains the `counter` directive
- `com.full360.prometheus.http.akka.AkkaHttpHistogram`, it contains the `histogram` directive
- `com.full360.prometheus.http.akka.AkkaHttpGauge`, it contains the `gauge` directive
- `com.full360.prometheus.http.akka.AkkaHttpSummary`, it contains the `summary` directive

Once you define your metrics needs, follow this steps:

1. Import the metrics you need, the most common usage is `AkkaHttpCounter` and `AkkaHttpHistogram`.
2. Add the directives to the beggining of the route you are exposing.

### To external calls (Futures)

1. Import `com.full360.prometheus.Prometheus._` into your file.
2. Create a variable to hold the name of the metric, i.e. `val name: String = "some_endpoint_call_duration_seconds"` it should not contain any white spaces
3. Create a variable to hold the help text of the metric, i.e. `val help: String = "Some endpoint call duration in seconds"` it should be a human readable version of the name
4. Wrap the call of the external endpoint with the `prometheusFuture` method:
    ```scala
    def callExternalEndpoint:Future[String]={
      prometheusFuture(Histogram, name, help, Map("method" ->  "callExternalEndpoint"), timeUnit = SECONDS) {
        Future{
          //some call to external endpoint
        }
      }
    }
    ```
Let's check in detail the `prometheusFuture` method call in point 4:

- The first parameter is the type of metric you want to add, it could be:
  - `Histogram`
  - `Counter`
  - `Gauge`
  - `Summary`
- The second and third parameters are the variables you set in the steps 2 and 3
- The fourth parameter are the labels for the metric
- To ensure that your metrics are reliable, always set the timestamp value as `SECONDS`

## Exposing metrics

The metrics are exposed as a resource called `metrics` available via http `GET`. In order to enable this, you should add the metrics route to your service routes:

1. Add the `com.full360.prometheus.http.akka.AkkaHttpMetricService` trait to the class where you are binding the routes to your server
2. Add the `route` route to the list of routes you are binding.
