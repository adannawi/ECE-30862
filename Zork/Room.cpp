#include "Room.h"

Room::Room(xml_node<>* node){
    createRoom(node);
}

void Room::createRoom(xml_node<> * node){
    node = node -> first_node();
    while (node != NULL){
	if (string(node->name()) == string("name")){
	    this->name = node->value();
	}
	if (string(node->name()) == string("description")){
	    this->description = node->value();
	}
	if (string(node->name()) == string("item")){
	    this->items[node->value()] = node->value();
	}
	if (string(node->name()) == string("trigger")){
	    this->triggers.push_front(new Trigger(node));
	}
	if (string(node->name()) == string("border")){
	    setBorder(node);
	}
	if (string(node->name()) == string("creature")){
	    this->creatures[node->value()] = node->value();
	}
	if (string(node->name()) == string("container")){
	    this->containers[node->value()] = node->value();
	}
	if (string(node->name()) == string("type")){
	    this->type = node->value();
	}
	node = node -> next_sibling();
    }
}
void Room::setBorder(xml_node<> * node) {
    xml_node<> * borderNode = node -> first_node();
    string dir = "N/A";
    string name = "N/A";
    while (borderNode != NULL){
	if (string(borderNode -> name()) == string("direction")){
	    dir = borderNode->value();
	}
	if (string(borderNode -> name()) == string("name")){
	    name = borderNode -> value();
	}
	borderNode = borderNode -> next_sibling();
    }
    this->borders[dir] = name;
}
