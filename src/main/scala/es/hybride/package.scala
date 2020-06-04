package es

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.{util => ju}

package object hybride {
  type Key = String
  type DeviceType = String

  val props = new ju.Properties()
  props.put(
    "bootstrap.servers",
    "172.18.0.2:9092,172.18.0.4:9092,172.18.0.5:9092"
  );
  props.put("acks", "all");
  props.put(
    "key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  );
  props.put(
    "value.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  );

  val producer: Producer[String, String] = new KafkaProducer(props);

}
