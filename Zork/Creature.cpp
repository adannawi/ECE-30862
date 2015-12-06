#include "Creature.h"
//#include "Zork.h"
#include <list>

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
	    this->triggers.push_front(new Trigger(node));
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
	    this->print = node->value();
	}
	if (string(node->name()) == string("action")){
	    this->action = node->value();
	}
	if (string(node->name()) == string("trigger")){
	    this->triggers.push_front(new Trigger(node)); //handle trigger
	}
	node = node -> next_sibling();
    }
}

void Creature::addCondition(xml_node<> * node){
    node = node -> first_node();
    
    while (node != NULL){
	if (string(node->name()) == string("status")){
	    conditions.push_front(new StatusCondition(node));//add condition object
	}
	if (string(node->name()) == string("has")){
	    conditions.push_front(new HasCondition(node));//add condhas object
	}
	node = node -> next_sibling();
    }
}

bool Creature::attack(Zork&zork, string weapon){
    if (vulnerabilities.find(weapon) == vulnerabilities.end()){
	cout << "No vulnerabilities!!" << endl;
	return false;
    }
    for (list<Condition*>::iterator it = conditions.begin(); it != conditions.end(); ++it){
	cout << "Checking conditions.. " << endl;
	if (!((*it)->evaluate(zork))){
	    cout << "Conditions not met!" << endl;
	    return false;
	}
    }
    return true;
}
