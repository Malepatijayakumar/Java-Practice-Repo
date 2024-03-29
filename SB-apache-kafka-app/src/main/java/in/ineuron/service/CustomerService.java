package in.ineuron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import in.ineuron.model.Customer;
import in.ineuron.util.KafkaConstants;


@Service("customerService")
public class CustomerService {

	@Autowired
	private KafkaTemplate<String, Customer> kafkaTemplate;

	/**
	 * This method is used to publish customer records as msgs to kafka topic
	 * 
	 * @param customers
	 * @return
	 */
	public String add(Customer customer) {
		kafkaTemplate.send(KafkaConstants.TOPIC, customer);
		System.out.println("************Msg published to Kafka topic***************");
		return "Customer Records Added To Kafka Queue Successfully";
	}

	/**
	 * This method is used to consume messages from kafka topic
	 * 
	 * @param c
	 * @return
	 */
	@KafkaListener(topics = KafkaConstants.TOPIC, groupId = KafkaConstants.GROUP_ID)
	public Customer listener(Customer c) {
		System.out.println("***Msg recieved from Kafka Topic ::" + c);
		//saving logic
		return c;
	}
}
