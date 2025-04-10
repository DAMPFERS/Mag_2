import xml.etree.ElementTree as ET
from xml.dom import minidom
import random

def generate_launch_xml():
    launch = ET.Element("launch")

    turtlesim_node = ET.SubElement(launch, "node", name="turtlesim", pkg="turtlesim", type="turtlesim_node", output="screen")

    teleop_node = ET.SubElement(launch, "node", name="teleop", pkg="turtlesim", type="turtle_teleop_key", output="screen")
    
    NODES = 5
    
    for i in range(2,NODES + 2):
    	
    	turtle_node = ET.SubElement(launch, "node", name=f"turtle{i}_node", pkg="test_pack", type="lab1.py", output="screen")
    	ET.SubElement(turtle_node, "param", name="follower_name", value=f"turtle{i}")
    	ET.SubElement(turtle_node, "param", name="target_name", value=f"turtle{i-1}")
    	ET.SubElement(turtle_node, "param", name="speed", value=str(round(random.random() * 4, 2)))
    	ET.SubElement(turtle_node, "param", name="spawn_x", value=str(round(random.random() * 10, 2)))
    	ET.SubElement(turtle_node, "param", name="spawn_y", value=str(round(random.random() * 10, 2)))
    	ET.SubElement(turtle_node, "param", name="spawn_theta", value="0.0")


    tree = ET.ElementTree(launch)
    xml_str = ET.tostring(launch, encoding="utf-8")
    parsed_str = minidom.parseString(xml_str)
    pretty_xml_as_string = parsed_str.toprettyxml(indent="  ")

    with open("launch_file.xml", "w") as file:
        file.write(pretty_xml_as_string)

generate_launch_xml()

