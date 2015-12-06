#include "Trigger.h"
#include "Zork.h"

Trigger::Trigger(xml_node<>* node){
    createTrigger(node);
}

Trigger::~Trigger(){
}

bool Trigger::evaluate(Zork& zork){
    for(list<Condition*>::iterator it = conditions.begin(); it != conditions.end(); ++it){
	if (!(*it)->evaluate(zork)){
	    return false;
	}
    }
    return true;
}

void Trigger::createTrigger(xml_node<>* node){
    node = node -> first_node();
    while (node != NULL){
	if (string(node->name()) == string("type")){
	    this->type = node->value();
	}else if (string(node->name()) == string("command")){
	    this->conditions.push_front(new Command(node));
	    hasCommand = true;
	}else if (string(node->name()) == string("condition")){
	    addCondition(node);
	}else if (string(node->name()) == string("print")){
	    this->print = node->value();
	}else if (string(node->name()) == string("action")){
	    this->action.push_front(node->value());
	}
	node = node -> next_sibling();
    }
}

void Trigger::addCondition(xml_node<>* node){
     node = node -> first_node();
     // cout << "Node name in trigger addCondition: " << node->name() << endl;
    while (node != NULL){
	if (string(node->name()) == string("status")){
	    conditions.push_front(new StatusCondition(node));
	    return;
	}
	if (string(node->name()) == string("has")){
	    conditions.push_front(new HasCondition(node));
	    return;
	}
	node = node -> next_sibling();
	//	cout << "next sibling: " << node->name() << endl;
    }
}
