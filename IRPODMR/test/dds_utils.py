from cyclonedds.domain import DomainParticipant
from cyclonedds.topic import Topic
from cyclonedds.pub import Publisher, DataWriter
from cyclonedds.sub import Subscriber, DataReader
from cyclonedds.idl import IdlStruct
from dataclasses import dataclass
import yaml

#with open("config.yaml") as f:
#    config = yaml.safe_load(f)


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

        self.cmd_vel_topic = Topic(self.participant, 'CmdVel', Twist)
        self.pose_topic = Topic(self.participant, 'Pose', Pose)
        self.spawn_topic = Topic(self.participant, 'Spawn', Spawn)
        self.publisher = Publisher(self.participant)
        self.subscriber = Subscriber(self.participant)
        
    def create_writer(self, topic_type):
        if topic_type == "cmd_vel":
            return DataWriter(self.publisher, self.cmd_vel_topic)
        elif topic_type == "pose":
            return DataWriter(self.publisher, self.pose_topic)
        elif topic_type == "spawn":
            return DataWriter(self.publisher, self.spawn_topic)
        
    def create_reader(self, topic_type):
        if topic_type == "cmd_vel":
            return DataReader(self.subscriber, self.cmd_vel_topic)
        elif topic_type == "pose":
            return DataReader(self.subscriber, self.pose_topic)
        elif topic_type == "spawn":
            return DataReader(self.subscriber, self.spawn_topic)

