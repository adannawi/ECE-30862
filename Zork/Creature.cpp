#include "Creature.h"

Creature::Creature(xml_node<> * node){
    createCreature(node);
}

void Creature::createCreature(xml_node <> * node){
    node = node->first_node();
    while (node != NULL) {
	if (string(node->name()) == string("name")){
	    this->name = node->value();
	}
	if (string(node->name()) == string("status")){
	    this->status = node->value();
	}
	if (string(node->name()) == string("vulnerability")){
	    this->vulnerabilities[node->value()] = node->value();
	}
	if (string(node->name()) == string("trigger")){
	    //handle trigger
	}
	if (string(node->name()) == string("attack")){
	    getAttacked(node);
	}
	node = node -> next_sibling();
    }
}

void Creature::getAttacked(xml_node<> * node){
    node = node -> first_node();
    while (node != NULL){
	if (string(node->name()) == string("condition")){
	    addCondition(node);
	}
	if (string(node->name()) == string("print")){
	    this->print.push_front(node->value());
	}
	if (string(node->name()) == string("action")){
	    this->action.push_front(node->value());
	}
	if (string(node->name()) == string("trigger")){
	    //handle trigger
	}
	node = node -> next_sibling();
    }
}

void Creature::addCondition(xml_node<> * node){
    node = node -> first_node();
    
    while (node != NULL){
	if (string(node->name()) == string("status")){
	    //add condition object
	}
	if (string(node->name()) == string("has")){
	    //add condhas object
	}
	node = node -> next_sibling();
    }
}
