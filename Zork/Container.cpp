#include "Container.h"
#include <iostream>

Container::Container(xml_node<> * node){
    createContainer(node);
}

void Container::createContainer(xml_node<> * node){
    node = node->first_node();
    while (node != NULL){
	if (string(node->name()) == string("name")){
	    this->name = node->value();
	}
	if (string(node->name()) == string("item")){
	    this->items[node->value()] = node->value();
	}
	if (string(node->name()) == string("status")){
	    this->status = node->value();
	}
	if (string(node->name()) == string("accept")){
	    this->accept.push_front(node->value());
	    openState = true;
	}
	if (string(node->name()) == string("trigger")){
	    this->triggers.push_front(new Trigger(node));
	}

	node = node->next_sibling();
    }
}

