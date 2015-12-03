#include "Item.h"
#include <iostream>
#include <string>

Item::Item(){
}

Item::Item(xml_node<> * node){
    createItem(node);
}

void Item::createItem(xml_node<> * node){
    node = node->first_node();
    while (node != NULL){
	if (string(node->name()) == string("name")){
	    this->name = node->value();
	}
	if (string(node->name()) == string("writing")){
	    this->writing = node->value();
	}
	if (string(node->name()) == string("status")){
	    this->status = node->value();
	}
	if (string(node->name()) == string("turnon")){
	    createTurnOn(node->first_node());
	}
	//DEBUG cout << node -> name() << endl;
	node = node -> next_sibling();
    }
}

void Item::createTurnOn(xml_node<> * turnOn){
    while (turnOn != NULL){
	//	cout << "Turn On: " << turnOn->name() << endl;
	if (string(turnOn->name()) == string("print")){
	    this->turnOnMessage = turnOn->value();
	}
	if (string(turnOn->name()) == string("action")){
	    this->turnOnAction = turnOn->value();
	}
	turnOn = turnOn -> next_sibling();
    }
}
