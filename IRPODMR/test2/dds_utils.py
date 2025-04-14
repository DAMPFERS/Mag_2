from cyclonedds.domain import DomainParticipant
from cyclonedds.topic import Topic
from cyclonedds.pub import Publisher, DataWriter
from cyclonedds.sub import Subscriber, DataReader
from cyclonedds.idl import IdlStruct
from dataclasses import dataclass
from cyclonedds.core import Qos, Policy
import logging

logging.basicConfig(level=logging.INFO)

@dataclass
class Twist(IdlStruct):
    turtle_id: int
    linear_x: float
    angular_z: float

@dataclass
class Pose(IdlStruct):
    turtle_id: int
    x: float
    y: float
    theta: float

@dataclass
class Spawn(IdlStruct):
    turtle_id: int
    x: float
    y: float
    theta: float

class DDSNode:
    def __init__(self):
        self.participant = DomainParticipant(0)
        self.qos = Qos(
            Policy.Reliability.Reliable(max_blocking_time=10_000_000_000),
            Policy.Durability.TransientLocal,
            Policy.History.KeepLast(depth=10)
        )
        self.topics = {}
        self.publisher = Publisher(self.participant)
        self.subscriber = Subscriber(self.participant)
        logging.info(f"DDS Node initialized in domain {self.participant.domain_id}")

    def get_topic(self, name, type_cls):
        if name not in self.topics:
            self.topics[name] = Topic(
                self.participant, 
                name, 
                type_cls,
                qos=self.qos
            )
            logging.info(f"Created topic: {name}")
        return self.topics[name]

    def create_writer(self, topic_name, type_cls):
        topic = self.get_topic(topic_name, type_cls)
        return DataWriter(self.publisher, topic)

    def create_reader(self, topic_name, type_cls):
        topic = self.get_topic(topic_name, type_cls)
        return DataReader(self.subscriber, topic)
